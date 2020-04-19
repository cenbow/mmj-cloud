package com.qianmi.open.api.qmcs.message;

import com.qianmi.open.api.QianmiObject;
import com.qianmi.open.api.tool.mapping.ApiField;

import java.util.Date;

/**
 * 消息
 */
public class Message extends QianmiObject {

    @ApiField("id")
    private String id;

    @ApiField("topic")
    private String topic;

    @ApiField("pubAppKey")
    private String pubAppKey;

    @ApiField("pubTime")
    private Date pubTime;

    @ApiField("userId")
    private String userId;

    @ApiField("content")
    private String content;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getPubAppKey() {
        return pubAppKey;
    }

    public void setPubAppKey(String pubAppKey) {
        this.pubAppKey = pubAppKey;
    }

    public Date getPubTime() {
        return pubTime;
    }

    public void setPubTime(Date pubTime) {
        this.pubTime = pubTime;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
