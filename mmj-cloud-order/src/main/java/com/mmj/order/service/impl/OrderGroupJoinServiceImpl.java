package com.mmj.order.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.mmj.common.context.BaseContextHandler;
import com.mmj.common.model.order.UserLotteryDto;
import com.mmj.common.properties.SecurityConstants;
import com.mmj.order.mapper.OrderGroupJoinMapper;
import com.mmj.order.model.OrderGroupJoin;
import com.mmj.order.service.OrderGroupJoinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * <p>
 * 参团信息表 服务实现类
 * </p>
 *
 * @author zhangyicao
 * @since 2019-06-05
 */
@Service
public class OrderGroupJoinServiceImpl extends ServiceImpl<OrderGroupJoinMapper, OrderGroupJoin> implements OrderGroupJoinService {

    @Autowired
    private OrderGroupJoinMapper groupJoinMapper;

    /**
     * 根据团号号获取团信息
     *
     * @return
     */
    @Override

    public List<OrderGroupJoin> getListByGroupNo(String groupNo) {
        BaseContextHandler.set(SecurityConstants.SHARDING_GROUP_KEY, groupNo);
        EntityWrapper<OrderGroupJoin> wrapper = new EntityWrapper<>();
        wrapper.eq("GROUP_NO", groupNo);
        wrapper.orderAsc(Collections.singletonList("JOIN_TIME"));
        return selectList(wrapper);
    }

    @Override
    public List<UserLotteryDto> getJoinUser(Integer lotteryId) {
        return groupJoinMapper.getJoinUser(lotteryId);
    }
}
