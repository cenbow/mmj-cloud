package com.mmj.notice.service;

import com.baomidou.mybatisplus.service.IService;
import com.mmj.notice.model.WxForm;

/**
 * <p>
 * 微信模板消息 服务类
 * </p>
 *
 * @author shenfuding
 * @since 2019-07-19
 */
public interface WxFormService extends IService<WxForm> {

    /**
     * 保存模板消息id
     * @param wxForm
     * @return
     */
    WxForm save(WxForm wxForm);

    /**
     * 删除七天以前的formid
     */
    void del();
}
