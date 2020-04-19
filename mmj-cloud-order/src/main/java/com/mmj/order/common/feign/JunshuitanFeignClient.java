package com.mmj.order.common.feign;


import com.mmj.common.interceptor.FeignRequestInterceptor;
import com.mmj.common.model.ReturnData;
import com.mmj.order.model.dto.InventoryQueryDto;
import com.mmj.order.model.vo.InventoryQueryVo;
import com.mmj.order.model.vo.LogisticsVo;
import com.mmj.order.model.vo.PollQueryResponse;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@FeignClient(name = "mmj-cloud-third", fallbackFactory = JunshuitanFallbackFactory.class, configuration = FeignRequestInterceptor.class)
public interface JunshuitanFeignClient {


    @RequestMapping(value = "/jushuitan/inventory/query", method = RequestMethod.POST)
    public  @ResponseBody
    ReturnData<List<InventoryQueryDto>> inventoryQuery(@RequestBody InventoryQueryVo inventoryQueryVo);



    /**
     * 查询快递
     */
    @PostMapping("/api/logistics/query")
    public ReturnData<PollQueryResponse> query(@Valid @RequestBody LogisticsVo logisticsVo);


}
