package com.mmj.order.mapper;

import com.mmj.order.model.OrderKing;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 订单获得买买金表 Mapper 接口
 * </p>
 *
 * @author zhangyicao
 * @since 2019-07-10
 */
@Repository
public interface OrderKingMapper extends BaseMapper<OrderKing> {

    OrderKing getGiveBy(@Param("userId") Long userId);
}
