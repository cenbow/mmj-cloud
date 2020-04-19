package com.mmj.order.controller;

import com.baomidou.mybatisplus.plugins.Page;
import com.mmj.common.constants.UserConstant;
import com.mmj.common.context.BaseContextHandler;
import com.mmj.common.controller.BaseController;
import com.mmj.common.model.JwtUserDetails;
import com.mmj.common.model.ReturnData;
import com.mmj.common.model.order.OrderSearchResultDto;
import com.mmj.common.properties.SecurityConstants;
import com.mmj.common.utils.SecurityUserUtil;
import com.mmj.common.utils.StringUtils;
import com.mmj.order.model.OrderGood;
import com.mmj.order.model.OrderInfo;
import com.mmj.order.model.OrderLogistics;
import com.mmj.order.model.OrderPayment;
import com.mmj.order.model.dto.*;
import com.mmj.order.model.vo.*;
import com.mmj.order.service.GoodStockJstService;
import com.mmj.order.service.OrderInfoService;
import com.mmj.order.service.OrderLogisticsService;
import com.mmj.order.service.OrderPaymentService;
import com.mmj.order.utils.MessageUtils;
import com.mmj.order.utils.PassingDataUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jodd.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("/orderInfo")
@Api(value = "订单管理")
public class OrderController extends BaseController {

    @Autowired
    private OrderInfoService orderInfoService;

    @Autowired
    private OrderPaymentService orderPaymentService;

    @Autowired
    private OrderLogisticsService orderLogisticsService;

    @Autowired
    private GoodStockJstService goodStockJstService;

    @Autowired
    private MessageUtils messageUtils;

    @RequestMapping(value = "/getForFeign/{orderNo}", method = RequestMethod.POST)
    public OrderInfo getForFeign(@PathVariable("orderNo") String orderNo) {
        log.info("进入[getForFeign]方法,参数:{}", orderNo);
        return orderInfoService.getByOrderNo(orderNo);
    }


    /**
     * 订单发货
     *
     * @param orderNo
     * @return
     */
    @RequestMapping(value = "/toBeDelivered/{orderNo}", method = RequestMethod.POST)
    public boolean toBeDelivered(@PathVariable("orderNo") String orderNo) {
        try {
            orderInfoService.toBeDelivered(orderNo);
            return true;
        } catch (Exception e) {
            log.error("订单发货错误", e);
            return false;
        }
    }

    /**
     * 关闭订单
     *
     * @param orderNos
     * @return
     */
    @RequestMapping(value = "/close", method = RequestMethod.POST)
    public ReturnData<Boolean> close(@RequestBody List<String> orderNos) {
        try {
            String[] os = orderNos.stream().toArray(String[]::new);
            orderInfoService.closeOrder(os);
            return initSuccessObjectResult(true);
        } catch (Exception e) {
            log.error("关闭订单错误", e);
            return initSuccessObjectResult(false);
        }
    }

    @RequestMapping(value = "/getByOrderNo", method = RequestMethod.POST)
    public OrderInfo getByOrderNo(@RequestParam("orderNo") String orderNo) {
        return orderInfoService.getByOrderNo(orderNo);
    }

    @ApiOperation(value = "下单")
    @RequestMapping(value = "/batch/produce", method = RequestMethod.POST)
    public ReturnData<SaveOrderDto> saveOrder(@Valid @RequestBody OrderSaveVo orderSaveVo) {
        try {
            SaveOrderDto saveOrderDto = orderInfoService.produce(orderSaveVo);
            return initSuccessObjectResult(saveOrderDto);
        } catch (Exception e) {
            log.error("下单异常", e);
            return initExcetionObjectResult(StringUtils.isNotEmpty(e.getMessage()) ? e.getMessage() : "下单失败");
        }
    }


    @ApiOperation(value = "小程序订单列表")
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    public ReturnData<Page<OrderListDto>> getOrderList(@RequestBody OrderListVo orderListVo) {
        try {
            Page<OrderListDto> page = orderInfoService.getOrderList(orderListVo);
            return initSuccessObjectResult(page);
        } catch (Exception e) {
            log.error("小程序订单列表错误", e);
            return initErrorObjectResult("获取失败");
        }
    }


