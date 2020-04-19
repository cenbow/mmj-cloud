package com.mmj.good.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.mmj.good.model.GoodBanner;
import com.baomidou.mybatisplus.service.IService;
import com.mmj.good.model.GoodBannerEx;

/**
 * <p>
 * 分类横幅表 服务类
 * </p>
 *
 * @author lyf
 * @since 2019-06-03
 */
public interface GoodBannerService extends IService<GoodBanner> {

    Page<GoodBannerEx> queryListByClassCode(GoodBannerEx entityEx);
}
