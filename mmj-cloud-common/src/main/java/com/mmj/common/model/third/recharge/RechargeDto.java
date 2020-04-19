package com.mmj.common.model.third.recharge;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description: 话费充值返回
 * @auther: KK
 * @date: 2019/8/31
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RechargeDto {
    /**
     * 返回状态 true结果成功 false结果失败
     */
    private boolean resultStatus = false;
    /**
     * 业务订单号
     */
    private String outerId;
    /**
     * 充值状态 0充值中 1充值成功 2充值超时（待重试） 3充值失败
     */
    private int resultCode;
    /**
     * 充值第三方平台
     */
    private String channel;
    /**
     * 返回第三方业务单号
     */
    private String resultNo;
    /**
     * 备注信息
     */
    private String remark;
    /**
     * 第三方返回数据
     */
    private String data;
}
