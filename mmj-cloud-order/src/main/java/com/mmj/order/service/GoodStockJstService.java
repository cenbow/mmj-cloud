package com.mmj.order.service;


import com.mmj.order.model.dto.GoodsStockDto;
import com.mmj.order.model.vo.InventoryQueryVo;

import java.util.List;

/**
 * 库存同步聚水潭
 */
public interface GoodStockJstService {

    /**
     * @Description: 获取聚水潭库存数据
     * @author: KK
     * @date: 2018/10/25
     * @param: []
     * @return: java.util.List<com.mmj.ecommerce.goods.dto.GoodsStockDto>
     */
    List<GoodsStockDto> getGoodsStock();


    /**
     * @Description: 组合商品库存数据
     * @author: KK
     * @date: 2018/11/9
     * @param: [stockDtos]
     * @return: java.util.List<com.mmj.ecommerce.goods.dto.GoodsStockDto>
     */
    List<GoodsStockDto> combineSkuStock(List<GoodsStockDto> stockDtos);


    /**
     * 同步聚水潭
     *
     * @param inventoryQueryVo
     */
    void jstGoodNum();
}
