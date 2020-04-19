package com.mmj.good.controller;


import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.mmj.common.constants.CommonConstant;
import com.mmj.common.controller.BaseController;
import com.mmj.common.model.ReturnData;
import com.mmj.good.constants.GoodConstants;
import com.mmj.good.model.GoodCombination;
import com.mmj.good.model.GoodCombinationEx;
import com.mmj.good.model.GoodCombinationExcel;
import com.mmj.good.service.GoodCombinationService;
import com.xiaoleilu.hutool.poi.excel.ExcelReader;
import com.xiaoleilu.hutool.poi.excel.ExcelUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

/**
 * <p>
 * 组合商品表 前端控制器
 * </p>
 *
 * @author H.J
 * @since 2019-06-12
 */
@RestController
@RequestMapping("/goodCombination")
@Api(value = "组合商品管理")
public class GoodCombinationController extends BaseController {

    Logger logger = LoggerFactory.getLogger(GoodCombinationController.class);

    @Autowired
    private GoodCombinationService goodCombinationService;

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    /**
     * 组合商品excel导入
     *
     * @param file
     * @return
     */
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public ReturnData upload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return initExcetionObjectResult("请选择一个文件");
        }
        try (InputStream inputStream = file.getInputStream()) {
            //读取数据
            ExcelReader reader = ExcelUtil.getReader(inputStream);
            reader.addHeaderAlias("组合商品编码", "combinsku");
            reader.addHeaderAlias("组合款式编码", "combinspu");
            reader.addHeaderAlias("商品编码", "singlesku");
            reader.addHeaderAlias("数量", "packagenum");
            List<GoodCombinationExcel> goodCombinationExcels = reader.readAll(GoodCombinationExcel.class);
            reader.close();
            //导入数据
            goodCombinationService.upload(goodCombinationExcels);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return initExcetionObjectResult("导入失败！");
        }
        return initSuccessResult();
    }

    @ApiOperation(value = "查询组合商品信息")
    @RequestMapping(value = "/queryList/{goodSpu}", method = RequestMethod.POST)
    public ReturnData<List<GoodCombinationEx>> queryList(@PathVariable String goodSpu) {
        EntityWrapper<GoodCombination> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("GOOD_SPU", goodSpu);
        List<GoodCombination> goodCombinations = goodCombinationService.selectList(entityWrapper);
        if (goodCombinations != null && !goodCombinations.isEmpty()) {
            List<GoodCombinationEx> goodCombinationExes = JSON.parseArray(JSON.toJSONString(goodCombinations), GoodCombinationEx.class);
            for (GoodCombinationEx ex : goodCombinationExes) {
                Object o = redisTemplate.opsForValue().get(GoodConstants.SKU_STOCK + ex.getSubGoodSku());
                Object oU = redisTemplate.opsForValue().get(CommonConstant.GOOD_STOCK_OCCUPY + ex.getSubGoodSku());
                if (o != null && !"".equals(o)) {
                    if (oU != null && !"".equals(oU)) {
                        ex.setGoodNum(Integer.valueOf(String.valueOf(o)) - Integer.valueOf(String.valueOf(oU)));//单品库存
                    } else {
                        ex.setGoodNum(Integer.valueOf(String.valueOf(o)));//单品库存
                    }
                } else {
                    ex.setGoodNum(0);//单品库存
                }
            }
            return initSuccessObjectResult(goodCombinationExes);
        }
        return initSuccessResult();
    }

    @ApiOperation(value = "初始化组合关系")
    @RequestMapping(value = "/initGoodComb", method = RequestMethod.POST)
    public ReturnData initGoodComb(){
        List<GoodCombination> goodCombinations = goodCombinationService.selectList(new EntityWrapper<>());
        if (goodCombinations != null && !goodCombinations.isEmpty()) {
            goodCombinationService.initCombination(goodCombinations);
        }
        return initSuccessResult();
    }

    @ApiOperation(value = "初始化组合库存")
    @RequestMapping(value = "/initGoodCombNum", method = RequestMethod.POST)
    public ReturnData initGoodCombNum(){
        List<GoodCombination> goodCombinations = goodCombinationService.selectList(new EntityWrapper<>());
        if (goodCombinations != null && !goodCombinations.isEmpty()) {
            goodCombinationService.initCombinationNum(goodCombinations);
        }
        return initSuccessResult();
    }
}

