package com.mmj.common.model.order;

import lombok.Data;

/**
 * @description: 订单收货信息
 * @auther: KK
 * @date: 2019/8/8
 */
@Data
public class OrderConsigneeMQDto {
    private String orderNo;
    private String name;
    private String telNumber;
}
