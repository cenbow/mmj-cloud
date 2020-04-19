package com.mmj.common.model.order;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @description: 订单包裹队列消息
 * @auther: KK
 * @date: 2019/8/24
 */
@Data
public class OrdersPackageMQDto {
    /**
     * 订单号
     */
    private String orderNo;
    /**
     * 包裹号
     */
    private String packageNo;
    /**
     * 订单时间
     */
    private Date orderDate;
    /**
     * 订单金额
     */
    private Integer orderAmount;
    /**
     * 是否虚拟商品 1是
     */
    private Integer virtualGood;
    /**
     * 商品信息
     */
    private List<OrdersPackageMQDto.Goods> goods;

    @Data
    public static class Goods {
        private String goodName;
        private String goodImage;
        private String goodSku;
        private Integer goodNum;
    }
}
