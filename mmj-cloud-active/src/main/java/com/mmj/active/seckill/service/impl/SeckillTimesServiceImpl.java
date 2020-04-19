package com.mmj.active.seckill.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.mmj.active.common.feigin.GoodFeignClient;
import com.mmj.active.common.service.ActiveGoodService;
import com.mmj.active.seckill.mapper.SeckillTimesMapper;
import com.mmj.active.seckill.model.SeckillTimes;
import com.mmj.active.seckill.model.SeckillTimesEx;
import com.mmj.active.seckill.service.SeckillTimesService;
import com.mmj.common.constants.CommonConstant;
import com.mmj.common.exception.BusinessException;
import com.mmj.common.model.GoodStock;
import com.mmj.common.model.ReturnData;
import com.mmj.common.properties.SecurityConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * 秒杀期次表 服务实现类
 * </p>
 *
 * @author zhangyicao
 * @since 2019-06-13
 */
@Service
public class SeckillTimesServiceImpl extends ServiceImpl<SeckillTimesMapper, SeckillTimes> implements SeckillTimesService {

    Logger logger = LoggerFactory.getLogger(SeckillTimesServiceImpl.class);

    @Autowired
    private SeckillTimesService seckillTimesService;
    @Autowired
    private ActiveGoodService activeGoodService;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private SeckillTimesMapper seckillTimesMapper;
    @Autowired
    private GoodFeignClient goodFeignClient;

    public List<SeckillTimesEx> queryAndGood(Integer isActive, Integer seckillId, Integer seckillPriod, String times, Integer seckillType) {
        return seckillTimesMapper.queryAndGood(isActive, seckillId, seckillPriod, times, seckillType);
    }

//    @Transactional(rollbackFor = Exception.class)
//    public void deleteTimesGood(Integer goodId) {
//        EntityWrapper<ActiveGood> entityWrapper = new EntityWrapper<>();
//        entityWrapper.eq("ACTIVE_TYPE", ActiveGoodsConstants.ActiveType.SECKILL);
//        entityWrapper.eq("GOOD_ID", goodId);
//        List<ActiveGood> activeGoods = activeGoodService.selectList(entityWrapper);
//        if (activeGoods != null && !activeGoods.isEmpty()) {
//            boolean b = activeGoodService.delete(entityWrapper);
//            if (b) {
//                for (ActiveGood activeGood : activeGoods) {
//                    String baseKey = activeGood.getBusinessId() + ":" + activeGood.getGoodId() + ":" + activeGood.getGoodSku();
//                    String key = SeckillConstants.SECKILL_STORE + baseKey;
//                    //获取剩余库存
//                    Object o = redisTemplate.opsForValue().getAndSet(key, 0);
//                    Integer store = 0;
//                    if (o != null && !o.equals("")) {
//                        if ((Integer) o == 0) {
//                            return;
//                        }
//                        store = (Integer) o;
//                    } else {
//                        return;
//                    }
//                    try {
//                        //归还剩余库存 TODO 放到MQ
//                        if (store != null && store != 0) {
//                            seckillTimesService.decrStore(activeGood.getGoodSku(), store.intValue(), activeGood.getBusinessId());
//                        }
//                    } catch (Exception e) {
//                        //记录调用失败信息
//                        redisTemplate.opsForValue().set(SeckillConstants.SECKILL_STORE_FAIL + baseKey, store);
//                        logger.error("秒杀归还剩余库存失败：{}:{}_{}_{}", SeckillConstants.SECKILL_STORE_FAIL + activeGood.getBusinessId(), activeGood.getGoodId(), activeGood.getGoodSku(), store);
//                    }
//                    //删除活动库存缓存
//                    redisTemplate.delete(key);
//                    //删除活动虚拟库存缓存
//                    redisTemplate.delete(SeckillConstants.SECKILL_VIRTUAL_STORE + baseKey);
//                }
//            } else {
//                throw new BusinessException("秒杀商品删除失败!");
//            }
//        } else {
//            throw new BusinessException("秒杀商品不存在！");
//        }
//    }

//    @Transactional(rollbackFor = Exception.class)
//    public void deleteTimes(Integer timesId) {
//        //删除期次
//        boolean b = deleteById(timesId);
//        if (!b) {
//            throw new BusinessException("删除失败！");
//        }
//        //删除商品
//        EntityWrapper<ActiveGood> entityWrapper = new EntityWrapper<>();
//        entityWrapper.eq("ACTIVE_TYPE", ActiveGoodsConstants.ActiveType.SECKILL);
//        entityWrapper.in("BUSINESS_ID", String.valueOf(timesId));
//        List<ActiveGood> activeGoods = activeGoodService.selectList(entityWrapper);
//        boolean delete = activeGoodService.delete(entityWrapper);
//        if (!delete) {
//            throw new BusinessException("档期商品删除失败！");
//        }
//        //解除库存占用
//        if (activeGoods != null && !activeGoods.isEmpty()) {
//            for (ActiveGood activeGood : activeGoods) {
//                String baseKey = activeGood.getBusinessId() + ":" + activeGood.getGoodId() + ":" + activeGood.getGoodSku();
//                String key = SeckillConstants.SECKILL_STORE + baseKey;
//                Object o = redisTemplate.opsForValue().getAndSet(key, 0);
//                Integer store = 0;
//                if (o != null && !o.equals("")) {
//                    if ((Integer) o == 0) {
//                        continue;
//                    }
//                    store = (Integer) o;
//                } else {
//                    continue;
//                }
//                try {
//                    //归还剩余库存 TODO 放到MQ
//                    if (store != null && store != 0) {
//                        decrStore(activeGood.getGoodSku(), store.intValue(), activeGood.getBusinessId());
//                    }
//                } catch (Exception e) {
//                    //记录调用失败信息
//                    redisTemplate.opsForValue().set(SeckillConstants.SECKILL_STORE_FAIL + baseKey, store);
//                    logger.error("秒杀归还剩余库存失败：{}:{}_{}_{}", SeckillConstants.SECKILL_STORE_FAIL + baseKey, store);
//                    continue;
//                }
//                //删除活动库存缓存
//                redisTemplate.delete(key);
//                //删除活动虚拟库存缓存
//                redisTemplate.delete(SeckillConstants.SECKILL_VIRTUAL_STORE + baseKey);
//            }
//        }
//    }

    public void decrStore(String sku, Integer num, Integer businessId) {
        GoodStock goodStock = new GoodStock();
        goodStock.setGoodSku(sku);
        goodStock.setGoodNum(num);
        goodStock.setStatus(CommonConstant.GoodStockStatus.RELIEVE);
        goodStock.setBusinessId(String.valueOf(businessId));
        goodStock.setBusinessType("ACTIVE-SECKILL-OUT");
        ReturnData resultData = goodFeignClient.relieve(Arrays.asList(goodStock));
        if (resultData == null || resultData.getCode() != SecurityConstants.SUCCESS_CODE) {
            throw new BusinessException(resultData.getDesc());
        }
    }

}
