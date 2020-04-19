package com.mmj.common.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

@Data
public class SmsDto implements Serializable {

    private static final long serialVersionUID = -270652866486344807L;

    private String id;

    private Long userId;

    private String template;//自定义消息用

    private boolean needTemplate = true;

    private String phone;//手机号码

    private String nickName;

    private String orderNo;

    private Date endTime;//结束时间

    private Integer noticeId;

    //消息类型
    private int msgType;

    //业务模块
    private String model;

    //模块具体节点
    private String type;

    //发送消息的节点
    private String node;

    private Long delayTime;//发送(延迟)时间(分钟)

    private Map<String, Object> params;//业务参数

    private Map<String, Object> smsParams;//短信参数(与模板对应)

    public SmsDto(String id) {
        this.id = id;
    }

    public SmsDto() {
    }

    public SmsDto(String id, Long userId, String orderNo) {
        this.id = id;
        this.userId = userId;
        this.orderNo = orderNo;
    }
}
