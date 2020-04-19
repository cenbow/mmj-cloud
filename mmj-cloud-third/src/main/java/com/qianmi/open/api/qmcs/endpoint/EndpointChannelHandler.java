package com.qianmi.open.api.qmcs.endpoint;

import com.qianmi.open.api.qmcs.BufferManager;
import com.qianmi.open.api.qmcs.Text;
import com.qianmi.open.api.qmcs.channel.ChannelContext;
import com.qianmi.open.api.qmcs.channel.ChannelException;
import com.qianmi.open.api.qmcs.channel.ChannelSender;
import com.qianmi.open.api.qmcs.handler.SimpleChannelHandler;
import com.qianmi.open.api.qmcs.schedule.Scheduler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

// make timing
public class EndpointChannelHandler extends SimpleChannelHandler {

	private static final Log log = LogFactory.getLog(EndpointChannelHandler.class);

	private Endpoint endpoint;
	private AtomicInteger flag;
	private Map<Integer, SendCallback> callbackByFlag;
	// all connect in/out endpoints
	private Map<String, Identity> idByToken;
	private Scheduler<Identity> scheduler;
//	private StateHandler stateHandler;

	public EndpointChannelHandler() {
		this.flag = new AtomicInteger();
		this.callbackByFlag = new ConcurrentHashMap<Integer, SendCallback>();
		this.idByToken = new ConcurrentHashMap<String, Identity>();
	}

	protected void setEndpoint(Endpoint endpoint) {
		this.endpoint = endpoint;
	}

	public void setScheduler(Scheduler<Identity> scheduler) {
		this.scheduler = scheduler;
	}

	protected final boolean flush(Message msg, ChannelSender sender, int timeout) throws ChannelException {
		ByteBuffer buffer = BufferManager.getBuffer();
		MessageIO.writeMessage(buffer, msg);
		return sender.sendSync(buffer, new InnerSendHandler(buffer), timeout);
	}

	protected final void pending(Message msg, ChannelSender sender) throws ChannelException {
		this.pending(msg, sender, null);
	}

	// all send in Endpoint module must call here
	protected final void pending(Message msg, ChannelSender sender, SendCallback callback) throws ChannelException {
		if (callback != null) {
			callback.flag = msg.flag = this.flag.incrementAndGet();
			this.callbackByFlag.put(msg.flag, callback);
		}
		ByteBuffer buffer = BufferManager.getBuffer();
		MessageIO.writeMessage(buffer, msg);
		sender.send(buffer, new InnerSendHandler(buffer));
	}

	public void cancel(SendCallback callback) {
		this.callbackByFlag.remove(callback.flag);
	}

	@Override
	public void onConnect(ChannelContext context) throws Exception {
	}

	@Override
	public void onError(ChannelContext context) throws Exception {
		this.log.error(Text.E_CHANNEL_ERROR, context.getError());
	}

	@SuppressWarnings("unchecked")
	@Override
	public final void onMessage(ChannelContext context) throws Exception {
		Object msg = context.getMessage();

		if (msg instanceof ByteBuffer) {
			this.onMessage(context, (ByteBuffer) msg);
			return;
		}

		for (ByteBuffer buffer : (List<ByteBuffer>) msg)
			this.onMessage(context, buffer);
	}

	private void onMessage(ChannelContext context, ByteBuffer buffer) throws ChannelException {
		Message msg = MessageIO.readMessage(buffer);

		if (msg.messageType == MessageType.CONNECT) {
			this.handleConnect(context, msg);
			return;
		}

		SendCallback callback = this.callbackByFlag.remove(msg.flag);

		if (msg.messageType == MessageType.CONNECTACK) {
			this.handleConnectAck(callback, msg);
			return;
		}

		Identity msgFrom = msg.token != null ? this.idByToken.get(msg.token) : null;
		// must CONNECT/CONNECTACK for got token before SEND
		if (msgFrom == null) {
            ChannelException error = new ChannelException(String.format(
					"[%s] %s: type=%s, token=%s, flag=%s, code=%s, phase=%s, content=%s",
					this.endpoint.getIdentity(),
					Text.E_UNKNOWN_MSG_FROM,
					msg.messageType,
					msg.token,
					msg.flag,
					msg.statusCode,
					msg.statusPhase,
					msg.content));
			if (callback == null)
				throw error;
			callback.setError(error);
			return;
		}

		// raise callback of client
		if (callback != null) {
			this.handleCallback(callback, msg, msgFrom);
			return;
		} else if (this.isError(msg)) {
			this.log.error(String.format(Text.E_GOT_ERROR, msgFrom, msg.statusCode, msg.statusPhase));
			return;
		}

		// raise onMessage for async receive mode
		if (this.endpoint.getMessageHandler() == null)
			return;
		// exec directly
		if (this.scheduler == null) {
			this.internalOnMessage(context, msg, msgFrom);
			return;
		}
		// dispatch
		this.scheduler.schedule(msgFrom, this.createTask(context, msg, msgFrom));
	}

