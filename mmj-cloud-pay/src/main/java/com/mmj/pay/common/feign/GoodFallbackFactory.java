package com.mmj.pay.common.feign;

import com.mmj.common.exception.BusinessException;
import com.mmj.common.model.ReturnData;
import com.mmj.pay.common.model.dto.GoodInfo;
import com.mmj.pay.common.model.dto.GoodSale;
import feign.hystrix.FallbackFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public class GoodFallbackFactory implements FallbackFactory<GoodFeignClient> {

    private final Logger logger = LoggerFactory.getLogger(GoodFallbackFactory.class);

    @Override
    public GoodFeignClient create(Throwable cause) {
        logger.info("GoodFallbackFactory error message is {}", cause.getMessage());
        return new GoodFeignClient() {
            @Override
            public ReturnData<Object> queryList(GoodSale goodSale) {
                throw new BusinessException("查询单个商品sku报错!," + cause.getMessage(), 500);
            }

            @Override
            public GoodInfo getById(Integer id) {
                throw new BusinessException("更具商品id查询商品信息报错:" + cause.getMessage(), 500);
            }
        };

    }
}
