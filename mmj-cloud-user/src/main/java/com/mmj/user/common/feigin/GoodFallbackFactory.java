package com.mmj.user.common.feigin;

import com.mmj.common.exception.BusinessException;
import com.mmj.common.model.ReturnData;
import com.mmj.common.properties.SecurityConstants;
import com.mmj.user.common.model.GoodInfo;
import com.mmj.user.common.model.GoodSale;
import com.mmj.user.common.model.dto.CutGoodDto;
import com.mmj.user.common.model.vo.CutGoodVo;
import com.mmj.user.common.model.vo.GoodSaleVo;
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
            public ReturnData batchVerifyGoodSpu(List<String> spuList) {
                throw new BusinessException("调用商品SPU验证接口报错," + cause.getMessage(), 500);
            }

            @Override
            public GoodInfo getById(Integer id) {
                throw new BusinessException("调用查询商品接口报错," + cause.getMessage(), 500);
            }

            @Override
            public ReturnData<List<CutGoodDto>> queryGroupByInfo(CutGoodVo goodVo) {
                return new ReturnData<>(SecurityConstants.EXCEPTION_CODE, "调用商品信息失败");
            }

            @Override
            public ReturnData<Object> queryGoodSaleList(GoodSaleVo goodSale) {
                return new ReturnData<>(SecurityConstants.EXCEPTION_CODE, "调用商品信息失败");
            }

            @Override
            public ReturnData<List<com.mmj.common.model.good.GoodInfo>> queryGoodTT(com.mmj.common.model.good.GoodInfo goodSale) {
                return new ReturnData<>(SecurityConstants.EXCEPTION_CODE, "调用商品信息失败");
            }
        };
    }

}
