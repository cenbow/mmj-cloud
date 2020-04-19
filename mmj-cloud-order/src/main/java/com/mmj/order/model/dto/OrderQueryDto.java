package com.mmj.order.model.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.List;


/**
 * 小程序订单列表返回
 */
@Data
@EqualsAndHashCode()
@Accessors(chain = true)
public class OrderQueryDto {
    private List<OrderListDto> list;


}
