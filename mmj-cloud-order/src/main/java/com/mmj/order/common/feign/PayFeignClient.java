package com.mmj.order.common.feign;

import com.mmj.common.interceptor.FeignRequestInterceptor;
import com.mmj.common.model.ReturnData;
import com.mmj.order.common.model.WxpayRedpack;
import com.mmj.order.common.model.vo.CartOrderGoodsDetails;
import com.mmj.order.common.model.vo.WxpayOrderEx;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@FeignClient(name = "mmj-cloud-pay", fallbackFactory = PayFallbackFactory.class, configuration = FeignRequestInterceptor.class)
public interface PayFeignClient {

    /**
     * 获取订单支付接口
     *
     * @param wxpayOrderEx
     * @return
     */
    @RequestMapping(value = "/wxpayOrder/getPayInfo", method = RequestMethod.POST)
    @ResponseBody
    ReturnData<Map<String, String>> getPayInfo(@RequestBody WxpayOrderEx wxpayOrderEx);


    /**
     * 生单后的价格计算
     */
    @RequestMapping(value = "/price/get/order/calcFinalPrice", method = RequestMethod.POST)
    ReturnData<CartOrderGoodsDetails> calcFinalPrice(@RequestBody CartOrderGoodsDetails cogd);


    @RequestMapping(value = "/wxpayRedpack/sendRedpack", method = RequestMethod.POST)
    ReturnData<WxpayRedpack> sendRedpack(@RequestBody WxpayRedpack wxpayRedpack);

}
