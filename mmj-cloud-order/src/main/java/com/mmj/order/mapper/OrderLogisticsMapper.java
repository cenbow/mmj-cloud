package com.mmj.order.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.mmj.order.model.OrderLogistics;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 订单快递信息表 Mapper 接口
 * </p>
 *
 * @author zhangyicao
 * @since 2019-06-24
 */
@Repository
public interface OrderLogisticsMapper extends BaseMapper<OrderLogistics> {

    List<OrderLogistics> getUser(@Param("consumerName") String consumerName,
                                 @Param("consumerMobile") String consumerMobile,
                                 @Param("page") Integer page, @Param("size") Integer size);

    int getUserCount(@Param("consumerName") String consumerName,
                     @Param("consumerMobile") String consumerMobile);
}
