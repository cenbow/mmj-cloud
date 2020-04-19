package com.mmj.active.common.model.dto;

import lombok.Data;

/**
 * @description: 下单返回
 * @auther: KK
 * @date: 2019/7/23
 */
@Data
public class SaveOrderDto {
    private String orderId;

    private String orderNo;

    private String orderAmount;
}
