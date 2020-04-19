package com.mmj.order.common.model.dto;

import lombok.Data;

import java.util.Date;

@Data
public class OrderCheckDto {

    //是否是团主  true:团主  false:团员
    private boolean isLan = false;

    //团主是否会员(目前只有二人团使用
    private boolean launchIsMember = false;

    //团过期时间
    private Date expireDate;

    public OrderCheckDto() {
    }

    public OrderCheckDto(boolean isLan, Date expireDate) {
        this.isLan = isLan;
        this.expireDate = expireDate;
    }

    public OrderCheckDto(boolean isLan, boolean launchIsMember, Date expireDate) {
        this.isLan = isLan;
        this.launchIsMember = launchIsMember;
        this.expireDate = expireDate;
    }
}
