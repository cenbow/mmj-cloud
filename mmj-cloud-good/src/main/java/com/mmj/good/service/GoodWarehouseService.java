package com.mmj.good.service;

import com.mmj.good.model.GoodWarehouse;
import com.baomidou.mybatisplus.service.IService;

import java.util.List;

/**
 * <p>
 * 商品库存表 服务类
 * </p>
 *
 * @author lyf
 * @since 2019-06-03
 */
public interface GoodWarehouseService extends IService<GoodWarehouse> {
    List<GoodWarehouse> query(GoodWarehouse goodWarehouse);



}
