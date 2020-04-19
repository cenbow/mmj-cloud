package com.mmj.active.callCharge.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description: 话费充值
 * @auther: KK
 * @date: 2019/8/1
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RechargeRecordDto {
    /**
     * 订单号
     */
    private String orderNo;
}
