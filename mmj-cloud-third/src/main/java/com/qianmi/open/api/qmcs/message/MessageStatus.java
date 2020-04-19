package com.qianmi.open.api.qmcs.message;

/**
 * 消息状态
 */
public class MessageStatus {

    public static final int SUCCESS = 1;
    public static final int FAIL = 0;

    private boolean isFail = false; // 是否处理失败
    private String reason; // 处理失败原因

    public boolean isFail() {
        return this.isFail;
    }

    public void fail() {
        this.isFail = true;
    }

    public String getReason() {
        return this.reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

}
