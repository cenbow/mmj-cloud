package com.mmj.active.common.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.mmj.active.common.constants.ActiveGoodsConstants;
import com.mmj.active.common.feigin.GoodFeignClient;
import com.mmj.active.common.feigin.OrderFeignClient;
import com.mmj.active.common.mapper.ActiveGoodMapper;
import com.mmj.active.common.model.ActiveGood;
import com.mmj.active.common.model.ActiveGoodEx;
import com.mmj.active.common.model.ActiveGoodStore;
import com.mmj.active.common.model.OrderGroup;
import com.mmj.active.common.model.dto.PassingDataDto;
import com.mmj.active.common.service.ActiveGoodService;
import com.mmj.active.cut.model.dto.CutOrderDto;
import com.mmj.active.cut.service.CutUserService;
import com.mmj.active.seckill.constants.SeckillConstants;
import com.mmj.common.constants.CommonConstant;
import com.mmj.common.constants.MQTopicConstant;
import com.mmj.common.exception.BusinessException;
import com.mmj.common.model.GoodStock;
import com.mmj.common.model.JwtUserDetails;
import com.mmj.common.model.active.ActiveGoodStoreResult;
import com.mmj.common.model.order.OrdersMQDto;
import com.mmj.common.utils.PriceConversion;
import com.mmj.common.utils.SecurityUserUtil;
import com.xiaoleilu.hutool.date.DateField;
import com.xiaoleilu.hutool.date.DateUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * <p>
 * 活动商品关联表 服务实现类
 * </p>
 *
 * @author KK
 * @since 2019-06-15
 */
@Service
public class ActiveGoodServiceImpl extends ServiceImpl<ActiveGoodMapper, ActiveGood> implements ActiveGoodService {

    Logger logger = LoggerFactory.getLogger(ActiveGoodServiceImpl.class);

    @Autowired
    private ActiveGoodMapper activeGoodMapper;
    @Autowired
    private OrderFeignClient orderFeignClient;
    @Autowired
    private CutUserService cutUserService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public ActiveGoodStoreResult cutOrderCheck(ActiveGoodStore activeGoodStore) {
        List<ActiveGoodStore.GoodSales> goodSales = activeGoodStore.getGoodSales();
        String passingData = activeGoodStore.getPassingData();
        if (goodSales != null && !goodSales.isEmpty() && passingData != null && passingData.trim().length() != 0) {
            //订单商品
            ActiveGoodStore.GoodSales goodSale = goodSales.get(0);
            //活动id
            Integer activeId = JSON.parseObject(passingData).getInteger("activeId");
            String cutNo = JSON.parseObject(passingData).getString("cutNo");
            Integer totalGoodsAmount = PriceConversion.stringToInt(goodSale.getUnitPrice()) * goodSale.getGoodNum();
            CutOrderDto cutOrderDto = cutUserService.checkOrder(activeId, cutNo);
            BigDecimal surplusAmount = cutOrderDto.getSurplusAmount();
            if (Objects.isNull(surplusAmount)) {
                return new ActiveGoodStoreResult(false);
            }
            int goodAmountInt = PriceConversion.bigDecimalToInt(cutOrderDto.getGoodAmount());
            return new ActiveGoodStoreResult(totalGoodsAmount.intValue() == goodAmountInt, cutOrderDto.getGoodAmount().subtract(surplusAmount).doubleValue());

        }
        return new ActiveGoodStoreResult(false);
    }

    /**
     * 秒杀库存校验
     *
     * @param activeGoodStore
     */
    @Transactional(rollbackFor = Exception.class)
    public Integer seckillCheck(ActiveGoodStore activeGoodStore) {
        List<ActiveGoodStore.GoodSales> goodSales = activeGoodStore.getGoodSales();
        String passingData = activeGoodStore.getPassingData();
        Integer inOrOut = null;
        if (goodSales != null && !goodSales.isEmpty() && passingData != null && passingData.trim().length() != 0) {
            //订单商品
            ActiveGoodStore.GoodSales goodSale = goodSales.get(0);
            //活动id
            Integer activeId = JSON.parseObject(passingData).getInteger("activeId");
            //秒杀库存扣减
            logger.info("--------------秒杀商品库存扣减:{}__{}__{}__{}", activeGoodStore.getActiveType(), goodSale.getSaleId(), activeId, goodSale.getGoodNum());
            boolean flag = false;
            String baseKey = activeId + ":" + goodSale.getGoodId();
            String key = SeckillConstants.SECKILL_STORE + baseKey + ":" + goodSale.getSku();
            try {
                inOrOut = seckilllimitCheck(goodSale, baseKey, activeId, false);
                Long increment = redisTemplate.opsForValue().increment(key, -goodSale.getGoodNum());
                flag = true;//库存扣减成功
                if (increment < 0) {
                    flag = false;
                    redisTemplate.opsForValue().increment(key, goodSale.getGoodNum());
                    throw new BusinessException("库存不足！");
                }
            } catch (Exception e) {
                //异常回退库存
                if (flag && activeGoodStore.getOrderCheck() != null && !activeGoodStore.getOrderCheck()) {
                    redisTemplate.opsForValue().increment(key, goodSale.getGoodNum());
                }
                logger.error(e.getMessage(), e);
                throw new BusinessException(e.getMessage());
            }
        }
        return inOrOut;
    }

