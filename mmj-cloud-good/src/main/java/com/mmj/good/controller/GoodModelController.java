package com.mmj.good.controller;


import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.mmj.common.model.ReturnData;
import com.mmj.good.model.GoodModel;
import com.mmj.good.model.GoodModelEx;
import com.mmj.good.model.GoodSale;
import com.mmj.good.service.GoodModelService;
import com.mmj.good.service.GoodSaleService;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.mmj.common.controller.BaseController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 商品规格表 前端控制器
 * </p>
 *
 * @author H.J
 * @since 2019-06-03
 */
@RestController
@RequestMapping("/goodModel")
public class GoodModelController extends BaseController {

    Logger logger = LoggerFactory.getLogger(GoodModelController.class);

    @Autowired
    private GoodModelService goodModelService;

    @ApiOperation(value = "商品规格表列表查询")
    @RequestMapping(value = "/queryList", method = RequestMethod.POST)
    public ReturnData<Page<GoodModelEx>> queryList(@RequestBody GoodModel entity) {
        return initSuccessObjectResult(goodModelService.queryList(entity));
    }

    @ApiOperation(value = "商品规格删除")
    @RequestMapping(value = "/delete/{modelId}", method = RequestMethod.POST)
    public ReturnData<List<GoodModelEx>> delete(@PathVariable Integer modelId) {
        boolean result = goodModelService.deleteById(modelId);
        if (result) {
            return initSuccessResult();
        }
        return initExcetionObjectResult("删除失败！");
    }

    @ApiOperation(value = "商品规格表列表查询")
    @RequestMapping(value = "/queryOrderList", method = RequestMethod.POST)
    public ReturnData<List<GoodModelEx>> queryOrderList(@RequestBody List<String> goodSku) {
        return initSuccessObjectResult(goodModelService.queryListBySku(goodSku));
    }


}

