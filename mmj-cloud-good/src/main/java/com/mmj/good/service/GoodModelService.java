package com.mmj.good.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.mmj.good.model.GoodModel;
import com.baomidou.mybatisplus.service.IService;
import com.mmj.good.model.GoodModelEx;

import java.util.List;

/**
 * <p>
 * 商品规格表 服务类
 * </p>
 *
 * @author lyf
 * @since 2019-06-03
 */
public interface GoodModelService extends IService<GoodModel> {

    Page<GoodModelEx> queryList(GoodModel goodModel);

    List<GoodModelEx> queryListBySku(List<String> goodSkus);
}
