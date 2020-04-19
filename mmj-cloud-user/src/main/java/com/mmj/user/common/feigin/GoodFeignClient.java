package com.mmj.user.common.feigin;

import com.mmj.common.interceptor.FeignRequestInterceptor;
import com.mmj.common.model.ReturnData;
import com.mmj.user.common.model.GoodInfo;
import com.mmj.user.common.model.GoodSale;
import com.mmj.user.common.model.dto.CutGoodDto;
import com.mmj.user.common.model.vo.CutGoodVo;
import com.mmj.user.common.model.vo.GoodSaleVo;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "mmj-cloud-good", fallbackFactory = GoodFallbackFactory.class, configuration = FeignRequestInterceptor.class)
public interface GoodFeignClient {

    @RequestMapping(value = "/goodInfo/batchVerifyGoodSpu", method = RequestMethod.GET)
    @ResponseBody
    ReturnData batchVerifyGoodSpu(@RequestParam("spuList") List<String> spuList);

    @RequestMapping(value = "/goodInfo/getById/{id}", method = RequestMethod.POST)
    @ResponseBody
    GoodInfo getById(@PathVariable("id") Integer id);

    /**
     * 砍价-根据goodIds查询商品价格/销量
     *
     * @param goodVo
     * @return
     */
    @RequestMapping(value = "/goodSale/queryGroupByInfo", method = RequestMethod.POST)
    @ResponseBody
    ReturnData<List<CutGoodDto>> queryGroupByInfo(@RequestBody CutGoodVo goodVo);

    /**
     * 购物车-商品销售资料列表查询-简
     *
     * @param goodSale
     * @return
     */
    @RequestMapping(value = "/goodSale/queryList", method = RequestMethod.POST)
    @ResponseBody
    ReturnData<Object> queryGoodSaleList(@RequestBody GoodSaleVo goodSale);

    /**
     * 查询商品
     *
     * @param goodSale
     * @return
     */
    @RequestMapping(value = "/goodInfo/queryGoodTT", method = RequestMethod.POST)
    @ResponseBody
    ReturnData<List<com.mmj.common.model.good.GoodInfo>> queryGoodTT(@RequestBody com.mmj.common.model.good.GoodInfo goodSale);

}
