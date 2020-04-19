package com.qianmi.open.api.qmcs.channel;


import com.qianmi.open.api.qmcs.ResetableTimer;

import java.net.URI;

public interface ClientChannel extends ChannelSender {
	public boolean isConnected();
	public ChannelHandler getChannelHandler();
	public void setChannelHandler(ChannelHandler handler);
	public void setUri(URI uri);
	public URI getUri();
	public void setHeartbeatTimer(ResetableTimer timer);
}