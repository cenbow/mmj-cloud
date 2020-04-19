package com.mmj.pay.service;

import com.baomidou.mybatisplus.service.IService;
import com.mmj.pay.model.WxpayRefund;

/**
 * <p>
 * 微信退款表 服务类
 * </p>
 *
 * @author shenfuding
 * @since 2019-07-15
 */
public interface WxpayRefundService extends IService<WxpayRefund> {

    /**
     * 微信退款接口
     * @param wxpayRefund
     * @return
     */
    WxpayRefund refund(WxpayRefund wxpayRefund);

    /**
     * 退款成功时候的处理
     * @param xml
     */
    void success(String xml);
}
