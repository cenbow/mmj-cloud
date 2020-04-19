package com.mmj.active.seckill.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.mmj.active.common.constants.ActiveGoodsConstants;
import com.mmj.active.common.feigin.GoodFeignClient;
import com.mmj.active.common.model.ActiveGood;
import com.mmj.active.common.model.ActiveGoodEx;
import com.mmj.active.common.model.SMSInfoDto;
import com.mmj.active.common.service.ActiveGoodService;
import com.mmj.active.seckill.constants.SeckillConstants;
import com.mmj.active.seckill.mapper.SeckillInfoMapper;
import com.mmj.active.seckill.model.*;
import com.mmj.active.seckill.service.SeckillInfoService;
import com.mmj.active.seckill.service.SeckillTimesService;
import com.mmj.common.constants.CommonConstant;
import com.mmj.common.constants.MQCommonTopic;
import com.mmj.common.constants.MQTopicConstant;
import com.mmj.common.exception.BusinessException;
import com.mmj.common.model.*;
import com.mmj.common.model.order.OrderStore;
import com.mmj.common.properties.SecurityConstants;
import com.mmj.common.utils.DateUtils;
import com.mmj.common.utils.OrderStoreUtils;
import com.mmj.common.utils.SecurityUserUtil;
import com.mmj.common.utils.SnowflakeIdWorker;
import com.xiaoleilu.hutool.date.DateField;
import com.xiaoleilu.hutool.date.DateTime;
import com.xiaoleilu.hutool.date.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * <p>
 * 秒杀信息表 服务实现类
 * </p>
 *
 * @author zhangyicao
 * @since 2019-06-13
 */
@Service
public class SeckillInfoServiceImpl extends ServiceImpl<SeckillInfoMapper, SeckillInfo> implements SeckillInfoService {

    Logger logger = LoggerFactory.getLogger(SeckillInfoServiceImpl.class);

    @Autowired
    private SeckillInfoMapper seckillInfoMapper;
    @Autowired
    private SeckillTimesService seckillTimesService;
    @Autowired
    private ActiveGoodService activeGoodService;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private GoodFeignClient goodFeignClient;
    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;
    @Autowired
    private OrderStoreUtils orderStoreUtils;
    @Autowired
    private SnowflakeIdWorker snowflakeIdWorker;

