package com.mmj.order.common.feign;

import com.mmj.common.exception.BusinessException;
import com.mmj.common.model.ReturnData;
import com.mmj.order.common.model.dto.OrderAfterSaleDto;
import com.mmj.order.common.model.vo.AddAfterSaleVo;
import com.mmj.order.model.AfterSales;
import com.mmj.order.model.dto.AfterSaleDto;
import com.mmj.order.model.vo.OrderAfterVo;
import feign.hystrix.FallbackFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AfterSaleFallbackFactory implements FallbackFactory<AfterSaleFeignClient> {

    private final Logger logger = LoggerFactory.getLogger(UserFallbackFactory.class);

    @Override
    public AfterSaleFeignClient create(Throwable cause) {
        return new AfterSaleFeignClient() {
            @Override
            public ReturnData<List<OrderAfterSaleDto>> getAfterSale(OrderAfterVo orderAfterVo) {
                throw new BusinessException("通过订单号与用户id获取订单售后信息失败!," + cause.getMessage(), 500);
            }

            @Override
            public ReturnData<Integer> getAfterSaleCount() {
                throw new BusinessException("获取当前用户的售后数量失败," + cause.getMessage(), 500);
            }

            @Override
            public ReturnData<List<AfterSales>> getUserOrderAfter() {
                throw new BusinessException("获取当前用户的订单售后列表失败" + cause.getMessage(), 500);
            }

            @Override
            public ReturnData<String> addAfterSale(AddAfterSaleVo addAfterSaleVo) {
                throw new BusinessException("取消订单新增售后接口失败" + cause.getMessage(), 500);
            }

            @Override
            public ReturnData<AfterSaleDto> getAfterSaleInfo(AddAfterSaleVo addAfterSaleVo) {
                throw new BusinessException("获取售后基本信息异常" + cause.getMessage(), 500);
            }

            @Override
            public ReturnData delAfterSaleNo(String orderNo) {
                throw new BusinessException("同步删除售后订单异常" + cause.getMessage(), 500);
            }
        };
    }
}
