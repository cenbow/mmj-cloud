package com.mmj.order.service.impl;

import com.mmj.order.model.Order;
import com.mmj.order.mapper.OrderMapper;
import com.mmj.order.service.OrderService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * 订单信息表 服务实现类
 * </p>
 *
 * @author lyf
 * @since 2019-05-27
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {
    
    @Autowired
    OrderMapper orderMapper;

    @Transactional
    @Override
    public void insertOrder(Order order) {
        orderMapper.insert(order);
        throw new NullPointerException();
    }

    @Override
    public List<Order> selectOrders() {
        return orderMapper.selectOrders();
    }

}