    @Transactional(rollbackFor = Exception.class)
    public void save(SeckillInfoEx entityEx) {
        //插入/修改info表
        SeckillInfo seckillInfo = JSON.parseObject(JSON.toJSONString(entityEx), SeckillInfo.class);
        List<SeckillTimesEx> seckillTimesExes = entityEx.getSeckillTimesExes();
        JwtUserDetails userDetails = SecurityUserUtil.getUserDetails();
        Integer seckillType = seckillInfo.getSeckillType();
        List<Map<String, Object>> store = new ArrayList<>();
        DateTime date = DateUtil.date();
        boolean flag = true; //操作类型 新增
        if (seckillInfo.getSeckillId() != null) {
            seckillInfo.setModifyId(userDetails.getUserId());
            seckillInfo.setModifyTime(date);
            flag = false;
        } else {
            seckillInfo.setCreaterId(userDetails.getUserId());
        }
        //新增或更新活动信息
        insertOrUpdate(seckillInfo);
        if(seckillType == SeckillConstants.SECKILL_TYPE_1) {
            redisTemplate.opsForValue().set(SeckillConstants.SECKILL_LIMIT_IN, seckillInfo.getEveryoneLimit());
        }

        //删除期次
        if (!flag) {
            EntityWrapper<SeckillTimes> entityWrapper = new EntityWrapper<>();
            entityWrapper.eq("SECKILL_ID", seckillInfo.getSeckillId());
            if(seckillType == SeckillConstants.SECKILL_TYPE_1) {
                entityWrapper.eq("IS_ACTIVE", SeckillConstants.SeckillTimesActive.NO);
            }
            List<SeckillTimes> seckillTimes = seckillTimesService.selectList(entityWrapper);
            seckillTimesService.delete(entityWrapper);
            //删除活动商品
            deleteGood(seckillTimes.stream().map(SeckillTimes::getTimesId).collect(Collectors.toList()), seckillTimesExes, seckillType);
        }
        //插入/修改time表
        List<ActiveGood> activeGoodBatch = new ArrayList<>();
        Set<Integer> set = new HashSet<>();
        if (seckillTimesExes != null && !seckillTimesExes.isEmpty()) {
            for (SeckillTimesEx seckillTimesEx : seckillTimesExes) {
                SeckillTimes seckillTimes = JSON.parseObject(JSON.toJSONString(seckillTimesEx), SeckillTimes.class);
                seckillTimes.setSeckillId(seckillInfo.getSeckillId());
                if (seckillInfo.getSeckillType() == SeckillConstants.SECKILL_TYPE_2) {
                    seckillTimes.setIsActive(1);
                }
                set.add(seckillTimesEx.getSeckillPriod());
                seckillTimesService.insertOrUpdate(seckillTimes);
                if(seckillType == SeckillConstants.SECKILL_TYPE_2) {
                    redisTemplate.opsForValue().set(SeckillConstants.SECKILL_LIMIT_OUT + seckillTimes.getTimesId(), seckillInfo.getEveryoneLimit(), 30, TimeUnit.DAYS);
                }
                List<ActiveGoodEx> activeGoods = seckillTimesEx.getActiveGoodExes();
                if (activeGoods != null && !activeGoods.isEmpty()) {
                    for (ActiveGoodEx activeGoodEx : activeGoods) {
                        activeGoodEx.setActiveType(ActiveGoodsConstants.ActiveType.SECKILL);
                        activeGoodEx.setGoodLimit(0);
                        activeGoodEx.setBusinessId(seckillTimes.getTimesId());
                        activeGoodEx.setGoodStatus("1");
                        activeGoodBatch.add(JSON.parseObject(JSON.toJSONString(activeGoodEx), ActiveGood.class));
                        if (seckillType != null && seckillType == SeckillConstants.SECKILL_TYPE_2 && (activeGoodEx.getVirtualFlag() == null || activeGoodEx.getVirtualFlag() == 0)) {
                            Map<String, Object> map = new HashMap<>();
                            if (activeGoodEx.getMapperyId() != null) {
                                map.put("isNew", "false");
                            } else {
                                map.put("isNew", "true");
                            }
                            map.put("goodId", activeGoodEx.getGoodId());
                            map.put("saleId", activeGoodEx.getSaleId());
                            map.put("goodSku", activeGoodEx.getGoodSku());
                            map.put("businessId", activeGoodEx.getBusinessId());
                            map.put("activeStore", activeGoodEx.getActiveStore());
                            map.put("activeStoreOld", activeGoodEx.getActiveStoreOld() == null ? 0 : activeGoodEx.getActiveStoreOld());
                            map.put("activeVirtual", activeGoodEx.getActiveVirtual());
                            map.put("msg", activeGoodEx.getGoodName() + "-" + activeGoodEx.getGoodSku());
                            store.add(map);
                        }
                    }
                }
            }
        }

        //插入/修改good表
        if (activeGoodBatch != null && !activeGoodBatch.isEmpty()) {
            activeGoodService.insertBatch(activeGoodBatch);
        }

        //缓存档期
        if(seckillType == SeckillConstants.SECKILL_TYPE_1){
            String priodNow = SeckillConstants.SeckillTimesPriod.SECKILL_PRIOD_NOW_1;
            String priodMax = SeckillConstants.SeckillTimesPriod.SECKILL_PRIOD_MAX_1;
            if (flag) {
                redisTemplate.opsForValue().set(priodNow, 0);
            } else {
                Object priod = redisTemplate.opsForValue().get(priodNow);
                if (priod == null || "".equals(priod)) {
                    int i = differentDays(seckillInfo.getCreaterTime(), date);
                    int size = set.size();
                    int nowPriod = i % size;
                    redisTemplate.opsForValue().set(priodNow, nowPriod == 0 ? size : nowPriod);
                }

            }
            redisTemplate.opsForValue().set(priodMax, set.size());
        } else {
            if (flag) {//新增
                if (store != null && !store.isEmpty()) {
                    List<GoodStock> goodStocks = new ArrayList<>();
                    for (Map<String, Object> map : store) {
                        Integer activeStore = (Integer) map.get("activeStore");
                        if (map.get("goodSku") != null && !"".equals(map.get("goodSku")) && activeStore > 0) {
                            GoodStock goodStock = new GoodStock();
                            goodStock.setGoodSku((String) map.get("goodSku"));
                            goodStock.setGoodNum(-activeStore);
                            goodStock.setStatus(CommonConstant.GoodStockStatus.OCCUPY);
                            goodStock.setBusinessId(String.valueOf(map.get("businessId")));
                            goodStock.setBusinessType("ACTIVE-SECKILL-OUT");
                            goodStock.setExpireTime(seckillInfo.getEveryEndTime());
                            goodStocks.add(goodStock);
                        }
                    }
                    ReturnData resultData = goodFeignClient.occupy(goodStocks);
                    if (resultData == null || resultData.getCode() != SecurityConstants.SUCCESS_CODE) {
                        throw new BusinessException(resultData.getDesc());
                    }
                }
            } else {//修改
                if (store != null && !store.isEmpty()) {
                    List<GoodStock> goodStocks = new ArrayList<>();
                    for (Map<String, Object> map : store) {
                        Integer activeStore = Integer.valueOf(String.valueOf(map.get("activeStore")));
                        Integer activeStoreOld = Integer.valueOf(String.valueOf(map.get("activeStoreOld")));
                        String isNew = (String) map.get("isNew");
                        if ("false".equals(isNew)) {//修改
                            if (activeStore.compareTo(activeStoreOld) != 0) {
                                if (map.get("goodSku") != null && !"".equals(map.get("goodSku"))) {
                                    GoodStock goodStock = new GoodStock();
                                    goodStock.setGoodSku((String) map.get("goodSku"));
                                    if (activeStore - activeStoreOld > 0) {
                                        goodStock.setGoodNum(-(activeStore - activeStoreOld));
                                        goodStock.setStatus(CommonConstant.GoodStockStatus.OCCUPY);
                                    } else if (activeStore - activeStoreOld < 0) {
                                        goodStock.setGoodNum(activeStoreOld - activeStore);
                                        goodStock.setStatus(CommonConstant.GoodStockStatus.RELIEVE);
                                    } else {
                                        continue;
                                    }
                                    goodStock.setBusinessId(String.valueOf(map.get("businessId")));
                                    goodStock.setBusinessType("ACTIVE-SECKILL-OUT");
                                    goodStock.setExpireTime(seckillInfo.getEveryEndTime());
                                    goodStocks.add(goodStock);
                                }
                            }
                        } else {//新增
                            if (map.get("goodSku") != null && !"".equals(map.get("goodSku")) && activeStore > 0) {
                                GoodStock goodStock = new GoodStock();
                                goodStock.setGoodSku((String) map.get("goodSku"));
                                goodStock.setGoodNum(-activeStore);
                                goodStock.setStatus(CommonConstant.GoodStockStatus.OCCUPY);
                                goodStock.setBusinessId(String.valueOf(map.get("businessId")));
                                goodStock.setBusinessType("ACTIVE-SECKILL-OUT");
                                goodStock.setExpireTime(seckillInfo.getEveryEndTime());
                                goodStocks.add(goodStock);
                            }
                        }
                    }
                    ReturnData resultData = goodFeignClient.occupy(goodStocks);
                    if (resultData == null || resultData.getCode() != SecurityConstants.SUCCESS_CODE) {
                        throw new BusinessException(resultData.getDesc());
                    }
                }
            }
            //缓存库存  缓存虚拟库存
            for (Map<String, Object> map : store) {
                String key = map.get("businessId") + ":" + map.get("goodId") + ":" + map.get("goodSku");
                redisTemplate.opsForValue().set(SeckillConstants.SECKILL_STORE + key, map.get("activeStore"), 30, TimeUnit.DAYS);
                redisTemplate.opsForValue().set(SeckillConstants.SECKILL_VIRTUAL_STORE + key, map.get("activeVirtual"), 30, TimeUnit.DAYS);
            }
        }
    }

