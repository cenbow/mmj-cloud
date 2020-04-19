package com.mmj.notice.service;

import com.baomidou.mybatisplus.service.IService;
import com.mmj.notice.model.WxMenu;
import com.mmj.notice.model.WxMenuEx;

import java.util.List;

/**
 * <p>
 *  公众号菜单栏配置表 服务类
 * </p>
 *
 * @author shenfuding
 * @since 2019-07-26
 */
public interface WxMenuService extends IService<WxMenu> {

    /**
     * 保存公众号菜单栏信息
     * @param wxMenuExes
     * @return
     */
    void save(List<WxMenuEx> wxMenuExes);

    /**
     * 查询公众号菜单栏配置信息
     * @param appid
     * @return
     */
    WxMenuEx query(String appid);
}
