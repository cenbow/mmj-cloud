package com.mmj.order.service;

import com.mmj.order.model.Order;
import java.util.List;
import com.baomidou.mybatisplus.service.IService;




/**
 * <p>
 * 订单信息表 服务类
 * </p>
 *
 * @author lyf
 * @since 2019-05-27
 */
public interface OrderService extends IService<Order> {
    
    public void insertOrder(Order order);
    
    public List<Order> selectOrders();

}
