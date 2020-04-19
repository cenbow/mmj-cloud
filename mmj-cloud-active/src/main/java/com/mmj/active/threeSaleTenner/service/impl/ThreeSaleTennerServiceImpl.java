package com.mmj.active.threeSaleTenner.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.mmj.active.common.constants.ActiveGoodsConstants;
import com.mmj.active.common.feigin.GoodFeignClient;
import com.mmj.active.common.mapper.ActiveGoodMapper;
import com.mmj.active.common.model.ActiveGood;
import com.mmj.active.common.model.ActiveGoodEx;
import com.mmj.active.common.service.ActiveGoodService;
import com.mmj.active.threeSaleTenner.constant.ThreeSaleTennerStatus;
import com.mmj.active.threeSaleTenner.mapper.ThreeSaleTennerMapper;
import com.mmj.active.threeSaleTenner.model.ThreeSaleOrder;
import com.mmj.active.threeSaleTenner.model.ThreeSaleTenner;
import com.mmj.active.threeSaleTenner.service.ThreeSaleTennerService;
import com.mmj.common.model.*;
import com.mmj.common.properties.SecurityConstants;
import com.mmj.common.utils.SecurityUserUtil;
import com.xiaoleilu.hutool.date.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * <p>
 * 十元三件活动表 服务实现类
 * </p>
 *
 * @author dashu
 * @since 2019-06-12
 */
@Slf4j
@Service
public class ThreeSaleTennerServiceImpl extends ServiceImpl<ThreeSaleTennerMapper, ThreeSaleTenner> implements ThreeSaleTennerService {
    @Autowired
    private ThreeSaleTennerMapper threeSaleTennerMapper;
    @Autowired
    private ActiveGoodService activeGoodService;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private GoodFeignClient goodFeignClient;
    @Autowired
    private ActiveGoodMapper activeGoodMapper;


    @Override
    public ReturnData<Object> save(ThreeSaleTenner entity) {
        ReturnData<Object> rd = new ReturnData<>();
        if(entity.getLimitHours() == null){
            rd.setCode(SecurityConstants.FAIL_CODE);
            rd.setDesc("参与时间间隔为空");
            return rd;
        }
        JwtUserDetails userDetails = SecurityUserUtil.getUserDetails();
        if(entity.getInfoId() == null){
            entity.setCreaterId(userDetails.getUserId());
            entity.setCreaterTime(DateUtil.date());
            threeSaleTennerMapper.insertAllColumn(entity);
        }else{
            entity.setModifyId(userDetails.getUserId());
            entity.setModifyTime(DateUtil.date());
            threeSaleTennerMapper.updateById(entity);
        }
        rd.setCode(SecurityConstants.SUCCESS_CODE);
        rd.setData(entity.getInfoId());

        //清除小程序端查询商品的缓存
        redisTemplate.delete("ThreeSaleTennerGood");
        return rd;
    }


    @Override
    public ThreeSaleTenner query() {
        return threeSaleTennerMapper.selectOne(new ThreeSaleTenner());
    }

    @Override
    public ThreeSaleTenner selectThreeSaleTenner() {
        return threeSaleTennerMapper.selectOne(new ThreeSaleTenner());
    }


    /**
     * @return
     * isBuy: true 可以购买， false： 不可以购买
     * shareTime： 分享时间
     * buyCout: 购买次数
     */
    @Override
    public Map<String, Object> selectIsBuy(Long userid,Integer infoId){
        Map<String,Object> map = new HashMap<>();
        Long shareTime = (Long) redisTemplate.opsForValue().get("3sale10_" + userid);
        if (shareTime == null){
            map.put("isBuy",true);
        }else{
            Integer buyCount = (Integer) redisTemplate.opsForHash().get("3sale10", "3sale10_" + userid);
            if(buyCount != null && buyCount >0){
                map.put("isBuy",true);
                map.put("buyCount",buyCount);
            }else{
                map.put("shareTime",shareTime > 1 ? shareTime: null );
                map.put("isBuy",false);
            }
        }

        ThreeSaleOrder threeSaleOrder = (ThreeSaleOrder) redisTemplate.opsForValue().get("ThreeSaleTenner:" + userid);  //获取订单号
        if(threeSaleOrder != null){
            map.put("orderNo",threeSaleOrder.getOrderNo());  //订单号
            map.put("orderStatus",threeSaleOrder.getOrderStatus());   //订单状态 1:待付款  2：取消付款 3：已支付  4:已分享
        }
        return map;
    }