	private Runnable createTask(final ChannelContext context, final Message message, final Identity messageFrom) {
		return new MessageScheduleTask(message) {
			@Override
			public void run() {
				try {
					internalOnMessage(context, message, messageFrom);
				} catch (ChannelException e) {
					log.error(e);
				}
			}
		};
	}

	private void internalOnMessage(ChannelContext context, Message msg, Identity msgFrom) throws ChannelException {
		if (msg.messageType == MessageType.SENDACK) {
			this.endpoint.getMessageHandler().onMessage(msg.content, msgFrom);
			return;
		}

		EndpointContext endpointContext = new EndpointContext(context, this.endpoint, msgFrom, msg);

		try {
			this.endpoint.getMessageHandler().onMessage(endpointContext);
		} catch (Exception e) {
			this.log.error(e);
			// onMessage error should be reply to client
			if (e instanceof ChannelException)
				endpointContext.error(
						((ChannelException) e).getErrorCode(),
						this.parseStatusPhase(((ChannelException) e)));
			else
				endpointContext.error(0, this.parseStatusPhase(e));
		}
	}

	// deal with connect-in message from endpoint,
	// parse identity send from endpoint and assign it a token,
	// token just used for routing message-from, not auth
	private void handleConnect(ChannelContext context, Message connectMessage) throws ChannelException {
		Message ack = this.createConnectAckMessage(connectMessage);
		ack.messageType = MessageType.CONNECTACK;
		try {
			Identity id = this.endpoint.getIdentity().parse(connectMessage.content);
			EndpointProxy proxy = this.endpoint.getEndpoint(id);
			// set connect-in version as the sender protocol version
			ChannelSenderWrapper senderWrapper =
					new ChannelSenderWrapper(context.getSender());
			proxy.add(senderWrapper);
			if (proxy.getToken() == null) {
				synchronized (proxy) {
					if (proxy.getToken() == null)
						// uuid for token? or get from id?
						proxy.setToken(UUID.randomUUID().toString());
				}
			}
			ack.token = proxy.getToken();
			this.idByToken.put(proxy.getToken(), id);
// TODO
//			if (this.stateHandler != null)
//				this.stateHandler.onConnect(proxy, senderWrapper);

			this.log.info(String.format(Text.E_ACCEPT, this.endpoint.getIdentity(), id, proxy.getToken()));
		} catch (ChannelException e) {
			ack.statusCode = e.getErrorCode();
			ack.statusPhase = this.parseStatusPhase(e);
			this.log.error(Text.E_REFUSE, e);
		}
		final ByteBuffer buffer = BufferManager.getBuffer();
		MessageIO.writeMessage(buffer, ack);
		context.reply(buffer, new InnerSendHandler(buffer));
	}

	private void handleConnectAck(SendCallback callback, Message msg) throws ChannelException {
		if (callback == null)
			throw new ChannelException(Text.E_NO_CALLBACK);
		if (this.isError(msg))
			callback.setError(new ChannelException(msg.statusCode, msg.statusPhase));
		else if (msg.token == null) {
			callback.setError(new ChannelException(Text.E_NULL_TOKEN));
		} else {
			callback.setComplete();
			// set token for proxy for sending message next time
			callback.getTarget().setToken(msg.token);
			// store token from target endpoint for receiving it's message
			// next time
			this.idByToken.put(msg.token, callback.getTarget().getIdentity());
			this.log.info(String.format(Text.E_CONNECT_SUCCESS, callback.getTarget().getIdentity(), msg.token));
		}
	}

	private void handleCallback(SendCallback callback, Message msg, Identity msgFrom) {
		if (!callback.getTarget().getIdentity().equals(msgFrom)) {
			this.log.warn(String.format(
					Text.E_IDENTITY_NOT_MATCH_WITH_CALLBACK,
					msgFrom, callback.getTarget().getIdentity()));
			return;
		}
		if (this.isError(msg))
			callback.setError(new ChannelException(msg.statusCode, msg.statusPhase));
		else
			callback.setReturn(msg.content);
	}

	private boolean isError(Message msg) {
		return msg.statusCode != 1 ||
				(msg.statusPhase != null && msg.statusPhase != "");
	}

	private Message createConnectAckMessage(Message connectMessage) {
		Message msg = new Message();
		// version match with message from
		msg.flag = connectMessage.flag;
		msg.token = connectMessage.token;
		return msg;
	}

	private String parseStatusPhase(Exception e) {
		return e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage();
	}

	private String parseStatusPhase(ChannelException e) {
		return e.getMessage() == null && e.getErrorCode() <= 0 ? Text.E_UNKNOWN_ERROR : e.getMessage();
	}

	class InnerSendHandler implements ChannelSender.SendHandler {
		private ByteBuffer buffer;

		public InnerSendHandler(ByteBuffer buffer) {
			this.buffer = buffer;
		}

		@Override
		public void onSendComplete(boolean success) {
			BufferManager.returnBuffer(this.buffer);
		}

	}
}
