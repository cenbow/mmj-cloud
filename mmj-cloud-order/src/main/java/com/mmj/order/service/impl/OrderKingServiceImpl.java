package com.mmj.order.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.google.common.collect.Maps;
import com.mmj.common.constants.OrderStatus;
import com.mmj.common.context.BaseContextHandler;
import com.mmj.common.properties.SecurityConstants;
import com.mmj.order.constant.AfterSalesStatus;
import com.mmj.order.constant.MMKingShareType;
import com.mmj.order.mapper.OrderKingMapper;
import com.mmj.order.model.OrderKing;
import com.mmj.order.service.OrderKingService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <p>
 * 订单获得买买金表 服务实现类
 * </p>
 *
 * @author cgf
 * @since 2019-07-10
 */
@Slf4j
@Service
public class OrderKingServiceImpl extends ServiceImpl<OrderKingMapper, OrderKing> implements OrderKingService {

    @Autowired
    private OrderKingMapper orderKingMapper;

    @Override
    public int frozenKingNum(Long userId) {
        EntityWrapper<OrderKing> wrapper = new EntityWrapper<>();
        wrapper.setSqlSelect("IFNULL(SUM(num),0) AS num");
        wrapper.where(String.format(" USER_ID = %d AND `STATUS` IN(0,2) ", userId));
        BaseContextHandler.set(SecurityConstants.SHARDING_KEY, userId);
        int cnt = Integer.parseInt(selectObj(wrapper).toString());
        log.info("###--冻结买买金数:{},userId:{}", cnt, userId);
        return cnt;
    }

    @Override
    public Map<String, Object> getKingByOrder(String orderNo, Integer orderStatus, Integer afterSaleStatus) {
        Assert.isTrue(StringUtils.isNotEmpty(orderNo), "订单号不能为空");
        Map<String, Object> result = Maps.newHashMapWithExpectedSize(3);

        List<OrderKing> list = getByOrderNo(orderNo);
        if (null == list || list.size() <= 0) {
            result.put("kingNum", 0);
            result.put("status", -1);
            result.put("msg", "查询不到对应的订单");
            return result;
        }

        AtomicInteger kingNum = new AtomicInteger(0);

        if (null != orderStatus && null == afterSaleStatus) {
            //查询没售后的订单
            list.parallelStream().forEach(ok -> {
                if (MMKingShareType.OrderKingStatus.NORMAL.intValue() == ok.getStatus())
                    kingNum.getAndAdd(ok.getNum());
            });
            result.put("kingNum", kingNum.intValue());
            result.put("status", 1);
            result.put("msg", orderStatus == OrderStatus.COMPLETED.getStatus() ? "已到账" : "确认收货后到账");
            return result;
        }

        if (null != afterSaleStatus) {
            //查询没售后的订单
            list.parallelStream().forEach(ok -> {
                if (MMKingShareType.OrderKingStatus.FROZEN.intValue() == ok.getStatus() ||
                        MMKingShareType.OrderKingStatus.DELETE.intValue() == ok.getStatus())
                    kingNum.getAndAdd(ok.getNum());
            });
            if (afterSaleStatus.intValue() == AfterSalesStatus.QUALITY_TEST_REFISE.getStatus()
                    || afterSaleStatus.intValue() == AfterSalesStatus.RETRUN_GOODS_REFUSE.getStatus()) {
                //拒绝退货和质检不通过 都不扣买买金
                list.parallelStream().forEach(ok -> {
                    if (MMKingShareType.OrderKingStatus.NORMAL.intValue() == ok.getStatus())
                        kingNum.getAndAdd(ok.getNum());
                });
            }
            result.put("kingNum", kingNum.intValue());
            result.put("status", 0);
            result.put("msg", afterSaleStatus.intValue() == AfterSalesStatus.RETURN_MONEY_FINISH.getStatus() ? "由于退款已收回" : "已到账");
            return result;
        }
        return null;
    }

    @Override
    public boolean updateMMKing(String orderNo, Long userId, Integer status) {
        OrderKing ok = new OrderKing();
        ok.setStatus(status);
        ok.setUpdateTime(new Date());
        EntityWrapper<OrderKing> wrapper = new EntityWrapper<>();
        wrapper.eq("ORDER_NO", orderNo);
        BaseContextHandler.set(SecurityConstants.SHARDING_KEY, userId);
        return update(ok, wrapper);
    }

    @Override
    public String getGiveBy(Long userId) {
        BaseContextHandler.set(SecurityConstants.SHARDING_KEY, userId);
        OrderKing ok = orderKingMapper.getGiveBy(userId);
        log.info("查询买送订单结果:{}", JSONObject.toJSONString(ok));
        if (null != ok)
            return JSONObject.toJSONString(ok);
        return null;
    }


    private List<OrderKing> getByOrderNo(String orderNo) {
        OrderKing ok = new OrderKing();
        ok.setOrderNo(orderNo);
        EntityWrapper<OrderKing> wrapper = new EntityWrapper<>(ok);
        return selectList(wrapper);
    }
}
