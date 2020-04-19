package com.mmj.third.recharge.service;

import com.mmj.common.model.third.recharge.NumberInfoDto;
import com.mmj.common.model.third.recharge.RechargeDto;
import com.mmj.common.model.third.recharge.RechargeVo;

import java.util.Map;

/**
 * @description: 话费充值
 * @auther: KK
 * @date: 2019/8/31
 */
public interface RechargeService {
    /**
     * 话费充值
     *
     * @param rechargeVo
     * @return
     */
    RechargeDto recharge(RechargeVo rechargeVo);

    /**
     * 通过业务订单号获取第三方充值订单状态
     *
     * @param outerId
     * @return
     */
    RechargeDto getRechargeInfo(String outerId);

    /**
     * 获取手机号码归属地信息
     *
     * @param mobile
     */
    NumberInfoDto getNumberInfo(String mobile);

    /**
     * 第三方充值异步回调
     *
     * @param params
     */
    void bm001Callback(Map<String, String> params);
}
