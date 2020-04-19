package com.mmj.third.jushuitan.controller;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.mmj.common.controller.BaseController;
import com.mmj.common.model.ReturnData;
import com.mmj.third.jushuitan.model.dto.InventoryQueryDto;
import com.mmj.third.jushuitan.model.dto.SkuMapQueryDto;
import com.mmj.third.jushuitan.model.dto.SkuQueryDto;
import com.mmj.third.jushuitan.model.request.CancelOrderRequest;
import com.mmj.third.jushuitan.model.request.LogisticsUploadRequest;
import com.mmj.third.jushuitan.model.vo.InventoryQueryVo;
import com.mmj.third.jushuitan.model.vo.SkuMapQueryVo;
import com.mmj.third.jushuitan.model.vo.SkuQueryVo;
import com.mmj.third.jushuitan.service.JushuitanService;
import com.netflix.discovery.converters.Auto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * @description: 聚水潭对接
 * @auther: KK
 * @date: 2019/6/1
 */
@Slf4j
@RestController
@RequestMapping("/jushuitan")
@Api(value = "聚水潭对接", description = "聚水潭对接")
public class JushuitanController extends BaseController {
    @Autowired
    private JushuitanService jushuitanService;

    @PostMapping("/refresh/token")
    @ApiOperation("聚水潭-刷新Token")
    public void refreshToken() {
        log.info("=> 刷新聚水潭Token");
        jushuitanService.refreshToken();
    }

    @PostMapping("/orders/cancel")
    @ApiOperation("聚水潭-消息推送-取消订单")
    public Object cancelOrder(@RequestBody CancelOrderRequest request) {
        log.info("=> 聚水潭取消订单 request:{}", JSONObject.toJSONString(request));
        jushuitanService.cancelOrder(request);
        Map resultMap = Maps.newHashMapWithExpectedSize(2);
        resultMap.put("code", 0);
        resultMap.put("msg", "success");
        return resultMap;
    }

    @PostMapping("/logistics/upload")
    @ApiOperation("聚水潭-消息推送-物流同步")
    public Object logisticsUpload(@RequestBody LogisticsUploadRequest request) {
        log.info("=> 聚水潭物流同步 request:{}", JSONObject.toJSONString(request));
        jushuitanService.logisticsUpload(request);
        Map resultMap = Maps.newHashMapWithExpectedSize(2);
        resultMap.put("code", 0);
        resultMap.put("msg", "success");
        return resultMap;
    }

    @PostMapping("/inventory/query")
    @ApiOperation("查询聚水潭库存")
    public ReturnData<List<InventoryQueryDto>> inventoryQuery(@Valid @RequestBody InventoryQueryVo inventoryQueryVo) {
        return initSuccessObjectResult(jushuitanService.inventoryQuery(inventoryQueryVo));
    }

    @PostMapping("/sku/query")
    @ApiOperation("普通商品查询")
    public ReturnData<List<SkuQueryDto>> skuQuery(@Valid @RequestBody SkuQueryVo skuQueryVo) {
        return initSuccessObjectResult(jushuitanService.skuQuery(skuQueryVo));
    }

    @PostMapping("/sku/map/query")
    @ApiOperation("商品映射查询")
    public ReturnData<List<SkuMapQueryDto>> skuQuery(@Valid @RequestBody SkuMapQueryVo skuMapQueryVo) {
        return initSuccessObjectResult(jushuitanService.skuMapQuery(skuMapQueryVo));
    }

}
