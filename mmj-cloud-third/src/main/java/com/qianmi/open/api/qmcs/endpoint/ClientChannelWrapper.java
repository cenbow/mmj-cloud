package com.qianmi.open.api.qmcs.endpoint;

import com.qianmi.open.api.qmcs.ResetableTimer;
import com.qianmi.open.api.qmcs.channel.ChannelHandler;
import com.qianmi.open.api.qmcs.channel.ClientChannel;

import java.net.URI;

public class ClientChannelWrapper extends ChannelSenderWrapper implements ClientChannel {
	private ClientChannel clientChannel;

	public ClientChannelWrapper(ClientChannel clientChannel) {
		super(clientChannel);
		this.clientChannel = clientChannel;
	}

	@Override
	public boolean isConnected() {
		return this.clientChannel.isConnected();
	}

	@Override
	public ChannelHandler getChannelHandler() {
		return this.clientChannel.getChannelHandler();
	}

	@Override
	public void setChannelHandler(ChannelHandler handler) {
		this.clientChannel.setChannelHandler(handler);
	}

	@Override
	public void setUri(URI uri) {
		this.clientChannel.setUri(uri);
	}

	@Override
	public URI getUri() {
		return this.clientChannel.getUri();
	}

	@Override
	public void setHeartbeatTimer(ResetableTimer timer) {
		this.clientChannel.setHeartbeatTimer(timer);
	}
}
