package com.mmj.good.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.mmj.good.model.GoodLabel;
import com.baomidou.mybatisplus.service.IService;
import com.mmj.good.model.GoodLabelEx;

/**
 * <p>
 * 商品标签表 服务类
 * </p>
 *
 * @author lyf
 * @since 2019-06-03
 */
public interface GoodLabelService extends IService<GoodLabel> {

    void save(GoodLabelEx entityEx) throws Exception;

    Page<GoodLabelEx> queryList(GoodLabelEx entityEx);
}
