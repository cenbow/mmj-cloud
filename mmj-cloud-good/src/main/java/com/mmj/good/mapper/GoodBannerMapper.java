package com.mmj.good.mapper;

import com.baomidou.mybatisplus.plugins.Page;
import com.mmj.good.model.GoodBanner;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.mmj.good.model.GoodBannerEx;

import java.util.List;

/**
 * <p>
 * 分类横幅表 Mapper 接口
 * </p>
 *
 * @author lyf
 * @since 2019-06-03
 */
public interface GoodBannerMapper extends BaseMapper<GoodBanner> {


    List<GoodBannerEx> queryListByClassCode(Page<GoodBannerEx> page,GoodBannerEx entityEx);

}
