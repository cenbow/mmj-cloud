package com.mmj.good.stock.service.impl;

import com.baomidou.mybatisplus.enums.SqlLike;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.mmj.common.constants.CommonConstant;
import com.mmj.common.exception.BusinessException;
import com.mmj.good.constants.GoodConstants;
import com.mmj.good.model.GoodCombination;
import com.mmj.good.model.GoodInfo;
import com.mmj.good.model.GoodSale;
import com.mmj.good.service.GoodCombinationService;
import com.mmj.good.service.GoodInfoService;
import com.mmj.good.stock.mapper.GoodStockMapper;
import com.mmj.good.stock.model.GoodStock;
import com.mmj.good.stock.service.GoodStockService;
import com.xiaoleilu.hutool.date.DateField;
import com.xiaoleilu.hutool.date.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * <p>
 * 库存记录表 服务实现类
 * </p>
 *
 * @author shenfuding
 * @since 2019-08-08
 */
@Service
public class GoodStockServiceImpl extends ServiceImpl<GoodStockMapper, GoodStock> implements GoodStockService {

    Logger logger = LoggerFactory.getLogger(GoodStockServiceImpl.class);

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private GoodInfoService goodInfoService;

    @Autowired
    private GoodCombinationService goodCombinationService;

    public void occupyToCache(List<GoodStock> goodStocks) {
        Map<String, Integer> map = new HashMap<>();
        boolean flag = false;
        String error = null;
        for (GoodStock goodStock : goodStocks) {
            logger.info("--------occupyToCache:{}_{}", goodStock.getGoodSku(), -goodStock.getGoodNum());
            try {
                GoodInfo goodInfo = goodInfoService.getBySku(goodStock.getGoodSku());
                if (goodInfo == null) {
                    logger.error("relieveToCache-商品不存在:{}", goodStock.getGoodSku());
                    break;
                }
                if (goodInfo.getVirtualFlag() == 0) {
                    if (goodInfo.getCombinaFlag() == 1) {
                        Map<Object, Object> entries = redisTemplate.opsForHash().entries(GoodConstants.SKU_STOCK_COMBINE + goodStock.getGoodSku());
                        //子商品sku
                        Set<Object> keys = entries.keySet();
                        if (keys != null && !keys.isEmpty()) {
                            Iterator<Object> iterator = keys.iterator();
                            while (iterator.hasNext()) {
                                //实际sku
                                String subGoodSku = String.valueOf(iterator.next());
                                if (subGoodSku != null && !"".equals(subGoodSku)) {
                                    Integer num = (Integer) entries.get(subGoodSku);//包裹数
                                    Integer goodNum = goodStock.getGoodNum();//购买数量-
                                    Integer occupyNum = num * goodNum; //占用库存数量 -
                                    //扣减库存
                                    Object o = redisTemplate.opsForValue().get(GoodConstants.SKU_STOCK + subGoodSku);
                                    logger.info("--------occupyToCache-c:包裹数{}_购买数量{}_占用库存数量{}_库存数量{}", num, goodNum, -occupyNum, o);
                                    if (o != null && !"".equals(o)) {
                                        Long used = redisTemplate.opsForValue().increment(CommonConstant.GOOD_STOCK_OCCUPY + subGoodSku, -occupyNum);
                                        map.put(goodStock.getGoodSku(), occupyNum);
                                        Long stockNum = Long.valueOf(String.valueOf(o));
                                        if (used.compareTo(stockNum) > 0) {
                                            flag = true;
                                            error = goodStock.getGoodSku();
                                            logger.error("--------occupyToCache1库存不足:{}_{}", goodStock.getGoodSku(), used);
                                            break;
                                        }
                                    } else {
                                        flag = true;
                                        error = goodStock.getGoodSku();
                                        logger.error("--------occupyToCache1:{}信息异常", goodStock.getGoodSku());
                                        break;
                                    }
                                }
                            }
                            if (flag) {
                                break;
                            }
                        }
                    } else {
                        Object o = redisTemplate.opsForValue().get(GoodConstants.SKU_STOCK + goodStock.getGoodSku());
                        if (o != null && !"".equals(o)) {
                            logger.info("--------occupyToCache_detail:{}_{}_{}", goodStock.getGoodSku(), o, -goodStock.getGoodNum());
                            Long used = redisTemplate.opsForValue().increment(CommonConstant.GOOD_STOCK_OCCUPY + goodStock.getGoodSku(), -goodStock.getGoodNum());
                            map.put(goodStock.getGoodSku(), goodStock.getGoodNum());
                            Long num = Long.valueOf(String.valueOf(o));
                            if (used.compareTo(num) > 0) {
                                flag = true;
                                error = goodStock.getGoodSku();
                                logger.error("--------occupyToCache2库存不足:{}_{}", goodStock.getGoodSku(), used);
                                break;
                            }
                        } else {
                            flag = true;
                            error = goodStock.getGoodSku();
                            logger.error("--------occupyToCache2:{}信息异常", goodStock.getGoodSku());
                            break;
                        }
                    }
                }
            } catch (Exception e) {
                flag = true;
                error = goodStock.getGoodSku();
                logger.error(e.getMessage(), e);
            }
        }
        if (flag) {
            if (map != null && !map.isEmpty()) {
                Set<String> key = map.keySet();
                Iterator<String> i = key.iterator();
                while (i.hasNext()) {
                    logger.error("--------occupyToCache库存不足-退回已占用:{}_{}", i, map.get(i));
                    redisTemplate.opsForValue().increment(CommonConstant.GOOD_STOCK_OCCUPY + i, map.get(i));
                }
            }
            throw new BusinessException(error + "库存不足！");
        } else {
            redisTemplate.opsForValue().set(CommonConstant.GOOD_STOCK_OCCUPY_BUSINESS + goodStocks.get(0).getBusinessId(), goodStocks.get(0).getExpireTime().getTime(), goodStocks.get(0).getExpireTime().getTime() - new Date().getTime(), TimeUnit.MILLISECONDS);
        }
    }

