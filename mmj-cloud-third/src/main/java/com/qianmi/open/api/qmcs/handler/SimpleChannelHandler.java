package com.qianmi.open.api.qmcs.handler;

import com.qianmi.open.api.qmcs.channel.ChannelContext;
import com.qianmi.open.api.qmcs.channel.ChannelHandler;

public abstract class SimpleChannelHandler implements ChannelHandler {

    @Override
    public void onConnect(ChannelContext context) throws Exception {

    }

    @Override
    public void onError(ChannelContext context) throws Exception {

    }

    @Override
    public void onClosed(String reason) {

    }
}
