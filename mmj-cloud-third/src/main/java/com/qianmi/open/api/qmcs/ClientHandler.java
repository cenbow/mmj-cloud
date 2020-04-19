package com.qianmi.open.api.qmcs;

import com.qianmi.open.api.qmcs.channel.ChannelException;
import com.qianmi.open.api.qmcs.endpoint.EndpointContext;
import com.qianmi.open.api.qmcs.endpoint.Identity;
import com.qianmi.open.api.qmcs.message.Message;
import com.qianmi.open.api.qmcs.message.MessageFields;
import com.qianmi.open.api.qmcs.message.MessageKind;
import com.qianmi.open.api.qmcs.message.MessageStatus;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.RejectedExecutionException;

/**
 */
public class ClientHandler implements com.qianmi.open.api.qmcs.endpoint.MessageHandler {

    private static final Log log = LogFactory.getLog(ClientHandler.class);

    protected QmcsClient qmcsClient;
    protected volatile boolean stopped;

    public ClientHandler(QmcsClient qmcsClient) {
        this.qmcsClient = qmcsClient;
    }

    @Override
    public void onMessage(Map<String, Object> message, Identity messageFrom) {
        if (log.isDebugEnabled()) {
            log.debug("unexpected message: " + message);
        }
    }

    @Override
    public void onMessage(final EndpointContext context) throws Exception {
        final Map<String, Object> map = context.getMessage();

        if (log.isDebugEnabled()) {
            log.debug(String.format("onMessage from %s: %s", context.getMessageFrom(), map));
        }

        handleMessage(parse(map), false);
    }

    protected void handleMessage(final Message message, final boolean ignore) {
        while (!stopped) {
            try {
                qmcsClient.getThreadPool().submit(new Runnable() {
                    public void run() {
                        MessageStatus status = new MessageStatus();

                        if (!ignore) {
                            try {
                                qmcsClient.getMessageHandler().onMessage(message, status);
                            } catch (Exception e) {
                                log.error(String.format("handle message fail: %s", message), e);
                                return;
                            }
                        }

                        if (!status.isFail()) {
                            try {
                                confirm(message.getId());
                            } catch (Exception e) {
                                log.warn(String.format("confirm message fail: %s", message), e);
                            }
                        }
                    }
                });
                break;
            } catch (RejectedExecutionException ree) {
                log.warn("all qmcs worker threads are currently busy, waiting 50 ms,appkey: "
                        + qmcsClient.getAppKey() + ", group: " + qmcsClient.getGroupName());
                try {
                    Thread.sleep(50L);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    protected void confirm(String msgId) throws ChannelException {
        Map<String, Object> msg = new HashMap<String, Object>();
        msg.put(MessageFields.TYPE, MessageKind.CONFIRM);
        msg.put(MessageFields.MESSAGE_ID, msgId);
        qmcsClient.getClient().send(msg);
    }

    public void close() {
        this.stopped = true;
    }

    protected Message parse(Map<String, Object> raw) throws IOException {
        Message msg = new Message();
        msg.setId((String) raw.get(MessageFields.MESSAGE_ID));
        msg.setTopic((String) raw.get(MessageFields.TOPIC));
        msg.setPubAppKey((String) raw.get(MessageFields.PUB_APP_KEY));
        msg.setUserId((String) raw.get(MessageFields.USER_ID));
        msg.setPubTime((Date) raw.get(MessageFields.PUBLISH_TIME));
        msg.setContent((String) raw.get(MessageFields.CONTENT));
        return msg;
    }
}
