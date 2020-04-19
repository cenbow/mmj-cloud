package com.mmj.user.common.feigin;

import com.mmj.common.exception.BusinessException;
import com.mmj.common.model.ReturnData;
import com.mmj.user.common.model.OrderGood;
import com.mmj.user.common.model.OrderInfo;
import com.mmj.user.common.model.dto.OrderGoodsDto;
import com.mmj.user.common.model.vo.OrderFinishGoodVo;
import com.mmj.user.common.model.vo.OrderGoodVo;
import com.mmj.user.common.model.vo.OrderInfoGoodVo;
import com.mmj.user.common.model.vo.UserOrderVo;
import feign.hystrix.FallbackFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderGoodFallbackFactory implements FallbackFactory<OrderGoodFeignClient> {
    private final Logger logger = LoggerFactory.getLogger(OrderGoodFallbackFactory.class);

    @Override
    public OrderGoodFeignClient create(Throwable cause) {
        logger.info("OrderGoodFallbackFactory error message is {}", cause.getMessage());
        return new OrderGoodFeignClient() {
            @Override
            public ReturnData<List<OrderGoodsDto>> getOrderGoodList(OrderGoodVo orderGoodVo) {
                throw new BusinessException("调用订单获取商品信息报错," + cause.getMessage(), 500);
            }

            @Override
            public ReturnData<List<OrderInfo>> getUserAllOrderNos(UserOrderVo userOrderVo) {
                throw new BusinessException("调用订单获取订单信息报错," + cause.getMessage(), 500);
            }

            @Override
            public ReturnData< List<OrderGood>> getOrderInfoByGood(OrderInfoGoodVo orderInfoGoodVo) {
                throw new BusinessException("调用订单获取商品信息报错," + cause.getMessage(), 500);
            }

            @Override
            public ReturnData<OrderInfo> getOrderInfo(UserOrderVo userOrderVo) {
                throw new BusinessException("调用订单获取订单信息报错," + cause.getMessage(), 500);
            }

            @Override
            public ReturnData<List<OrderGoodsDto>> get(OrderFinishGoodVo orderFinishGoodVo) {
                throw new BusinessException("调用订单获取商品信息报错," + cause.getMessage(), 500);
            }
        };
    }
}
