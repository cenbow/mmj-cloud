package com.mmj.order.common.feign;

import com.mmj.common.interceptor.FeignRequestInterceptor;
import com.mmj.common.model.ReturnData;
import com.mmj.order.common.model.dto.OrderAfterSaleDto;
import com.mmj.order.common.model.vo.AddAfterSaleVo;
import com.mmj.order.model.AfterSales;
import com.mmj.order.model.dto.AfterSaleDto;
import com.mmj.order.model.vo.OrderAfterVo;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "mmj-cloud-aftersale", fallbackFactory = AfterSaleFallbackFactory.class, configuration = FeignRequestInterceptor.class)
public interface AfterSaleFeignClient {

    /**
     * 通过订单号，用户id，获取订单信息
     *
     * @param orderAfterVo
     * @return
     */
    @RequestMapping(value = "/afterSales/get/afterSale/info", method = RequestMethod.POST)
    @ResponseBody
    ReturnData<List<OrderAfterSaleDto>> getAfterSale(@RequestBody OrderAfterVo orderAfterVo);

    /**
     * 获取当前用户的订单售后数量
     *
     * @return
     */
    @RequestMapping(value = "/afterSales/get/afterSaleCount", method = RequestMethod.POST)
    @ResponseBody
    ReturnData<Integer> getAfterSaleCount();


    /**
     * 获取当前用户的订单售后列表
     *
     * @return
     */
    @RequestMapping(value = "/afterSales/get/user/orderAfter", method = RequestMethod.POST)
    @ResponseBody
    ReturnData<List<AfterSales>> getUserOrderAfter();


    /**
     * 取消订单进入售后
     *
     * @param addAfterSaleVo
     * @return
     */
    @RequestMapping(value = "/afterSales/add/afterSale", method = RequestMethod.POST)
    @ResponseBody
    ReturnData<String> addAfterSale(@RequestBody AddAfterSaleVo addAfterSaleVo);


    @RequestMapping(value = "/afterSales/get/afterSaleInfo", method = RequestMethod.POST)
    @ResponseBody
    ReturnData<AfterSaleDto> getAfterSaleInfo(@RequestBody AddAfterSaleVo addAfterSaleVo);

    /**
     * 逻辑删除售后订单
     *
     * @param orderNo
     * @return
     */
    @RequestMapping(value = "/afterSales/delAfterSaleNo/{orderNo}", method = RequestMethod.POST)
    @ResponseBody
    ReturnData delAfterSaleNo(@PathVariable("orderNo") String orderNo);
}
