package com.mmj.active.common.model;

import lombok.Data;

@Data
public class SMSInfoDto {
    private Long userId;

    private String orderNo;

    private String goodName;

    private String code;

    private String url;

    private Double amount;

    private String express;

    private String expressCode;

    public SMSInfoDto() {
    }

    public SMSInfoDto(Long userId, String orderNo, String goodName) {
        this.userId = userId;
        this.orderNo = orderNo;
        this.goodName = goodName;
    }
}
