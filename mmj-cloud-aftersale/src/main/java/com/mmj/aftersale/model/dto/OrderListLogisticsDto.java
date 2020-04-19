package com.mmj.aftersale.model.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 订单快递信息
 */
@Data
@EqualsAndHashCode()
@Accessors(chain = true)
public class OrderListLogisticsDto {
    private String packageNo;
    private Integer orderStatus;

    private String logisticsNo;  // 快递单号
    private String logisticsName;  // 快递公司名称名称
//    private String logisticsAction;  // 物流动作



}
