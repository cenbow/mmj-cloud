package com.mmj.order.model.dto;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode()
@Accessors(chain = true)
public class OrderStatsDto {
    private long waitPayNum = 0;    // 待支付
    private long waitGroupNum = 0;   // 待成团
    private long waitShipNum = 0;     // 待发货
    private long waitReceipt = 0;   // 待收货
    private long afterSaleNum = 0;  //  售后
}
