package com.mmj.active.freeorder.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class FreeOrderRelationsVo implements Serializable {

    private static final long serialVersionUID = 1077367713555692330L;

    private Integer id;

    private String launchOrderNo;

    private Long launchUserId;

    private String orderNo;

    private Long userId;

    private String nickName;

    private String headImgUrl;

    private String remark;

    private Date createTime;

    private Date updateTime;
}