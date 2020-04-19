package com.mmj.order.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.mmj.order.mapper.OrderPackageLogMapper;
import com.mmj.order.model.OrderPackageLog;
import com.mmj.order.service.OrderPackageLogService;
import com.mmj.order.utils.OrderNoUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 订单包裹记录表 服务实现类
 * </p>
 *
 * @author lyf
 * @since 2019-06-04
 */
@Service
public class OrderPackageLogServiceImpl extends ServiceImpl<OrderPackageLogMapper, OrderPackageLog> implements OrderPackageLogService {
    @Autowired
    private OrderNoUtils orderNoUtils;

    @Override
    public List<OrderPackageLog> selectByOrderNo(String orderNo) {
        orderNoUtils.shardingKey(orderNo);
        OrderPackageLog queryOrderPackageLog = new OrderPackageLog();
        queryOrderPackageLog.setOrderNo(orderNo);
        EntityWrapper<OrderPackageLog> orderPackageEntityWrapper = new EntityWrapper<>(queryOrderPackageLog);
        return selectList(orderPackageEntityWrapper);
    }

    @Override
    public OrderPackageLog selectByPackageNo(String packageNo) {
        orderNoUtils.shardingKey(packageNo);
        OrderPackageLog queryOrderPackageLog = new OrderPackageLog();
        queryOrderPackageLog.setPackageNo(packageNo);
        EntityWrapper<OrderPackageLog> orderPackageEntityWrapper = new EntityWrapper<>(queryOrderPackageLog);
        return selectOne(orderPackageEntityWrapper);
    }
}