    @ApiOperation(value = "小程序订单详情")
    @RequestMapping(value = "/details", method = RequestMethod.POST)
    public ReturnData<OrderDetaislDto> getOrderDetail(@RequestBody OrderDetailVo orderDetailVo) {
        try {
            JwtUserDetails jwtUser = SecurityUserUtil.getUserDetails();
            Long userId = jwtUser.getUserId();
//            Long userId = 29733959705296896L;
//            orderDetailVo.setOrderNo("2019051311381796088100131");
            orderDetailVo.setUserId(String.valueOf(userId));
            BaseContextHandler.set(SecurityConstants.SHARDING_KEY, orderDetailVo.getUserId());
            OrderDetaislDto orderDetaislDto = orderInfoService.getDetails(orderDetailVo);
            return initSuccessObjectResult(orderDetaislDto);
        } catch (Exception e) {
            log.error("小程序订单详情错误", e);
            return initErrorObjectResult("获取失败");
        }
    }

    @ApiOperation(value = "小程序订单详情--会员")
    @RequestMapping(value = "/get/memberDetails", method = RequestMethod.POST)
    public ReturnData<OrderDetaisMemberDto> getmemberDetails(@RequestBody OrderDetailVo orderDetailVo) {

        JwtUserDetails jwtUser = SecurityUserUtil.getUserDetails();
        Long userId = jwtUser.getUserId();
        orderDetailVo.setUserId(String.valueOf(userId));
        BaseContextHandler.set(SecurityConstants.SHARDING_KEY, orderDetailVo.getUserId());
        OrderDetaisMemberDto orderDetaisMemberDto = orderInfoService.getMemberDetails(orderDetailVo);
        return initSuccessObjectResult(orderDetaisMemberDto);

    }

    @ApiOperation(value = "小程序订单详情--活动相关--拼团")
    @RequestMapping(value = "/get/groupDetails", method = RequestMethod.POST)
    public ReturnData<OrderDetailGroupDto> getGroupDetails(@RequestBody OrderDetailVo orderDetailVo) {
        try {
            OrderDetailGroupDto orderDetaisGroupDto = orderInfoService.getGroupDetails(orderDetailVo);
            return initSuccessObjectResult(orderDetaisGroupDto);
        } catch (Exception e) {
            log.error("小程序订单详情--活动相关--拼团错误", e);
            return initErrorObjectResult("获取失败");
        }
    }

    @ApiOperation(value = "根据团号查询抽奖活动id")
    @RequestMapping(value = "/getLotteryId/{groupNo}", method = RequestMethod.POST)
    public Integer getLotteryId(@PathVariable("groupNo") String groupNo) {
        return orderInfoService.getLotteryId(groupNo);
    }
/*

    @ApiOperation(value = "boss后台订单列表")
    @RequestMapping(value = "/boss/list", method = RequestMethod.POST)
    public ReturnData<Page<BossListDto>> getBossList(@RequestBody BossListVo bossListVo) {
        try {
            Page<BossListDto> page = orderInfoService.getBossList(bossListVo);
            return initSuccessObjectResult(page);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return initErrorObjectResult("获取失败");
        }
    }
*/

    @ApiOperation(value = "boss后台订单列表")
    @RequestMapping(value = "/boss/list", method = RequestMethod.POST)
    public ReturnData<Page<OrderSearchResultDto>> getBossList(@RequestBody BossListVo bossListVo) {
        try {
            return orderInfoService.getOrderListFromES(bossListVo);
        } catch (Exception e) {
            log.error("boss后台订单列表错误", e);
            return initErrorObjectResult("获取失败," + e.getMessage());
        }
    }

