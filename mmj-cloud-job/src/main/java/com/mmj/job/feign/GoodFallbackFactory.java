package com.mmj.job.feign;

import com.mmj.common.model.ReturnData;
import com.mmj.common.properties.SecurityConstants;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @description: 用户模块熔断
 * @auther: KK
 * @date: 2019/8/3
 */
@Component
@Slf4j
public class GoodFallbackFactory implements FallbackFactory<GoodFeignClient> {
    private final ReturnData returnData = new ReturnData(SecurityConstants.EXCEPTION_CODE, "fail");

    @Override
    public GoodFeignClient create(Throwable throwable) {
        return new GoodFeignClient() {
            @Override
            public ReturnData cleanExpire() {
                return returnData;
            }

            @Override
            public ReturnData clean() {
                return returnData;
            }

            @Override
            public ReturnData synGoodsStock() {
                return returnData;
            }

            @Override
            public ReturnData synGoodsStockZh() {
                return returnData;
            }

            @Override
            public ReturnData synStock1() {
                return returnData;
            }
        };
    }
}
