package com.mmj.order.mapper;

import java.util.List;
import java.util.Set;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.mmj.common.model.UserOrderStatistics;
import com.mmj.order.model.OrderInfo;

/**
 * <p>
 * 订单信息表 Mapper 接口
 * </p>
 *
 * @author shenfuding
 * @since 2019-07-13
 */
@Repository
public interface OrderInfoMapper extends BaseMapper<OrderInfo> {

    /**
     * 查询用户消费总金额
     * 结果是long 取整数
     *
     * @param userId
     * @return
     */
    Double getConsumeMoneyTwo(@Param("userIds") List<Long> userId, @Param("orderNo") String orderNo);

    /**
     * 支付前查询历史消费金额
     *
     * @param userId
     * @return
     */
    Double getConsumeMoney(@Param("userIds") List<Long> userId);

    List<OrderInfo> getLotteryWaitPay(@Param("businessId") Integer businessId);


    List<UserOrderStatistics> getUsersOrdersDataForChannel(@Param("set") Set<Long> userIdSet,
                                                     @Param("startTime") String startTime,
                                                     @Param("endTime") String endTime);
}
