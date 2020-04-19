package com.mmj.pay.controller;

import com.mmj.common.utils.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.mmj.common.controller.BaseController;
import com.mmj.common.model.JwtUserDetails;
import com.mmj.common.model.ReturnData;
import com.mmj.common.utils.SecurityUserUtil;
import com.mmj.pay.dto.OrderPriceDetailsInfo;
import com.mmj.pay.model.vo.CartOrderCouponParam;
import com.mmj.pay.model.vo.CartOrderGoodsDetails;
import com.mmj.pay.service.CalcOrderPriceService;

@Slf4j
@RestController
@RequestMapping("/price")
@Api(value = "订单价格计算")
public class CalcOrderPriceController extends BaseController {

    @Autowired
    private CalcOrderPriceService calcOrderPriceService;

    @ApiOperation(value = "生单前计算支付价格信息")
    @RequestMapping(value = "/get/calcFinalPrice", method = RequestMethod.POST)
    public ReturnData<OrderPriceDetailsInfo> getCalcOrderPrice(@RequestBody CartOrderCouponParam param) {
        log.info("-->订单支付页面结算，参数：{}", JSONObject.toJSONString(param));
        JwtUserDetails jwtUser = SecurityUserUtil.getUserDetails();
        long userId = jwtUser.getUserId();
        param.setUserId(userId);
        log.info("-->下单用户：{}", userId);
        try {
            return initSuccessObjectResult(calcOrderPriceService.calcOrderPrice(param, null));
        } catch (Exception e) {
            return initErrorObjectResult(StringUtils.isNotEmpty(e.getMessage()) ? e.getMessage() : "计算价格发生错误，请联系客服");
        }

    }

    /**
     * 对生成的订单进行价格计算，并将优惠和运费按金额比例分摊到各个商品
     */
    @ApiOperation(value = "生单后的内部调用价格计算")
    @RequestMapping(value = "/get/order/calcFinalPrice", method = RequestMethod.POST)
    public ReturnData<CartOrderGoodsDetails> calcFinalPrice(@RequestBody CartOrderGoodsDetails cogd) {
        try {
            JwtUserDetails jwtUser = SecurityUserUtil.getUserDetails();
            long userId = jwtUser.getUserId();
            cogd.setUserid(userId);
            log.info("-->下单用户：{}", userId);
            return initSuccessObjectResult(calcOrderPriceService.calcFinalPrice(cogd));
        } catch (Exception e) {
            return initErrorObjectResult(StringUtils.isNotEmpty(e.getMessage()) ? e.getMessage() : "计算价格发生异常");
        }
    }


}