    /**
     * 秒杀每人每天购买数量校验
     *
     * @param goodSale
     * @param baseKey
     * @param activeId
     */
    public Integer seckilllimitCheck(ActiveGoodStore.GoodSales goodSale, String baseKey, Integer activeId, boolean flag) {
        JwtUserDetails jwtUser = SecurityUserUtil.getUserDetails();
        String orderKey = SeckillConstants.SECKILL_ORDER_LIMIT + jwtUser.getUserId() + ":" + baseKey;
        Object out = redisTemplate.opsForValue().get(SeckillConstants.SECKILL_LIMIT_OUT + activeId);
        Integer limitNum = 0;
        Integer inOrOut = 0; //in
        if (out == null || "".equals(out)) {
            Object in = redisTemplate.opsForValue().get(SeckillConstants.SECKILL_LIMIT_IN);
            limitNum = Integer.valueOf(String.valueOf(in));
        } else {
            limitNum = Integer.valueOf(String.valueOf(out));
            inOrOut = 1; //out
        }
        logger.info("------------seckilllimitCheck:{}_{}_{}", flag, activeId, limitNum);
        if (flag) {
            Integer num = 0;
            Object o = redisTemplate.opsForValue().get(orderKey);
            if (o != null && !"".equals(o)) {
                num = Integer.valueOf(String.valueOf(o));
            }
            if (goodSale.getGoodNum().compareTo(limitNum - num) > 0) {
                throw new BusinessException("每人每天限购" + limitNum + "件！");
            }

        } else {
            Long num = redisTemplate.opsForValue().increment(orderKey, goodSale.getGoodNum());
            if (num > limitNum) {
                redisTemplate.opsForValue().increment(orderKey, -goodSale.getGoodNum());
                throw new BusinessException("每人每天限购" + limitNum + "件！");
            }
            if (goodSale.getGoodNum().compareTo(num.intValue()) == 0 && goodSale.getGoodNum() > 0) {
                redisTemplate.expire(orderKey, 24, TimeUnit.HOURS);
            }
        }
        return inOrOut;
    }

    /**
     * 秒杀金额校验
     *
     * @param activeGoodStore
     */
    public void seckillAmountCheck(ActiveGoodStore activeGoodStore) {
        List<ActiveGoodStore.GoodSales> goodSales = activeGoodStore.getGoodSales();
        if (goodSales != null && !goodSales.isEmpty()) {
            Integer activeId = JSON.parseObject(activeGoodStore.getPassingData()).getInteger("activeId");
            EntityWrapper<ActiveGood> entityWrapper = new EntityWrapper<>();
            entityWrapper.eq("ACTIVE_TYPE", activeGoodStore.getActiveType());
            entityWrapper.eq("BUSINESS_ID", activeId);
            entityWrapper.eq("GOOD_STATUS", 1);
            entityWrapper.in("GOOD_SKU", goodSales.stream().map(ActiveGoodStore.GoodSales::getSku).collect(Collectors.toList()));
            List<ActiveGood> activeGoods = selectList(entityWrapper);
            if (activeGoods != null && !activeGoods.isEmpty()) {
                for (ActiveGood activeGood : activeGoods) {
                    for (ActiveGoodStore.GoodSales goodSale : goodSales) {
                        if (activeGood.getGoodSku().compareTo(goodSale.getSku()) == 0 && activeGood.getActivePrice().compareTo(PriceConversion.stringToInt(goodSale.getUnitPrice())) != 0) {
                            throw new BusinessException(goodSale.getSku() + "金额有误！");
                        }
                    }
                }
            }
        }
    }

