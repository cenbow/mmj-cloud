package com.qianmi.open.api.qmcs;

import com.qianmi.open.api.Constants;
import com.qianmi.open.api.qmcs.channel.ChannelException;
import com.qianmi.open.api.qmcs.channel.ClientChannel;
import com.qianmi.open.api.qmcs.channel.ClientChannelSharedSelector;
import com.qianmi.open.api.qmcs.endpoint.*;
import com.qianmi.open.api.tool.util.SignUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

class InnerClient {

    private static final Log log = LogFactory.getLog(InnerClient.class);

    private static final String TIMESTAMP = "timestamp";
    private static final String APP_KEY = "app_key";
    private static final String GROUP_NAME = "group_name";
    private static final String SIGN = "sign";
    private static final String SDK = "sdk";

    private Identity id;
    private Endpoint endpoint;
    private URI serverUri;
    private EndpointProxy server;
    private ClientChannelSharedSelector selector;
    private Timer reconnectTimer;             // 客户端重连定时器
    private int reconnectInterval = 30000;    // 重连周期（单位：毫秒）

    private String appKey;
    private String appSecret;
    private String groupName;

    InnerClient(String appKey, String appSecret, String groupName) {
        this.appKey = appKey;
        this.appSecret = appSecret;
        this.groupName = groupName;
        this.id = new ClientIdentity(appKey, groupName);

        this.selector = new ClientChannelSharedSelector();
        this.selector.setHeartbeat(45000);

        EndpointChannelHandler channelHandler = new EndpointChannelHandler();

        this.endpoint = new Endpoint(id);
        this.endpoint.setClientChannelSelector(selector);
        this.endpoint.setChannelHandler(channelHandler);
    }

    protected Identity getIdentity() {
        return this.id;
    }

    protected void setMessageHandler(MessageHandler messageHandler) {
        this.endpoint.setMessageHandler(messageHandler);
    }

    protected void connect(String uri) throws ChannelException {
        try {
            this.connect(new URI(uri));
        } catch (URISyntaxException e) {
            log.error(e);
        }
        this.startReconnect();
    }

    private void connect(URI uri) throws ChannelException {
        this.serverUri = uri;
        this.server = this.endpoint.getEndpoint(new ServerIdentity(), uri, createConnectHeaders());
        log.info(String.format("%s connected to server: %s", this.id, this.serverUri));
    }

    protected void disconnect(String reason) {
        this.stopReconnect();

        try {
            ClientChannel channel = this.selector.getChannel(this.serverUri);
            if (channel != null) {
                channel.close(reason);
            }
        } catch (ChannelException e) {
            log.error(e);
        }

    }

    protected final void send(Map<String, Object> message) throws ChannelException {
        this.server.send(message);
    }

    protected final void sendAndWait(Map<String, Object> message, int timeout) throws ChannelException {
        this.server.sendAndWait(message, timeout);
    }

    protected boolean isOnline() {
        return this.server != null && this.server.hasValidSender();
    }

    private void startReconnect() {
        this.reconnectTimer = new Timer("qmcs-reconnect", true);
        this.reconnectTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    if (!isOnline()) {
                        log.warn(String.format("%s reconnecting...", id));
                        connect(serverUri);
                    }
                } catch (Exception e) {
                    log.warn("reconnect error", e);
                }
            }
        }, this.reconnectInterval, this.reconnectInterval);
    }

    private void stopReconnect() {
        if (this.reconnectTimer != null) {
            this.reconnectTimer.cancel();
            this.reconnectTimer = null;
        }
    }

    protected Map<String, Object> createConnectHeaders() {
        Map<String, String> signHeader = new HashMap<String, String>();
        signHeader.put(APP_KEY, this.appKey);
        signHeader.put(GROUP_NAME, this.groupName);
        signHeader.put(TIMESTAMP, String.valueOf(System.currentTimeMillis()));
        try {
            signHeader.put(SIGN, SignUtil.sign(signHeader, this.appSecret));
        } catch (Exception e) {
            log.error("sign error", e);
        }
        Map<String, Object> requestHeader = new HashMap<String, Object>();
        requestHeader.putAll(signHeader);
        requestHeader.put(SDK, Constants.SDK_VERSION);
        return requestHeader;
    }

    public class ServerIdentity implements Identity {
        public Identity parse(Object data) throws ChannelException {
            return null;
        }

        public void render(Object to) {
        }

        public boolean equals(Identity id) {
            return id instanceof ServerIdentity;
        }

        public String toString() {
            return id.toString();
        }
    }

}
