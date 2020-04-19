package com.mmj.user.common.feigin;

import com.mmj.common.interceptor.FeignRequestInterceptor;
import com.mmj.common.model.ReturnData;
import com.mmj.user.common.model.OrderInfo;
import com.mmj.user.common.model.dto.OrderGoodsDto;
import com.mmj.user.common.model.vo.MemberOrderVo;
import com.mmj.user.common.model.vo.OrderDetailVo;
import com.mmj.user.common.model.vo.OrderGoodVo;
import com.mmj.user.common.model.vo.UserOrderVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@FeignClient(name = "mmj-cloud-order", fallbackFactory = OrderFallbackFactory.class, configuration = FeignRequestInterceptor.class)
public interface OrderFeignClient {

    @RequestMapping(value = "/orderKing/frozenKingNum/{userId}", method = RequestMethod.POST)
    @ResponseBody
    int frozenKingNum(@PathVariable("userId") Long userId);

    @RequestMapping(value = "/async/getOrderByOrderNo/{orderNo}", method = RequestMethod.POST)
    ReturnData<OrderInfo> getOrderByOrderNo(@PathVariable("orderNo") String orderNo);

    @RequestMapping(value = "/async/get/orderInfo", method = RequestMethod.POST)
    @ResponseBody
    ReturnData<OrderInfo> getAsyncOrderInfo(@RequestBody UserOrderVo userOrderVo);

    @RequestMapping(value = "/orderInfo/get/orderList", method = RequestMethod.POST)
    @ResponseBody
    ReturnData<List<OrderInfo>> getOrderList(@RequestBody MemberOrderVo memberOrderVo);

    @RequestMapping(value = "/async/get/orderList", method = RequestMethod.POST)
    @ResponseBody
    ReturnData<List<OrderInfo>> getAsyncOrderList(@RequestBody MemberOrderVo memberOrderVo);

    @RequestMapping(value = "/orderInfo/get/getConsumeMoney", method = RequestMethod.POST)
    @ResponseBody
    ReturnData<Double> getConsumeMoney(@RequestBody Long userId);

    @RequestMapping(value = "/async/get/getConsumeMoney", method = RequestMethod.POST)
    @ResponseBody
    ReturnData<Double> getAsyncConsumeMoney(@RequestBody Long userId);

    @RequestMapping(value = "/orderInfo/get/getConsumeMoneyTwo", method = RequestMethod.POST)
    @ResponseBody
    ReturnData<Double> getConsumeMoneyTwo(@RequestBody OrderDetailVo orderDetailVo);

    @RequestMapping(value = "/async/get/getConsumeMoneyTwo", method = RequestMethod.POST)
    @ResponseBody
    ReturnData<Double> getAsyncConsumeMoneyTwo(@RequestBody OrderDetailVo orderDetailVo);

    @ApiOperation(value = "获取订单商品列表")
    @RequestMapping(value = "/async/get/orderGoods", method = RequestMethod.POST)
    ReturnData<List<OrderGoodsDto>> getAsyncOrderGoodList(@RequestBody OrderGoodVo orderGoodVo);

    @RequestMapping(value = "/orderKing/getGiveBy/{userId}", method = RequestMethod.POST)
    String getGiveBy(@PathVariable("userId") Long userId);

    @RequestMapping(value = "/updateById", method = RequestMethod.POST)
    boolean updateById(@RequestBody Object ok);

    /**
     * 判断是否新用户
     *
     * @param userId
     * @return
     */
    @RequestMapping(value = "orderInfo/check/oldUser", method = RequestMethod.POST)
    ReturnData<Boolean> checkNewUser(Map<String, Object> map);
}