    public void deleteGood(List<Integer> businessIds,List<SeckillTimesEx> seckillTimesExes, Integer seckillType){
        EntityWrapper<ActiveGood> goodEntityWrapper = new EntityWrapper<>();
        goodEntityWrapper.eq("ACTIVE_TYPE", ActiveGoodsConstants.ActiveType.SECKILL);
        goodEntityWrapper.in("BUSINESS_ID", businessIds);
        List<ActiveGood> activeGoods = activeGoodService.selectList(goodEntityWrapper);
        List<ActiveGoodEx> newActiveGoods = new ArrayList<>();
        if (activeGoods != null && !activeGoods.isEmpty()) {
            if (seckillType == SeckillConstants.SECKILL_TYPE_2 && seckillTimesExes != null && !seckillTimesExes.isEmpty()) {
                for (SeckillTimesEx seckillTimesEx : seckillTimesExes) {
                    List<ActiveGoodEx> activeGoodExes = seckillTimesEx.getActiveGoodExes();
                    if (activeGoodExes != null && !activeGoodExes.isEmpty()) {
                        newActiveGoods.addAll(activeGoodExes);
                    }
                }

                if (newActiveGoods != null && !newActiveGoods.isEmpty()) {
                    for (ActiveGood activeGood : activeGoods) {
                        boolean exist = false;
                        for (ActiveGoodEx activeGoodEx : newActiveGoods) {
                            if (activeGood.getGoodSku() == activeGoodEx.getGoodSku()) {
                                exist = true;
                                break;
                            }
                        }
                        if (!exist) {//该商品已删除 退库存 TODO 放到MQ
                            GoodStock goodStock = new GoodStock();
                            goodStock.setGoodSku(activeGood.getGoodSku());
                            goodStock.setGoodNum(activeGood.getActiveStore());
                            goodStock.setStatus(CommonConstant.GoodStockStatus.RELIEVE);
                            goodStock.setBusinessId(String.valueOf(activeGood.getBusinessId()));
                            goodStock.setBusinessType("ACTIVE-SECKILL-OUT");
                            try {
                                ReturnData resultData = goodFeignClient.relieve(Arrays.asList(goodStock));
                                if (resultData == null || resultData.getCode() != SecurityConstants.SUCCESS_CODE) {
                                    logger.error("active-deleteGood" + resultData.getDesc());
                                }
                            } catch (Exception e) {
                                logger.error("active-deleteGood", e);
                            }
                        }
                    }
                }
            }
        }
        activeGoodService.delete(goodEntityWrapper);
    }


    /**
     * 查询进行中的档期
     *
     * @return
     */
    public Integer getNowPriod() {
        String priodNow = "";
        priodNow = SeckillConstants.SeckillTimesPriod.SECKILL_PRIOD_NOW_1;
        Object priod = redisTemplate.opsForValue().get(priodNow);
        if (priod == null || "".equals(priod)) {
            String priodMax = SeckillConstants.SeckillTimesPriod.SECKILL_PRIOD_MAX_1;
            EntityWrapper<SeckillInfo> infoWrapper = new EntityWrapper<>();
            infoWrapper.eq("SECKILL_TYPE", SeckillConstants.SECKILL_TYPE_1);
            SeckillInfo seckillInfo = selectOne(infoWrapper);
            if (seckillInfo != null) {
                EntityWrapper<SeckillTimes> entityWrapper = new EntityWrapper<>();
                entityWrapper.setSqlSelect("SECKILL_PRIOD");
                entityWrapper.eq("SECKILL_ID", seckillInfo.getSeckillId());
                entityWrapper.eq("IS_ACTIVE", SeckillConstants.SeckillTimesActive.NO);
                entityWrapper.groupBy("SECKILL_PRIOD");
                List<SeckillTimes> seckillTimes = seckillTimesService.selectList(entityWrapper);
                if (seckillTimes != null && !seckillTimes.isEmpty()) {
                    int i = differentDays(seckillInfo.getCreaterTime(), new Date());
                    Integer nowPriod = i % seckillTimes.size();
                    redisTemplate.opsForValue().set(priodNow, nowPriod == 0 ? seckillTimes.size() : nowPriod);
                    redisTemplate.opsForValue().set(priodMax, seckillTimes.size());
                    return nowPriod;
                }
            }
        } else {
            return (Integer) priod;
        }
        return null;
    }

    /**
     * 查询最大档期
     *
     * @return
     */
    public Integer getMaxPriod() {
        String priodMax = "";
        priodMax = SeckillConstants.SeckillTimesPriod.SECKILL_PRIOD_MAX_1;
        Object max = redisTemplate.opsForValue().get(priodMax);
        if (max != null && !"".equals(max)) {
            return (Integer) max;
        } else {
            EntityWrapper<SeckillInfo> infoWrapper = new EntityWrapper<>();
            infoWrapper.eq("SECKILL_TYPE", SeckillConstants.SECKILL_TYPE_1);
            SeckillInfo seckillInfo = selectOne(infoWrapper);
            if (seckillInfo != null) {
                EntityWrapper<SeckillTimes> entityWrapper = new EntityWrapper<>();
                entityWrapper.eq("SECKILL_ID", seckillInfo.getSeckillId());
                entityWrapper.eq("IS_ACTIVE", SeckillConstants.SeckillTimesActive.NO);
                int times = seckillTimesService.selectCount(entityWrapper);
                redisTemplate.opsForValue().set(priodMax, times);
                return times;
            }
        }
        return 0;
    }

    /**
     * 获取下一个档期
     *
     * @return
     */
    public Integer getNextPriod() {
        Integer nowPriod = getNowPriod();
        Integer maxPriod = getMaxPriod();
        if (nowPriod != null && nowPriod != 0 && maxPriod != 0) {
            return nowPriod + 1 <= maxPriod ? nowPriod + 1 : 1;
        }
        return null;
    }

    /**
     * 获取下一个档期
     *
     * @return
     */
    public Integer getNextPriodS() {
        Integer nowPriod = getNowPriod();
        Integer maxPriod = getMaxPriod();
        if (nowPriod != null && maxPriod != 0) {
            return nowPriod + 1 <= maxPriod ? nowPriod + 1 : 1;
        }
        return null;
    }

    public SeckillInfoEx queryDetail(Integer seckillType, Integer seckillId) {
        if (SeckillConstants.SECKILL_TYPE_1 == seckillType) {
            return seckillInfoMapper.queryDetailIn(seckillType, seckillId);
        }
        return seckillInfoMapper.queryDetail(seckillType, seckillId);
    }

    public SeckillInfoEx queryDetailActive(Integer seckillType, Integer seckillId) {
        return seckillInfoMapper.queryDetailActive(seckillType, seckillId);
    }

