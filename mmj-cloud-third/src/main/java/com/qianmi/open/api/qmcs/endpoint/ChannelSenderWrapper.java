package com.qianmi.open.api.qmcs.endpoint;

import com.qianmi.open.api.qmcs.channel.ChannelException;
import com.qianmi.open.api.qmcs.channel.ChannelSender;
import com.qianmi.open.api.qmcs.channel.ClientChannel;

import java.net.SocketAddress;
import java.nio.ByteBuffer;

public class ChannelSenderWrapper implements ChannelSender {
	private ChannelSender sender;

	public ChannelSenderWrapper(ChannelSender sender) {
		this.sender = sender;
	}

	public ChannelSender getChannelSender() {
		return this.sender;
	}

	public boolean isValid() {
		return (this.sender instanceof ClientChannel && ((ClientChannel) this.sender).isConnected());
	}

	@Override
	public void send(byte[] data, int offset, int length) throws ChannelException {
		this.sender.send(data, offset, length);
	}

	@Override
	public void send(ByteBuffer dataBuffer, SendHandler sendHandler) throws ChannelException {
		this.sender.send(dataBuffer, sendHandler);
	}

	@Override
	public void send(String msg, SendHandler sendHandler) throws ChannelException {
		this.sender.send(msg, sendHandler);
	}

	@Override
	public boolean sendSync(ByteBuffer dataBuffer, SendHandler sendHandler, int timeoutMilliseconds) throws ChannelException {
		return this.sender.sendSync(dataBuffer, sendHandler, timeoutMilliseconds);
	}

	@Override
	public void close(String reason) {
		this.sender.close(reason);
	}

	@Override
	public SocketAddress getLocalAddress() {
		return this.sender.getLocalAddress();
	}

	@Override
	public SocketAddress getRemoteAddress() {
		return this.sender.getRemoteAddress();
	}
}
