package com.mmj.order.common.model.dto;

import lombok.Data;

/**
 * @Description: 支付成功模板消息
 * @Auther: KK
 * @Date: 2018/12/28
 */
@Data
public class OrderPaySuccessDto {

    private Long userId;
    private String orderNo;
    private String orderSource;
    private String amount;
    private String goodsTitle;
    private String groupNo;
    //是否成团
    private boolean groupStatus;

    //订单类型
    private Integer orderType;

    public OrderPaySuccessDto() {
    }

    public OrderPaySuccessDto(Long userId, String orderNo, String goodsTitle) {
        this.userId = userId;
        this.orderNo = orderNo;
        this.goodsTitle = goodsTitle;
    }

    public OrderPaySuccessDto(Long userId, String orderNo, String orderSource, String amount, String goodsTitle, Integer orderType) {
        this.userId = userId;
        this.orderNo = orderNo;
        this.orderSource = orderSource;
        this.amount = amount;
        this.goodsTitle = goodsTitle;
        this.orderType = orderType;
    }

    public OrderPaySuccessDto(Long userId, String orderNo, String orderSource, String amount, String goodsTitle) {
        this.userId = userId;
        this.orderNo = orderNo;
        this.orderSource = orderSource;
        this.amount = amount;
        this.goodsTitle = goodsTitle;
    }

    public OrderPaySuccessDto(Long userId, String orderNo, String orderSource, String amount, String goodsTitle, String groupNo, boolean groupStatus) {
        this.userId = userId;
        this.orderNo = orderNo;
        this.orderSource = orderSource;
        this.amount = amount;
        this.goodsTitle = goodsTitle;
        this.groupNo = groupNo;
        this.groupStatus = groupStatus;
    }
}
