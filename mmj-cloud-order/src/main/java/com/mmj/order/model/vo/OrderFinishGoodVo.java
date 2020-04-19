package com.mmj.order.model.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class OrderFinishGoodVo {


    @NotNull
    private Long userId;  // 下单用户id


    @NotNull
    private  String orderNo; // 订单号

}
