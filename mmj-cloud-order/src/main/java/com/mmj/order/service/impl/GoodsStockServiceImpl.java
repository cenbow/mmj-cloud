package com.mmj.order.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.google.common.collect.Lists;
import com.mmj.common.constants.ActiveGoodsConstants;
import com.mmj.common.model.ReturnData;
import com.mmj.order.common.feign.GoodFeignClient;
import com.mmj.order.common.model.dto.GoodCombination;
import com.mmj.order.common.model.dto.GoodSale;
import com.mmj.order.constant.GoodStockStatus;
import com.mmj.order.service.GoodsStockService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class GoodsStockServiceImpl implements GoodsStockService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final static String STOCK_KEY = GoodStockStatus.SKU_STOCK;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private GoodFeignClient goodFeignClient;


    private String getKey(String sku) {
        return STOCK_KEY + sku;
    }

    public String getCombineKey() {
        return GoodStockStatus.SKU_STOCK_COMBINE;
    }

    public String getSingleKey() {
        return GoodStockStatus.SKU_STOCK_SIGNLE;
    }


    /**
     * 设置库存
     *
     * @param sku
     * @param num
     * @return
     */
    @Override
    public boolean setStock(String sku, int num) {
        num = num < 0 ? 0 : num;
        logger.info("=> 设置库存 sku:{},num:{}", sku, num);
        redisTemplate.opsForValue().set(getKey(sku), String.valueOf(num));
        return true;
    }

    /**
     * 批量设置库存
     *
     * @param stockMap
     * @return
     */
    @Override
    public boolean setStock(Map<String, String> stockMap) {
        redisTemplate.opsForValue().multiSet(stockMap);
        return true;
    }

    /**
     * 得到库存
     *
     * @param sku
     * @return
     */
    @Override
    public int getStock(String sku) {
        String val = redisTemplate.opsForValue().get(getKey(sku));
        int n = 0;
        if (StringUtils.isBlank(val)) {
            GoodSale goodSale = new GoodSale();
            goodSale.setGoodSku(sku);
            ReturnData<Object> returnData = goodFeignClient.queryList(goodSale);
            if (returnData != null) {
                List<GoodSale> list = JSONArray.parseArray(JSON.toJSONString(returnData.getData()), GoodSale.class);
                if (list != null && list.size() > 0) {
                    GoodSale goodsSku = list.get(0);
                    if (Objects.nonNull(goodsSku)) {
                        n = goodsSku.getGoodNum();
                        logger.info("=> 获取库存 获取数据库库存 sku:{},num:{},cache:{}", sku, n, val);
                        setStock(sku, n);
                    }
                }
            }


        } else {
            n = Integer.parseInt(val);
        }
        logger.info("=> 获取库存 sku:{},num:{},original:{}", sku, n, val);
        return n;


    }


    /**
     * 通过skuId 得到对应Sku
     *
     * @param skuId
     * @return
     */
    private String getSku(Integer skuId) {

        GoodSale goodSale = new GoodSale();
        goodSale.setSaleId(skuId);
        ReturnData<Object> returnData = goodFeignClient.queryList(goodSale);
        if (returnData != null) {
            List<GoodSale> list = JSONArray.parseArray(JSON.toJSONString(returnData.getData()), GoodSale.class);
            if (list.size() <= 0 || list == null) {
                return "";
            } else {
                return list.get(0).getGoodSku();
            }
        } else {
            return "";
        }


    }


    /**
     * 判断是否还有num个库存
     *
     * @param sku
     * @param num
     * @return
     */
    @Override
    public boolean hasStock(String sku, int num) {
        int stoakNum = getStock(sku);
        logger.info("=> 判断库存 sku:{},stoakNum:{},num:{}", sku, stoakNum, num);
        return stoakNum >= num;
    }

    /**
     * 通过skuId 查询库存
     *
     * @param skuId
     * @param num
     * @return
     */
    @Override
    public boolean hasSkuIdStock(Integer skuId, int num) {
        return hasStock(getSku(skuId), num);
    }


    /**
     * 减库存
     *
     * @param orderNo
     * @param sku
     * @param num
     * @return
     */
    @Override
    public boolean decr(String orderNo, String sku, int num) {
        logger.info("=> 扣减库存 sku:{},orderNo:{},num:{}", sku, orderNo, num);
        boolean bool = hasOrderDecr(orderNo);
        if (bool) {
            logger.warn("=> 扣减库存 该订单已扣除过 sku:{},orderNo:{},num:{}", sku, orderNo, num);
            return false;
        }
        Map<Object, Object> map = existCombineGoods(sku);
        int stockNum;
        if (map != null && !map.isEmpty()) {
            logger.info("===> 扣减组合库存{}", JSON.toJSONString(map));
            //组合商品
            stockNum = decrCombine(orderNo, map, num);
        } else {
            stockNum = decrSku(orderNo, sku, num);
            if (stockNum >= 0 && existSingleGoods(sku)) {
                logger.info("=> 扣减库存，组合商品关联sku同步 sku:{}", sku);
                synCombineStockBySingle(sku);
            }
        }
        return stockNum >= 0 ? true : false;
    }


    /**
     * 通过skuId扣减库存
     *
     * @param orderNo
     * @param skuId
     * @param num
     * @return
     */
    @Override
    public boolean decrSkuId(String orderNo, Integer skuId, int num) {
        return decr(orderNo, getSku(skuId), num);
    }


    /**
     * 加上库存
     *
     * @param orderNo
     * @param sku
     * @param num
     * @return
     */
    @Override
    public boolean addStock(String orderNo, String sku, int num) {
        return decr(orderNo, sku, -num);
    }

    /**
     * 添加组合商品
     *
     * @param combineSku
     */
    @Override
    public void addCombineGoods(String combineSku) {
        Long n = redisTemplate.opsForSet().add(getCombineKey(), combineSku);
    }


    /**
     * 批量添加组合商品
     *
     * @param combineSku
     */
    @Override
    public void addCombineGoods(String... combineSku) {
        Long n = redisTemplate.opsForSet().add(getCombineKey(), combineSku);
    }


    /**
     * 判断是否组合商品
     *
     * @param goodSku
     * @return
     */
    @Override
    /*public boolean existCombineGoods(String combineSku) {
        String key = GoodStockStatus.SKU_STOCK_COMBINE + combineSku + "*";
        return redisTemplate.hasKey(key);

//        return redisTemplate.opsForSet().isMember(getCombineKey(), combineSku);

    }*/

    public Map<Object, Object> existCombineGoods(String goodSku){
        return redisTemplate.opsForHash().entries(ActiveGoodsConstants.SKU_STOCK_COMBINE + goodSku);
    }


    /**
     * 添加组合商品的关联sku
     *
     * @param sku
     * @param combineSku
     */
    @Override
    public void addCombineSingleGoods(String sku, String combineSku) {
        Long n = redisTemplate.opsForSet().add(getSingleKey(), sku);
    }


    /**
     * 批量添加组合商品的关联sku
     *
     * @param sku
     */
    @Override
    public void addCombineSingleGoods(String... sku) {

        Long n = redisTemplate.opsForSet().add(getSingleKey(), sku);
    }


    /**
     * 判断sku是否被组合商品关联
     *
     * @param sku
     * @return
     */
    @Override
    public boolean existSingleGoods(String sku) {
        String key = getCombineKey() + ".*";

        return redisTemplate.opsForHash().hasKey(key, sku);

    }


    /**
     * 根据sku同步所属组合商品的库存
     *
     * @param sku
     * @return
     */
    @Override
    public boolean synCombineStockBySingle(String sku) {
        GoodCombination goodCombination = new GoodCombination();
        goodCombination.setGoodSku(sku);
        try {
            ReturnData<List<GoodCombination>> returnData = goodFeignClient.queryList(goodCombination);
            if (returnData != null) {
                List<GoodCombination> list = returnData.getData();
                if (list.size() > 0 && list != null) {
                    list.stream().forEach(r -> {
                        synCombineStockBySku(r.getGoodSku());
                    });
                }
            }
        } catch (Exception e) {
            logger.info("查询组合商品报错:" + e);
        }
        return true;
    }

    /**
     * 同步组合商品库存
     *
     * @param sku
     * @return
     */
    @Override
    public boolean synCombineStockBySku(String sku) {

        GoodCombination goodCombination = new GoodCombination();
        goodCombination.setGoodSku(sku);

        ReturnData<List<GoodCombination>> returnData = goodFeignClient.queryList(goodCombination);
        if (returnData != null) {
            List<GoodCombination> combinGoods = returnData.getData();
            if (Objects.nonNull(combinGoods) && combinGoods.size() > 0) {
                List<Integer> numList = Lists.newArrayListWithCapacity(combinGoods.size());
                combinGoods.stream().forEach(c -> {
                    int stockNum = getStock(c.getSubGoodSku());
                    if (stockNum > 0) {
                        stockNum = stockNum / c.getSubGoodNum();
                    } else {
                        stockNum = 0;
                    }
                    numList.add(stockNum);
                });
                OptionalInt minVal = numList.stream().filter(n -> n.intValue() >= 0).mapToInt(Integer::intValue).min();
                int stockNum = 0;
                if (minVal.isPresent()) {
                    stockNum = minVal.getAsInt();
                }

                // 设置库存
                setStock(sku, stockNum);
                // 同步数据库
                GoodSale goodSale = new GoodSale();
                goodSale.setGoodSku(sku);
                goodSale.setGoodNum(stockNum);
                List<GoodSale> list = new ArrayList<>();
                list.add(goodSale);
                logger.info("当前商品的sku{}，库存数量为{}", sku, stockNum);
                try {
                    goodFeignClient.updateGoodNum(list);
                } catch (Exception e) {
                    logger.info("修改商品库存接口异常:" + e);
                }

                return true;
            }
        }
        return false;
    }


    /**
     * 通过skuId添加库存
     *
     * @param orderNo
     * @param skuId
     * @param num
     * @return
     */
    @Override
    public boolean addStockSkuId(String orderNo, Integer skuId, int num) {
        return addStock(orderNo, getSku(skuId), num);
    }


    /**
     * 添加2人团创建时记录
     *
     * @param sku
     * @param orderNo
     * @param num
     * @return
     */
    @Override
    public boolean addUngrouped(String sku, String orderNo, int num) {
        logger.info("=> 2人团创建团  sku:{},orderNo:{},num:{}", sku, orderNo, num);
        Map<Object, Object> map = existCombineGoods(sku);
        if (map != null && !map.isEmpty()) {//组合商品
            logger.info("=> 2人团创建团-组合商品  sku:{},orderNo:{},num:{}", sku, orderNo, num);
            return addCombineUngrouped(sku, orderNo, num);
        }
        return addSingleUngrouped(sku, orderNo, num);
    }

    /**
     * 2人团拼团成功后删除记录
     *
     * @param sku
     * @param orderNo
     * @param num
     * @return
     */
    @Override
    public boolean removeUngrouped(String sku, String orderNo, int num) {
        logger.info("=> 2人团已成团 sku:{},orderNo:{},num:{}", sku, orderNo, num);
        Map<Object, Object> map = existCombineGoods(sku);
        if (map != null && !map.isEmpty()) {//组合商品
            logger.info("=> 2人团已成团-组合商品 sku:{},orderNo:{},num:{}", sku, orderNo, num);
            return removeCombineUngrouped(sku, orderNo, num);
        }
        return removeSingleUngrouped(sku, orderNo, num);
    }

    /**
     * 2人团未成团购买数量
     *
     * @param sku
     * @return
     */
    @Override
    public int getUngroupedNum(String sku) {
        logger.info("=> 获取2人团未拼团sku数量 sku:{}", sku);
        String key = "STOCK:GROUP:" + sku;
        String val = redisTemplate.opsForValue().get(key);
        int n = 0;
        if (StringUtils.isNotBlank(val))
            n = Integer.parseInt(val);
        logger.info("=> 获取2人团未拼团sku数量 sku:{},size:{}", sku, n);
        return n < 0 ? 0 : n;
    }


    /**
     * 该订单是否有扣减库存
     *
     * @param orderNo
     * @return
     */
    private boolean hasOrderDecr(String orderNo) {
        return false;
    }


    /**
     * 组合商品扣减库存
     *
     * @param map
     * @param num
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public int decrCombine(String orderNo, Map<Object, Object> map, int num) {
        int stockNum = 1;
        /*GoodCombination goodCombination = new GoodCombination();
        goodCombination.setGoodSku(combineSku);
//        List<GoodCombination> combineGoods = goodFeignClient.queryList(goodCombination);
        ReturnData<List<GoodCombination>> returnData = goodFeignClient.queryList(goodCombination);
        if (returnData != null) {
            List<GoodCombination> combineGoods = returnData.getData();
            if (Objects.nonNull(combineGoods) && combineGoods.size() > 0) {
                stockNum = decrSku(orderNo, combineSku, num);
                if (stockNum < 0)
                    return stockNum;
                for (int i = 0; i < combineGoods.size(); i++) {
                    GoodCombination combine = combineGoods.get(i);
                    int qty = num * combine.getSubGoodNum();
                    int stock = decrSku(orderNo, combine.getSubGoodSku(), qty);
                    logger.info("=> 组合商品扣减库存 combineSku:{},num:{},sku:{},package:{},qty:{},stockNum:{}",
                            combine.getGoodSku(), num, combine.getSubGoodSku(), combine.getSubGoodNum(), qty, stock);
                }
            }
        }*/

        Iterator<Object> i = map.keySet().iterator();
        List<Map<String, Object>> list = new ArrayList<>();
        boolean flag = false;
        while (i.hasNext()) {
            String subGoodSku = String.valueOf(i.next());//单品sku
            Object subGoodNum = map.get(subGoodSku);
            Integer qty = num * Integer.valueOf(String.valueOf(subGoodNum));
            Integer stock = decrSku(orderNo, subGoodSku, qty);
            if (stock >= 0) {
                Map<String, Object> success = new HashMap<>();
                success.put("subGoodSku", subGoodSku);
                success.put("subGoodNum", qty);
                list.add(success);
            } else {
                flag = true;
                break;
            }
            logger.info("=> 组合商品扣减库存 num:{},sku:{},package:{},qty:{},stockNum:{}", num, subGoodSku, subGoodNum, qty, stock);
        }
        if (flag && !list.isEmpty()) {
            list.stream().forEach(m->{
                decrSku(orderNo, String.valueOf(m.get("subGoodSku")), -(Integer) m.get("subGoodNum"));
            });
            stockNum = -1;
        }

        return stockNum;
    }

    /**
     * 商品扣减库存
     *
     * @param sku
     * @param num
     * @return
     */
    private int decrSku(String orderNo, String sku, int num) {

        Long stockNum = redisTemplate.opsForValue().increment(getKey(sku), -(long) num);
        logger.info("=> 扣减库存 sku:{},orderNo:{},num:{},stockNum:{}", sku, orderNo, num, stockNum);
        if (stockNum.intValue() >= 0) {
            // 同步数据库
            logger.info("当前商品的sku为:{},库存数量为:{}", sku, stockNum);
            try {
                List<GoodSale> list = new ArrayList<>();
                GoodSale goodSale = new GoodSale();
                goodSale.setGoodSku(sku);
                goodSale.setGoodNum(stockNum.intValue());
                list.add(goodSale);
                goodFeignClient.updateGoodNum(list);
                logger.info("当前商品的sku为:{}库存数量为:{}，已经同步更新到数据库", sku, stockNum);
            } catch (Exception e) {
                logger.info("更新数据库sku异常:" + e);
            }


        } else {
            redisTemplate.opsForValue().increment(getKey(sku), num);
        }
        return stockNum.intValue();
    }

    /**
     * 2人团创建团时记录组合商品关联的单品商品数
     *
     * @param sku
     * @param orderNo
     * @param num
     * @return
     */
    public boolean addCombineUngrouped(String sku, String orderNo, int num) {
        GoodCombination goodCombination = new GoodCombination();
        goodCombination.setGoodSku(sku);
        ReturnData<List<GoodCombination>> returnData = goodFeignClient.queryList(goodCombination);
        if (returnData != null) {
            List<GoodCombination> combineGoods = returnData.getData();
            if (Objects.nonNull(combineGoods) && !combineGoods.isEmpty()) {
                combineGoods.stream().forEach(combine -> {
                    int qty = num * combine.getSubGoodNum();
                    addSingleUngrouped(combine.getSubGoodSku(), orderNo, qty);
                });
            }
        }
        return addSingleUngrouped(sku, orderNo, num);
    }


    /**
     * 2人团创建团时记录单品商品数
     *
     * @param sku
     * @param orderNo
     * @param num
     * @return
     */
    public boolean addSingleUngrouped(String sku, String orderNo, int num) {
        String key = "STOCK:GROUP:" + sku;
        long stockNum = redisTemplate.opsForValue().increment(key, num);
        logger.info("=> 2人团创建团 sku:{},orderNo:{},stockNum:{}", sku, orderNo, stockNum);
        return stockNum > 0 ? true : false;
    }

    /**
     * 2人团创建团时扣减组合商品关联的单品异常记录数
     *
     * @param sku
     * @param orderNo
     * @param num
     * @return
     */
    public boolean removeCombineUngrouped(String sku, String orderNo, int num) {
        GoodCombination goodCombination = new GoodCombination();
        goodCombination.setGoodSku(sku);
        ReturnData<List<GoodCombination>> returnData = goodFeignClient.queryList(goodCombination);
        if (returnData != null) {
            List<GoodCombination> combineGoods = returnData.getData();
            if (Objects.nonNull(combineGoods) && !combineGoods.isEmpty()) {
                combineGoods.stream().forEach(combine -> {
                    int qty = num * combine.getSubGoodNum();
                    removeSingleUngrouped(combine.getSubGoodSku(), orderNo, qty);
                });
            }
        }
        return removeSingleUngrouped(sku, orderNo, num);
    }


    /**
     * 2人团创建团时扣减单品异常记录数
     *
     * @param sku
     * @param orderNo
     * @param num
     * @return
     */
    public boolean removeSingleUngrouped(String sku, String orderNo, int num) {
        String key = "STOCK:GROUP:" + sku;
        Long stockNum = redisTemplate.opsForValue().increment(key, -num);
        logger.info("=> 2人团已成团 sku:{},orderNo:{},stockNum:{},num:{}", sku, orderNo, stockNum, num);
        return true;
    }


}