    @Override
    public Map<String,Object> addShareTime(Long userid, Integer status,String orderNo) {
        Map<String,Object> map = new HashMap<>();
        String key = "3sale10_"+userid;
        map.put("orderNo",orderNo);
        switch (status){
            case ThreeSaleTennerStatus.WAIT_PAY: //待付款
                ThreeSaleOrder threeSaleOrder = new ThreeSaleOrder();
                threeSaleOrder.setOrderNo(orderNo);
                threeSaleOrder.setOrderStatus(1);
                redisTemplate.opsForValue().set("ThreeSaleTenner:"+userid,threeSaleOrder);  //将订单号添加到缓存中
                break;

            case ThreeSaleTennerStatus.CANCEL_PAY://取消付款
                ThreeSaleOrder saleOrder = (ThreeSaleOrder) redisTemplate.opsForValue().get("ThreeSaleTenner:" + userid);
                if(null == saleOrder || saleOrder.getOrderStatus() == 3){   //代表分享完成后取消付款,或者是 支付完成后取消付款
                    Long shareTime = (Long) redisTemplate.opsForValue().get(key);
                    if(shareTime > 0){
                        redisTemplate.opsForHash().increment("3sale10",key,1);
                    }else{
                        redisTemplate.delete(key);
                    }
                }
                redisTemplate.delete("ThreeSaleTenner:"+userid);  //删除订单号缓存
                break;

            case  ThreeSaleTennerStatus.FINISH_PAY: //支付完成
                if(redisTemplate.hasKey(key)){
                    redisTemplate.opsForHash().increment("3sale10",key,-1);
                    redisTemplate.opsForValue().set(key,1L); //抽奖次数购买
                }else{
                    redisTemplate.opsForValue().set(key,0L); //自身购买
                }
                ThreeSaleOrder threeSale = new ThreeSaleOrder();
                threeSale.setOrderNo(orderNo);
                threeSale.setOrderStatus(3);
                redisTemplate.opsForValue().set("ThreeSaleTenner:"+userid,threeSale);  //将订单号添加到缓存中
                break;

            case ThreeSaleTennerStatus.SHARE_FINISH://分享完成
                Long currentTiem = new Date().getTime();
                redisTemplate.opsForValue().set(key,currentTiem);
                map.put("shareTime",currentTiem); //返回前端分享时间
                Integer limitHours = threeSaleTennerMapper.selectOne(new ThreeSaleTenner()).getLimitHours();
                redisTemplate.expire("3sale10_"+userid,limitHours, TimeUnit.HOURS);
                redisTemplate.delete("ThreeSaleTenner:"+userid);  //删除订单号缓存
                break;
        }
        return map;
    }


    @Override
    public boolean addBuyCout(Long userid) {
        String key = "3sale10";
        String attributeKey = "3sale10_"+userid;
        redisTemplate.opsForHash().increment(key, attributeKey,1);
        //抽奖中次数，这个时候取消分享的过期时间， 因为只要还有购买次数，就不需要展示过期时间
        redisTemplate.persist(attributeKey);
        return true;
    }

    @Override
    public Page<ActiveGood> selectGoods(ThreeSaleTenner entity) {
        String key = null;
        try {
            key= md5Key(entity);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Boolean hasKey = redisTemplate.opsForHash().hasKey("ThreeSaleTennerGood", key);
        if(hasKey){
            Page<ActiveGood> page = (Page<ActiveGood>) redisTemplate.opsForHash().get("ThreeSaleTennerGood",key);
            log.info("-->十元三件查询商品,直接从缓存中获取商品数据:{}",JSON.toJSONString(page));
            return page;
        }else{
            ThreeSaleTenner threeSaleTenner = threeSaleTennerMapper.selectOne(new ThreeSaleTenner());
            EntityWrapper<ActiveGood> entityWrapper = new EntityWrapper();
            entityWrapper.eq("ACTIVE_TYPE",ActiveGoodsConstants.ActiveType.TEN_YUAN_THREE_PIECES);
            entityWrapper.groupBy("GOOD_ID");
            entityWrapper.orderBy("GOOD_ORDER DESC,GOOD_ID DESC");
            List<ActiveGood> activeGoodList = activeGoodService.selectList(entityWrapper);
            log.info("-->十元三件查询商品,分页查询active表数据:{}",JSON.toJSONString(activeGoodList));
            List<Integer> collect = activeGoodList.stream().map(ActiveGood::getGoodId).collect(Collectors.toList());
            ThreeSaleTennerOrder threeSaleTennerOrder = JSON.parseObject(JSON.toJSONString(threeSaleTenner), ThreeSaleTennerOrder.class);
            threeSaleTennerOrder.setGoodIdList(collect);
            threeSaleTennerOrder.setCurrentPage(entity.getCurrentPage());
            threeSaleTennerOrder.setPageSize(entity.getPageSize());
            log.info("-->十元三件查询商品,调用俊哥接口,将商品进行排序入参:{}",JSON.toJSONString(threeSaleTennerOrder));
            List<Integer> goodIds = goodFeignClient.threeSaleTennerOrder(threeSaleTennerOrder).getData();
            log.info("-->十元三件查询商品,调用俊哥接口,排序后的商品ids:{}",JSON.toJSONString(goodIds));
            List<ActiveGood> list = null;
            if(CollectionUtils.isNotEmpty(goodIds)){
                ActiveGoodEx activeGoodEx = new ActiveGoodEx();
                activeGoodEx.setGoodIds(goodIds);
                activeGoodEx.setActiveType(ActiveGoodsConstants.ActiveType.TEN_YUAN_THREE_PIECES);
                if(CollectionUtils.isNotEmpty(goodIds)){
                    List<String> goodIdsJoin = new ArrayList<>();
                    CollectionUtils.collect(goodIds,
                            new Transformer() {
                                public Object transform(Object input) {
                                    return String.valueOf(input);
                                }
                            }, goodIdsJoin);
                    activeGoodEx.setOrderSql(String.format(" FIELD(GOOD_ID, %s) ", String.join(",",goodIdsJoin)));
                }
                list = activeGoodMapper.queryBaseOrder(new Page<>(1,entity.getPageSize()),activeGoodEx);
            }
            Page<ActiveGood> page = new Page<>(entity.getCurrentPage(),entity.getPageSize());
            page.setRecords(list);
            page.setTotal(activeGoodList.size()); //总数量
            redisTemplate.opsForHash().put("ThreeSaleTennerGood",key,page);
            return page;
        }
    }

    public String md5Key(Object o) throws UnsupportedEncodingException {
        return DigestUtils.md5DigestAsHex(JSON.toJSONString(o).getBytes("UTF-8"));
    }

}
