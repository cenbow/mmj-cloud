package com.mmj.order.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.mmj.order.model.OrderGroup;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 订单团信息表 Mapper 接口
 * </p>
 *
 * @author zhangyicao
 * @since 2019-06-05
 */
@Repository
public interface OrderGroupMapper extends BaseMapper<OrderGroup> {

    Integer selectFullGroupCount(OrderGroup orderGroup);

    /**
     * 支持各种团购活动
     * @param orderGroup
     * @return
     */
    Integer getCompletedGroupCount(OrderGroup orderGroup);

    OrderGroup getByGroupFromView(@Param("groupNo") String groupNo,@Param("orderNo") String orderNo);

    /**
     * 查询各种团购活动成团的团信息
     * @param orderGroup
     * @return
     */
    List<OrderGroup> getCompletedGroupList(OrderGroup orderGroup);
}
