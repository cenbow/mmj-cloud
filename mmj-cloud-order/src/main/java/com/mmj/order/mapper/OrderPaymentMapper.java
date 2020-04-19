package com.mmj.order.mapper;

import com.mmj.order.model.OrderPayment;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 订单支付信息表 Mapper 接口
 * </p>
 *
 * @author zhangyicao
 * @since 2019-06-24
 */
@Repository
public interface OrderPaymentMapper extends BaseMapper<OrderPayment> {

    /**
     * 通过订单号 获取支付信息
     *
     * @param orderNo
     * @return
     */
    List<OrderPayment> selectByOrderNo(@Param(value = "orderNo") String orderNo);

}
