package com.mmj.order.async;

import com.mmj.common.controller.BaseController;
import com.mmj.common.model.ReturnData;
import com.mmj.common.model.UserOrderStatistics;
import com.mmj.common.model.UserOrderStatisticsParam;
import com.mmj.order.model.OrderGood;
import com.mmj.order.model.OrderInfo;
import com.mmj.order.model.OrderLogistics;
import com.mmj.order.model.dto.OrderGoodsDto;
import com.mmj.order.model.dto.OrderPackageDto;
import com.mmj.order.model.vo.*;
import com.mmj.order.service.OrderGoodService;
import com.mmj.order.service.OrderInfoService;
import com.mmj.order.service.OrderLogisticsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jodd.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 本控制器为mmj-cloud-order下共用，提供不需要进行令牌认证的方法给消息服务以及定时任务调度进行调用
 *
 * @author shenfuding
 */
@Slf4j
@RestController
@RequestMapping("/async")
@Api(value = "订单模块异步处理控制器")
public class OrderAsyncController extends BaseController {
    @Autowired
    private OrderInfoService orderInfoService;

    @Autowired
    private OrderGoodService orderGoodService;

    @Autowired
    private OrderLogisticsService logisticsService;

    @RequestMapping(value = "/get/orderGoodImg/{orderNo}", method = RequestMethod.POST)
    public String orderGoodImg(@PathVariable("orderNo") String orderNo) {
        List<OrderGood> list = orderGoodService.selectByOrderNo(orderNo);
        if (null == list || list.size() == 0)
            return null;
        return list.get(0).getGoodImage();

    }

    @RequestMapping(value = "/getOrderByOrderNo/{orderNo}", method = RequestMethod.POST)
    public ReturnData<OrderInfo> getOrderByOrderNo(@PathVariable("orderNo") String orderNo) {
        try {
            OrderInfo orderInfo = orderInfoService.getByOrderNo(orderNo);
            return initSuccessObjectResult(orderInfo);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
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
            log.error(e.getMessage(), e);
            return initErrorObjectResult("获取当前订单异常");
        }
    }

    @ApiOperation(value = "获取订单商品列表")
    @RequestMapping(value = "/get/orderGoods", method = RequestMethod.POST)
    public ReturnData<List<OrderGoodsDto>> getOrderGoodList(@RequestBody OrderGoodVo orderGoodVo) {
        try {
            List<OrderGoodsDto> list = orderInfoService.getOrderGoodList(orderGoodVo);
            return initSuccessObjectResult(list);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return initErrorObjectResult("获取订单商品列表异常");
        }
    }

    @ApiOperation(value = "查询历史消费金额")
    @RequestMapping(value = "/get/getConsumeMoney", method = RequestMethod.POST)
    public ReturnData<Double> getConsumeMoney(@RequestBody Long userId) {
        try {
            return initSuccessObjectResult(orderInfoService.getConsumeMoney(userId));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return initErrorObjectResult("查询历史消费金额失败");
        }
    }

    @ApiOperation(value = "查询历史消费金额Two")
    @RequestMapping(value = "/get/getConsumeMoneyTwo", method = RequestMethod.POST)
    public ReturnData<Double> getConsumeMoneyTwo(@RequestBody OrderDetailVo orderDetailVo) {
        try {
            return initSuccessObjectResult(orderInfoService.getConsumeMoneyTwo(orderDetailVo));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return initErrorObjectResult("查询历史消费金额Two异常");
        }
    }

    @ApiOperation(value = "查询是否会员首单")
    @RequestMapping(value = "/get/orderList", method = RequestMethod.POST)
    public ReturnData<List<OrderInfo>> getOrderList(@RequestBody MemberOrderVo memberOrderVo) {
        try {
            return initSuccessObjectResult(orderInfoService.getOrderList(memberOrderVo));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return initErrorObjectResult("获取订单列表失败");
        }
    }

    /**
     * 查询抽奖的待付款订单
     *
     * @param busId
     * @return
     */
    @RequestMapping(value = "getLotteryWaitPay/{busId}", method = RequestMethod.POST)
    public List<OrderInfo> getLotteryWaitPay(@PathVariable("busId") Integer busId) {
        return orderInfoService.getLotteryWaitPay(busId);
    }

    @ApiOperation(value = "获取包裹信息")
    @RequestMapping(value = "/get/orderPackages", method = RequestMethod.POST)
    public ReturnData<List<OrderPackageDto>> getOrderPackage(@RequestBody OrderGoodVo orderGoodVo) {
        try {
            List<OrderPackageDto> list = orderInfoService.getOrderPackages(orderGoodVo.getOrderNo(), orderGoodVo.getUserId());
            return initSuccessObjectResult(list);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return initErrorObjectResult("获取订单商品列表失败");
        }
    }

    @RequestMapping(value = "/usersOrdersDataForChannel", method = RequestMethod.POST)
    @ApiOperation("渠道数据统计")
    public ReturnData<List<UserOrderStatistics>> getUsersOrdersDataForChannel(
            @RequestBody UserOrderStatisticsParam param) {
        List<UserOrderStatistics> list = orderInfoService.getUsersOrdersDataForChannel(param);
        return initSuccessObjectResult(list);
    }

    @ApiOperation(value = "扣减库存")
    @RequestMapping(value = "/decr", method = RequestMethod.POST)
    public ReturnData<Boolean> decr(@RequestBody DecrGoodNum decrGoodNum) {
        Boolean result = orderInfoService.decrGood(decrGoodNum);
        return initSuccessObjectResult(result);
    }

    @RequestMapping(value = "/getLogistics/{orderNo}", method = RequestMethod.POST)
    public ReturnData<OrderLogistics> getLogistics(@PathVariable("orderNo") String orderNo) {
        OrderLogistics logistics = logisticsService.selectOneByOrderNo(orderNo);
        return initSuccessObjectResult(logistics);
    }

    @RequestMapping(value = "/getAddress/{orderNo}", method = RequestMethod.POST)
    public String getAddress(@PathVariable("orderNo") String orderNo) {
        OrderLogistics logistics = logisticsService.selectOneByOrderNo(orderNo);
        if (null == logistics)
            return "";
        return logistics.getProvince() + logistics.getCity() + logistics.getArea() + logistics.getConsumerAddr();
    }

    @RequestMapping(value = "/order/{orderNo}", method = RequestMethod.POST)
    public OrderInfo getOrderInfo(@PathVariable("orderNo") String orderNo) {
        log.info("进入[get]方法,参数:{}", orderNo);
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
            log.error(e.getMessage(), e);
            return false;
        }
    }
}
