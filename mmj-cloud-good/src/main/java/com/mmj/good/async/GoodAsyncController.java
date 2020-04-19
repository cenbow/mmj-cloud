package com.mmj.good.async;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.mmj.common.constants.CommonConstant;
import com.mmj.common.exception.BusinessException;
import com.mmj.common.model.ReturnData;
import com.mmj.good.constants.GoodConstants;
import com.mmj.good.model.GoodCombination;
import com.mmj.good.model.GoodFile;
import com.mmj.good.model.GoodInfo;
import com.mmj.good.model.GoodSale;
import com.mmj.good.service.GoodCombinationService;
import com.mmj.good.service.GoodFileService;
import com.mmj.good.service.GoodInfoService;
import com.mmj.good.service.GoodSaleService;
import com.mmj.good.stock.model.GoodStock;
import com.mmj.good.stock.service.GoodStockService;
import com.mmj.good.util.RedisCacheUtil;
import com.xiaoleilu.hutool.date.DateUtil;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import io.swagger.annotations.Api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;

import com.mmj.common.controller.BaseController;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 本控制器为mmj-cloud-good下共用，提供不需要进行令牌认证的方法给消息服务以及定时任务调度进行调用
 * @author shenfuding
 * BaseContextHandler.set(SecurityConstants.SHARDING_KEY, 你要操作的userId);
 *
 */
@Slf4j
@RestController
@RequestMapping("/async")
@Api(value = "商品模块异步处理控制器")
public class GoodAsyncController extends BaseController {

    @Autowired
    private GoodFileService goodFileService;

    @Autowired
    private GoodSaleService goodSaleService;

    @Autowired
    private GoodStockService goodStockService;

    @Autowired
    private GoodInfoService goodInfoService;

    @Autowired
    private GoodCombinationService goodCombinationService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private String GOOD_ASYNC_SYNGOODSSTOCKZH = "GOOD:ASYNC:SYNGOODSSTOCKZH";

    private String GOOD_ASYNC_SYNGOODSSTOCK = "GOOD:ASYNC:SYNGOODSSTOCK";

    private String GOOD_ASYNC_SYNSTOCK = "GOOD:ASYNC:SYNSTOCK";

    Logger logger = LoggerFactory.getLogger(GoodAsyncController.class);

    @ApiOperation(value = "文件信息保存")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public ReturnData save(@RequestBody List<GoodFile> goodFiles){
        goodFileService.insertOrUpdateBatch(goodFiles);
        return initSuccessResult();
    }

    @ApiOperation(value = "修改商品库存")
    @RequestMapping(value = "/updateGoodNum", method = RequestMethod.POST)
    public ReturnData updateGoodNum(@RequestBody List<GoodSale> goodSales){
        goodSaleService.updateGoodNum(goodSales);
        return initSuccessResult();
    }

    /**
     * job
     * 清理过期数据 每分钟执行一次
     * @return
     */
    @ApiOperation(value = "清理过期数据")
    @RequestMapping(value = "/cleanExpire", method = RequestMethod.POST)
    public ReturnData cleanExpire() {
        goodStockService.cleanExpire();
        return initSuccessResult();
    }

    /**
     * job
     * 清理数据 每个月1号0点执行
     * @return
     */
    @ApiOperation(value = "清理数据")
    @RequestMapping(value = "/clean", method = RequestMethod.POST)
    public ReturnData clean() {
        long date = DateUtil.date().getTime() - 30 * 24 * 60 * 60 * 1000L;
        EntityWrapper<GoodStock> entityWrapper = new EntityWrapper<>();
        entityWrapper.le("CREATER_TIME", new Date(date));
        //删除数据
        goodStockService.delete(entityWrapper);
        return initSuccessResult();
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

    @ApiOperation(value = "查询销量前十商品")
    @RequestMapping(value = "/queryTopGood", method = RequestMethod.POST)
    public ReturnData<List<Map<String, Object>>> queryTopGood() {
        return initSuccessObjectResult(goodInfoService.queryTopGood());
    }

    @ApiOperation(value = "根据id查询商品")
    @RequestMapping(value = "/getById/{id}", method = RequestMethod.POST)
    public GoodInfo getById(@PathVariable("id") Integer id) {
        return goodInfoService.getById(id);
    }

    /**
     * job
     *
     * @return
     */
    @ApiOperation(value = "自动上架")
    @RequestMapping(value = "/autoOnshelve", method = RequestMethod.POST)
    public ReturnData autoOnshelve() {
        RedisCacheUtil.clearGoodInfoCache(redisTemplate);
        goodInfoService.autoOnshelve();
        return initSuccessResult();
    }

    @ApiOperation(value = "同步聚水潭-非组合商品")
    @RequestMapping(value = "/synGoodsStock", method = RequestMethod.POST)
    public ReturnData synGoodsStock() {
        Long increment = redisTemplate.opsForValue().increment(GOOD_ASYNC_SYNGOODSSTOCK, 1);
        redisTemplate.expire(GOOD_ASYNC_SYNGOODSSTOCK, 1, TimeUnit.HOURS);
        if (increment != 1) {
            return initSuccessResult();
        }
        goodSaleService.synGoodsStock();
        return initSuccessResult();
    }

    @ApiOperation(value = "同步聚水潭-组合商品")
    @RequestMapping(value = "/synGoodsStockZh", method = RequestMethod.POST)
    public ReturnData synGoodsStockZh() {
        Long increment = redisTemplate.opsForValue().increment(GOOD_ASYNC_SYNGOODSSTOCKZH, 1);
        redisTemplate.expire(GOOD_ASYNC_SYNGOODSSTOCKZH, 1, TimeUnit.HOURS);
        if (increment != 1) {
            return initSuccessResult();
        }
        goodCombinationService.synGoodsStockZh();
        return initSuccessResult();
    }


    @ApiOperation(value = "更新库存占用信息")
    @RequestMapping(value = "/synStock", method = RequestMethod.POST)
    public ReturnData synStock() {
        Long increment = redisTemplate.opsForValue().increment(GOOD_ASYNC_SYNSTOCK, 1);
        redisTemplate.expire(GOOD_ASYNC_SYNSTOCK, 1, TimeUnit.HOURS);
        if (increment != 1) {
            return initSuccessResult();
        }
        goodStockService.asyncStock();
        return initSuccessResult();
    }

    @ApiOperation(value = "更新库存占用信息")
    @RequestMapping(value = "/synStock1", method = RequestMethod.POST)
    public ReturnData synStock1() {
        Long increment = redisTemplate.opsForValue().increment(GOOD_ASYNC_SYNSTOCK, 1);
        redisTemplate.expire(GOOD_ASYNC_SYNSTOCK, 1, TimeUnit.HOURS);
        if (increment != 1) {
            return initSuccessResult();
        }
        goodStockService.asyncStock("1");
        return initSuccessResult();
    }


}
