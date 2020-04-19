package com.mmj.good.stock.controller;


import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.mmj.common.constants.CommonConstant;
import com.mmj.common.controller.BaseController;
import com.mmj.common.exception.BusinessException;
import com.mmj.common.model.ReturnData;
import com.mmj.good.stock.model.GoodStock;
import com.mmj.good.stock.service.GoodStockService;
import com.xiaoleilu.hutool.date.DateField;
import com.xiaoleilu.hutool.date.DateUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 库存记录表 前端控制器
 * </p>
 *
 * @author H.J
 * @since 2019-08-08
 */
@RestController
@RequestMapping("/stock")
@Api(value = "商品库存管理")
public class GoodStockController extends BaseController {

    @Autowired
    private GoodStockService goodStockService;

    @ApiOperation(value = "查询SKU库存使用情况")
    @RequestMapping(value = "/queryList", method = RequestMethod.POST)
    public ReturnData<List<GoodStock>> queryList(@RequestBody GoodStock goodStock) {
        EntityWrapper<GoodStock> entityWrapper = new EntityWrapper<>(goodStock);
        return initSuccessObjectResult(goodStockService.selectList(entityWrapper));
    }

    /**
     * 占用库存
     * @param goodStocks
     * @return
     */
    @ApiOperation(value = "占用SKU库存记录")
    @RequestMapping(value = "/occupy", method = RequestMethod.POST)
    public ReturnData occupy(@RequestBody List<GoodStock> goodStocks) {
        //占用库存
        try {
            goodStockService.occupyToCache(goodStocks);
        } catch (BusinessException e) {
            return initExcetionObjectResult(e.getMessage());
        }
        //插入数据
        goodStockService.insertBatch(goodStocks);
        return initSuccessResult();
    }

    /**
     * 扣减不归还占用，待同步聚水潭统一计算
     * @param goodStocks
     * @return
     */
    @ApiOperation(value = "扣减SKU库存记录")
    @RequestMapping(value = "/deduct", method = RequestMethod.POST)
    public ReturnData deduct(@RequestBody List<GoodStock> goodStocks) {
        //更新数据
        goodStockService.updateBatch(goodStocks);
        //扣减库存
        goodStockService.deductToCache(goodStocks);
        return initSuccessResult();
    }


    /**
     * 释放归还占用
     * @param goodStocks
     * @return
     */
    @ApiOperation(value = "释放SKU库存记录")
    @RequestMapping(value = "/relieve", method = RequestMethod.POST)
    public ReturnData relieve(@RequestBody List<GoodStock> goodStocks) {
        //插入数据
        goodStockService.insertBatch(goodStocks);
        //释放库存
        goodStockService.relieveToCache(goodStocks);
        return initSuccessResult();
    }

    /**
     * 回退归还占用
     * @param goodStocks
     * @return
     */
    @ApiOperation(value = "回退SKU库存记录")
    @RequestMapping(value = "/rollback", method = RequestMethod.POST)
    public ReturnData rollback(@RequestBody List<GoodStock> goodStocks) {
        //插入数据
        goodStockService.insertBatch(goodStocks);
        //归还库存
        goodStockService.rollbackToCache(goodStocks);
        return initSuccessResult();
    }

    @ApiOperation(value = "校验是否占用库存")
    @RequestMapping(value = "/checkOccupyTime/{businessId}", method = RequestMethod.POST)
    public ReturnData<Boolean> checkOccupyTime(@PathVariable String businessId) {
        return initSuccessObjectResult(goodStockService.checkOccupyTime(businessId));
    }

    /**
     * 查询 OCCUPY RELIEVE ROLLBACK 库存之和
     * @param goodSku
     * @return
     */
    @ApiOperation(value = "查询SKU库存占用情况")
    @RequestMapping(value = "/query/{goodSku}", method = RequestMethod.POST)
    public ReturnData<Integer> query(@PathVariable Integer goodSku) {
        EntityWrapper<GoodStock> entityWrapper = new EntityWrapper<>();
        entityWrapper.setSqlSelect(" SUM(GOOD_NUM) GOOD_NUM ");
        entityWrapper.eq("GOOD_SKU", goodSku);
        entityWrapper.ne("STATUS", CommonConstant.GoodStockStatus.EXPIRE);
        entityWrapper.ne("STATUS", CommonConstant.GoodStockStatus.DEDUCT);
        entityWrapper.groupBy("GOOD_SKU");
        GoodStock goodStock = goodStockService.selectOne(entityWrapper);
        return initSuccessObjectResult(goodStock.getGoodNum());
    }
}

