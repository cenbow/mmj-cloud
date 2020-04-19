package com.mmj.good.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.mmj.good.model.GoodWarehouse;
import com.mmj.good.mapper.GoodWarehouseMapper;
import com.mmj.good.service.GoodWarehouseService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 商品库存表 服务实现类
 * </p>
 *
 * @author lyf
 * @since 2019-06-03
 */
@Service
public class GoodWarehouseServiceImpl extends ServiceImpl<GoodWarehouseMapper, GoodWarehouse> implements GoodWarehouseService {



    @Autowired
    private GoodWarehouseMapper goodWarehouseMapper;

    public List<GoodWarehouse> query(GoodWarehouse goodWarehouse){
        EntityWrapper<GoodWarehouse> entityWrapper = new EntityWrapper<>(goodWarehouse);
        return goodWarehouseMapper.selectList(entityWrapper);
    }

}