    @Override
    public Boolean produceNewComers(ActiveGoodStore activeGoodStore) {
        //判断类型
        Assert.isTrue(activeGoodStore.getActiveType() == ActiveGoodsConstants.ActiveType.GROUP_JIELIGOU, "类型不正确");
        //判断商品不能为空
        Assert.isTrue(activeGoodStore.getGoodSales().isEmpty(), "商品不能为空");

        //判断商品是否存在
        EntityWrapper<ActiveGood> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("GOOD_ID", activeGoodStore.getGoodSales().get(0).getGoodId());
        List<ActiveGood> activeGoods = activeGoodMapper.selectList(entityWrapper);

        ActiveGood activeGood = !activeGoods.isEmpty() ? activeGoods.get(0) : null;
        Assert.notNull(activeGood, "新人团订单商品不存在");

        //判断新老用户参与
        JwtUserDetails jwtUserDetails = SecurityUserUtil.getUserDetails();
        //boolean checkUser = orderFeignClient.checkNewUser(jwtUserDetails.getUserId()).getData();
        Map<String, Object> map = new HashMap<>();
        map.put("userId", jwtUserDetails.getUserId());
        boolean checkUser = orderFeignClient.checkNewUser(map).getData();//false:否（新用户），true:是(老用户)

        //正常模式，新人才能参与
        if (checkUser && activeGood.getGoodLimit() == 1 && activeGood.getLimitType() == 0) {
            Assert.state(false, "该商品限新人参团");
        }

        PassingDataDto passingDataDto = disPassingData(activeGoodStore.getPassingData());
        if (Objects.nonNull(passingDataDto) && StringUtils.isNotBlank(passingDataDto.getGroupNo())) { //拼友
            //老带新，只能新用户参与拼团，老用户不可以
            if (activeGood.getGoodLimit() == 1 && activeGood.getLimitType() == 1 && checkUser) {
                Assert.state(false, "该商品限新人参团，团员必须是新人");
            }

            OrderGroup orderGroup = new OrderGroup();
            orderGroup.setGroupNo(passingDataDto.getGroupNo());
            OrderGroup groups = orderFeignClient.getGroupInfo(orderGroup);
            Assert.isTrue(groups == null || (groups.getExpireDate().getTime() < new Date().getTime()), "该团已过期");
        }
        return true;
    }

    private PassingDataDto disPassingData(String passingData) {
        if (StringUtils.isBlank(passingData)) return null;
        return JSONObject.parseObject(passingData, PassingDataDto.class);
    }

    public Integer decActiveVirtual(Integer businessId) {
        return activeGoodMapper.decActiveVirtual(businessId);
    }

    /**
     * 根据活动id删除商品
     *
     * @param businessId
     */
    @Override
    public void deleteBusinessId(Integer businessId) {
        EntityWrapper<ActiveGood> activeGoodEntityWrapper = new EntityWrapper<>();
        activeGoodEntityWrapper.eq("BUSINESS_ID", businessId);
        activeGoodMapper.delete(activeGoodEntityWrapper);
    }

    /**
     * 查询活动商品基本信息
     *
     * @param activeGood
     * @return
     */
    public Page<ActiveGood> queryBaseList(ActiveGood activeGood) {
        Page<ActiveGood> page = new Page<>(activeGood.getCurrentPage(), activeGood.getPageSize());
        List<ActiveGood> activeGoods = activeGoodMapper.queryBaseList(page, activeGood);
        page.setRecords(activeGoods);
        return page;
    }

    /**
     * 查询活动商品基本信息排序
     *
     * @param activeGoodEx
     * @return
     */
    public Page<ActiveGood> queryBaseOrder(ActiveGoodEx activeGoodEx) {
        Page<ActiveGood> page = new Page<>(activeGoodEx.getCurrentPage(), activeGoodEx.getPageSize());
        List<ActiveGood> activeGoods = activeGoodMapper.queryBaseOrder(page, activeGoodEx);
        page.setRecords(activeGoods);
        return page;
    }


    public void cleanGoodCache(Integer activeType) {
        /**
         * 自定义排序查询(包含置顶) currentPage_md5
         */
        String GOOD_INFO_QUERYORDERALL = "QUERYORDERALL:" + activeType;

        /**
         * 自定义排序查询 currentPage_md5
         */
        String GOOD_INFO_QUERYORDERLIST = "QUERYORDERLIST:" + activeType;

        /**
         * 置顶商品查询 currentPage_md5
         */
        String GOOD_INFO_QUERYORDERTOPLIST = "QUERYORDERTOPLIST:" + activeType;

        List<Object> list = new ArrayList<>();
        List<Object> objectList = scanKeys(GOOD_INFO_QUERYORDERALL);
        if (objectList != null && !objectList.isEmpty()) {
            list.addAll(objectList);
        }
        List<Object> objectList1 = scanKeys(GOOD_INFO_QUERYORDERLIST);
        if (objectList1 != null && !objectList1.isEmpty()) {
            list.addAll(objectList1);
        }
        List<Object> objectList2 = scanKeys(GOOD_INFO_QUERYORDERTOPLIST);
        if (objectList2 != null && !objectList2.isEmpty()) {
            list.addAll(objectList2);
        }
        if (list != null && !list.isEmpty()) {
            redisTemplate.opsForHash().delete("GOOD_INFO", list.toArray());
        }
    }

    public List<Object> scanKeys(String key) {
        List<Object> list = new ArrayList<>();
        Cursor<Map.Entry<Object, Object>> scan = redisTemplate.opsForHash().scan("GOOD_INFO", ScanOptions.scanOptions().match(key + "*").build());
        while (scan.hasNext()) {
            Map.Entry<Object, Object> entry = scan.next();
            list.add(entry.getKey());
        }
        return list;
    }

}
