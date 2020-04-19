package com.mmj.pay.service;

import com.baomidou.mybatisplus.service.IService;
import com.mmj.pay.model.WxMchInfo;

/**
 * <p>
 * 商户号信息 服务类
 * </p>
 *
 * @author shenfuding
 * @since 2019-09-02
 */
public interface WxMchInfoService extends IService<WxMchInfo> {

    /**
     * 根据订单号获取商户信息
     * @param orderNo
     * @return
     */
    WxMchInfo getMchInfo(String orderNo);
}
