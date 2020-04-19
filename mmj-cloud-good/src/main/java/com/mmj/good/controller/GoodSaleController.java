package com.mmj.good.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.mmj.common.constants.CommonConstant;
import com.mmj.common.exception.BusinessException;
import com.mmj.common.model.ReturnData;
import com.mmj.common.properties.SecurityConstants;
import com.mmj.good.constants.GoodConstants;
import com.mmj.good.feigin.JunshuitanFeignClient;
import com.mmj.good.feigin.dto.InventoryQuery;
import com.mmj.good.model.*;
import com.mmj.good.service.*;
import com.mmj.good.stock.service.GoodStockService;
import com.mmj.good.util.RedisCacheUtil;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import com.mmj.common.controller.BaseController;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 商品销售信息表 前端控制器
 * </p>
 *
 * @author H.J
 * @since 2019-06-03
 */
@RestController
@RequestMapping("/goodSale")
public class GoodSaleController extends BaseController {

    Logger logger = LoggerFactory.getLogger(GoodSaleController.class);

    @Autowired
    private GoodSaleService goodSaleService;
    @Autowired
    private GoodModelService goodModelService;
    @Autowired
    private GoodFileService goodFileService;
    @Autowired
    private GoodWarehouseService goodWarehouseService;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private GoodInfoService goodInfoService;
    @Autowired
    private JunshuitanFeignClient junshuitanFeignClient;
    @Autowired
    private GoodStockService goodStockService;

    public static void main(String[] args) {
        System.out.println((int)(19.9 * 100));
        BigDecimal bigDecimal = new BigDecimal("19.9");
        BigDecimal bigDecimal1 = new BigDecimal("100");
        System.out.println(bigDecimal.multiply(bigDecimal1).intValue());

        Integer a = new Integer(1990);

        BigDecimal b1 = new BigDecimal(String.valueOf(a));
        BigDecimal b2 = new BigDecimal("100");
        System.out.println(b1.divide(b2).doubleValue());
    }


    @ApiOperation(value = "新增或更新商品销售信息")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public ReturnData<String> saveInfo(@RequestBody List<GoodSaleEx> goodSaleExes) throws Exception {
        RedisCacheUtil.clearGoodInfoCache(redisTemplate);
        RedisCacheUtil.clearGoodSaleCache(redisTemplate);
        EntityWrapper<GoodSale> goodSaleEntityWrapper = new EntityWrapper<>();
        goodSaleEntityWrapper.ne("GOOD_ID", goodSaleExes.get(0).getGoodId());
        goodSaleEntityWrapper.in("GOOD_SKU", goodSaleExes.stream().map(GoodSale::getGoodSku).collect(Collectors.toList()));
        List<GoodSale> goodSales = goodSaleService.selectList(goodSaleEntityWrapper);
        if (goodSales != null && !goodSales.isEmpty()) {
            return initExcetionObjectResult("存在重复SKU:" + goodSales.get(0).getGoodSku());
        }
        try {
            goodSaleService.save(goodSaleExes);
        } catch (BusinessException e) {
            return initExcetionObjectResult(e.getMessage());
        }

        return initSuccessObjectResult("success");
    }

    @ApiOperation(value = "商品销售资料查询")
    @RequestMapping(value = "/query/{goodId}", method = RequestMethod.POST)
    public ReturnData<Object> query(@PathVariable Integer goodId) {
        //从缓存获取
        String key = RedisCacheUtil.GOOD_SALE_QUERY + goodId;
        Object o = redisTemplate.opsForHash().get(RedisCacheUtil.GOOD_SALE, key);
        GoodInfo goodInfo = goodInfoService.selectById(goodId);
        if (o != null && !"".equals(o)) {
            JSONArray array = JSON.parseArray(String.valueOf(o));
            for (int i = 0; i < array.size(); i++) {
                JSONObject jsonObject = array.getJSONObject(i);
                if (goodInfo.getCombinaFlag() == 1) {
                    Integer num = goodSaleService.queryCombNum(jsonObject.getString("goodSku"));
                    if (num == null) {
                        num = 0;
                    }
                    array.getJSONObject(i).put("goodNum", num);
                } else {
                    String goodSku = jsonObject.getString("goodSku");
                    array.getJSONObject(i).put("goodNum", goodSaleService.queryNum(goodSku));

                    Object o2 = redisTemplate.opsForValue().get(GoodConstants.SKU_SALE + goodSku);
                    if (o2 != null && !"".equals(o2)) {
                        array.getJSONObject(i).put("saleNum", o2);
                    } else {
                        array.getJSONObject(i).put("saleNum", 0);
                    }
                }
            }
            return initSuccessObjectResult(array);
        }
        List<GoodSaleEx> goodSaleExes = queryGoodSale(goodId, goodInfo.getCombinaFlag());
        if (goodSaleExes != null) {
            String jsonString = JSON.toJSONString(goodSaleExes);
            redisTemplate.opsForHash().put(RedisCacheUtil.GOOD_SALE, key, jsonString);
            return initSuccessObjectResult(JSON.parseArray(jsonString));
        } else {
            return initSuccessResult();
        }
    }

