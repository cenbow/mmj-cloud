package com.mmj.order.service;

import com.baomidou.mybatisplus.service.IService;
import com.mmj.order.model.OrderKing;

import java.util.Map;

/**
 * <p>
 * 订单获得买买金表 服务类
 * </p>
 *
 * @author cgf
 * @since 2019-07-10
 */
public interface OrderKingService extends IService<OrderKing> {

    /**
     * 查询冻结的买买金数量
     * @param userId
     * @return
     */
    int frozenKingNum(Long userId);

    /**
     * 查询买买金订单
     * @param orderNo
     * @param orderStatus
     * @param afterSaleStatus
     * @return
     */
    public Map<String,Object> getKingByOrder(String orderNo, Integer orderStatus, Integer afterSaleStatus);

    boolean updateMMKing(String orderNo,Long userId, Integer status);

    String getGiveBy(Long userId);
}
