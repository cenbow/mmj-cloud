package com.qianmi.open.api.qmcs.endpoint;

import com.qianmi.open.api.qmcs.channel.ChannelContext;
import com.qianmi.open.api.qmcs.channel.ChannelException;

import java.util.Map;

public class EndpointContext {
	private ChannelContext channelContext;
	private Endpoint endpoint;
	private Identity messageFrom;
	private Message origin;

	public EndpointContext(ChannelContext channelContext,
                           Endpoint endpoint,
                           Identity messageFrom, Message origin) {
		this.channelContext = channelContext;
		this.endpoint = endpoint;
		this.messageFrom = messageFrom;
		this.origin = origin;
	}

	public Identity getMessageFrom() {
		return this.messageFrom;
	}

	public Map<String, Object> getMessage() {
		return this.origin.content;
	}

	public void reply(Map<String, Object> message) throws ChannelException {
		this.endpoint.send(this.channelContext.getSender(), this.createMessage(message));
	}

	public void error(int statusCode, String statusPhase) throws ChannelException {
		Message msg = this.createMessage(null);
		msg.statusCode = statusCode;
		msg.statusPhase = statusPhase;
		this.endpoint.send(this.channelContext.getSender(), msg);
	}

	private Message createMessage(Map<String, Object> message) {
		Message msg = new Message();
		// reply with incoming message's version
		msg.messageType = MessageType.SENDACK;
		msg.flag = this.origin.flag;
		msg.token = this.origin.token;
		msg.content = message;
		return msg;
	}
}
