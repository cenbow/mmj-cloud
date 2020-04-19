package com.qianmi.open.api.qmcs.endpoint;

import com.qianmi.open.api.qmcs.Text;
import com.qianmi.open.api.qmcs.channel.ChannelException;
import com.qianmi.open.api.qmcs.channel.ChannelSender;
import com.qianmi.open.api.qmcs.channel.ClientChannelSelector;
import com.qianmi.open.api.qmcs.channel.ClientChannelSharedSelector;
import com.qianmi.open.api.qmcs.schedule.Scheduler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.net.URI;
import java.util.*;

// Abstract network model
// https://docs.google.com/drawings/d/1PRfzMVNGE4NKkpD9A_-QlH2PV47MFumZX8LbCwhzpQg/edit
public class Endpoint {

	private static final Log logger = LogFactory.getLog(Endpoint.class);

	protected static int TIMOUT = 5000;

	private Identity identity;
	private ClientChannelSelector channelSelector;
	private EndpointChannelHandler channelHandler;
	private MessageHandler messageHandler;

	// in/out endpoints
	private List<EndpointProxy> connected;

	public Endpoint(Identity identity) {
		this.connected = new ArrayList<EndpointProxy>();
		this.identity = identity;
		this.setClientChannelSelector(new ClientChannelSharedSelector());
		this.setChannelHandler(new EndpointChannelHandler());

		if (this.identity == null)
			throw new NullPointerException("identity");
	}

	public Identity getIdentity() {
		return this.identity;
	}

	public void setMessageHandler(MessageHandler handler) {
		this.messageHandler = handler;
	}

	public MessageHandler getMessageHandler() {
		return this.messageHandler;
	}

	public void setChannelHandler(EndpointChannelHandler channelHandler) {
		this.channelHandler = channelHandler;
		this.channelHandler.setEndpoint(this);
	}

	public void setClientChannelSelector(ClientChannelSelector selector) {
		this.channelSelector = selector;
	}

	public void setScheduler(Scheduler<Identity> scheduler) {
		this.channelHandler.setScheduler(scheduler);
	}

	public Iterator<EndpointProxy> getConnected() {
		return this.connected.iterator();
	}

	public synchronized EndpointProxy getEndpoint(Identity target, URI uri) throws ChannelException {
		return this.getEndpoint(target, uri, null);
	}

	// connect to target via special uri
	public synchronized EndpointProxy getEndpoint(
            Identity target, URI uri, Map<String, Object> extras) throws ChannelException {
		// connect message
		Message msg = new Message();
		msg.messageType = MessageType.CONNECT;
		Map<String, Object> content = new HashMap<String, Object>();
		this.identity.render(content);
		// pass extra data
		if (extras != null)
			content.putAll(extras);
		msg.content = content;

		EndpointProxy e = this.getEndpoint(target);
		// always clear, cached proxy will have broken channel
		e.remove(uri);
		// always reget channel, make sure it's valid
		ClientChannelWrapper channel = new ClientChannelWrapper(
				// set default version on this channel
				this.channelSelector.getChannel(uri));
		channel.setChannelHandler(this.channelHandler);
		// send connect
		this.sendAndWait(e, channel, msg, TIMOUT);
		e.add(channel);
		return e;
	}

	public synchronized EndpointProxy getEndpoint(Identity target) throws ChannelException {
		if (target.equals(this.identity))
			throw new ChannelException(Text.E_ID_DUPLICATE);

		for (EndpointProxy e : this.connected) {
			if (e.getIdentity() != null &&
					e.getIdentity().equals(target))
				return e;
		}
		EndpointProxy e = this.createProxy(target.toString());
		e.setIdentity(target);
		return e;
	}

	protected void send(ChannelSender sender, Message message) throws ChannelException {
		this.channelHandler.pending(message, sender);
	}

	protected boolean sendSync(ChannelSender sender, Message message, int timeout) throws ChannelException {
		return this.channelHandler.flush(message, sender, timeout);
	}

	protected Map<String, Object> sendAndWait(EndpointProxy e,
			ChannelSender sender,
			Message message,
			int timeout) throws ChannelException {
		SendCallback callback = new SendCallback(e);
		this.channelHandler.pending(message, sender, callback);
		try {
			callback.waitReturn(timeout);
		} finally {
			this.channelHandler.cancel(callback);
		}
		if (callback.getError() != null)
			throw callback.getError();
		return callback.getReturn();
	}

	private EndpointProxy createProxy(String reason) {
		EndpointProxy e = new EndpointProxy(this);
		this.connected.add(e);
		if (this.logger.isDebugEnabled())
			this.logger.debug(Text.E_CREATE_NEW + ": " + reason);
		return e;
	}
}
