package com.mmj.order.service;

import com.baomidou.mybatisplus.service.IService;
import com.mmj.order.constant.OrderType;
import com.mmj.order.model.OrderPayment;

import java.util.List;

/**
 * <p>
 * 订单支付信息表 服务类
 * </p>
 *
 * @author lyf
 * @since 2019-06-04
 */
public interface OrderPaymentService extends IService<OrderPayment> {

    OrderPayment selectOneByOrderNo(String orderNo);

    /**
     * 获取当前的支付信息
     *
     * @param orderNo
     * @param userId
     * @return
     */
    List<OrderPayment> getOrderPayment(String orderNo, Long userId);


    /**
     * 判断当前用户是否有个支付行为
     *
     * @param userId
     * @return
     */
    Boolean getPayAct(Long userId);

}
