package com.mmj.common.model.active;


import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @description: 充值失败
 * @auther: KK
 * @date: 2019/8/31
 */
@Data
public class RechargeVo {
    /**
     * 订单号
     */
    @NotNull
    private String orderNo;
}
