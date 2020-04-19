package com.mmj.pay.service;

import com.baomidou.mybatisplus.service.IService;
import com.mmj.pay.model.WxpayTransfers;

/**
 * <p>
 * 微信发送零钱表 服务类
 * </p>
 *
 * @author shenfuding
 * @since 2019-07-16
 */
public interface WxpayTransfersService extends IService<WxpayTransfers> {

    /**
     * 发送零钱
     * @param wxpayTransfers
     * @return
     */
    WxpayTransfers transfers(WxpayTransfers wxpayTransfers);
}