    @ApiOperation(value = "商品销售资料查询-BOSS")
    @RequestMapping(value = "/queryBoss/{goodId}", method = RequestMethod.POST)
    public ReturnData<List<GoodSaleEx>> queryBoss(@PathVariable Integer goodId) {
        GoodInfo goodInfo = goodInfoService.selectById(goodId);
        return initSuccessObjectResult(queryGoodSale(goodId, goodInfo.getCombinaFlag()));
    }

    public List<GoodSaleEx> queryGoodSale(Integer goodId, Integer combinaFlag){
        List<GoodSaleEx> goodSaleExes = queryByGoodId(goodId);
        if (goodSaleExes != null) {
            //返回json
            for (int i = 0; i < goodSaleExes.size(); i++) {
                GoodSaleEx goodSaleEx = goodSaleExes.get(i);
                if (combinaFlag == 1) {
                    Integer num = goodSaleService.queryCombNum(goodSaleEx.getGoodSku());
                    if (num == null) {
                        num = 0;
                    }
                    goodSaleEx.setGoodNum(num);
                } else {
                    goodSaleEx.setGoodNum(goodSaleService.queryNum(goodSaleEx.getGoodSku()));

                    Object o2 = redisTemplate.opsForValue().get(GoodConstants.SKU_SALE + goodSaleEx.getGoodSku());
                    if (o2 != null && !"".equals(o2)) {
                        goodSaleEx.setSaleNum(Integer.valueOf(String.valueOf(o2)));
                    } else {
                        goodSaleEx.setSaleNum(0);
                    }
                }
            }
        }
        return goodSaleExes;
    }

    public List<GoodSaleEx> queryByGoodId(Integer goodId){
        //查询
        EntityWrapper<GoodSale> goodSaleWrapper = new EntityWrapper<>();
        goodSaleWrapper.eq("GOOD_ID", goodId);
        List<GoodSale> goodSales = goodSaleService.selectList(goodSaleWrapper);
        List<GoodSaleEx> goodSaleExes = new ArrayList<>();
        if (goodSales != null && !goodSales.isEmpty()) {
            //查询规格
            EntityWrapper<GoodModel> goodModelWrapper = new EntityWrapper<>();
            goodModelWrapper.eq("GOOD_ID", goodId);
            goodModelWrapper.orderBy("MODEL_ORDER");
            List<GoodModel> goodModels = goodModelService.selectList(goodModelWrapper);

            //查询仓库
            EntityWrapper<GoodWarehouse> goodWarehouseWrapper = new EntityWrapper<>();
            goodWarehouseWrapper.eq("GOOD_ID", goodId);
            List<GoodWarehouse> goodWarehouses = goodWarehouseService.selectList(goodWarehouseWrapper);

            //组装数据
            if (goodModels != null && !goodSales.isEmpty()) {
                //查询规格图片
                EntityWrapper<GoodFile> goodFileWrapper = new EntityWrapper<>();
                goodFileWrapper.eq("GOOD_ID", goodId);
                goodFileWrapper.eq("FILE_TYPE", GoodConstants.FileType.SALEMODEL);
                List<GoodFile> goodFiles = goodFileService.selectList(goodFileWrapper);
                //销售资料
                for (GoodSale goodSale : goodSales) {
                    GoodSaleEx goodSaleEx = JSON.parseObject(JSON.toJSONString(goodSale), GoodSaleEx.class);
                    //规格
                    List<GoodModelEx> goodModelExes = new ArrayList<>();
                    for (GoodModel goodModel : goodModels) {
                        GoodModelEx goodModelEx = JSON.parseObject(JSON.toJSONString(goodModel), GoodModelEx.class);
                        if (goodSale.getSaleId().compareTo(goodModel.getSaleId()) == 0) {
                            if (goodFiles != null && !goodFiles.isEmpty()) {
                                for (GoodFile goodFile : goodFiles) {
                                    if (goodModelEx.getModelId() != null && goodFile.getModelId().compareTo(goodModelEx.getModelId()) == 0) {
                                        goodModelEx.setGoodFile(goodFile);
                                        break;
                                    }
                                }
                            }
                            goodModelExes.add(goodModelEx);
                        }
                    }
                    goodSaleEx.setGoodModelExes(goodModelExes);
                    //sku仓库
                    if(goodWarehouses != null && !goodWarehouses.isEmpty()) {
                        List<GoodWarehouse> result = new ArrayList<>();
                        for (GoodWarehouse goodWarehouse : goodWarehouses) {
                            if (goodSaleEx.getSaleId() != null && goodWarehouse.getSaleId().compareTo(goodSaleEx.getSaleId()) == 0) {
                                result.add(goodWarehouse);
                            }
                        }
                        goodSaleEx.setGoodWarehouses(result);
                    }
                    goodSaleExes.add(goodSaleEx);
                }
            }
        }
        return goodSaleExes;
    }

