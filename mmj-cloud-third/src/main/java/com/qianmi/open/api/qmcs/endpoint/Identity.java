package com.qianmi.open.api.qmcs.endpoint;

import com.qianmi.open.api.qmcs.channel.ChannelException;

public interface Identity {
	public Identity parse(Object data) throws ChannelException;

	public void render(Object to);

	public boolean equals(Identity id);
}