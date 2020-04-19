package com.mmj.good.feigin;


import com.mmj.common.interceptor.FeignRequestInterceptor;
import com.mmj.common.model.ReturnData;
import com.mmj.good.feigin.dto.InventoryQuery;
import com.mmj.good.feigin.dto.SkuQueryDto;
import com.mmj.good.feigin.dto.SkuQueryVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@FeignClient(name = "mmj-cloud-third", fallbackFactory = JunshuitanFallbackFactory.class, configuration = FeignRequestInterceptor.class)
public interface JunshuitanFeignClient {


    @RequestMapping(value = "/jushuitan/inventory/query", method = RequestMethod.POST)
    @ResponseBody
    ReturnData<List<InventoryQuery>> inventoryQuery(@RequestBody InventoryQuery inventoryQuery);

    /**
     * 普通商品查询
     * @param skuQueryVo
     * @return
     */
    @RequestMapping(value = "/jushuitan/sku/query", method = RequestMethod.POST)
    @ResponseBody
    ReturnData<List<SkuQueryDto>> skuQuery(@Valid @RequestBody SkuQueryVo skuQueryVo);

}