    @ApiOperation(value = "商品销售资料分组查询")
    @RequestMapping(value = "/queryGroupByInfo", method = RequestMethod.POST)
    public ReturnData<Object> queryGroupByInfo(@RequestBody GoodSaleEx goodSaleEx) throws Exception {
        //从缓存获取
        String key = RedisCacheUtil.GOOD_SALE_QUERYGROUPBYINFO + RedisCacheUtil.getKey(goodSaleEx);
        Object o = redisTemplate.opsForHash().get(RedisCacheUtil.GOOD_SALE, key);
        if (o != null && !"".equals(o)) {
            return initSuccessObjectResult(JSON.parseArray(String.valueOf(o)));
        }
        //查询
        List<GoodSaleEx> goodSaleExes = goodSaleService.queryGroupByInfo(goodSaleEx);
        //返回json
        if (goodSaleExes != null) {
            String jsonString = JSON.toJSONString(goodSaleExes);
            redisTemplate.opsForHash().put(RedisCacheUtil.GOOD_SALE, key, jsonString);
            return initSuccessObjectResult(JSON.parseArray(jsonString));
        } else {
            return initSuccessResult();
        }
    }

    @ApiOperation(value = "商品销售资料列表查询-简")
    @RequestMapping(value = "/queryList", method = RequestMethod.POST)
    public ReturnData<Object> queryList(@RequestBody GoodSaleEx goodSaleEx) throws Exception {
        //从缓存获取
        String key = RedisCacheUtil.GOOD_SALE_QUERYLIST + RedisCacheUtil.getKey(goodSaleEx);
        Object o = redisTemplate.opsForHash().get(RedisCacheUtil.GOOD_SALE, key);
        if (o != null && !"".equals(o)) {
            return initSuccessObjectResult(JSON.parseArray(String.valueOf(o)));
        }
        GoodSale goodSale = JSON.parseObject(JSON.toJSONString(goodSaleEx), GoodSale.class);
        EntityWrapper<GoodSale> entityWrapper = new EntityWrapper<>(goodSale);
        entityWrapper.in(goodSaleEx.getGoodIds() != null, "GOOD_ID", goodSaleEx.getGoodIds());
        entityWrapper.in(goodSaleEx.getSaleIds() != null, "SALE_ID", goodSaleEx.getSaleIds());
        entityWrapper.in(goodSaleEx.getGoodSkus() != null, "GOOD_SKU", goodSaleEx.getGoodSkus());
        List<GoodSale> goodSales = goodSaleService.selectList(entityWrapper);
        //返回json
        if (goodSales != null) {
            String jsonString = JSON.toJSONString(goodSales);
            redisTemplate.opsForHash().put(RedisCacheUtil.GOOD_SALE, key, jsonString);
            return initSuccessObjectResult(JSON.parseArray(jsonString));
        } else {
            return initSuccessResult();
        }
    }