    @ApiOperation(value = "boss后台订单详情")
    @RequestMapping(value = "/boss/detail", method = RequestMethod.POST)
    public ReturnData<BossDetailDto> getBossDetail(@RequestBody BossDetailVo bossDetailVo) {
        try {
            BossDetailDto bossDetailDto = orderInfoService.getBossDetail(bossDetailVo);
            if (bossDetailDto != null) {
                return initSuccessObjectResult(bossDetailDto);
            } else {
                return initExcetionObjectResult("当前订单号不存在!");
            }
        } catch (Exception e) {
            log.error("boss后台订单详情", e);
            return initErrorObjectResult("获取失败");
        }
    }


    @ApiOperation(value = "取消订单")
    @RequestMapping(value = "/cancelled", method = RequestMethod.POST)
    public ReturnData<String> cancelOrder(@RequestBody CancelVo cancelVo) {
        try {
            JwtUserDetails jwtUser = SecurityUserUtil.getUserDetails();
            Long userId = jwtUser.getUserId();
            cancelVo.setUserId(String.valueOf(userId));
            BaseContextHandler.set(SecurityConstants.SHARDING_KEY, userId);
            String result = orderInfoService.cancel(cancelVo);
            return initSuccessObjectResult(result);
        } catch (Exception e) {
            log.error("取消订单错误", e);
            return initErrorObjectResult(StringUtils.isNotEmpty(e.getMessage()) ? e.getMessage() : "取消订单失败");
        }
    }

    @ApiOperation(value = "删除订单")
    @RequestMapping(value = "/remove", method = RequestMethod.POST)
    public ReturnData<String> removeOrder(@RequestBody RemoveOrderVo removeOrderVo) {
        try {
            JwtUserDetails jwtUser = SecurityUserUtil.getUserDetails();
            Long userId = jwtUser.getUserId();
            removeOrderVo.setUserId(String.valueOf(userId));
            String result = orderInfoService.removeOrder(removeOrderVo);
            return initSuccessObjectResult(result);
        } catch (Exception e) {
            log.error("删除订单错误", e);
            return initErrorObjectResult("删除订单失败");
        }
    }

    @ApiOperation(value = "确认收货")
    @RequestMapping(value = "/receive", method = RequestMethod.POST)
    public ReturnData<String> receive(@RequestBody ReceiveOrderVo receiveOrderVo) {
        try {
            JwtUserDetails jwtUser = SecurityUserUtil.getUserDetails();
            Long userId = jwtUser.getUserId();
            receiveOrderVo.setUserId(String.valueOf(userId));
            BaseContextHandler.set(SecurityConstants.SHARDING_KEY, receiveOrderVo.getUserId());
            String result = orderInfoService.receiveOrder(receiveOrderVo);
            return initSuccessObjectResult(result);
        } catch (Exception e) {
            log.error("确认收货错误", e);
            return initErrorObjectResult("确认收货失败");
        }
    }


    @ApiOperation(value = "获取订单支付信息")
    @RequestMapping(value = "/orderPay", method = RequestMethod.POST)
    public ReturnData<OrderPayment> getOrderPayment(@RequestBody OrderPaymentVo orderPaymentVo) {
        try {
            JwtUserDetails jwtUser = SecurityUserUtil.getUserDetails();
            Long userId = jwtUser.getUserId();
            OrderPayment orderPayment = orderInfoService.getOrderPayment(orderPaymentVo.getOrderNo(), userId);
            return initSuccessObjectResult(orderPayment);
        } catch (Exception e) {
            log.error("获取订单支付信息错误", e);
            return initErrorObjectResult("获取订单支付信息失败");
        }
    }

//    @ApiOperation(value = "修改订单的状态信息")
//    @RequestMapping(value = "/update/orderStatus", method = RequestMethod.POST)
//    public ReturnData<String> updateStatus(@RequestBody UpdateStatusVo updateStatusVo) {
//        try {
//            String result = orderInfoService.updateStatus(updateStatusVo);
//            return initSuccessObjectResult(result);
//        } catch (Exception e) {
//            log.error(e.getMessage(), e);
//            return initErrorObjectResult("修改订单的状态信息失败");
//        }
//    }

