package com.mmj.third.jushuitan.service.impl;

import com.google.common.collect.Lists;
import com.mmj.common.utils.DateUtils;
import com.mmj.third.jushuitan.MQProducer;
import com.mmj.third.jushuitan.model.dto.CancelOrderDto;
import com.mmj.third.jushuitan.model.dto.InventoryQueryDto;
import com.mmj.third.jushuitan.model.dto.SkuMapQueryDto;
import com.mmj.third.jushuitan.model.dto.SkuQueryDto;
import com.mmj.third.jushuitan.model.request.*;
import com.mmj.third.jushuitan.model.response.*;
import com.mmj.third.jushuitan.model.vo.InventoryQueryVo;
import com.mmj.third.jushuitan.model.vo.OrderLogisticsVo;
import com.mmj.third.jushuitan.model.vo.SkuMapQueryVo;
import com.mmj.third.jushuitan.model.vo.SkuQueryVo;
import com.mmj.third.jushuitan.service.JushuitanService;
import com.mmj.third.jushuitan.utils.JushuitanHttpClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Objects;

/**
 * @description: 聚水潭服务实现
 * @auther: KK
 * @date: 2019/6/6
 */
@Service
public class JushuitanServiceImpl implements JushuitanService {
    @Autowired
    private JushuitanHttpClient jushuitanHttpClient;
    @Autowired
    private MQProducer mqProducer;

    //  订单物流
    private static final String ORDER_LOGISTICS_TOPIC = "mmj-order-logistics-topic";

    //  取消订单
    private static final String ORDER_CANCEL_TOPIC = "mmj-order-cancel-topic";

    @Override
    public void cancelOrder(CancelOrderRequest request) {
        mqProducer.send(new CancelOrderDto(request.getSoId(), request.getRemark()), ORDER_CANCEL_TOPIC);
    }

    @Override
    public void logisticsUpload(LogisticsUploadRequest request) {
        OrderLogisticsVo orderLogisticsVo = new OrderLogisticsVo();
        orderLogisticsVo.setPackageNo(request.getSoId());
        orderLogisticsVo.setSendTime(DateUtils.parse(request.getSendDate()));
        orderLogisticsVo.setLogisticsNo(request.getLId());
        orderLogisticsVo.setCompanyCode(request.getLcId());
        orderLogisticsVo.setCompanyName(request.getLogisticsCompany());
        mqProducer.send(orderLogisticsVo, ORDER_LOGISTICS_TOPIC);
    }


    @Override
    public void refreshToken() {
        JushuitanResponse response = jushuitanHttpClient.execute("refresh.token", null, JushuitanResponse.class);
        Assert.notNull(response, "刷新token失败");
        Assert.isTrue(0 == response.getCode() && true == response.getIssuccess(), "刷新聚水潭token异常 错误提示:" + response.getMsg());
    }

    @Override
    public void jushuitanOrderUpload(OrdersUploadRequest... request) {
        if (Objects.isNull(request) || request.length == 0)
            return;
        JushuitanResponse response = jushuitanHttpClient.execute("jushuitan.orders.upload", request, JushuitanResponse.class);
        Assert.notNull(response, "订单上传失败");
        Assert.isTrue(0 == response.getCode() && true == response.getIssuccess(), "上传聚水潭订单异常 错误提示:" + response.getMsg());
    }

    @Override
    public OrdersSingleQueryResponse ordersSingleQuery(OrdersSingleQueryRequest request) {
        OrdersSingleQueryResponse response = jushuitanHttpClient.execute("orders.single.query", request, OrdersSingleQueryResponse.class);
        Assert.notNull(response, "订单查询失败");
        return response;
    }

    @Override
    public List<InventoryQueryDto> inventoryQuery(InventoryQueryVo inventoryQueryVo) {
        InventoryQueryRequest request = new InventoryQueryRequest();
        request.setWmsCoId(inventoryQueryVo.getWmsCoId());
        request.setSkuIds(String.join(",", inventoryQueryVo.getSkus()));
        InventoryQueryResponse response = jushuitanHttpClient.execute("inventory.query", request, InventoryQueryResponse.class);
        Assert.notNull(response, "库存查询失败");
        Assert.isTrue(0 == response.getCode(), "查询聚水潭商品库存异常 错误提示:" + response.getMsg());
        List<InventoryQueryDto> inventoryQueryDtos = Lists.newArrayListWithCapacity(response.getInventorys().size());
        response.getInventorys().stream().forEach(inventory ->
                inventoryQueryDtos.add(new InventoryQueryDto(inventory.getIId(), inventory.getSkuId(), inventory.getQty() + inventory.getVirtualQty() - inventory.getOrderLock())));
        return inventoryQueryDtos;
    }

    @Override
    public List<SkuQueryDto> skuQuery(SkuQueryVo skuQueryVo) {
        SkuQueryRequest request = new SkuQueryRequest();
        request.setSkuIds(String.join(",", skuQueryVo.getSkus()));
        SkuQueryResponse response = jushuitanHttpClient.execute("sku.query", request, SkuQueryResponse.class);
        Assert.notNull(response, "普通商品查询失败");
        Assert.isTrue(0 == response.getCode(), "查询聚水潭普通商品异常 错误提示:" + response.getMsg());
        List<SkuQueryDto> skuQueryDtos = Lists.newArrayListWithCapacity(response.getDatas().size());
        response.getDatas().stream().forEach(data -> {
            SkuQueryDto skuQueryDto = new SkuQueryDto();
            BeanUtils.copyProperties(data, skuQueryDto);
            skuQueryDto.setSku(data.getSkuId());
            skuQueryDtos.add(skuQueryDto);
        });
        return skuQueryDtos;
    }

    @Override
    public List<SkuMapQueryDto> skuMapQuery(SkuMapQueryVo skuMapQueryVo) {
        SkuMapQueryRequest request = new SkuMapQueryRequest();
        request.setSkuIds(String.join(",", skuMapQueryVo.getSkus()));
        SkuMapQueryResponse response = jushuitanHttpClient.execute("skumap.query", request, SkuMapQueryResponse.class);
        Assert.notNull(response, "商品映射查询失败");
        Assert.isTrue(0 == response.getCode(), "查询聚水潭商品映射异常 错误提示:" + response.getMsg());
        List<SkuMapQueryDto> skuMapQueryDtos = Lists.newArrayListWithCapacity(response.getDatas().size());
        response.getDatas().stream().forEach(data -> {
            SkuMapQueryDto skuMapQueryDto = new SkuMapQueryDto();
            BeanUtils.copyProperties(data, skuMapQueryDto);
            skuMapQueryDto.setSku(data.getSkuId());
            skuMapQueryDtos.add(skuMapQueryDto);
        });
        return skuMapQueryDtos;
    }
}
