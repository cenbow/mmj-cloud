package com.mmj.user.common.feigin;

import com.mmj.common.exception.BusinessException;
import com.mmj.common.model.ReturnData;
import com.mmj.user.common.model.OrderInfo;
import com.mmj.user.common.model.dto.OrderGoodsDto;
import com.mmj.user.common.model.vo.MemberOrderVo;
import com.mmj.user.common.model.vo.OrderDetailVo;
import com.mmj.user.common.model.vo.OrderGoodVo;
import com.mmj.user.common.model.vo.UserOrderVo;
import feign.hystrix.FallbackFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

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
            public int frozenKingNum(Long userId) {
                throw new BusinessException("调用查询冻结的买买金接口报错", 500);
            }

            @Override
            public ReturnData<OrderInfo> getOrderByOrderNo(String orderNo) {
                throw new BusinessException("调用异步查询订单接口报错了，" + cause.getMessage(), 500);
            }


            @Override
            public ReturnData<OrderInfo> getAsyncOrderInfo(@RequestBody UserOrderVo userOrderVo) {
                throw new BusinessException("调用异步查询订单接口报错，" + cause.getMessage(), 500);
            }

            @Override
            public ReturnData<List<OrderInfo>> getOrderList(@RequestBody MemberOrderVo memberOrderVo) {
                throw new BusinessException("会员调用是否首单接口报错，" + cause.getMessage(), 500);
            }

            @Override
            public ReturnData<List<OrderInfo>> getAsyncOrderList(@RequestBody MemberOrderVo memberOrderVo) {
                throw new BusinessException("会员异步调用是否首单接口报错，" + cause.getMessage(), 500);
            }

            @Override
            public ReturnData<Double> getConsumeMoney(@RequestBody Long userId) {
                throw new BusinessException("调用历史消费金额报错，" + cause.getMessage(), 500);
            }

            @Override
            public ReturnData<Double> getAsyncConsumeMoney(@RequestBody Long userId) {
                throw new BusinessException("调用异步查询消息金额异常，" + cause.getMessage(), 500);
            }

            @Override
            public ReturnData<Double> getConsumeMoneyTwo(@RequestBody OrderDetailVo orderDetailVo) {
                throw new BusinessException("调用历史消费金额Two报错，" + cause.getMessage(), 500);
            }

            @Override
            public ReturnData<Double> getAsyncConsumeMoneyTwo(@RequestBody OrderDetailVo orderDetailVo) {
                throw new BusinessException("调用异步查询历史消费金额Two异常，" + cause.getMessage(), 500);
            }

            @Override
            public ReturnData<List<OrderGoodsDto>> getAsyncOrderGoodList(@RequestBody OrderGoodVo orderGoodVo) {
                throw new BusinessException("调用异步查询订单商品异常，" + cause.getMessage(), 500);
            }

            @Override
            public String getGiveBy(Long userId) {
                throw new BusinessException("查询买送活动订单报错，" + cause.getMessage(), 500);
            }

            @Override
            public boolean updateById(Object ok) {
                throw new BusinessException("更新买送订单买买金状态报错，" + cause.getMessage(), 500);
            }

            @Override
            public ReturnData<Boolean> checkNewUser(Map<String, Object> map) {
                throw new BusinessException("查询是否新用户报错，" + cause.getMessage(), 500);
            }
        };

    }
}
