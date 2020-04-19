package com.mmj.good.service.impl;

import com.baomidou.mybatisplus.plugins.Page;
import com.mmj.good.model.GoodBanner;
import com.mmj.good.mapper.GoodBannerMapper;
import com.mmj.good.model.GoodBannerEx;
import com.mmj.good.service.GoodBannerService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 分类横幅表 服务实现类
 * </p>
 *
 * @author lyf
 * @since 2019-06-03
 */
@Service
public class GoodBannerServiceImpl extends ServiceImpl<GoodBannerMapper, GoodBanner> implements GoodBannerService {

    @Autowired
    private GoodBannerMapper goodBannerMapper;

    public Page<GoodBannerEx> queryListByClassCode(GoodBannerEx entityEx) {
        Page<GoodBannerEx> page = new Page<>(entityEx.getCurrentPage(), entityEx.getPageSize());
        List<GoodBannerEx> goodBannerExes = goodBannerMapper.queryListByClassCode(page, entityEx);
        page.setRecords(goodBannerExes);
        return page;
    }
}
