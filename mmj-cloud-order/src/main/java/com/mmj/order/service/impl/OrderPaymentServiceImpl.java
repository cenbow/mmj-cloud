package com.mmj.order.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.mmj.common.context.BaseContextHandler;
import com.mmj.common.properties.SecurityConstants;
import com.mmj.order.constant.OrderType;
import com.mmj.order.mapper.OrderPaymentMapper;
import com.mmj.order.model.Order;
import com.mmj.order.model.OrderPayment;
import com.mmj.order.service.OrderPaymentService;
import com.mmj.order.utils.OrderNoUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * <p>
 * 订单支付信息表 服务实现类
 * </p>
 *
 * @author lyf
 * @since 2019-06-04
 */
@Service
public class OrderPaymentServiceImpl extends ServiceImpl<OrderPaymentMapper, OrderPayment> implements OrderPaymentService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private OrderPaymentMapper orderPaymentMapper;
    @Autowired
    private OrderNoUtils orderNoUtils;

    @Override
    public OrderPayment selectOneByOrderNo(String orderNo) {
        orderNoUtils.shardingKey(orderNo);
        OrderPayment queryOrderPayment = new OrderPayment();
        queryOrderPayment.setOrderNo(orderNo);
        EntityWrapper<OrderPayment> orderPaymentEntityWrapper = new EntityWrapper<>(queryOrderPayment);
        return selectOne(orderPaymentEntityWrapper);
    }

    /**
     * @param orderNo
     * @param userId
     * @return
     */
    @Override
    public List<OrderPayment> getOrderPayment(String orderNo, Long userId) {
        orderNoUtils.shardingKey(orderNo);
        HashMap<String, Object> map = new HashMap<>();
        map.put("ORDER_NO", orderNo);
        map.put("CREATER_ID", userId);
        List<OrderPayment> list = orderPaymentMapper.selectByMap(map);
        return list;
    }

    /**
     * 判断当前用户有支付行为
     *
     * @param userId
     * @return
     */
    @Override
    public Boolean getPayAct(Long userId) {
        BaseContextHandler.set(SecurityConstants.SHARDING_KEY, userId);
        HashMap<String, Object> map = new HashMap<>();
        map.put("CREATER_ID", userId);
        List<OrderPayment> list = orderPaymentMapper.selectByMap(map);
        if (list.size() > 0 && list != null) {
            return true;
        } else {
            return false;
        }
    }

}
