package com.mmj.active.callCharge.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import com.mmj.active.callCharge.model.CallChargeRecord;
import com.mmj.active.callCharge.model.dto.PayInfoDto;
import com.mmj.active.callCharge.model.dto.RechargeRecordDto;
import com.mmj.active.callCharge.model.vo.BossQueryVo;
import com.mmj.active.callCharge.model.vo.RechargeOrderVo;
import com.mmj.common.model.active.RechargeVo;
import com.mmj.common.model.third.recharge.RechargeDto;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author KK
 * @since 2019-08-31
 */
public interface CallChargeRecordService extends IService<CallChargeRecord> {
    /**
     * 用户是否享受权益
     *
     * @param userId
     * @return
     */
    boolean userRight(Long userId);

    /**
     * 话费订单下单
     *
     * @param rechargeOrderVo
     * @return
     */
    RechargeRecordDto produceOrder(RechargeOrderVo rechargeOrderVo);

    /**
     * 查询所有话费订单
     *
     * @param bossQueryVo
     * @return
     */
    Page<CallChargeRecord> getCallChargeRecordList(BossQueryVo bossQueryVo);

    /**
     * 获取订单信息（供前端用）
     *
     * @param appId
     * @param openId
     * @param orderNo
     * @return
     */
    PayInfoDto getOrderPayInfo(String appId, String openId, String orderNo);

    /**
     * 支付失败
     *
     * @param rechargeVo
     */
    void payFail(RechargeVo rechargeVo);

    /**
     * 支付成功
     *
     * @param orderNo
     * @param jsonObject
     */
    void paySuccess(String orderNo, JSONObject jsonObject);

    /**
     * 话费充值
     *
     * @param orderNo
     */
    void recharge(String orderNo);

    /**
     * 异步响应
     *
     * @param rechargeDto
     */
    void callback(RechargeDto rechargeDto);
}