    @ApiOperation(value = "统计订单数据")
    @RequestMapping(value = "/stats", method = RequestMethod.POST)
    public ReturnData<OrderStatsDto> getUserOrderStats() {
        try {
            JwtUserDetails jwtUser = SecurityUserUtil.getUserDetails();
            Long userId = jwtUser.getUserId();
            OrderStatsDto orderStatsDto = orderInfoService.getUserOrderStats(userId);
            return initSuccessObjectResult(orderStatsDto);
        } catch (Exception e) {
            log.error("统计订单数据错误", e);
            return initErrorObjectResult("获取订单数据失败!");
        }
    }

    @ApiOperation(value = "虚拟商品发货")
    @RequestMapping(value = "/send/virtualgood", method = RequestMethod.POST)
    public ReturnData<String> sendVirtualGood(@RequestBody OrderGoodVo orderGoodVo) {
        try {
            String result = orderInfoService.sendVirtualGood(orderGoodVo);
            return initSuccessObjectResult(result);
        } catch (Exception e) {
            log.error("虚拟商品发货错误", e);
            return initExcetionObjectResult("虚拟商品发货失败");
        }
    }

    @ApiOperation(value = "重新上传ERP")
    @RequestMapping(value = "/upload/erp", method = RequestMethod.POST)
    public ReturnData<String> uploadErp(@RequestBody UploadErpVo uploadErpVo) {
        try {
            orderInfoService.uploadErp(uploadErpVo);
            return initSuccessResult();
        } catch (Exception e) {
            log.error("重新上传ERP错误", e);
            return initExcetionObjectResult("重新上传ERP失败");
        }
    }


    @ApiOperation(value = "扣减库存")
    @RequestMapping(value = "/decr", method = RequestMethod.POST)
    public ReturnData<Boolean> decr(@RequestBody DecrGoodNum decrGoodNum) {
//        try {
//            String ikey = "mmj_order_gooodNum_decr";
//            RLock fairLock = redissonClient.getFairLock(ikey);
//            fairLock.lock(5, TimeUnit.SECONDS);
        Boolean result = orderInfoService.decrGood(decrGoodNum);
//            fairLock.unlock();
        return initSuccessObjectResult(result);
//        } catch (Exception e) {
//            log.error(e.getMessage(), e);
//            return initErrorObjectResult("扣减库存");
//        }
    }


    @ApiOperation(value = "获取订单商品列表")
    @RequestMapping(value = "/get/orderGoods", method = RequestMethod.POST)
    public ReturnData<List<OrderGoodsDto>> getOrderGoodList(@RequestBody OrderGoodVo orderGoodVo) {
        try {
            List<OrderGoodsDto> list = orderInfoService.getOrderGoodList(orderGoodVo);
            return initSuccessObjectResult(list);
        } catch (Exception e) {
            log.error("获取订单商品列表错误", e);
            return initErrorObjectResult("获取订单商品列表失败");
        }
    }


    @ApiOperation(value = "获取订单状态为完成的商品列表")
    @RequestMapping(value = "/get/finish/orderGoods", method = RequestMethod.POST)
    public ReturnData<List<OrderGoodsDto>> get(@RequestBody OrderFinishGoodVo orderFinishGoodVo) {
        try {
            List<OrderGoodsDto> list = orderInfoService.getOrderFinishList(orderFinishGoodVo);
            return initSuccessObjectResult(list);
        } catch (Exception e) {
            log.error("获取订单状态为完成的商品列表错误", e);
            return initErrorObjectResult("获取订单商品列表失败");
        }
    }


    @ApiOperation(value = "获取包裹信息")
    @RequestMapping(value = "/get/orderPackages", method = RequestMethod.POST)
    public ReturnData<List<OrderPackageDto>> getOrderPackage(@RequestBody OrderGoodVo orderGoodVo) {
        try {
            List<OrderPackageDto> list = orderInfoService.getOrderPackages(orderGoodVo.getOrderNo(), orderGoodVo.getUserId());
            return initSuccessObjectResult(list);
        } catch (Exception e) {
            log.error("获取包裹信息错误", e);
            return initErrorObjectResult("获取订单商品列表失败");
        }
    }

