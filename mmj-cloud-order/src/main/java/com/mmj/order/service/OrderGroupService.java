package com.mmj.order.service;

import com.baomidou.mybatisplus.service.IService;
import com.mmj.order.common.model.dto.OrderCheckDto;
import com.mmj.order.model.OrderGroup;
import com.mmj.order.model.OrderInfo;
import com.mmj.order.model.dto.GroupInfoDto;
import com.mmj.order.model.dto.PayResultDto;
import com.mmj.order.model.vo.OrderSaveVo;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 订单团信息表 服务类
 * </p>
 *
 * @author zhangyicao
 * @since 2019-06-11
 */
public interface OrderGroupService extends IService<OrderGroup> {

    /**
     * 下单验证团订单参数是否正确，并返回下单人身份和团订单支付过期时间
     *
     * @param orderSaveVo
     * @param userId
     * @return true拼主 false拼友
     */
    OrderCheckDto checkGroupOrder(OrderSaveVo orderSaveVo, Long userId);

    OrderGroup getByGroupNo(String groupNo);

    List<OrderGroup> getCompletedGroupList(OrderGroup orderGroup);

    Integer getCompletedGroupCount(OrderGroup orderGroup);

    /**
     * 获取团信息
     *
     * @param userid
     * @param groupNo
     * @param orderNo
     * @return
     */
    GroupInfoDto getGroupInfo(Long userid, String groupNo, String orderNo);

    void payOrder(OrderInfo orderInfo);

    List<Map<String, Object>> getRedPackList();

    OrderGroup getByOrderNo(String orderNo);
}
