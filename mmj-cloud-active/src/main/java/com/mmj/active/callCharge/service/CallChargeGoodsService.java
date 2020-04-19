package com.mmj.active.callCharge.service;

import com.baomidou.mybatisplus.service.IService;
import com.mmj.active.callCharge.model.CallChargeGoods;
import com.mmj.active.callCharge.model.dto.RechargeGoodsDto;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author KK
 * @since 2019-08-31
 */
public interface CallChargeGoodsService extends IService<CallChargeGoods> {
    /**
     * 获取充值商品信息
     *
     * @return
     */
    List<RechargeGoodsDto> getRechargeGoods();

    /**
     * 获取商品剩余可用库存
     *
     * @param goodsId
     * @return
     */
    int getTodaySendNumber(Integer goodsId);

    /**
     * 扣减商品库存
     *
     * @param goodsId
     * @param num
     * @return
     */
    int deductionStock(Integer goodsId, long num);

    /**
     * 每天10点重置
     */
    void restartTask();

    /**
     * 每一分钟统计发放数量，并持久化到数据
     */
    void statSendNumber();
}