    @ApiOperation(value = "获取用户所有订单号")
    @RequestMapping(value = "/get/allOrderNos", method = RequestMethod.POST)
    public ReturnData<List<OrderInfo>> getUserAllOrderNos(@RequestBody UserOrderVo userOrderVo) {
        try {
            List<OrderInfo> list = orderInfoService.getUserAllOrderNos(userOrderVo.getUserId());
            return initSuccessObjectResult(list);
        } catch (Exception e) {
            log.error("获取用户所有订单号错误", e);
            return initErrorObjectResult("获取用户所有订单号失败");
        }
    }

    @ApiOperation(value = "通过skuId 等获取订单商品")
    @RequestMapping(value = "/get/orderInfoByGood", method = RequestMethod.POST)
    public ReturnData<List<OrderGood>> getOrderInfoByGood(@RequestBody OrderInfoGoodVo orderInfoGoodVo) {
        try {
            JwtUserDetails jwtUser = SecurityUserUtil.getUserDetails();
            Long userId = jwtUser.getUserId();
            orderInfoGoodVo.setUserId(userId);
            List<OrderGood> list = orderInfoService.getOrderInfoByGood(orderInfoGoodVo);
            return initSuccessObjectResult(list);
        } catch (Exception e) {
            log.error("通过skuId 等获取订单商品错误", e);
            return initErrorObjectResult("获取用户所有订单号失败");
        }
    }


    @ApiOperation(value = "0元支付修改订单并上传聚水潭")
    @RequestMapping(value = "/update/orderInfo", method = RequestMethod.POST)
    public ReturnData<String> updateOrderInfo(@RequestBody UpdateStatusVo updateStatusVo) {
        try {
            String result = orderInfoService.updateOrderInfo(updateStatusVo);
            return initSuccessObjectResult(result);
        } catch (Exception e) {
            log.error("0元支付修改订单并上传聚水潭错误", e);
            return initErrorObjectResult("修改失败");
        }
    }

    @ApiOperation(value = "获取当前订单")
    @RequestMapping(value = "/get/orderInfo", method = RequestMethod.POST)
    public ReturnData<OrderInfo> getOrderInfo(@RequestBody UserOrderVo userOrderVo) {
        try {
            if (StringUtil.isNotBlank(userOrderVo.getOrderNo()) && StringUtil.isNotBlank(userOrderVo.getUserId())) {
                OrderInfo orderInfo = orderInfoService.selectByOrderNo(userOrderVo.getOrderNo(), Long.valueOf(userOrderVo.getUserId()));
                return initSuccessObjectResult(orderInfo);
            } else {
                return initErrorObjectResult("订单号或用户有空值!");
            }
        } catch (Exception e) {
            log.error("获取当前订单错误", e);
            return initErrorObjectResult("修改失败");
        }
    }

    @ApiOperation(value = "判断用户是否有支付行为")
    @RequestMapping(value = "/get/payAct", method = RequestMethod.POST)
    public ReturnData<Boolean> getPay() {
        try {
            JwtUserDetails jwtUser = SecurityUserUtil.getUserDetails();
            Long userId = jwtUser.getUserId();
            Boolean flag = orderPaymentService.getPayAct(userId);
            return initSuccessObjectResult(flag);
        } catch (Exception e) {
            log.error("判断用户是否有支付行为错误", e);
            return initErrorObjectResult("获取支付行为失败");
        }
    }


    @ApiOperation(value = "获取收件人信息")
    @RequestMapping(value = "/get/logistics", method = RequestMethod.POST)
    public ReturnData<List<OrderLogistics>> getLogistics(@RequestBody UserOrderVo userOrderVo) {
        try {
            List<OrderLogistics> list = orderLogisticsService.getOrderLogistics(userOrderVo.getOrderNo(), Long.valueOf(userOrderVo.getUserId()));
            return initSuccessObjectResult(list);
        } catch (Exception e) {
            log.error("获取收件人信息错误", e);
            return initErrorObjectResult("获取收件人信息失败");
        }
    }


