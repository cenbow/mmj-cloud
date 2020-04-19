package com.mmj.common.model.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description: 订单状态更新
 * @auther: KK
 * @date: 2019/8/8
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderStatusMQDto {
    private String orderNo;
    private Integer orderStatus;
}
