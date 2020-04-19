package com.mmj.pay.service;

import com.baomidou.mybatisplus.service.IService;
import com.mmj.pay.model.WxpayRedpack;

/**
 * <p>
 * 微信红包记录表 服务类
 * </p>
 *
 * @author shenfuding
 * @since 2019-07-17
 */
public interface WxpayRedpackService extends IService<WxpayRedpack> {

    /**
     * 发送普通红包
     * @param wxpayRedpack
     * @return
     */
    WxpayRedpack sendRedpack(WxpayRedpack wxpayRedpack);
}
