package com.mmj.order.mapper;

import com.mmj.order.model.Order;
import com.baomidou.mybatisplus.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 * 订单信息表 Mapper 接口
 * </p>
 *
 * @author zhangyicao
 * @since 2019-06-24
 */
public interface OrderMapper extends BaseMapper<Order> {

    List<Order> selectOrders();

}
