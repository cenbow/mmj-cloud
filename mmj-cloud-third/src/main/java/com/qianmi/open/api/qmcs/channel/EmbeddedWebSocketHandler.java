package com.qianmi.open.api.qmcs.channel;

import com.qianmi.open.api.qmcs.Text;
import com.qianmi.open.api.qmcs.embedded.websocket.WebSocket;
import com.qianmi.open.api.qmcs.embedded.websocket.exception.WebSocketException;
import com.qianmi.open.api.qmcs.embedded.websocket.frame.Frame;
import com.qianmi.open.api.qmcs.embedded.websocket.frame.rfc6455.PongFrame;
import com.qianmi.open.api.qmcs.embedded.websocket.handler.WebSocketHandler;
import org.apache.commons.logging.Log;

public class EmbeddedWebSocketHandler implements WebSocketHandler {
	private Log logger;
	private EmbeddedWebSocketClientChannel clientChannel;

	public EmbeddedWebSocketHandler(Log logger,
                                    EmbeddedWebSocketClientChannel clientChannel) {
		this.logger = logger;
		this.clientChannel = clientChannel;
	}

	@Override
	public void onOpen(WebSocket socket) {
		clientChannel.socket = socket;
	}

	@Override
	public void onError(WebSocket socket, WebSocketException e) {
		if (this.clientChannel != null) {
			this.clientChannel.stopHeartbeat();
			this.clientChannel.error = e;
		}
		if (this.haveHandler()) {
			try {
				this.getHandler().onError(this.createContext(e));
			} catch (Exception unexpected) {
				this.error(unexpected);
			}
		}
		this.clear(socket);
		this.error(e);
	}

	@Override
	public void onClose(WebSocket socket) {
		this.clear(socket);
		this.logger.warn(Text.CHANNEL_CLOSED);
	}

	@Override
	public void onCloseFrame(WebSocket socket, int statusCode, String reasonText) {
		this.logger.warn(String.format(Text.WS_CONNECTION_CLOSED_BY, statusCode, reasonText));
	}

	@Override
	public void onMessage(WebSocket socket, Frame frame) {
		if (frame instanceof PongFrame) {
			return;
		}
		if (!this.haveHandler())
			return;
		try {
			this.getHandler().onMessage(this.createContext(frame.getContents()));
		} catch (Exception e) {
			this.clientChannel.close(e.getMessage());
			this.error(e);
		}
	}

	private boolean haveHandler() {
		return this.clientChannel != null &&
				this.clientChannel.getChannelHandler() != null;
	}

	private ChannelHandler getHandler() {
		return this.clientChannel.getChannelHandler();
	}

	private void clear(WebSocket socket) {
		socket.close();
	}

	private ChannelContext createContext(Object message) {
		ChannelContext ctx = new ChannelContext();
		ctx.setSender(this.clientChannel);
		ctx.setMessage(message);
		return ctx;
	}

	private ChannelContext createContext(Throwable error) {
		ChannelContext ctx = new ChannelContext();
		ctx.setSender(this.clientChannel);
		ctx.setError(error);
		return ctx;
	}

	private void error(Throwable e) {
		this.logger.error(Text.ERROR_AT_CLIENT, e);
	}
}