    public void relieveToCache(List<GoodStock> goodStocks) {
        goodStocks.stream().forEach(goodStock -> {
            logger.info("--------relieveToCache:{}_{}", goodStock.getGoodSku(), -goodStock.getGoodNum());
            try {
                GoodInfo goodInfo = goodInfoService.getBySku(goodStock.getGoodSku());
                if (goodInfo == null) {
                    logger.error("relieveToCache-商品不存在:{}", goodStock.getGoodSku());
                } else {
                    if (goodInfo.getVirtualFlag() == 0) {
                        if (goodInfo.getCombinaFlag() == 1) {
                            Map<Object, Object> entries = redisTemplate.opsForHash().entries(GoodConstants.SKU_STOCK_COMBINE + goodStock.getGoodSku());
                            //子商品sku
                            Set<Object> keys = entries.keySet();
                            if (keys != null && !keys.isEmpty()) {
                                Iterator<Object> iterator = keys.iterator();
                                while (iterator.hasNext()) {
                                    try {
                                        //实际sku
                                        String subGoodSku = String.valueOf(iterator.next());
                                        if (subGoodSku != null && !"".equals(subGoodSku)) {
                                            Integer num = (Integer) entries.get(subGoodSku);//包裹数
                                            Integer goodNum = goodStock.getGoodNum();//释放数量-
                                            Integer relieveNum = num * goodNum; //释放库存数量 -
                                            logger.info("--------relieveToCache-c:包裹数{}_释放数量{}_释放库存数量{}", num, goodNum, -relieveNum);
                                            //释放库存
                                            redisTemplate.opsForValue().increment(CommonConstant.GOOD_STOCK_OCCUPY + subGoodSku, -relieveNum);
                                        }
                                    } catch (Exception e) {
                                        logger.error(e.getMessage(), e);
                                    }
                                }
                            }
                        } else {
                            redisTemplate.opsForValue().increment(CommonConstant.GOOD_STOCK_OCCUPY + goodStock.getGoodSku(), -goodStock.getGoodNum());
                        }
                    }
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        });
    }

    public void deductToCache(List<GoodStock> goodStocks) {
        goodStocks.stream().forEach(goodStock -> {
            logger.info("--------deductToCache:{}_{}", goodStock.getGoodSku(), -goodStock.getGoodNum());
            try {
                GoodInfo goodInfo = goodInfoService.getBySku(goodStock.getGoodSku());
                if (goodInfo == null) {
                    logger.error("deductToCache-商品不存在:{}", goodStock.getGoodSku());

                } else {
                    if (goodInfo.getVirtualFlag() == 0) {
                        if (goodInfo.getCombinaFlag() == 1) {
                            Map<Object, Object> entries = redisTemplate.opsForHash().entries(GoodConstants.SKU_STOCK_COMBINE + goodStock.getGoodSku());
                            //子商品sku
                            Set<Object> keys = entries.keySet();
                            if (keys != null && !keys.isEmpty()) {
                                Iterator<Object> iterator = keys.iterator();
                                while (iterator.hasNext()) {
                                    try {
                                        //实际sku
                                        String subGoodSku = String.valueOf(iterator.next());
                                        if (subGoodSku != null && !"".equals(subGoodSku)) {
                                            Integer num = (Integer) entries.get(subGoodSku);//包裹数
                                            Integer goodNum = goodStock.getGoodNum();//扣减数量-
                                            Integer deductNum = num * goodNum; //扣减库存数量 -
                                            logger.info("--------deductToCache-c:包裹数{}_扣减数量{}_扣减库存数量{}", num, goodNum, -deductNum);
                                            //扣减库存
                                            redisTemplate.opsForValue().increment(CommonConstant.GOOD_STOCK_DEDUCT + subGoodSku, -deductNum);
                                        }
                                    } catch (Exception e) {
                                        logger.error(e.getMessage(), e);
                                    }
                                }
                            }
                        } else {
                            redisTemplate.opsForValue().increment(CommonConstant.GOOD_STOCK_DEDUCT + goodStock.getGoodSku(), -goodStock.getGoodNum());
                        }
                    }
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        });
    }

    public void rollbackToCache(List<GoodStock> goodStocks) {
        goodStocks.stream().forEach(goodStock -> {
            logger.info("--------rollbackToCache:{}_{}", goodStock.getGoodSku(), -goodStock.getGoodNum());
            try {
                GoodInfo goodInfo = goodInfoService.getBySku(goodStock.getGoodSku());
                if (goodInfo == null) {
                    logger.error("rollbackToCache-商品不存在:{}", goodStock.getGoodSku());

                } else {
                    if (goodInfo.getVirtualFlag() == 0) {
                        if (goodInfo.getCombinaFlag() == 1) {
                            Map<Object, Object> entries = redisTemplate.opsForHash().entries(GoodConstants.SKU_STOCK_COMBINE + goodStock.getGoodSku());
                            //子商品sku
                            Set<Object> keys = entries.keySet();
                            if (keys != null && !keys.isEmpty()) {
                                Iterator<Object> iterator = keys.iterator();
                                while (iterator.hasNext()) {
                                    try {
                                        //实际sku
                                        String subGoodSku = String.valueOf(iterator.next());
                                        if (subGoodSku != null && !"".equals(subGoodSku)) {
                                            Integer num = (Integer) entries.get(subGoodSku);//包裹数
                                            Integer goodNum = goodStock.getGoodNum();//扣减数量-
                                            Integer rollbackNum = num * goodNum; //回滚库存数量 -
                                            logger.info("--------rollbackToCache-c:包裹数{}_扣减数量{}_回滚库存数量{}", num, goodNum, -rollbackNum);
                                            //回滚库存
                                            redisTemplate.opsForValue().increment(CommonConstant.GOOD_STOCK_OCCUPY + subGoodSku, -rollbackNum);
                                        }
                                    } catch (Exception e) {
                                        logger.error(e.getMessage(), e);
                                    }
                                }
                            }
                        } else {
                            redisTemplate.opsForValue().increment(CommonConstant.GOOD_STOCK_OCCUPY + goodStock.getGoodSku(), -goodStock.getGoodNum());
                        }
                    }
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        });
    }

    public void timeOutToCache(List<GoodStock> goodStocks) {
        goodStocks.stream().forEach(goodStock -> {
            logger.info("--------timeOutToCache:{}_{}", goodStock.getGoodSku(), goodStock.getGoodNum());
            try {
                GoodInfo goodInfo = goodInfoService.getBySku(goodStock.getGoodSku());
                if (goodInfo == null) {
                    logger.error("timeOutToCache-商品不存在:{}", goodStock.getGoodSku());
                } else {
                    if (goodInfo.getVirtualFlag() == 0) {
                        EntityWrapper<GoodStock> entityWrapper = new EntityWrapper<>();
                        entityWrapper.eq("BUSINESS_ID", goodStock.getBusinessId());
                        entityWrapper.eq("GOOD_SKU", goodStock.getGoodSku());
                        entityWrapper.ne("STATUS", CommonConstant.GoodStockStatus.OCCUPY);
                        entityWrapper.ne("STATUS", CommonConstant.GoodStockStatus.DEDUCT);
                        entityWrapper.ne("STATUS", CommonConstant.GoodStockStatus.EXPIRE);
                        List<GoodStock> others = selectList(entityWrapper);
                        Integer goodNum = goodStock.getGoodNum();
                        if (others != null && !others.isEmpty()) {
                            for (GoodStock gs : others) {
                                goodNum = goodNum + gs.getGoodNum();
                            }
                        }
                        if (goodNum != 0) {
                            if (goodInfo.getCombinaFlag() == 1) {
                                Map<Object, Object> entries = redisTemplate.opsForHash().entries(GoodConstants.SKU_STOCK_COMBINE + goodStock.getGoodSku());
                                //子商品sku
                                Set<Object> keys = entries.keySet();
                                if (keys != null && !keys.isEmpty()) {
                                    Iterator<Object> iterator = keys.iterator();
                                    while (iterator.hasNext()) {
                                        try {
                                            //实际sku
                                            String subGoodSku = String.valueOf(iterator.next());
                                            if (subGoodSku != null && !"".equals(subGoodSku)) {
                                                Integer num = (Integer) entries.get(subGoodSku);//包裹数
                                                Integer timeOutNum = num * goodNum; //过期库存数量 -
                                                logger.info("--------timeOutToCache-c:包裹数{}_扣减数量{}_过期库存数量{}", num, goodNum, timeOutNum);
                                                //过期库存
                                                redisTemplate.opsForValue().increment(CommonConstant.GOOD_STOCK_OCCUPY + subGoodSku, timeOutNum);
                                            }
                                        } catch (Exception e) {
                                            logger.error(e.getMessage(), e);
                                        }
                                    }
                                }
                            } else {
                                redisTemplate.opsForValue().increment(CommonConstant.GOOD_STOCK_OCCUPY + goodStock.getGoodSku(), goodNum);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        });
    }

    //更新库存
    public void refreshOccupy(List<GoodSale> goodSales) {
        goodSales.stream().forEach(goodSale -> {
            refreshOccupy(goodSale.getGoodSku(), goodSale.getGoodNum());
        });
    }

    public void refreshOccupy(String goodSku, Integer goodNum) {
        logger.info("--------refreshOccupy:{}_{}", goodSku, goodNum);
        //聚水潭库存
        redisTemplate.opsForValue().set(GoodConstants.SKU_STOCK + goodSku, goodNum);
        //重置扣减量 - 暂时关闭
//        Object deduct = redisTemplate.opsForValue().getAndSet(CommonConstant.GOOD_STOCK_DEDUCT + goodSku, 0);
//        logger.info("--------refreshOccupy:deduct_{}", deduct);
//        //更新库存占用量 占用量-扣减量
//        if (deduct != null && !"".equals(deduct) && (Integer) deduct != 0  && goodNum > 0) {
//            redisTemplate.opsForValue().increment(CommonConstant.GOOD_STOCK_OCCUPY + goodSku, -(Integer) deduct);
//        }
    }

    //更新组合库存
    public void refreshZhOccupy(List<GoodCombination> goodCombinations) {
        goodCombinations.stream().forEach(goodCombination -> {
            refreshZhOccupy(goodCombination.getSubGoodSku(), goodCombination.getSubGoodNum());
        });
    }

    public void refreshZhOccupy(String goodSku, Integer goodNum) {
        logger.info("--------refreshZhOccupy:{}_{}", goodSku, goodNum);
        //聚水潭库存
        redisTemplate.opsForValue().set(GoodConstants.SKU_STOCK + goodSku, goodNum);
        //重置扣减量 - 暂时关闭
//        Object deduct = redisTemplate.opsForValue().getAndSet(CommonConstant.GOOD_STOCK_DEDUCT + goodSku, 0);
//        logger.info("--------refreshZhOccupy:deduct_{}", deduct);
//        //更新库存占用量 占用量-扣减量
//        if (deduct != null && !"".equals(deduct) && (Integer) deduct != 0 && goodNum > 0) {
//            redisTemplate.opsForValue().increment(CommonConstant.GOOD_STOCK_OCCUPY + goodSku, -(Integer) deduct);
//        }
    }

    public void initToCache(List<GoodStock> goodStocks) {
        goodStocks.stream().forEach(goodStock -> {
            redisTemplate.opsForValue().increment(CommonConstant.GOOD_STOCK_OCCUPY + goodStock.getGoodSku(), 0);
        });
    }

    public void cleanExpire() {
        long date = DateUtil.date().setField(DateField.SECOND, 0).setField(DateField.MILLISECOND, 0).getTime();
        EntityWrapper<GoodStock> entityWrapper = new EntityWrapper<>();
        //entityWrapper.ge("EXPIRE_TIME", new Date(date - 1 * 60 * 1000));
        entityWrapper.lt("EXPIRE_TIME", new Date(date));
        entityWrapper.eq("STATUS", CommonConstant.GoodStockStatus.OCCUPY);
        GoodStock goodStock = new GoodStock();
        goodStock.setStatus(CommonConstant.GoodStockStatus.EXPIRE);
        List<GoodStock> goodStocks = selectList(entityWrapper);
        if (goodStocks != null && !goodStocks.isEmpty()) {
            //修改为过期
            update(goodStock, entityWrapper);
            //释放占用
            timeOutToCache(goodStocks);
        }
    }

    public boolean checkOccupyTime(String businessId) {
        logger.info("--------checkOccupyTime:{}", businessId);
        Object o = redisTemplate.opsForValue().get(CommonConstant.GOOD_STOCK_OCCUPY_BUSINESS + businessId);
        if (o != null && !"".equals(o)) {
            Long time = Long.valueOf(String.valueOf(o));
            Long now = new Date().getTime();
            Long ex = time - now;
            if (ex >= 2000) {
                if (ex <= 120000) {
                    Long newEx = now + 120000;
                    EntityWrapper<GoodStock> entityWrapper = new EntityWrapper<>();
                    entityWrapper.eq("STATUS", CommonConstant.GoodStockStatus.OCCUPY);
                    entityWrapper.eq("BUSINESS_ID", businessId);
                    GoodStock goodStock = new GoodStock();
                    goodStock.setExpireTime(new Date(newEx));
                    update(goodStock, entityWrapper);
                    redisTemplate.opsForValue().set(CommonConstant.GOOD_STOCK_OCCUPY_BUSINESS + businessId, newEx, newEx, TimeUnit.MILLISECONDS);
                }
                return true;
            }
        }
        return false;
    }

    public void updateBatch(List<GoodStock> goodStocks) {
        for (GoodStock gs : goodStocks) {
            try {
                GoodInfo goodInfo = goodInfoService.getBySku(gs.getGoodSku());
                if (goodInfo == null) {
                    logger.error("updateBatch-商品不存在:{}", gs.getGoodSku());
                } else {
                    if (goodInfo.getVirtualFlag() == 0) {
                        GoodStock goodStock = new GoodStock();
                        goodStock.setStatus(CommonConstant.GoodStockStatus.DEDUCT);
                        EntityWrapper<GoodStock> entityWrapper = new EntityWrapper<>();
                        entityWrapper.eq("BUSINESS_ID", gs.getBusinessId());
                        entityWrapper.eq("GOOD_SKU", gs.getGoodSku());
                        entityWrapper.eq("STATUS", CommonConstant.GoodStockStatus.OCCUPY);
                        boolean update = update(goodStock, entityWrapper);
                        if (!update) {
                            if (goodInfo.getCombinaFlag() == 1) {
                                Map<Object, Object> entries = redisTemplate.opsForHash().entries(GoodConstants.SKU_STOCK_COMBINE + goodStock.getGoodSku());
                                //子商品sku
                                Set<Object> keys = entries.keySet();
                                if (keys != null && !keys.isEmpty()) {
                                    Iterator<Object> iterator = keys.iterator();
                                    while (iterator.hasNext()) {
                                        try {
                                            //实际sku
                                            String subGoodSku = String.valueOf(iterator.next());
                                            if (subGoodSku != null && !"".equals(subGoodSku)) {
                                                Integer num = (Integer) entries.get(subGoodSku);//包裹数
                                                Integer goodNum = gs.getGoodNum(); //扣减数量-
                                                Integer deductNum = num * goodNum; //过期库存数量 -
                                                logger.info("--------updateBatch-c:包裹数{}_扣减数量{}_过期库存数量{}", num, goodNum, -deductNum);
                                                //过期库存
                                                redisTemplate.opsForValue().increment(CommonConstant.GOOD_STOCK_OCCUPY + subGoodSku, -deductNum);
                                            }
                                        } catch (Exception e) {
                                            logger.error(e.getMessage(), e);
                                        }
                                    }
                                }
                            } else {
                                redisTemplate.opsForValue().increment(CommonConstant.GOOD_STOCK_OCCUPY + gs.getGoodSku(), -gs.getGoodNum());
                            }
                            insert(gs);
                        }
                    }
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    public void asyncStock() {
        //查询库存记录
        EntityWrapper<GoodStock> stockEntityWrapper = new EntityWrapper<>();
        stockEntityWrapper.ne("STATUS", 5);
        int count = selectCount(stockEntityWrapper);
        int num = count / 1000;
        if (num * 1000 < count) {
            num++;
        }
        for (int i = 1; i <= num; i++) {
            Page<GoodStock> page = new Page<>(i, 1000);
            Page<GoodStock> goodStockPage = selectPage(page, stockEntityWrapper);
            if (goodStockPage != null && goodStockPage.getRecords() != null && !goodStockPage.getRecords().isEmpty()) {
                for (GoodStock goodStock : goodStockPage.getRecords()) {
                    asyncStock(goodStock);
                }
            }
        }
    }

    public void asyncStock(GoodStock goodStock) {
        if (!goodCombinationService.isCombination(goodStock.getGoodSku())) {
            try {
                logger.info("---------synStock:{}_{}_{}", goodStock.getGoodSku(), -goodStock.getGoodNum(), goodStock.getStatus());
                redisTemplate.opsForValue().increment(CommonConstant.GOOD_STOCK_OCCUPY + goodStock.getGoodSku(), -goodStock.getGoodNum());
                redisTemplate.opsForValue().increment(CommonConstant.GOOD_STOCK_DEDUCT + goodStock.getGoodSku(), -goodStock.getGoodNum());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Map<Object, Object> entries = redisTemplate.opsForHash().entries(GoodConstants.SKU_STOCK_COMBINE + goodStock.getGoodSku());
            //子商品sku
            Set<Object> keys = entries.keySet();
            if (keys != null && !keys.isEmpty()) {
                Iterator<Object> iterator = keys.iterator();
                while (iterator.hasNext()) {
                    try {
                        //实际sku
                        String subGoodSku = String.valueOf(iterator.next());
                        if (subGoodSku != null && !"".equals(subGoodSku)) {
                            Integer bg = (Integer) entries.get(subGoodSku);//包裹数
                            Integer goodNum = goodStock.getGoodNum();//扣减数量-
                            Integer deductNum = bg * goodNum; //扣减库存数量 -
                            //扣减库存
                            logger.info("---------synStockZh:{}_{}_{}", goodStock.getGoodSku(), -deductNum, goodStock.getStatus());
                            redisTemplate.opsForValue().increment(CommonConstant.GOOD_STOCK_OCCUPY + subGoodSku, -deductNum);
                            redisTemplate.opsForValue().increment(CommonConstant.GOOD_STOCK_DEDUCT + subGoodSku, -deductNum);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void asyncStock(String flag) {
        GoodStock updateGoodStock = new GoodStock();
        updateGoodStock.setStatus(6);
        GoodStock queryGoodStock = new GoodStock();
        queryGoodStock.setStatus(2);
        EntityWrapper<GoodStock> entityWrapper = new EntityWrapper<>(queryGoodStock);
        entityWrapper.le("CREATER_TIME", new Date(System.currentTimeMillis() - 43200000));
        entityWrapper.like(true, "BUSINESS_TYPE", "order", SqlLike.RIGHT);
        boolean status = update(updateGoodStock, entityWrapper);
        if (status) {
            EntityWrapper<GoodStock> stockEntityWrapper = new EntityWrapper<>();
            stockEntityWrapper.setSqlSelect(" GOOD_SKU,SUM(GOOD_NUM) GOOD_NUM ");
            stockEntityWrapper.in("STATUS", Arrays.asList(1, 2, 3, 4));
            stockEntityWrapper.groupBy("GOOD_SKU");
            List<GoodStock> goodStocks = selectList(stockEntityWrapper);
            for (GoodStock goodStock : goodStocks) {
                asyncStock(goodStock);
            }
        }
    }
}
