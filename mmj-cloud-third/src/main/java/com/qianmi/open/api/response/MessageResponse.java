package com.qianmi.open.api.response;

import com.qianmi.open.api.QianmiResponse;
import com.qianmi.open.api.qmcs.message.Message;
import com.qianmi.open.api.tool.mapping.ApiField;

/**
 * 消息响应内容
 */
public class MessageResponse extends QianmiResponse {

    /** 消息响应类型，对应MessageType */
    @ApiField("type")
    private Integer type;

    /** 请求消息在服务器端的处理状态，0：失败，1：成功 */
    @ApiField("status")
    private Integer status;

    /** 当status为0时，表示消息处理失败的原因 */
    @ApiField("errMsg")
    private String errMsg;

    /** 当status为1时，表示返回的消息内容 */
    @ApiField("content")
    private Message content;

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public Message getContent() {
        return content;
    }

    public void setContent(Message content) {
        this.content = content;
    }
}
