package com.mmj.good.service.impl;

import com.baomidou.mybatisplus.plugins.Page;
import com.mmj.good.model.GoodModel;
import com.mmj.good.mapper.GoodModelMapper;
import com.mmj.good.model.GoodModelEx;
import com.mmj.good.service.GoodModelService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 商品规格表 服务实现类
 * </p>
 *
 * @author lyf
 * @since 2019-06-03
 */
@Service
public class GoodModelServiceImpl extends ServiceImpl<GoodModelMapper, GoodModel> implements GoodModelService {

    @Autowired
    private GoodModelMapper goodModelMapper;

    public Page<GoodModelEx> queryList(GoodModel goodModel){
        Page<GoodModelEx> page = new Page<>(goodModel.getCurrentPage(), goodModel.getPageSize());
        List<GoodModelEx> goodModelExes = goodModelMapper.queryList(page, goodModel);
        page.setRecords(goodModelExes);
        return page;
    }

    public List<GoodModelEx> queryListBySku(List<String> goodSkus) {
        return goodModelMapper.queryListBySku(goodSkus);
    }

}
