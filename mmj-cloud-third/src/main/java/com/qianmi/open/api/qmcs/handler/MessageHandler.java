package com.qianmi.open.api.qmcs.handler;

import com.qianmi.open.api.qmcs.message.Message;
import com.qianmi.open.api.qmcs.message.MessageStatus;

/**
 * 消息处理器
 */
public interface MessageHandler {

    /**
     * 消息通道客户端收到消息后，会回调该方法处理具体的业务，处理结果可以通过以下两种方式来表述：
     * 1。抛出异常或设置status.fail()表明消息处理失败，需要消息通道服务端重发
     * 2。不抛出异常，也没有设置status信息，则表明消息处理成功，消息通道服务端不会再投递此消息
     *
     * @param message 消息内容
     * @param status 处理结果，如果调用status.fail()，消息通道将会择机重发消息；否则，消息通道认为消息处理成功
     * @throws Exception 消息处理失败，消息通道将会择机重发消息
     */
    public void onMessage(Message message, MessageStatus status) throws Exception;

}
