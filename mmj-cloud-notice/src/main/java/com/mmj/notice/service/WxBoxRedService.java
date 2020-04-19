package com.mmj.notice.service;

import com.baomidou.mybatisplus.service.IService;
import com.mmj.notice.model.WxBoxRed;

/**
 * <p>
 * 物流箱红包码 服务类
 * </p>
 *
 * @author shenfuding
 * @since 2019-08-12
 */
public interface WxBoxRedService extends IService<WxBoxRed> {

    /**
     * 发送物流箱上面的红包
     * @param redCode
     */
    Object send(String redCode);
}
