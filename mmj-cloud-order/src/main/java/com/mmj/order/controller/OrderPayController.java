package com.mmj.order.controller;


import com.mmj.common.context.BaseContextHandler;
import com.mmj.common.controller.BaseController;
import com.mmj.common.model.JwtUserDetails;
import com.mmj.common.model.ReturnData;
import com.mmj.common.properties.SecurityConstants;
import com.mmj.common.utils.SecurityUserUtil;
import com.mmj.common.utils.SnowflakeIdWorker;
import com.mmj.common.utils.StringUtils;
import com.mmj.order.model.dto.OrderDetaislDto;
import com.mmj.order.model.dto.PayInfoDto;
import com.mmj.order.model.vo.OrderDetailVo;
import com.mmj.order.model.vo.PayInfoVo;
import com.mmj.order.service.OrderInfoService;
import com.mmj.order.service.OrderService;
import com.mmj.order.utils.MQConsumer;
import com.mmj.order.utils.MQProducer;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/orderPay")
@Api(value = "订单支付")
public class OrderPayController extends BaseController {

    @Autowired
    private MQProducer mQProducer;

    @Autowired
    private MQConsumer mqConsumer;

    @Autowired
    private OrderService orderService;

    @Autowired
    private SnowflakeIdWorker snowflakeIdWorker;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private OrderInfoService orderInfoService;


    @ApiOperation(value = "订单支付")
    @RequestMapping(value = "/getPayInfo", method = RequestMethod.POST)
    public ReturnData<PayInfoDto> getOrderPayInfo(@Valid @RequestBody PayInfoVo payInfoVo) {
        try {
            JwtUserDetails jwtUser = SecurityUserUtil.getUserDetails();
            Long userId = jwtUser.getUserId();
            PayInfoDto payInfoDto = orderInfoService.getOrderPayInfo(payInfoVo.getAppId(), payInfoVo.getOpenId(), payInfoVo.getOrderNo(), userId);
            return initSuccessObjectResult(payInfoDto);
        } catch (Exception e) {
            return initErrorObjectResult(StringUtils.isNotEmpty(e.getMessage()) ? e.getMessage() : "获取订单支付金额失败");
        }
    }


}
