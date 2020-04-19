package com.mmj.good.feigin;

import com.mmj.common.exception.BusinessException;
import com.mmj.common.model.ReturnData;
import com.mmj.good.feigin.dto.InventoryQuery;
import com.mmj.good.feigin.dto.SkuQueryDto;
import com.mmj.good.feigin.dto.SkuQueryVo;
import feign.hystrix.FallbackFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

@Component
public class JunshuitanFallbackFactory implements FallbackFactory<JunshuitanFeignClient> {

    private final Logger logger = LoggerFactory.getLogger(JunshuitanFallbackFactory.class);

    @Override
    public JunshuitanFeignClient create(Throwable cause) {
        logger.info("GoodFallbackFactory error message is {}", cause.getMessage());
        return new JunshuitanFeignClient() {
            @Override
            public ReturnData<List<InventoryQuery>> inventoryQuery(@RequestBody InventoryQuery inventoryQueryVo) {
                throw new BusinessException("调用聚水潭查询库存接口报错," + cause.getMessage(), 500);
            }

            @Override
            public ReturnData<List<SkuQueryDto>> skuQuery(@Valid @RequestBody SkuQueryVo skuQueryVo) {
                throw new BusinessException("调用普通商品查询接口报错," + cause.getMessage(), 500);
            }
        };
    }


}
