package com.mmj.user.common.model.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class OrderFinishGoodVo {

    @NotNull
    private Long userId;  // 下单用户id

    @NotNull
    private String orderNo; // 订单号

    public OrderFinishGoodVo(Long userId, String orderNo) {
        this.userId = userId;
        this.orderNo = orderNo;
    }

    public OrderFinishGoodVo() {
    }

    public OrderFinishGoodVo(Long userId) {
        this.userId = userId;
    }
}