    @ApiOperation(value = "查询是否会员首单")
    @RequestMapping(value = "/get/orderList", method = RequestMethod.POST)
    public ReturnData<List<OrderInfo>> getOrderList(@RequestBody MemberOrderVo memberOrderVo) {
        try {
            return initSuccessObjectResult(orderInfoService.getOrderList(memberOrderVo));
        } catch (Exception e) {
            log.error("查询是否会员首单错误", e);
            return initErrorObjectResult("获取订单列表失败");
        }
    }

    @ApiOperation(value = "查询历史消费金额")
    @RequestMapping(value = "/get/getConsumeMoney", method = RequestMethod.POST)
    public ReturnData<Double> getConsumeMoney(@RequestBody Long userId) {
        try {
            return initSuccessObjectResult(orderInfoService.getConsumeMoney(userId));
        } catch (Exception e) {
            log.error("查询历史消费金额错误", e);
            return initErrorObjectResult("查询历史消费金额失败");
        }
    }

    @ApiOperation(value = "查询历史消费金额Two")
    @RequestMapping(value = "/get/getConsumeMoneyTwo", method = RequestMethod.POST)
    public ReturnData<Double> getConsumeMoneyTwo(@RequestBody OrderDetailVo orderDetailVo) {
        try {
            return initSuccessObjectResult(orderInfoService.getConsumeMoneyTwo(orderDetailVo));
        } catch (Exception e) {
            log.error("查询历史消费金额Two错误", e);
            return initErrorObjectResult("获取订单列表失败");
        }
    }


    @ApiOperation("查询抽奖订单详情")
    @RequestMapping(value = "/lotteryDetail/{orderNo}", method = RequestMethod.POST)
    public ReturnData<Map<String, Object>> lotteryDetail(@PathVariable("orderNo") String orderNo) {
        try {
            return initSuccessObjectResult(orderInfoService.lotteryDetail(orderNo));
        } catch (Exception e) {
            log.error("查询抽奖订单详情错误", e);
            return initErrorObjectResult(e.getMessage());
        }
    }


    @ApiOperation("快递查询接口")
    @RequestMapping(value = "/logistics/query", method = RequestMethod.POST)
    public ReturnData<PollQueryResponse> queryLogistics(@Valid @RequestBody LogisticsQueryVo logisticsQueryVo) {
        try {
            PollQueryResponse pollQueryResponse = orderInfoService.queryLogistics(logisticsQueryVo);
            return initSuccessObjectResult(pollQueryResponse);
        } catch (Exception e) {
            log.error("快递查询接口错误", e);
            return initErrorObjectResult(e.getMessage());
        }
    }

    @ApiOperation("同步聚水潭库存")
    @RequestMapping(value = "/jst/goodNum", method = RequestMethod.POST)
    public ReturnData<Object> queryLogistics() {
        try {
            goodStockJstService.jstGoodNum();
            return initSuccessObjectResult("success");
        } catch (Exception e) {
            log.error("同步聚水潭库存错误", e);
            return initErrorObjectResult(e.getMessage());
        }
    }

    @ApiOperation("判断新老用户")
    @RequestMapping(value = "/check/oldUser", method = RequestMethod.POST)
    public ReturnData<Boolean> checkNewUser(@RequestBody Map<String, Object> map) {
        try {
            Object obj = map.get("userId");
            log.info("新老用户接口，当前用户是否为空:{}", obj);
            Long userId;
            if (Objects.isNull(obj)) {
                JwtUserDetails jwtUser = SecurityUserUtil.getUserDetails();
                userId = jwtUser.getUserId();
            } else {
                userId = Long.valueOf(obj.toString());
            }
            if (userId == UserConstant.USER_VISITOR_ID) {
                return initSuccessObjectResult(true);// 游客为老用户，For H5
            }
            log.info("用户id为:{}", userId);
            boolean flag = orderInfoService.checkOldUser(userId);
            return initSuccessObjectResult(flag);
        } catch (Exception e) {
            log.error("判断新老用户错误", e);
            return initErrorObjectResult(e.getMessage());
        }
    }

