package com.mmj.good.controller;


import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.mmj.common.model.ReturnData;
import com.mmj.good.model.GoodWarehouse;
import com.mmj.good.service.GoodWarehouseService;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.mmj.common.controller.BaseController;

import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * 商品库存表 前端控制器
 * </p>
 *
 * @author H.J
 * @since 2019-06-03
 */
@RestController
@RequestMapping("/goodWarehouse")
public class GoodWarehouseController extends BaseController {

    Logger logger = LoggerFactory.getLogger(GoodWarehouseController.class);

    @Autowired
    private GoodWarehouseService goodWarehouseService;


    @ApiOperation(value = "sku仓库列表查询")
    @RequestMapping(value = "/queryList", method = RequestMethod.POST)
    public ReturnData<List<GoodWarehouse>> queryList(@RequestBody GoodWarehouse goodWarehouse) {
        return initSuccessObjectResult(goodWarehouseService.query(goodWarehouse));
    }

    @ApiOperation(value = "更新sku仓库信息")
    @RequestMapping(value = "/updateBatchById", method = RequestMethod.POST)
    public ReturnData updateBatchById(@RequestBody List<GoodWarehouse> goodWarehouses) {
        boolean result = goodWarehouseService.updateBatchById(goodWarehouses);
        if (result) {
            return initSuccessResult();
        } else {
            return initExcetionObjectResult("修改失败！");
        }
    }

    @ApiOperation(value = "查询sku仓库信息")
    @RequestMapping(value = "/queryWarehouseNameBySku/{goodSku}", method = RequestMethod.POST)
    public ReturnData<List<String>> queryWarehouseNameBySku(@PathVariable String goodSku) {
        EntityWrapper<GoodWarehouse> entityWrapper = new EntityWrapper<>();
        GoodWarehouse goodWarehouse = new GoodWarehouse();
        goodWarehouse.setGoodSku(goodSku);
        GoodWarehouse result = goodWarehouseService.selectOne(entityWrapper);
        if (result != null) {
            String warehouseName = result.getWarehouseName();
            if (warehouseName != null && warehouseName.length() > 0) {
                return initSuccessObjectResult(Arrays.asList(warehouseName.split(",")));
            }
        }
        return initExcetionObjectResult("查询结果为空！");
    }

    @ApiOperation(value = "sku仓库列表查询")
    @RequestMapping(value = "/queryOrderList", method = RequestMethod.POST)
    public ReturnData<List<GoodWarehouse>> queryOrderList(@RequestBody List<String> goodSku) {
        EntityWrapper<GoodWarehouse> entityWrapper = new EntityWrapper<>();
        entityWrapper.in("GOOD_SKU", goodSku);
        return initSuccessObjectResult(goodWarehouseService.selectList(entityWrapper));
    }
}

