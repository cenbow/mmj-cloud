package com.qianmi.open.api.qmcs;

import com.qianmi.open.api.qmcs.channel.ChannelException;
import com.qianmi.open.api.qmcs.handler.MessageHandler;
import com.qianmi.open.api.qmcs.message.MessageFields;
import com.qianmi.open.api.qmcs.message.MessageKind;
import com.qianmi.open.api.tool.util.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 消息服务客户端
 */
public class QmcsClient {

    private static final Log log = LogFactory.getLog(QmcsClient.class);

    private final AtomicBoolean connected = new AtomicBoolean(false);

    private String uri;

    private String appKey;
    private String groupName;

    private int queueSize = 2000;  // 消息缓冲队列大小
    private int threadCount = Runtime.getRuntime().availableProcessors() * 2; // 并发处理的线程数量
    private ExecutorService threadPool = Executors.newFixedThreadPool(threadCount);

    private InnerClient client;
    private MessageHandler messageHandler;
    private ClientHandler handler;

    private int fetchInterval = 30;         // 定时获取消息周期（单位：秒）
    private Timer fetchTimer = null;        // 消息主动拉取定时器
    private TimerTask fetchTimerTask = null;

    public QmcsClient(String appKey, String appSecret) {
        this(appKey, appSecret, "default");
    }

    public QmcsClient(String appKey, String appSecret, String groupName) {
        this("ws://mc.api.qianmi.com/", appKey, appSecret, groupName);
    }

    public QmcsClient(String uri, String appKey, String appSecret, String groupName) {
        this.uri = uri;
        this.appKey = appKey;
        this.groupName = groupName;
        this.client = new InnerClient(appKey, appSecret, groupName);
    }

    /**
     * 连接到线上服务器
     * @throws ChannelException
     */
    public void connect() throws ChannelException {
        if (!connected.compareAndSet(false, true)) {
            return;
        }
        this.handler = new ClientHandler(this);
        this.client.setMessageHandler(handler);
        try {
            client.connect(uri);
        } catch (ChannelException e) {
            connected.set(false);
            throw e;
        }
        doPullRequest();
    }

    /**
     * 连接到指定的服务器
     * @param uri 服务器地址
     * @throws ChannelException
     */
    public void connect(String uri) throws ChannelException {
        this.uri = uri;
        connect();
    }

    /**
     * 向指定的主题发布一条与用户无关的消息
     * 注：目前通过此方法所发送的消息，服务器端不会处理
     * @param topic 主题名称
     * @param content 根据主题定义的消息内容
     * @throws QmcsException
     */
    protected void send(String topic, String content) throws QmcsException {
        if (StringUtils.isEmpty(topic)) {
            throw new QmcsException("topic is required");
        }
        if (StringUtils.isEmpty(content)) {
            throw new QmcsException("content is required");
        }
        Map<String, Object> msg = new HashMap<String, Object>();
        msg.put(MessageFields.TYPE, MessageKind.DATA);
        msg.put(MessageFields.TOPIC, topic);
        msg.put(MessageFields.CONTENT, content);
        client.sendAndWait(msg, 5000);
    }

    public void close() {
        close("client closed");
    }

    /**
     * 客户端主动断开连接
     * @param reason 关闭的原因
     */
    public void close(String reason) {
        this.stopPullRequest();
        if (handler != null) {
            handler.close();
        }
        if (threadPool != null) {
            threadPool.shutdown();
            threadPool = null;
        }
        client.disconnect(reason);
        log.warn("client closed");
    }

    /**
     * 判断连接是否有效
     * @return
     */
    public boolean isOnline() {
        return (client != null && client.isOnline());
    }

    protected void setUri(String uri) {
        this.uri = uri;
    }

    protected String getAppKey() {
        return appKey;
    }

    protected String getGroupName() {
        return groupName;
    }

    protected InnerClient getClient() {
        return client;
    }

    protected ExecutorService getThreadPool() {
        return threadPool;
    }

    protected MessageHandler getMessageHandler() {
        return messageHandler;
    }

    public void setMessageHandler(MessageHandler messageHandler) {
        this.messageHandler = messageHandler;
    }

    protected ClientHandler getHandler() {
        return handler;
    }

    protected int getQueueSize() {
        return queueSize;
    }

    public void setQueueSize(int queueSize) {
        if (queueSize < threadCount) {
            throw new IllegalArgumentException("queue size must greater than thread count");
        }
        this.queueSize = queueSize;
    }

    public void setFetchInterval(int fetchInterval) {
        this.fetchInterval = fetchInterval;
    }

    public void setThreadCount(int threadCount) {
        if (threadCount < 1) {
            throw new IllegalArgumentException("thread count must greater than 1");
        }
        this.threadCount = threadCount;
    }

    /**
     * 拉取消息
     */
    protected void pullRequest() {
        try {
            Map<String, Object> msg = new HashMap<String, Object>();
            msg.put(MessageFields.TYPE, MessageKind.PULL);
            if (isOnline()) {
                client.send(msg);
            }
        } catch (Exception e) {
            log.warn("pull request error", e);
        }
    }

    private void doPullRequest() {
        if (fetchInterval < 1) {
            return;
        }
        this.stopPullRequest();
        this.fetchTimerTask = new TimerTask() {
            public void run() {
                pullRequest();
            }
        };
        Date begin = new Date();
        begin.setTime(begin.getTime() + fetchInterval * 1000L);
        this.fetchTimer = new Timer("qmcs-pull", true);
        this.fetchTimer.schedule(this.fetchTimerTask, begin, fetchInterval * 1000L);
    }

    private void stopPullRequest() {
        if (this.fetchTimer != null) {
            this.fetchTimer.cancel();
            this.fetchTimer = null;
        }
    }

}
