package com.mmj.aftersale.common.feigin;

import com.mmj.aftersale.common.model.*;
import com.mmj.aftersale.model.dto.OrderGroup;
import com.mmj.aftersale.model.vo.OrderAfterVo;
import com.mmj.common.interceptor.FeignRequestInterceptor;
import com.mmj.common.model.ReturnData;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "mmj-cloud-order", fallbackFactory = OrderFallbackFactory.class, configuration = FeignRequestInterceptor.class)
public interface OrderFeignClient {

    @RequestMapping(value = "/orderInfo/get/orderInfo", method = RequestMethod.POST)
    @ResponseBody
    ReturnData<OrderInfo> getOrderInfo(@RequestBody UserOrderVo userOrderVo);

    @RequestMapping(value = "/async/getOrderByOrderNo/{orderNo}", method = RequestMethod.POST)
    ReturnData<OrderInfo> getOrderByOrderNo(@PathVariable("orderNo") String orderNo);

    @RequestMapping(value = "/orderInfo/orderPay", method = RequestMethod.POST)
    @ResponseBody
    ReturnData<OrderPayment> getOrderPay(@RequestParam("orderNo") String orderNo);

    @RequestMapping(value = "/async/get/orderPackages", method = RequestMethod.POST)
    @ResponseBody
    ReturnData<List<OrderPackageDto>> getOrderPackage(@RequestBody OrderGoodVo orderGoodVo);

    @RequestMapping(value = "/async/get/orderGoods", method = RequestMethod.POST)
    @ResponseBody
    ReturnData<List<OrderGoodsDto>> getOrderGoodList(@RequestBody OrderGoodVo orderGoodVo);

    @RequestMapping(value = "/orderInfo/close", method = RequestMethod.POST)
    @ResponseBody
    ReturnData<Boolean> close(@RequestBody List<String> orderNos);

    @RequestMapping(value = "/order/orderGroup/groupInfo", method = RequestMethod.POST)
    OrderGroup getGroupInfo(@RequestBody OrderGroup group);
    /**
     * 修改订单售后标识
     */
    @RequestMapping(value = "/orderInfo/update/afterSale/flag", method = RequestMethod.POST)
    ReturnData<Object> updateAfterSaleFlag(@RequestBody UserOrderVo userOrderVo);


    @RequestMapping(value = "/orderInfo/get/logistics", method = RequestMethod.POST)
    @ResponseBody
    ReturnData<List<OrderLogistics>> getLogistics(@RequestBody UserOrderVo userOrderVo);

    /**
     * 根据订单号查询订单支付信息
     * @param orderAfterVo
     * @return
     */
    @RequestMapping(value = "/order/orderPayment/selectByOrderPayment", method = RequestMethod.POST)
    @ResponseBody
    ReturnData<OrderPayment> selectByOrderPayment(@RequestBody OrderAfterVo orderAfterVo);
}
