package com.mmj.order.common.feign;

import com.mmj.common.exception.BusinessException;
import com.mmj.common.model.ReturnData;
import com.mmj.order.model.dto.InventoryQueryDto;
import com.mmj.order.model.vo.InventoryQueryVo;
import com.mmj.order.model.vo.LogisticsVo;
import com.mmj.order.model.vo.PollQueryResponse;
import feign.hystrix.FallbackFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class JunshuitanFallbackFactory implements FallbackFactory<JunshuitanFeignClient> {

    private final Logger logger = LoggerFactory.getLogger(GoodFallbackFactory.class);

    @Override
    public JunshuitanFeignClient create(Throwable cause) {
        logger.info("GoodFallbackFactory error message is {}", cause.getMessage());
        return new JunshuitanFeignClient() {
            @Override
            public ReturnData<List<InventoryQueryDto>> inventoryQuery(InventoryQueryVo inventoryQueryVo) {
                throw new BusinessException("调用聚水潭查询库存接口报错," + cause.getMessage(), 500);
            }

            @Override
            public ReturnData<PollQueryResponse> query(LogisticsVo logisticsVo) {
                throw new BusinessException("查询快递接口异常:" + cause.getMessage(), 500);
            }
        };
    }


}
