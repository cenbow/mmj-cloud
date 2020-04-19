package com.mmj.pay.service;

import com.baomidou.mybatisplus.service.IService;
import com.mmj.pay.model.WxpayOrder;
import com.mmj.pay.model.WxpayOrderEx;

import java.util.Map;

/**
 * <p>
 * 微信支付订单表 服务类
 * </p>
 *
 * @author lyf
 * @since 2019-06-05
 */
public interface WxpayOrderService extends IService<WxpayOrder> {

    /**
     * 获取支付信息
     * @param wxpayOrderEx
     * @return
     */
    Map getPayInfo(WxpayOrderEx wxpayOrderEx);

    /**
     * 主动从微信拉取订单号信息，拉取成功就往mq里面塞数据
     * @param wxpayOrder
     */
    void pullOrder(WxpayOrder wxpayOrder);

    /**
     * 微信回调触发业务处理
     * @param xmlToMap
     */
    void notifyUrl(Map<String, String> xmlToMap);
}