    @ApiOperation(value = "商品销售资料列表查询-简")
    @RequestMapping(value = "/queryListBoss", method = RequestMethod.POST)
    public ReturnData<List<GoodSale>> queryListBoss(@RequestBody GoodSaleEx goodSaleEx) throws Exception {
        GoodSale goodSale = JSON.parseObject(JSON.toJSONString(goodSaleEx), GoodSale.class);
        EntityWrapper<GoodSale> entityWrapper = new EntityWrapper<>(goodSale);
        entityWrapper.in(goodSaleEx.getGoodIds() != null, "GOOD_ID", goodSaleEx.getGoodIds());
        entityWrapper.in(goodSaleEx.getSaleIds() != null, "SALE_ID", goodSaleEx.getSaleIds());
        entityWrapper.in(goodSaleEx.getGoodSkus() != null, "GOOD_SKU", goodSaleEx.getGoodSkus());
        List<GoodSale> goodSales = goodSaleService.selectList(entityWrapper);
        for (GoodSale gs : goodSales) {
            GoodInfo goodInfo = goodInfoService.selectById(gs.getGoodId());
            Integer num;
            if (goodInfo.getCombinaFlag() == 1) {
                num = goodSaleService.queryCombNum(gs.getGoodSku());
            } else {
                num = goodSaleService.queryNum(gs.getGoodSku());
            }
            gs.setGoodNum(num);
        }
        return initSuccessObjectResult(goodSales);
    }

    @ApiOperation(value = "初始化商品库存")
    @RequestMapping(value = "/initGoodNum", method = RequestMethod.POST)
    public ReturnData<List<GoodSale>> initGoodNum() {
        int count = goodSaleService.selectCount(new EntityWrapper<>());
        if (count > 0) {
            if (count > 100) {
                int m = count/100;
                if (m * 100 < count) {
                    m++;
                }
                for (int i = 0; i < m; i++) {
                    Page<GoodSale> page = new Page<>(i + 1, 100);
                    List<GoodSale> goodSales = goodSaleService.selectPage(page).getRecords();
                    if (goodSales != null && !goodSales.isEmpty()) {
                        for (GoodSale goodSale : goodSales) {
                            redisTemplate.opsForValue().set(GoodConstants.SKU_STOCK + goodSale.getGoodSku(), goodSale.getGoodNum());
                            redisTemplate.opsForValue().set(GoodConstants.SKU_SALE + goodSale.getGoodSku(), goodSale.getSaleNum());
                        }
                    }
                }
            } else {
                List<GoodSale> goodSales = goodSaleService.selectList(new EntityWrapper<>());
                if (goodSales != null && !goodSales.isEmpty()) {
                    for (GoodSale goodSale : goodSales) {
                        redisTemplate.opsForValue().set(GoodConstants.SKU_STOCK + goodSale.getGoodSku(), goodSale.getGoodNum());
                        redisTemplate.opsForValue().set(GoodConstants.SKU_SALE + goodSale.getGoodSku(), goodSale.getSaleNum());
                    }
                }
            }
        }

        return initSuccessResult();
    }

    /*@ApiOperation(value = "修改商品库存")
    @RequestMapping(value = "/updateGoodNum", method = RequestMethod.POST)
    public ReturnData updateGoodNum(@RequestBody List<GoodSale> goodSales){
        goodSaleService.updateGoodNum(goodSales);
        return initSuccessResult();
    }*/

    @ApiOperation(value = "订单查询商品-下单验证")
    @RequestMapping(value = "/queryOrderGood", method = RequestMethod.POST)
    public ReturnData<List<GoodOrder>> queryOrderGood(@RequestBody List<String> goodSkus) {
        /*List<GoodOrder> goodOrders = goodSaleService.queryOrderGood(goodSkus);
        if (goodOrders != null && !goodOrders.isEmpty()) {
            for (GoodOrder goodOrder : goodOrders) {
                GoodInfo goodInfo = goodOrder.getGoodInfo();
                if (goodInfo != null && goodInfo.getCombinaFlag() == 1) {
                    Integer num = goodSaleService.sumCombNum(goodOrder.getGoodSku());
                    if (num != null && num >= 0) {
                        goodOrder.setGoodNum(num);
                    } else {
                        goodOrder.setGoodNum(0);
                    }
                }
            }
        }*/
        return initSuccessObjectResult(goodSaleService.queryOrderGood(goodSkus));
    }

}

