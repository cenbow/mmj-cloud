package com.mmj.third.jushuitan.service;

import com.mmj.third.jushuitan.model.dto.InventoryQueryDto;
import com.mmj.third.jushuitan.model.dto.SkuMapQueryDto;
import com.mmj.third.jushuitan.model.dto.SkuQueryDto;
import com.mmj.third.jushuitan.model.request.*;
import com.mmj.third.jushuitan.model.response.InventoryQueryResponse;
import com.mmj.third.jushuitan.model.response.OrdersSingleQueryResponse;
import com.mmj.third.jushuitan.model.vo.InventoryQueryVo;
import com.mmj.third.jushuitan.model.vo.SkuMapQueryVo;
import com.mmj.third.jushuitan.model.vo.SkuQueryVo;

import java.util.List;

/**
 * @description: 聚水潭服务
 * @auther: KK
 * @date: 2019/6/6
 */
public interface JushuitanService {
    /**
     * 刷新聚水潭token
     */
    void refreshToken();

    /**
     * 消息推送-取消订单
     *
     * @param request
     */
    void cancelOrder(CancelOrderRequest request);

    /**
     * 消息推送-物流同步
     *
     * @param request
     */
    void logisticsUpload(LogisticsUploadRequest request);

    /**
     * 订单上传(推荐)
     * 接口说明:
     * <p>
     * 此接口一次最大上传50个订单
     * <p>
     * 请开发者严格遵守报文结构
     */
    void jushuitanOrderUpload(OrdersUploadRequest... request);

    /**
     * 订单查询
     *
     * @param request
     * @return
     */
    OrdersSingleQueryResponse ordersSingleQuery(OrdersSingleQueryRequest request);

    /**
     * 库存查询
     *
     * @param inventoryQueryVo
     * @return
     */
    List<InventoryQueryDto> inventoryQuery(InventoryQueryVo inventoryQueryVo);

    /**
     * 普通商品查询
     *
     * @param skuQueryVo
     * @return
     */
    List<SkuQueryDto> skuQuery(SkuQueryVo skuQueryVo);

    /**
     * 商品映射查询
     *
     * @param skuMapQueryVo
     * @return
     */
    List<SkuMapQueryDto> skuMapQuery(SkuMapQueryVo skuMapQueryVo);

}
