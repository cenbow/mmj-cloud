package com.mmj.aftersale.common.feigin;

import com.mmj.aftersale.common.model.*;
import com.mmj.aftersale.model.dto.OrderGroup;
import com.mmj.aftersale.model.vo.OrderAfterVo;
import com.mmj.common.exception.BusinessException;
import com.mmj.common.model.ReturnData;
import feign.hystrix.FallbackFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Component
public class OrderFallbackFactory implements FallbackFactory<OrderFeignClient> {

    private final Logger logger = LoggerFactory.getLogger(OrderFallbackFactory.class);

    @Override
    public OrderFeignClient create(Throwable cause) {
        logger.info("OrderFallbackFactory error message is {}", cause.getMessage());
        return new OrderFeignClient() {
            @Override
            public ReturnData<OrderInfo> getOrderInfo(@RequestBody UserOrderVo userOrderVo) {
                throw new BusinessException("调用查询订单接口报错，" + cause.getMessage(), 500);
            }

            @Override
            public ReturnData<OrderInfo> getOrderByOrderNo(String orderNo) {
                throw new BusinessException("调用异步查询订单接口报错了，" + cause.getMessage(), 500);
            }

            @Override
            public ReturnData<OrderPayment> getOrderPay(String orderNo) {
                throw new BusinessException("调用查询订单支付接口报错," + cause.getMessage(), 500);
            }

            @Override
            public ReturnData<List<OrderPackageDto>> getOrderPackage(OrderGoodVo orderGoodVo) {
                throw new BusinessException("通过订单号获取包裹信息接口报错," + cause.getMessage(), 500);
            }

            @Override
            public ReturnData<List<OrderGoodsDto>> getOrderGoodList(@RequestBody OrderGoodVo orderGoodVo) {
                throw new BusinessException("通过订单号获取订单商品信息接口报错," + cause.getMessage(), 500);
            }

            @Override
            public ReturnData<Boolean> close(@RequestBody List<String> orderNos) {
                throw new BusinessException("调用关闭订单接口报错，" + cause.getMessage(), 500);
            }

            @Override
            public OrderGroup getGroupInfo(OrderGroup group) {
                throw new BusinessException("查询免费送团信息报错，" + cause.getMessage(), 500);
            }

            @Override
            public ReturnData<Object> updateAfterSaleFlag(UserOrderVo userOrderVo) {
                throw new BusinessException("调用修改订单售后标识接口错误，" + cause.getMessage(), 500);
            }

            @Override
            public ReturnData<List<OrderLogistics>> getLogistics(UserOrderVo userOrderVo) {
                throw new BusinessException("调用获取订单收件人信息接口异常:" + cause.getMessage(), 500);
            }

            @Override
            public ReturnData<OrderPayment> selectByOrderPayment(OrderAfterVo orderAfterVo) {
                throw new BusinessException("调用查询订单支付信息报错," + cause.getMessage(), 500);
            }
        };
    }

}
