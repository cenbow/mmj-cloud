package com.mmj.active.grouplottery.model.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class LotterySucceedMsg implements Serializable {

    private static final long serialVersionUID = -1130675674147993535L;
    private Boolean succeed;
    private Long userId;
    private String goodsName;
    private String nickName;
    private String address;
    private String code;
    private String date;
    private String orderNo;
}