    public static int differentDays(Date date1, Date date2) {
        try {
            date1 = DateUtils.SDF10.parse(DateUtils.SDF10.format(date1));
            date2 = DateUtils.SDF10.parse(DateUtils.SDF10.format(date2));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (int) ((date2.getTime() - date1.getTime()) / (1000 * 3600 * 24));
    }

    @Transactional(rollbackFor = Exception.class)
    public void delete(Integer seckillId) throws Exception {
        boolean a = deleteById(seckillId);
        if (a) {
            EntityWrapper<SeckillTimes> entityWrapper = new EntityWrapper<>();
            entityWrapper.eq("SECKILL_ID", seckillId);
            List<SeckillTimes> seckillTimes = seckillTimesService.selectList(entityWrapper);
            boolean b = seckillTimesService.delete(entityWrapper);
            if (b) {
                EntityWrapper<ActiveGood> activeGoodEntityWrapper = new EntityWrapper<>();
                activeGoodEntityWrapper.eq("ACTIVE_TYPE", ActiveGoodsConstants.ActiveType.SECKILL);
                activeGoodEntityWrapper.in("BUSINESS_ID", seckillTimes.stream().map(SeckillTimes::getTimesId).collect(Collectors.toList()));
                List<ActiveGood> activeGoods = activeGoodService.selectList(activeGoodEntityWrapper);
                activeGoodService.delete(activeGoodEntityWrapper);
                //退库存
                if (activeGoods != null && !activeGoods.isEmpty()) {
                    //秒杀活动库存 + businessId + ":" + goodId + ":" + saleId
                    for (ActiveGood activeGood : activeGoods) {
                        StringBuilder sb = new StringBuilder(SeckillConstants.SECKILL_STORE);
                        String key = sb.append(activeGood.getBusinessId()).append(":").append(activeGood.getGoodId()).append(":").append(activeGood.getGoodSku()).toString();
                        //获取剩余库存
                        Object o = redisTemplate.opsForValue().getAndSet(key, 0);
                        //删除活动库存缓存
                        redisTemplate.delete(key);
                        Integer store = 0;
                        if (o != null && !o.equals("")) {
                            if ((Integer) o <= 0) {
                                continue;
                            }
                            store = (Integer) o;
                        } else {
                            continue;
                        }
                        try {
                            if (store != null && store != 0) {
                                //调用退库存接口 TODO 放到MQ
                                seckillTimesService.decrStore(activeGood.getGoodSku(), store.intValue(), activeGood.getBusinessId());
                            }
                        } catch (Exception e) {
                            //记录调用失败信息
                            redisTemplate.opsForValue().set(SeckillConstants.SECKILL_STORE_FAIL + activeGood.getBusinessId() + ":" + activeGood.getGoodId() + ":" + activeGood.getGoodSku(), store);
                            logger.error("秒杀归还剩余库存失败：{}:{}_{}_{}", SeckillConstants.SECKILL_STORE_FAIL + activeGood.getBusinessId(), activeGood.getGoodId(), activeGood.getGoodSku(), store);
                            continue;
                        }
                        sb.setLength(0);
                    }
                }
            }
        }
    }


    @Transactional(rollbackFor = Exception.class)
    public List<GoodStock> cp(Integer nextPriod, Integer seckillType) throws Exception {
        logger.info("-------------------------seckill-cp:{}", DateUtils.SDF1.format(new Date()));
        List<GoodStock> goodStocks = null;
        //当前期次
        Integer nowPriod = getNowPriod();
        if (nextPriod == null) {
            nextPriod = getNextPriodS();
        }
        if (nextPriod != null) {
            goodStocks = new ArrayList<>();
            //查询活动信息
            EntityWrapper<SeckillInfo> infoWrapper = new EntityWrapper<>();
            infoWrapper.eq("SECKILL_TYPE", seckillType);
            SeckillInfo seckillInfo = selectOne(infoWrapper);
            if (nowPriod != 0) {
                //1.清理过期活动
                //查询过期档期
                EntityWrapper<SeckillTimes> timesWrapper = new EntityWrapper<>();
                timesWrapper.eq("SECKILL_PRIOD", nowPriod);
                timesWrapper.eq("IS_ACTIVE", SeckillConstants.SeckillTimesActive.YES);
                timesWrapper.eq("SECKILL_ID", seckillInfo.getSeckillId());
                List<SeckillTimes> seckillTimes = seckillTimesService.selectList(timesWrapper);
                if (seckillTimes != null && !seckillTimes.isEmpty()) {
                    //查询此阶段商品
                    EntityWrapper<ActiveGood> goodEntityWrapper = new EntityWrapper<>();
                    goodEntityWrapper.in("BUSINESS_ID", seckillTimes.stream().map(SeckillTimes::getTimesId).collect(Collectors.toList()));
                    goodEntityWrapper.eq("ACTIVE_TYPE", ActiveGoodsConstants.ActiveType.SECKILL);
                    goodEntityWrapper.eq("GOOD_STATUS", "1");
                    List<ActiveGood> activeGoods = activeGoodService.selectList(goodEntityWrapper);
                    //退库存
                    if (activeGoods != null && !activeGoods.isEmpty()) {
                        //秒杀活动库存 + businessId + ":" + goodId + ":" + saleId
                        for (ActiveGood activeGood : activeGoods) {
                            StringBuilder sb = new StringBuilder(SeckillConstants.SECKILL_STORE);
                            String key = sb.append(activeGood.getBusinessId()).append(":").append(activeGood.getGoodId()).append(":").append(activeGood.getGoodSku()).toString();
                            //获取剩余库存
                            //Object o = redisTemplate.opsForValue().getAndSet(key, 0);
                            //删除活动库存缓存
                            redisTemplate.delete(key);
                            /*Integer store = 0;
                            if (o != null && !o.equals("")) {
                                if ((Integer) o == 0) {
                                    continue;
                                }
                                store = (Integer) o;
                            } else {
                                continue;
                            }
                            try {
                                if (store != null && store != 0) {
                                    //调用退库存接口 TODO 放到MQ
                                    seckillTimesService.decrStore(activeGood.getGoodSku(), store.intValue(), activeGood.getBusinessId());
                                }
                            } catch (Exception e) {
                                //记录调用失败信息
                                redisTemplate.opsForValue().set(SeckillConstants.SECKILL_STORE_FAIL + activeGood.getBusinessId() + ":" + activeGood.getGoodId() + ":" + activeGood.getGoodSku(), store);
                                logger.error("秒杀归还剩余库存失败：{}:{}_{}_{}", SeckillConstants.SECKILL_STORE_FAIL + activeGood.getBusinessId(), activeGood.getGoodId(), activeGood.getGoodSku(), store);
                                continue;
                            }
                            sb.setLength(0);*/
                        }
                    }
                    //期次过期
                    EntityWrapper<SeckillTimes> passTimesWrapper = new EntityWrapper<>();
                    passTimesWrapper.eq("SECKILL_PRIOD", nowPriod);
                    passTimesWrapper.eq("IS_ACTIVE", SeckillConstants.SeckillTimesActive.YES);
                    passTimesWrapper.eq("SECKILL_ID", seckillInfo.getSeckillId());
                    SeckillTimes pass = new SeckillTimes();
                    pass.setIsActive(SeckillConstants.SeckillTimesActive.PASS);
                    List<SeckillTimes> passSeckillTimes = seckillTimesService.selectList(timesWrapper);
                    seckillTimesService.update(pass, passTimesWrapper);
                    //商品失效
                    EntityWrapper<ActiveGood> passGoodEntityWrapper = new EntityWrapper<>();
                    passGoodEntityWrapper.in("BUSINESS_ID", passSeckillTimes.stream().map(SeckillTimes::getTimesId).collect(Collectors.toList()));
                    passGoodEntityWrapper.eq("ACTIVE_TYPE", ActiveGoodsConstants.ActiveType.SECKILL);
                    passGoodEntityWrapper.eq("GOOD_STATUS", "1");
                    ActiveGood activeGood = new ActiveGood();
                    activeGood.setGoodStatus("-1");//删除
                    activeGoodService.update(activeGood, passGoodEntityWrapper);
                }
            }
            //2.切换活动
            //查询下一个档期
            EntityWrapper<SeckillTimes> nextTimesWrapper = new EntityWrapper<>();
            nextTimesWrapper.eq("SECKILL_ID", seckillInfo.getSeckillId());
            nextTimesWrapper.eq("SECKILL_PRIOD", nextPriod);
            nextTimesWrapper.eq("IS_ACTIVE", SeckillConstants.SeckillTimesActive.NO);
            List<SeckillTimes> nextSeckillTimes = seckillTimesService.selectList(nextTimesWrapper);
            if (nextSeckillTimes != null && !nextSeckillTimes.isEmpty()) {
                for (int i = 0; i < nextSeckillTimes.size(); i++) {
                    Integer timesId = nextSeckillTimes.get(i).getTimesId();
                    nextSeckillTimes.get(i).setTimesId(null);
                    nextSeckillTimes.get(i).setIsActive(SeckillConstants.SeckillTimesActive.YES);
                    //拷贝期次
                    seckillTimesService.insert(nextSeckillTimes.get(i));
                    //查询商品
                    EntityWrapper<ActiveGood> goodEntityWrapper = new EntityWrapper<>();
                    goodEntityWrapper.eq("BUSINESS_ID", timesId);
                    goodEntityWrapper.eq("ACTIVE_TYPE", ActiveGoodsConstants.ActiveType.SECKILL);
                    goodEntityWrapper.eq("GOOD_STATUS", "1");
                    List<ActiveGood> activeGoods = activeGoodService.selectList(goodEntityWrapper);
                    if (activeGoods != null && !activeGoods.isEmpty()) {
                        DateTime endTime = DateUtil.date(nextSeckillTimes.get(i).getEndTime().getTime());
                        DateTime date = DateUtil.date().setField(DateField.HOUR_OF_DAY, endTime.getField(DateField.HOUR_OF_DAY))
                                .setField(DateField.MINUTE, endTime.getField(DateField.MINUTE))
                                .setField(DateField.SECOND, endTime.getField(DateField.SECOND));
                        for (ActiveGood activeGood : activeGoods) {
                            if (activeGood.getGoodSku() != null && activeGood.getGoodSku().length() != 0) {
                                activeGood.setMapperyId(null);
                                activeGood.setArg2("1");
                                activeGood.setBusinessId(nextSeckillTimes.get(i).getTimesId());
                                //占用库存
                                GoodStock goodStock = new GoodStock();
                                goodStock.setGoodSku(activeGood.getGoodSku());
                                goodStock.setGoodNum(-activeGood.getActiveStore());
                                goodStock.setStatus(CommonConstant.GoodStockStatus.OCCUPY);
                                goodStock.setBusinessId(String.valueOf(activeGood.getBusinessId()));
                                goodStock.setBusinessType("ACTIVE-SECKILL-IN");
                                goodStock.setExpireTime(date);
                                try {
                                    ReturnData resultData = goodFeignClient.occupy(Arrays.asList(goodStock));
                                    if (resultData == null || resultData.getCode() != SecurityConstants.SUCCESS_CODE) {
                                        //库存不足
                                        activeGood.setActiveStore(0);
                                        logger.info("seckill-cp:库存不足" + activeGood.getGoodSku() + "_" + activeGood.getActiveStore());
                                    } else {
                                        goodStocks.add(goodStock);
                                    }
                                } catch (Exception e) {
                                    activeGood.setActiveStore(0);
                                    logger.error("seckill-cp:同步库存失败", e);
                                }
                            }
                        }

                        //拷贝商品
                        activeGoodService.insertBatch(activeGoods);

                        //缓存库存  缓存虚拟库存
                        for (ActiveGood activeGood : activeGoods) {
                            String key = activeGood.getBusinessId() + ":" + activeGood.getGoodId() + ":" + activeGood.getGoodSku();
                            redisTemplate.opsForValue().set(SeckillConstants.SECKILL_STORE + key, activeGood.getActiveStore(), 48, TimeUnit.HOURS);
                            redisTemplate.opsForValue().set(SeckillConstants.SECKILL_VIRTUAL_STORE + key, activeGood.getActiveVirtual(), 48, TimeUnit.HOURS);
                        }
                    }
                }

                //切换档期
                redisTemplate.opsForValue().set(SeckillConstants.SeckillTimesPriod.SECKILL_PRIOD_NOW_1, nextPriod);
            }
        }
        return goodStocks;
    }

    public void qto(Integer seckillType) throws Exception {
        logger.info("-------------------------seckill-qto:{}_{}",DateUtils.SDF1.format(new Date()), seckillType);
        //站内活动
        Integer nowPriod = getNowPriod();
        if ((SeckillConstants.SECKILL_TYPE_1 == seckillType && nowPriod != null) || SeckillConstants.SECKILL_TYPE_2 == seckillType) {
            int hour = LocalDateTime.now().getHour();
            long date = DateUtil.date().getTime();
            //活动信息
            EntityWrapper<SeckillInfo> infoWrapper = new EntityWrapper<>();
            infoWrapper.eq("SECKILL_TYPE", seckillType);
            if (SeckillConstants.SECKILL_TYPE_2 == seckillType) {
                if (hour == 0) {
                    infoWrapper.gt("EVERY_END_TIME", new Date(date - 24 * 60 * 60 * 1000));
                    infoWrapper.lt("EVERY_END_TIME", new Date(date));
                } else {
                    infoWrapper.gt("EVERY_END_TIME", DateUtils.SDF1.parse(DateUtils.SDF10.format(date) + " 00:00:00"));
                    infoWrapper.lt("EVERY_END_TIME", DateUtils.SDF1.parse(DateUtils.SDF10.format(date) + " 23:59:59"));
                }
            } else {
                if (hour == 0) {//站内活动 零点时无法判断当前期次
                    return;
                }
            }

            List<SeckillInfo> seckillInfos = selectList(infoWrapper);
            if (seckillInfos != null && !seckillInfos.isEmpty()) {
                //查询过期档期
                EntityWrapper<SeckillTimes> timesWrapper = new EntityWrapper<>();
                if (SeckillConstants.SECKILL_TYPE_1 == seckillType) {
                    timesWrapper.eq("SECKILL_PRIOD", nowPriod);
                    timesWrapper.eq("IS_ACTIVE", SeckillConstants.SeckillTimesActive.YES);
                    timesWrapper.eq("SECKILL_ID", seckillInfos.get(0).getSeckillId());
                    timesWrapper.gt("END_TIME", DateUtils.SDF1.parse("2019-10-01 " + (hour - 1 < 0 ? 23 : hour - 1) + ":50:00"));
                    timesWrapper.lt("END_TIME", DateUtils.SDF1.parse("2019-10-01 " + hour + ":10:00"));
                } else {
                    timesWrapper.in("SECKILL_ID", seckillInfos.stream().map(SeckillInfo::getSeckillId).collect(Collectors.toList()));
                    //timesWrapper.gt("END_TIME", new Date(date - 10 * 60 * 1000));
                    //timesWrapper.lt("END_TIME", new Date(date + 10 * 60 * 1000));
                }
                List<SeckillTimes> seckillTimes = seckillTimesService.selectList(timesWrapper);
                if (seckillTimes != null && !seckillTimes.isEmpty()) {
                    //查询此阶段商品
                    EntityWrapper<ActiveGood> goodEntityWrapper = new EntityWrapper<>();
                    if (SeckillConstants.SECKILL_TYPE_1 == seckillType) {
                        goodEntityWrapper.eq("BUSINESS_ID", seckillTimes.get(0).getTimesId());
                    } else {
                        goodEntityWrapper.in("BUSINESS_ID", seckillTimes.stream().map(SeckillTimes::getTimesId).collect(Collectors.toList()));
                    }
                    goodEntityWrapper.eq("ACTIVE_TYPE", ActiveGoodsConstants.ActiveType.SECKILL);
                    List<ActiveGood> activeGoods = activeGoodService.selectList(goodEntityWrapper);
                    //退库存
                    if (activeGoods != null && !activeGoods.isEmpty()) {
                        //秒杀活动库存 + businessId + ":" + goodId + ":" + saleId
                        for (ActiveGood activeGood : activeGoods) {
                            StringBuilder sb = new StringBuilder(SeckillConstants.SECKILL_STORE);
                            String key = sb.append(activeGood.getBusinessId()).append(":").append(activeGood.getGoodId()).append(":").append(activeGood.getGoodSku()).toString();
//                            //获取剩余库存
//                            Object o = redisTemplate.opsForValue().getAndSet(key, 0);
                            //删除活动库存缓存
                            redisTemplate.delete(key);
                            /*自动过期
                            Integer store = 0;
                            if (o != null && !o.equals("")) {
                                if ((Integer) o <= 0) {
                                    continue;
                                }
                                store = (Integer) o;
                            } else {
                                continue;
                            }
                            try {
                                if (store != null && store != 0) {
                                    //调用退库存接口 TODO 放到MQ
                                    seckillTimesService.decrStore(activeGood.getGoodSku(), store.intValue(), activeGood.getBusinessId());
                                }
                            } catch (Exception e) {
                                //记录调用失败信息
                                redisTemplate.opsForValue().set(SeckillConstants.SECKILL_STORE_FAIL + activeGood.getBusinessId() + ":" + activeGood.getGoodId() + ":" + activeGood.getGoodSku(), store);
                                logger.error("秒杀归还剩余库存失败：{}:{}_{}_{}", SeckillConstants.SECKILL_STORE_FAIL + activeGood.getBusinessId(), activeGood.getGoodId(), activeGood.getGoodSku(), store);
                                continue;
                            }
                            sb.setLength(0);*/
                        }
                    }
                }
            }
        }
    }

    public void dav(Integer seckillType) throws Exception {
        Integer nowPriod = getNowPriod();
        if ((SeckillConstants.SECKILL_TYPE_1 == seckillType && nowPriod != null) || SeckillConstants.SECKILL_TYPE_2 == seckillType) {
            DateTime date = DateUtil.date();
            //活动信息
            EntityWrapper<SeckillInfo> infoWrapper = new EntityWrapper<>();
            infoWrapper.eq("SECKILL_TYPE", seckillType);
            if (SeckillConstants.SECKILL_TYPE_2 == seckillType) {
                //infoWrapper.ge("EVERY_START_TIME", DateUtils.SDF1.parse(DateUtils.SDF10.format(date) + " 00:00:00"));
                //infoWrapper.le("EVERY_START_TIME", DateUtils.SDF1.parse(DateUtils.SDF10.format(date) + " 23:59:59"));

                infoWrapper.le("EVERY_START_TIME", date);
                infoWrapper.ge("EVERY_END_TIME", date);
            }
            List<SeckillInfo> seckillInfos = selectList(infoWrapper);
            if (seckillInfos != null && !seckillInfos.isEmpty()) {
                //查询进行中档期
                EntityWrapper<SeckillTimes> timesWrapper = new EntityWrapper<>();
                if (SeckillConstants.SECKILL_TYPE_1 == seckillType) {
                    timesWrapper.eq("SECKILL_PRIOD", nowPriod);
                    timesWrapper.eq("IS_ACTIVE", SeckillConstants.SeckillTimesActive.YES);
                    timesWrapper.eq("SECKILL_ID", seckillInfos.get(0).getSeckillId());
                    String time = date.hour(true) + ":" + date.minute() + ":" + date.second();
                    timesWrapper.gt("END_TIME", DateUtils.SDF1.parse("2019-10-01 " + time));
                    timesWrapper.lt("START_TIME", DateUtils.SDF1.parse("2019-10-01 " + time));
                } else {
                    timesWrapper.in("SECKILL_ID", seckillInfos.stream().map(SeckillInfo::getSeckillId).collect(Collectors.toList()));
                    //timesWrapper.gt("END_TIME", date);
                    //timesWrapper.lt("START_TIME", date);
                }
                List<SeckillTimes> seckillTimes = seckillTimesService.selectList(timesWrapper);
                //活动虚拟库存存
                if (seckillTimes != null && !seckillTimes.isEmpty()) {
                    EntityWrapper<ActiveGood> goodEntityWrapper = new EntityWrapper<>();
                    goodEntityWrapper.in("BUSINESS_ID", seckillTimes.stream().map(SeckillTimes::getTimesId).collect(Collectors.toList()));
                    goodEntityWrapper.eq("ACTIVE_TYPE", ActiveGoodsConstants.ActiveType.SECKILL);
                    goodEntityWrapper.eq("GOOD_STATUS", "1");
                    List<ActiveGood> activeGoods = activeGoodService.selectList(goodEntityWrapper);
                    if (activeGoods != null && !activeGoods.isEmpty()) {
                        for (ActiveGood activeGood : activeGoods) {
                            StringBuilder key = new StringBuilder(SeckillConstants.SECKILL_VIRTUAL_STORE);
                            key.append(activeGood.getBusinessId());
                            key.append(":");
                            key.append(activeGood.getGoodId());
                            key.append(":");
                            key.append(activeGood.getGoodSku());
                            Object o = redisTemplate.opsForValue().get(key.toString());
                            if (o != null && !"".equals(o)) {
                                if ((Integer) o <= 0) {
                                    continue;
                                }
                            }
                            redisTemplate.opsForValue().increment(key.toString(), -1);
                            key.setLength(0);
                        }
                    }
                }
            }
        }
    }

    public void orderFail(String orderNo) {
        String orderKey = ActiveGoodsConstants.ACTIVE_ORDER + orderNo; //set
        String orderSkuKey = ActiveGoodsConstants.ACTIVE_ORDER_SKU; //hash
        Set<Object> skus = redisTemplate.opsForSet().members(orderKey);
        if (skus != null && !skus.isEmpty()) {
            Iterator<Object> i = skus.iterator();
            while (i.hasNext()) {
                String sku = String.valueOf(i.next());
                Map<Object, Object> entries = redisTemplate.opsForHash().entries(orderSkuKey + sku);
                if (entries != null) {
                    Integer goodNum = Integer.valueOf(String.valueOf(entries.get("goodNum")));
                    String businessId = String.valueOf(entries.get("businessId"));
                    String goodSku = String.valueOf(entries.get("goodSku"));
                    Object goodId = entries.get("goodId");
                    Object o = redisTemplate.opsForValue().get(CommonConstant.GOOD_STOCK_OCCUPY_BUSINESS + businessId);
                    if (o != null && !"".equals(o)) {
                        //活动未过期
                        StringBuilder sb = new StringBuilder(SeckillConstants.SECKILL_STORE);
                        sb.append(businessId).append(":").append(goodId).append(":").append(goodSku);
                        redisTemplate.opsForValue().increment(sb.toString(), goodNum);

                        //处理已购数量
                        StringBuilder seckillOrderKey = new StringBuilder(SeckillConstants.SECKILL_ORDER_LIMIT);
                        seckillOrderKey.append(entries.get("userId")).append(":").append(businessId).append(":").append(goodId);
                        redisTemplate.opsForValue().increment(seckillOrderKey.toString(), -goodNum);
                    }
                }
            }

        }
    }

    public void paySuccess(String orderNo) {
        logger.info("seckill_paySuccess_orderNo_{}", orderNo);
        String orderKey = ActiveGoodsConstants.ACTIVE_ORDER + orderNo; //set
        String orderSkuKey = ActiveGoodsConstants.ACTIVE_ORDER_SKU; //hash
        Set<Object> skus = redisTemplate.opsForSet().members(orderKey);
        if (skus != null && !skus.isEmpty()) {
            Iterator<Object> i = skus.iterator();
            while (i.hasNext()) {
                String sku = String.valueOf(i.next());
                Map<Object, Object> entries = redisTemplate.opsForHash().entries(orderSkuKey + sku);
                if (entries != null) {
                    String businessId = String.valueOf(entries.get("businessId"));
                    Date ex = DateUtil.date().setField(DateField.HOUR_OF_DAY, 23).setField(DateField.MINUTE, 59).setField(DateField.SECOND, 59);
                    redisTemplate.opsForValue().set(CommonConstant.GOOD_STOCK_OCCUPY_BUSINESS + businessId, ex.getTime(), ex.getTime() - new Date().getTime(), TimeUnit.MILLISECONDS);
                    logger.info("seckill_paySuccess_businessId_{}", businessId);
                }
            }
        }
    }

    //订单取消 未支付
    public void payCancelled(String orderNo) {
        logger.info("seckill_payCancelled_{}", orderNo);
        String orderKey = ActiveGoodsConstants.ACTIVE_ORDER + orderNo; //set
        String orderSkuKey = ActiveGoodsConstants.ACTIVE_ORDER_SKU; //hash
        Set<Object> skus = redisTemplate.opsForSet().members(orderKey);
        if (skus != null && !skus.isEmpty()) {
            Iterator<Object> i = skus.iterator();
            while (i.hasNext()) {
                String sku = String.valueOf(i.next());
                logger.info("seckill_payCancelled_sku_{}", sku);
                Map<Object, Object> entries = redisTemplate.opsForHash().entries(orderSkuKey + sku);
                if (entries != null) {
                    Integer goodNum = Integer.valueOf(String.valueOf(entries.get("goodNum")));
                    String businessId = String.valueOf(entries.get("businessId"));
                    String goodSku = String.valueOf(entries.get("goodSku"));
                    Object goodId = entries.get("goodId");
                    //活动未过期
                    try {
                        StringBuilder sb = new StringBuilder(SeckillConstants.SECKILL_STORE);
                        sb.append(businessId).append(":").append(goodId).append(":").append(goodSku);
                        redisTemplate.opsForValue().increment(sb.toString(), goodNum);
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                    }

                    //处理已购数量
                    try {
                        StringBuilder seckillOrderKey = new StringBuilder(SeckillConstants.SECKILL_ORDER_LIMIT);
                        seckillOrderKey.append(entries.get("userId")).append(":").append(businessId).append(":").append(goodId);
                        redisTemplate.opsForValue().increment(seckillOrderKey.toString(), -goodNum);
                        logger.info("seckill_payCancelled_{}_{}", seckillOrderKey, -goodNum);
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                    }
                }
            }
        }
    }

    //订单关闭 已支付
    public void paydClosed(String orderNo) {
        logger.info("seckill_paydClosed_{}", orderNo);
        String orderKey = ActiveGoodsConstants.ACTIVE_ORDER + orderNo; //set
        String orderSkuKey = ActiveGoodsConstants.ACTIVE_ORDER_SKU; //hash
        Set<Object> skus = redisTemplate.opsForSet().members(orderKey);
        if (skus != null && !skus.isEmpty()) {
            Iterator<Object> i = skus.iterator();
            while (i.hasNext()) {
                String sku = String.valueOf(i.next());
                Map<Object, Object> entries = redisTemplate.opsForHash().entries(orderSkuKey + sku);
                if (entries != null) {
                    Integer goodNum = Integer.valueOf(String.valueOf(entries.get("goodNum")));
                    String businessId = String.valueOf(entries.get("businessId"));
                    String goodSku = String.valueOf(entries.get("goodSku"));
                    Integer inOrOut = Integer.valueOf(String.valueOf(entries.get("inOrOut")));
                    Object goodId = entries.get("goodId");
                    //处理库存
                    Date ex;
                    Object o = redisTemplate.opsForValue().get(CommonConstant.GOOD_STOCK_OCCUPY_BUSINESS + businessId);
                    if (o != null && !"".equals(o)) {
                        //活动未过期
                        ex = new Date(Long.valueOf(String.valueOf(o)));
                        StringBuilder sb = new StringBuilder(SeckillConstants.SECKILL_STORE);
                        sb.append(businessId).append(":").append(goodId).append(":").append(goodSku);
                        redisTemplate.opsForValue().increment(sb.toString(), goodNum);
                        //订单退回库存
                        GoodStock goodStockO = new GoodStock();
                        goodStockO.setGoodSku(goodSku);
                        goodStockO.setGoodNum(goodNum);
                        goodStockO.setStatus(CommonConstant.GoodStockStatus.ROLLBACK);
                        goodStockO.setBusinessId(orderNo);
                        if (inOrOut == 0) {
                            goodStockO.setBusinessType("ACTIVE-SECKILL-IN");
                        } else {
                            goodStockO.setBusinessType("ACTIVE-SECKILL-OUT");
                        }
                        goodStockO.setExpireTime(ex);
                        kafkaTemplate.send(MQTopicConstant.HOLD_GOOD_STOCK, JSONObject.toJSONString(Arrays.asList(goodStockO)));

                        //调整库存占用量
//                        GoodStock goodStockR = new GoodStock();
//                        goodStockR.setGoodSku(goodSku);
//                        goodStockR.setGoodNum(-goodNum);
//                        goodStockR.setStatus(CommonConstant.GoodStockStatus.OCCUPY);
//                        goodStockR.setBusinessId(businessId);
//                        if (inOrOut == 0) {
//                            goodStockR.setBusinessType("ACTIVE-SECKILL-IN");
//                        } else {
//                            goodStockR.setBusinessType("ACTIVE-SECKILL-OUT");
//                        }
//                        goodStockR.setExpireTime(ex);
//
//                        kafkaTemplate.send(MQTopicConstant.HOLD_GOOD_STOCK, JSONObject.toJSONString(Arrays.asList(goodStockR)));

                        //处理已购数量
                        StringBuilder seckillOrderKey = new StringBuilder(SeckillConstants.SECKILL_ORDER_LIMIT);
                        seckillOrderKey.append(entries.get("userId")).append(":").append(businessId).append(":").append(goodId);
                        redisTemplate.opsForValue().increment(seckillOrderKey.toString(), -goodNum);
                        logger.info("seckill_paydClosed_{}_{}", seckillOrderKey, -goodNum);
                    } else {
                        //活动已经过期 占用自动退回
                    }

                }
            }

        }
    }


    //限时秒杀-取消订单
    public void sendFlashSaleSMS(String orderNo) {
        OrderStore reader = orderStoreUtils.reader(orderNo);
        logger.info("限时秒杀-取消订单推送:{}", orderNo);

        Map<String, Object> smsMap = new HashMap<>();
        smsMap.put("orderno", orderNo);
        smsMap.put("title", reader.getGoodsList().get(0).getGoodName());
        smsMap.put("nickname", reader.getUserFullName());

        SmsDto sms = new SmsDto(com.mmj.common.utils.StringUtils.getUUid(), reader.getUserId(), orderNo);
        sms.setMsgType(MessageConstants.msgType.NODE);
        sms.setNode(MessageConstants.type.FLASH_TWO);
        sms.setModel(MessageConstants.module.FLASH);
        sms.setType(MessageConstants.type.FLASH_TWO);
        sms.setPhone(reader.getConsignee().getMobile());
        sms.setNickName(reader.getUserFullName());

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("orderNo", orderNo);
        sms.setParams(paramMap);
        sms.setSmsParams(smsMap);

        kafkaTemplate.send(MQCommonTopic.SMS_TOPIC, String.valueOf(snowflakeIdWorker.nextId()), JSON.toJSONString(sms));
        logger.info("发送短信消息 {} success", JSON.toJSONString(sms));
    }

}