    @ApiOperation("订单复购")
    @RequestMapping(value = "/good/buy/again", method = RequestMethod.POST)
    public ReturnData<Object> buyAgain(@RequestBody UserOrderVo userOrderVo) {
        try {
            JwtUserDetails jwtUser = SecurityUserUtil.getUserDetails();
            Long userId = jwtUser.getUserId();
            if (StringUtils.isEmpty(userOrderVo.getOrderNo())) {
                Assert.isTrue(false, "订单号不能为空!");
            }
            Integer count = orderInfoService.buyAgain(userId, userOrderVo.getOrderNo());
            return initSuccessObjectResult(count);
        } catch (Exception e) {
            log.error("订单复购错误", e);
            return initErrorObjectResult(e.getMessage());
        }
    }


    @ApiOperation("修改订单售后标识")
    @RequestMapping(value = "/update/afterSale/flag", method = RequestMethod.POST)
    public ReturnData<Object> updateAfterSaleFlag(@RequestBody UserOrderVo userOrderVo) {
        try {
            if (StringUtils.isEmpty(userOrderVo.getOrderNo()) || StringUtils.isEmpty(userOrderVo.getUserId())) {
                Assert.isTrue(false, "订单号或用户id为空");
            }
            String result = orderInfoService.updateAfterSaleFlag(userOrderVo);
            return initSuccessObjectResult(result);
        } catch (Exception e) {
            log.error("修改订单售后标识错误", e);
            return initErrorObjectResult(e.getMessage());
        }
    }

    @ApiOperation("修改订单收货地址")
    @RequestMapping(value = "/update/order/address", method = RequestMethod.POST)
    public ReturnData<Object> updateOrderAddress(@RequestBody OrderAddressVo orderAddressVo) {
        try {
            return initSuccessObjectResult(orderInfoService.updateOrderAddress(orderAddressVo));
        } catch (Exception e) {
            log.error("修改订单收货地址错误", e);
            return initErrorObjectResult(StringUtils.isNotEmpty(e.getMessage()) ? e.getMessage() : "修改订单收货地址失败");
        }
    }

    @RequestMapping(value = "/logistics/getMobile", method = RequestMethod.POST)
    public String getMobile(@RequestBody UpdateStatusVo updateStatusVo) {
        List<OrderLogistics> list = orderLogisticsService.getOrderLogistics(updateStatusVo.getOrderNo(),
                updateStatusVo.getUserId());
        if (null == list) {
            return null;
        }
        return list.get(0).getConsumerMobile();
    }

    @RequestMapping(value = "/getGroupNobyOrderNo/{orderNo}", method = RequestMethod.POST)
    public ReturnData getGroupNobyOrderNo(@PathVariable("orderNo") String orderNo) {
        JwtUserDetails userDetails = SecurityUserUtil.getUserDetails();
        if (null == userDetails)
            return initExcetionObjectResult("用户未登录");
        OrderInfo info = orderInfoService.selectByOrderNo(orderNo, userDetails.getUserId());
        if (null == info)
            return initExcetionObjectResult("订单不存在");
        PassingDataDto dto = PassingDataUtil.disPassingData(info.getPassingData());
        if (null == dto)
            return initExcetionObjectResult("订单团号不存在!");
        if (StringUtils.isEmpty(dto.getGroupNo()) && StringUtils.isEmpty(dto.getBindGroupNo()))
            return initExcetionObjectResult("订单团号不存在.");
        if (StringUtils.isNotEmpty(dto.getGroupNo()))
            return initSuccessObjectResult(dto.getGroupNo());
        if (StringUtils.isNotEmpty(dto.getBindGroupNo()))
            return initSuccessObjectResult(dto.getBindGroupNo());
        return initExcetionObjectResult("查询异常");
    }

    @RequestMapping(value = "/getSendMsg/{orderNo}", method = RequestMethod.POST)
    public ReturnData<String> getSendMsg(@PathVariable("orderNo") String orderNo) {
        try {
            return initSuccessObjectResult(messageUtils.getSendMsg(orderNo, true));
        } catch (Exception e) {
            log.error("获取预计发货时间错误", e);
            return initErrorObjectResult(e.getMessage());
        }
    }
}
