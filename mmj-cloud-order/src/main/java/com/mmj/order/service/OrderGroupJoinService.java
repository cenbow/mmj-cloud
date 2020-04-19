package com.mmj.order.service;

import com.baomidou.mybatisplus.service.IService;
import com.mmj.common.model.order.UserLotteryDto;
import com.mmj.order.model.OrderGroupJoin;

import java.util.List;

/**
 * <p>
 * 参团信息表 服务类
 * </p>
 *
 * @author zhangyicao
 * @since 2019-06-05
 */
public interface OrderGroupJoinService extends IService<OrderGroupJoin> {

    /**
     * 根据团号号获取团信息
     *
     * @return
     */
    List<OrderGroupJoin> getListByGroupNo(String groupNo);


    List<UserLotteryDto> getJoinUser(Integer lotteryId);
}
