package com.mmj.active.callCharge.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.mmj.active.callCharge.mapper.CallChargeGoodsMapper;
import com.mmj.active.callCharge.model.CallChargeGoods;
import com.mmj.active.callCharge.model.dto.RechargeGoodsDto;
import com.mmj.active.callCharge.service.CallChargeGoodsService;
import com.mmj.active.callCharge.service.CallChargeRecordService;
import com.mmj.common.model.JwtUserDetails;
import com.mmj.common.utils.PriceConversion;
import com.mmj.common.utils.SecurityUserUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author KK
 * @since 2019-08-31
 */
@Slf4j
@Service
public class CallChargeGoodsServiceImpl extends ServiceImpl<CallChargeGoodsMapper, CallChargeGoods> implements CallChargeGoodsService {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    private final static String CALL_CHARGE_GOODS = "CALL_CHARGE:GOODS:";
    private final static String CALL_CHARGE_TMP_MSG = "CALL_CHARGE:TMP_MSG";
    @Autowired
    private CallChargeRecordService callChargeRecordService;

    /**
     * 获取用户信息
     *
     * @return
     */
    private JwtUserDetails getUserDetails() {
        JwtUserDetails jwtUserDetails = SecurityUserUtil.getUserDetails();
        Assert.notNull(jwtUserDetails, "缺少用户信息");
        return jwtUserDetails;
    }

    /**
     * 获取商品剩余可用库存
     *
     * @param goodsId
     * @return
     */
    @Override
    public int getTodaySendNumber(Integer goodsId) {
        String key = CALL_CHARGE_GOODS + goodsId;
        String numStr = redisTemplate.opsForValue().get(key);
        return StringUtils.isBlank(numStr) ? 0 : Integer.parseInt(numStr);
    }

    @Override
    public int deductionStock(Integer goodsId, long num) {
        String key = CALL_CHARGE_GOODS + goodsId;
        Long stockNum = redisTemplate.opsForValue().increment(key, num);
        log.info("=> 话费商品库存 key:{},num:{},stockNum:{}", key, num, stockNum);
        return stockNum.intValue();
    }

    public List<RechargeGoodsDto> getRechargeGoods() {
        JwtUserDetails jwtUserDetails = getUserDetails();
        EntityWrapper<CallChargeGoods> callChargeGoodsEntityWrapper = new EntityWrapper<>();
        callChargeGoodsEntityWrapper.orderBy("ORIGINAL_PRICE");
        List<CallChargeGoods> callChargeGoodsList = selectList(callChargeGoodsEntityWrapper);
        List<RechargeGoodsDto> rechargeGoodsDtos = Lists.newArrayListWithCapacity(callChargeGoodsList.size());
        callChargeGoodsList.forEach(callChargeGoods -> {
            RechargeGoodsDto rechargeGoodsDto = new RechargeGoodsDto();
            rechargeGoodsDto.setGoodsId(callChargeGoods.getId());
            rechargeGoodsDto.setGoodsTitle(callChargeGoods.getGoodsTitle());
            boolean hasRight = getTodaySendNumber(callChargeGoods.getId()) > 0 ? callChargeRecordService.userRight(jwtUserDetails.getUserId()) : false;
            rechargeGoodsDto.setHasRight(hasRight);
            rechargeGoodsDto.setDiscountedPrice(PriceConversion.intToString(hasRight ? callChargeGoods.getRightPrice() : callChargeGoods.getUnitPrice()));
            if (hasRight) {
                rechargeGoodsDto.setMemberPrice(PriceConversion.intToString(callChargeGoods.getOriginalPrice() - callChargeGoods.getRightPrice()));
            }
            rechargeGoodsDto.setUnitPrice(PriceConversion.intToString(callChargeGoods.getOriginalPrice() - callChargeGoods.getUnitPrice()));
            rechargeGoodsDto.setOriginalPrice(PriceConversion.intToString(callChargeGoods.getOriginalPrice()));
            rechargeGoodsDto.setTodayLastNumber(getTodaySendNumber(callChargeGoods.getId()));
            rechargeGoodsDtos.add(rechargeGoodsDto);
        });
        return rechargeGoodsDtos;
    }

    @Override
    public void restartTask() {
        EntityWrapper<CallChargeGoods> callChargeGoodsEntityWrapper = new EntityWrapper<>();
        List<CallChargeGoods> callChargeGoodsList = selectList(callChargeGoodsEntityWrapper);
        callChargeGoodsList.stream().forEach(goods -> {
            String key = CALL_CHARGE_GOODS + goods.getId();
            log.info("=> 重新初始化话费商品 goodsId:{},todayPlanNumber:{}", goods.getId(), goods.getTodayPlanNumber());
            redisTemplate.opsForValue().set(key, goods.getTodayPlanNumber().toString(), 1, TimeUnit.DAYS);
            EntityWrapper<CallChargeGoods> queryCallChargeGoodsEntityWrapper = new EntityWrapper<>();
            queryCallChargeGoodsEntityWrapper.eq("ID", goods.getId());
            CallChargeGoods updateCallChargeGoods = new CallChargeGoods();
            updateCallChargeGoods.setTotalSendNumber(goods.getTotalSendNumber() + goods.getTodaySendNumber());
            boolean result = update(updateCallChargeGoods, queryCallChargeGoodsEntityWrapper);
            log.info("=> 重新计划发放数量 goodsId:{},todaySendNumber:{},todaySendNumber:{},result:{}", goods.getId(),
                    goods.getTodaySendNumber(), goods.getTodayPlanNumber(), result);
        });
    }

    @Override
    public void statSendNumber() {
        EntityWrapper<CallChargeGoods> callChargeGoodsEntityWrapper = new EntityWrapper<>();
        List<CallChargeGoods> callChargeGoodsList = selectList(callChargeGoodsEntityWrapper);
        callChargeGoodsList.stream().forEach(goods -> {
            int stockNum = getTodaySendNumber(goods.getId());
            EntityWrapper<CallChargeGoods> queryCallChargeGoodsEntityWrapper = new EntityWrapper<>();
            queryCallChargeGoodsEntityWrapper.eq("ID", goods.getId());
            CallChargeGoods updateCallChargeGoods = new CallChargeGoods();
            updateCallChargeGoods.setTodaySendNumber(goods.getTodayPlanNumber() - stockNum);
            boolean result = update(updateCallChargeGoods, queryCallChargeGoodsEntityWrapper);
            log.info("=> 话费商品同步发放数量->DB goodsId:{},stockNum:{},todaySendNumber:{},result:{}", goods.getId(),
                    stockNum, updateCallChargeGoods.getTodaySendNumber(), result);
        });
    }
}
