package com.mmj.order.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mmj.common.model.ReturnData;
import com.mmj.order.common.feign.GoodFeignClient;
import com.mmj.order.common.feign.JunshuitanFeignClient;
import com.mmj.order.common.model.dto.GoodCombination;
import com.mmj.order.common.model.dto.GoodWarehouse;
import com.mmj.order.common.model.vo.GoodSaleEx;
import com.mmj.order.constant.GoodStockStatus;
import com.mmj.order.model.dto.GoodsStockDto;
import com.mmj.order.model.dto.InventoryQueryDto;
import com.mmj.order.model.vo.InventoryQueryVo;
import com.mmj.order.service.GoodStockJstService;
import com.mmj.order.service.GoodsStockService;
import com.mmj.order.utils.http.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class GoodStockJstImpl implements GoodStockJstService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private GoodFeignClient goodFeignClient;

    @Autowired
    private JunshuitanFeignClient junshuitanFeignClient;

    @Autowired
    private GoodsStockService goodsStockService;


    /**
     * 同步库存
     *
     * @param
     */
    @Override
    public void jstGoodNum() {
        logger.info("开始同步聚水潭库存");
        List<GoodsStockDto> goodsStockDtos = Lists.newArrayList();
        logger.info("获取当前所有单品的库存{}", getGoodsStock());
        goodsStockDtos.addAll(getGoodsStock());
        logger.info("单品商品开始同步库存");
        inDB(goodsStockDtos);
        logger.info("获取当前所有组合商品的的库存数量{}", combineSkuStock(goodsStockDtos).size());
        List<GoodsStockDto> combineStocks = combineSkuStock(goodsStockDtos);
        logger.info("组合商品开始同步库存");
        inDB(combineStocks);
        goodsStockDtos.addAll(combineStocks);
    }


    /**
     * 单个商品sku库存
     *
     * @return
     */
    @Override
    public List<GoodsStockDto> getGoodsStock() {
        // 获取所有商品的sku
        List<String> skus = new ArrayList<>();
        try {
            GoodSaleEx goodSaleEx = new GoodSaleEx();
            ReturnData<Object> returnData = goodFeignClient.queryGroupByInfo(goodSaleEx);
            if (returnData != null && returnData.getData() != null) {
                List<GoodSaleEx> list = JSONArray.parseArray(JSON.toJSONString(returnData.getData()), GoodSaleEx.class);
                for (int i = 0; i < list.size(); i++) {
                    skus.add(list.get(i).getGoodSku());
                }
            }

        } catch (Exception e) {
            logger.info("获取所有商品的sku的接口异常:" + e);

        }
        // 组合中的单个sku数量
        String key = GoodStockStatus.SKU_STOCK_SIGNLE + "*";
        Set<String> singles = redisTemplate.keys(key);
        logger.info("组合商品中单品数量为:{}", singles.size());


        List<String> allSku = new ArrayList<>();

        if (skus != null && skus.size() > 0) {
            skus.stream().forEach(r -> {
                if (StringUtils.isNotEmpty(r)) {
                    allSku.add(r);
                }
            });
        }
        if (singles.size() > 0 && singles != null) {
            singles.stream().forEach(r -> {
                allSku.add((String) r);
            });
        }
        // 去重
        List<String> allSkus = allSku.stream().distinct().collect(Collectors.toList());
        // 获取系统的sku
        List<GoodsStockDto> stockDtos = convertSkus(allSkus);
        // 除去异常库存
        abnormal(stockDtos);
        return stockDtos;
    }


    /**
     * 组合商品
     *
     * @param stockDtos
     * @return
     */
    @Override
    public List<GoodsStockDto> combineSkuStock(List<GoodsStockDto> stockDtos) {
        List<GoodsStockDto> combineStocks = Lists.newArrayList();

        GoodCombination combination = new GoodCombination();
        try {
            ReturnData<List<GoodCombination>> returnData = goodFeignClient.queryList(combination);

            List<GoodCombination> combinations = returnData.getData();
            List<String> combineSkus = Lists.newArrayListWithCapacity(combinations.size());
            List<String> singleSkus = Lists.newArrayListWithCapacity(combinations.size());
            combinations.stream().forEach(c -> {
                combineSkus.add(c.getGoodSku());
                singleSkus.add(c.getSubGoodSku());
            });
            goodsStockService.addCombineGoods(combineSkus.toArray(new String[combineSkus.size()]));
            goodsStockService.addCombineSingleGoods(singleSkus.toArray(new String[singleSkus.size()]));
            Map<String, List<GoodCombination>> combinGoodsMap = combinations.stream().collect(Collectors.groupingBy(GoodCombination::getSubGoodSku));
            combinGoodsMap.forEach((k, v) -> {
                GoodsStockDto goodsStockDto = new GoodsStockDto();
                goodsStockDto.setSku(k);
                if (v.size() > 0) {
                    List<Integer> numList = Lists.newArrayListWithCapacity(v.size());
                    v.forEach(c -> {
                        GoodsStockDto stockDto = stockDtos.stream().filter(s -> s.getSku().equals(c.getSubGoodSku()) && s.getStocknum() > 0).findAny().orElse(new GoodsStockDto(c.getSubGoodSku(), 0));
                        numList.add(stockDto.getStocknum() / c.getSubGoodNum());
                    });
                    OptionalInt minVal = numList.stream().filter(n -> n.intValue() >= 0).mapToInt(Integer::intValue).min();
                    if (minVal.isPresent()) {
                        goodsStockDto.setStocknum(minVal.getAsInt());
                    } else {
                        goodsStockDto.setStocknum(0);
                    }
                } else {
                    goodsStockDto.setStocknum(0);
                }
                combineStocks.add(goodsStockDto);
            });

        } catch (Exception e) {
            logger.info("查询组合商品异常:" + e);
        }
        return combineStocks;
    }


    /**
     * 获取系统内的所有的商品信息
     *
     * @param
     * @return
     */
    private List<GoodsStockDto> convertSkus(List<String> skus) {
        List<GoodsStockDto> goodsStockDtos = Lists.newArrayList();
        List<String> skuList = skus.stream().filter(sku -> org.apache.commons.lang.StringUtils.isNotBlank(sku)).collect(Collectors.toList());
        List<String> skuTemp = Lists.newArrayList();
        for (int i = 0; i < skuList.size(); i++) {
            skuTemp.add(skuList.get(i));
            if (skuTemp.size() % 50 == 0) {
                goodsStockDtos.addAll(getGoodJstStock(skuTemp));
                skuTemp.clear();
            }
        }
        if (skuTemp.size() > 0) {
            goodsStockDtos.addAll(getGoodJstStock(skuTemp));
            skuTemp.clear();
        }
        return goodsStockDtos;
    }


    /**
     * 获取聚水潭库存数
     *
     * @param skus
     * @return
     */
    public List<GoodsStockDto> getGoodJstStock(List<String> skus) {
        InventoryQueryVo inventoryQueryVo = new InventoryQueryVo();
        inventoryQueryVo.setSkus(skus);
        List<GoodsStockDto> goodsStockDtoList = new ArrayList<>();
        try {
            ReturnData<List<InventoryQueryDto>> returnData = junshuitanFeignClient.inventoryQuery(inventoryQueryVo);
            if (returnData != null && returnData.getData().size() > 0) {
                List<InventoryQueryDto> list = returnData.getData();
                logger.info("调用聚水潭库存返回的数据为:{}",list);
                for (int i = 0; i < list.size(); i++) {
                    GoodsStockDto goodsStockDto = new GoodsStockDto();
                    goodsStockDto.setSku(list.get(i).getSku());
                    goodsStockDto.setStocknum(list.get(i).getStockNum());
                    goodsStockDtoList.add(goodsStockDto);
                }

            }
        } catch (Exception e) {
            logger.info("查询聚水潭库存数量接口异常" + e);
        }

        return goodsStockDtoList;
    }


    private void abnormal(List<GoodsStockDto> stockDtos) {
        //去除异常订单库内的数量
        // todo 待提供
        stockDtos.stream().forEach(r -> {
            int ungroupedNum = 0; // 团占用库存
            int flashNum = 0; //秒杀活动占用的库存
            int barginNum = 0;//砍价订单占用库存
            logger.info("=> 同步聚水潭库存 sku:{},jstStock:{},ungroupedNum:{},flashNum:{},barginNum:{}", r.getSku(), r.getStocknum(), ungroupedNum, flashNum, barginNum);
            r.setStocknum(r.getStocknum() - ungroupedNum - flashNum - barginNum);
        });

    }

    private int inDB(List<GoodsStockDto> goodsStockDtos) {
        if (goodsStockDtos.size() == 0) return 0;
        //更新缓存
        updateAllGoodsSkuStockCache(goodsStockDtos);
        //更新数据库
        updateAllGoodsSkuStock(goodsStockDtos);
        return goodsStockDtos.size();
    }


    /**
     * 批量更新缓存
     *
     * @param goodsStockDtos
     */
    public void updateAllGoodsSkuStockCache(List<GoodsStockDto> goodsStockDtos) {
        Map<String, String> stockMap = Maps.newHashMapWithExpectedSize(goodsStockDtos.size());
        goodsStockDtos.stream().forEach(goodsStockDto -> {
            stockMap.put(GoodStockStatus.SKU_STOCK + goodsStockDto.getSku(), String.valueOf(goodsStockDto.getStocknum()));
        });
        goodsStockService.setStock(stockMap);
    }


    /**
     * 批量更新数据
     *
     * @param goodsStockDtos
     */
    private void updateAllGoodsSkuStock(List<GoodsStockDto> goodsStockDtos) {
        List<GoodWarehouse> list = new ArrayList<>();
        for (int i = 0; i < goodsStockDtos.size(); i++) {
            GoodWarehouse goodWarehouse = new GoodWarehouse();
            goodWarehouse.setGoodSku(goodsStockDtos.get(i).getSku());
            goodWarehouse.setWarehouseNum(goodsStockDtos.get(i).getStocknum());
            list.add(goodWarehouse);
        }
        try {
            goodFeignClient.updateBatchById(list);
        } catch (Exception e) {
            logger.info("批量更新sku数据异常:" + e);
        }


    }


}
