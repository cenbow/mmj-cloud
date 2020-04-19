package com.mmj.order.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.mmj.common.model.order.UserLotteryDto;
import com.mmj.order.model.OrderGroupJoin;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 参团信息表 Mapper 接口
 * </p>
 *
 * @author zhangyicao
 * @since 2019-06-05
 */
@Repository
public interface OrderGroupJoinMapper extends BaseMapper<OrderGroupJoin> {

    List<OrderGroupJoin> getByGroupNoNoFromView(@Param("groupNo") String groupNo);

    List<UserLotteryDto> getJoinUser(@Param("lotteryId") Integer lotteryId);
}
