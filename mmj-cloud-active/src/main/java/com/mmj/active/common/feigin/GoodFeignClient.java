package com.mmj.active.common.feigin;

import com.baomidou.mybatisplus.plugins.Page;
import com.mmj.active.common.model.*;
import com.mmj.active.common.model.vo.CutGoodVo;
import com.mmj.common.interceptor.FeignRequestInterceptor;
import com.mmj.common.model.GoodStock;
import com.mmj.common.model.ReturnData;
import com.mmj.common.model.ThreeSaleTennerOrder;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@FeignClient(name = "mmj-cloud-good", fallbackFactory = GoodFallbackFactory.class, configuration = FeignRequestInterceptor.class)
public interface GoodFeignClient {

    @RequestMapping(value = "/goodInfo/batchVerifyGoodSpu", method = RequestMethod.GET)
    @ResponseBody
    ReturnData batchVerifyGoodSpu(@RequestParam("spuList") List<String> spuList);

    @RequestMapping(value = "/async/getById/{id}", method = RequestMethod.POST)
    @ResponseBody
    GoodInfo getById(@PathVariable("id") Integer id);

    @RequestMapping(value = "/goodInfo/queryGoodImgUrl/{id}", method = RequestMethod.POST)
    String queryGoodImgUrl(@PathVariable("id") Integer id);

    /**
     * 砍价-根据goodIds查询商品价格/销量
     *
     * @param goodVo
     * @return
     */
    @RequestMapping(value = "/goodSale/queryGroupByInfo", method = RequestMethod.POST)
    @ResponseBody
    ReturnData<Object> queryGroupByInfo(@RequestBody CutGoodVo goodVo);

    /**
     * 专题-根据分类编码获取分类名称
     *
     * @param goodClassBase
     * @return
     */
    @RequestMapping(value = "/goodClass/queryDetail", method = RequestMethod.POST)
    @ResponseBody
    ReturnData<List<GoodClass>> queryGoodClassDetail(@RequestBody GoodClassBase goodClassBase);

    /**
     * 商品基础资料列表查询
     *
     * @param entityEx
     * @return
     */
    @RequestMapping(value = "/goodInfo/queryBaseList", method = RequestMethod.POST)
    @ResponseBody
    ReturnData<Object> queryBaseList(@RequestBody GoodInfoBaseQueryEx entityEx);

    /**
     * 商品销售资料列表查询-简
     *
     * @param goodSaleEx
     * @return
     */
    @RequestMapping(value = "/goodSale/queryList", method = RequestMethod.POST)
    @ResponseBody
    ReturnData<List<GoodSale>> queryList(@RequestBody GoodSaleEx goodSaleEx);

    /**
     * 文件信息保存
     *
     * @param goodFiles
     * @return
     */
    @RequestMapping(value = "/async/save", method = RequestMethod.POST)
    @ResponseBody
    ReturnData saveFile(@RequestBody List<GoodFile> goodFiles);

    /**
     * 查询销量前十商品
     *
     * @return
     */
    @RequestMapping(value = "/async/queryTopGood", method = RequestMethod.POST)
    @ResponseBody
    ReturnData<List<Map<String, Object>>> queryTopGood();

    /**
     * 搜索商品
     *
     * @param content
     * @return
     */
    @RequestMapping(value = "/goodInfo/searchGoods", method = RequestMethod.POST)
    @ResponseBody
    ReturnData<Page<GoodInfoEx>> searchGoods(@RequestBody String content);

    /**
     * 条件查询商品
     *
     * @param goodInfo
     * @return
     */
    @RequestMapping(value = "/goodInfo/queryGood", method = RequestMethod.POST)
    @ResponseBody
    ReturnData<List<GoodInfo>> queryGood(@RequestBody GoodInfo goodInfo);

    /**
     * 查询id排序-专题拼团
     *
     * @param entityEx
     * @return
     */
    @ApiOperation(value = "查询id排序-专题拼团")
    @RequestMapping(value = "/goodInfo/queryGoodIdOrder", method = RequestMethod.POST)
    ReturnData<List<Integer>> queryGoodIdOrder(@RequestBody GoodInfoEx entityEx);

    /**
     * 查询商品库存
     *
     * @param goodIds
     * @return
     */
    @RequestMapping(value = "/goodInfo/queryGoodNum", method = RequestMethod.POST)
    ReturnData<List<GoodNum>> queryGoodNum(@RequestBody List<Integer> goodIds);

    /**
     * 十元三件查询商品
     * @param threeSaleTennerOrder
     * @return
     */
    @RequestMapping(value = "/goodInfo/threeSaleTennerOrder", method = RequestMethod.POST)
    ReturnData<List<Integer>> threeSaleTennerOrder(ThreeSaleTennerOrder threeSaleTennerOrder);

    /**
     * 占用SKU库存记录
     * @param goodStocks
     * @return
     */
    @RequestMapping(value = "/async/occupy", method = RequestMethod.POST)
    ReturnData occupy(@RequestBody List<GoodStock> goodStocks);

    /**
     * 扣减SKU库存记录
     * @param goodStocks
     * @return
     */
    @RequestMapping(value = "/stock/deduct", method = RequestMethod.POST)
    ReturnData deduct(@RequestBody List<GoodStock> goodStocks);

    /**
     * 释放SKU库存记录
     * @param goodStocks
     * @return
     */
    @RequestMapping(value = "/stock/relieve", method = RequestMethod.POST)
    ReturnData relieve(@RequestBody List<GoodStock> goodStocks);

    /**
     * 回退SKU库存记录
     * @param goodStocks
     * @return
     */
    @RequestMapping(value = "/stock/rollback", method = RequestMethod.POST)
    ReturnData rollback(@RequestBody List<GoodStock> goodStocks);
}
