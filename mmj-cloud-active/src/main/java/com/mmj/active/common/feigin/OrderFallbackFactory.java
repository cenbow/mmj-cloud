package com.mmj.active.common.feigin;

import com.alibaba.fastjson.JSONObject;
import com.mmj.active.common.model.OrderGroup;
import com.mmj.active.common.model.OrderGroupJoin;
import com.mmj.active.common.model.OrderInfo;
import com.mmj.active.common.model.dto.DecrGoodNum;
import com.mmj.active.common.model.dto.OrderSaveVo;
import com.mmj.active.common.model.dto.SaveOrderDto;
import com.mmj.active.relayLottery.model.dto.GroupInfoDto;
import com.mmj.common.exception.BusinessException;
import com.mmj.common.model.ReturnData;
import com.mmj.common.model.order.UserLotteryDto;
import com.mmj.common.properties.SecurityConstants;
import feign.hystrix.FallbackFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@Component
public class OrderFallbackFactory implements FallbackFactory<OrderFeignClient> {

    private final Logger logger = LoggerFactory.getLogger(OrderFallbackFactory.class);

    @Override
    public OrderFeignClient create(Throwable cause) {
        logger.info("OrderFallbackFactory error message is {}", cause.getMessage());
        return new OrderFeignClient() {
            @Override
            public OrderInfo getByOrderNo(String orderNo) {
                throw new BusinessException("调用查询订单接口报错," + cause.getMessage(), 500);
            }

            @Override
            public ReturnData<List<OrderGroup>> completedGroupList(OrderGroup orderGroup) {
                throw new BusinessException("调用查询团关系接口报错," + cause.getMessage(), 500);
            }

            @Override
            public ReturnData<Integer> completedGroupCount(OrderGroup orderGroup) {
                throw new BusinessException("调用查询团数量接口报错," + cause.getMessage(), 500);
            }

            @Override
            public GroupInfoDto groupInfo(@RequestParam("userid") Long userid,
                                          @RequestParam("groupNo") String groupNo,
                                          @RequestParam("orderNo") String orderNo) {
                throw new BusinessException("调用查询团信息接口报错," + cause.getMessage(), 500);
            }

            @Override
            public List<Map<String, Object>> getRedPackList() {
                throw new BusinessException("调用免费送红包接口报错," + cause.getMessage(), 500);
            }

            @Override
            public ReturnData<Boolean> decr(@RequestBody DecrGoodNum decrGoodNum) {
                throw new BusinessException("调用库存扣减接口报错," + cause.getMessage(), 500);
            }

            @Override
            public OrderGroup getGroupInfo(OrderGroup group) {
                throw new BusinessException("调用查询团信息接口报错," + cause.getMessage(), 500);
            }

            @Override
            public OrderInfo getForFeign(String orderNo) {
                throw new BusinessException("查询订单接口异常," + cause.getMessage(), 500);
            }

            @Override
            public List<OrderGroupJoin> getGroupJoin(OrderGroupJoin join) {
                throw new BusinessException("调用查询团关联信息接口报错," + cause.getMessage(), 500);
            }

            @Override
            public Integer getLotteryId(String groupNo) {
                throw new BusinessException("根据团号查询活动id接口报错," + cause.getMessage(), 500);
            }

            @Override
            public ReturnData<SaveOrderDto> saveOrder(OrderSaveVo orderSaveVo) {
                return new ReturnData<>(SecurityConstants.EXCEPTION_CODE, "下单失败");
            }

            @Override
            public ReturnData<Boolean> checkNewUser(@RequestBody Map<String, Object> map) {
                throw new BusinessException("查询是否新用户报错，" + cause.getMessage(), 500);
            }

            @Override
            public boolean toBeDelivered(String orderNo) {
                throw new BusinessException("订单发货报错，" + cause.getMessage(), 500);
            }

            @Override
            public String getMobile(JSONObject jsonObject) {
                throw new BusinessException("查询中奖人手机号报错，" + cause.getMessage(), 500);
            }

            @Override
            public List<UserLotteryDto> getJoinUser(Integer lotteryId) {
                throw new BusinessException("查询参与活动的用户订单信息，" + cause.getMessage(), 500);
            }

            @Override
            public List<OrderInfo> getLotteryWaitPay(Integer busId) {
                throw new BusinessException("查询抽奖待成团订单异常，" + cause.getMessage(), 500);
            }

            @Override
            public ReturnData<Boolean> decrAsync(@RequestBody DecrGoodNum decrGoodNum) {
                throw new BusinessException("调用库存扣减decrAsync接口报错," + cause.getMessage(), 500);
            }

            @Override
            public String orderGoodImg(String orderNo) {
                throw new BusinessException("查询订单商品图片失败," + cause.getMessage(), 500);
            }

            @Override
            public String getLogistics(String orderNo) {
                throw new BusinessException("查询订单的收货地址失败," + cause.getMessage(), 500);
            }
        };
    }

}
