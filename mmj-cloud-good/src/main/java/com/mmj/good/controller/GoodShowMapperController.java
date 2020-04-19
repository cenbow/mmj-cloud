package com.mmj.good.controller;


import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.mmj.common.controller.BaseController;
import com.mmj.common.model.ReturnData;
import com.mmj.good.model.GoodShowMapper;
import com.mmj.good.service.GoodShowMapperService;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 商品展示关联表 前端控制器
 * </p>
 *
 * @author H.J
 * @since 2019-06-11
 */
@RestController
@RequestMapping("/goodShowMapper")
public class GoodShowMapperController extends BaseController {

    Logger logger = LoggerFactory.getLogger(GoodShowMapperController.class);

    @Autowired
    private GoodShowMapperService goodShowMapperService;

    @ApiOperation(value = "商品字典列表查询")
    @RequestMapping(value = "/queryList", method = RequestMethod.POST)
    public ReturnData<List<GoodShowMapper>> queryList(@RequestBody GoodShowMapper entity) {
        EntityWrapper<GoodShowMapper> entityWrapper = new EntityWrapper<>(entity);
        return initSuccessObjectResult(goodShowMapperService.selectList(entityWrapper));
    }

    @ApiOperation(value = "商品类型保存")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public ReturnData save(@RequestBody List<GoodShowMapper> list) {
        boolean result = goodShowMapperService.insertBatch(list);
        if (result) {
            return initSuccessResult();
        }
        return initExcetionObjectResult("保存失败！");
    }

}

