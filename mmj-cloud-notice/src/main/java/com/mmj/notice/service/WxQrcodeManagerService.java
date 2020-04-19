package com.mmj.notice.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import com.mmj.notice.model.WxQrcodeManager;

/**
 * <p>
 * 公众号二维码 服务类
 * </p>
 *
 * @author shenfuding
 * @since 2019-08-13
 */
public interface WxQrcodeManagerService extends IService<WxQrcodeManager> {

    /**
     * 公众号二维码保存
     * @param wxQrcodeManager
     */
    void save(WxQrcodeManager wxQrcodeManager);

    /**
     * 分页查询公众号二维码列表信息
     * @param wxQrcodeManager
     * @return
     */
    Page<WxQrcodeManager> queryPage(WxQrcodeManager wxQrcodeManager);

    /**
     * 根据id查询公众号二维码信息
     * @param id
     * @return
     */
    WxQrcodeManager query(String id);
}
