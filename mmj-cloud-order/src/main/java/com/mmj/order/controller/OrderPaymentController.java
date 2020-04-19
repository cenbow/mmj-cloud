package com.mmj.order.controller;


import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.mmj.common.context.BaseContextHandler;
import com.mmj.common.controller.BaseController;
import com.mmj.common.model.ReturnData;
import com.mmj.common.properties.SecurityConstants;
import com.mmj.order.model.OrderPayment;
import com.mmj.order.model.vo.OrderAfterVo;
import com.mmj.order.service.OrderPaymentService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 订单支付信息表 前端控制器
 * </p>
 *
 * @author lyf
 * @since 2019-06-04
 */
@Slf4j
@RestController
@RequestMapping("/order/orderPayment")
public class OrderPaymentController extends BaseController {

    @Autowired
    private OrderPaymentService orderPaymentService;


    /**
     * 根据订单号查询支付信息
     * @param orderAfterVo
     * @return
     */
    @ApiOperation(value = "根据订单号查询支付信息")
    @RequestMapping(value = "/selectByOrderPayment", method = RequestMethod.POST)
    public ReturnData<OrderPayment> selectByOrderPayment(@RequestBody OrderAfterVo orderAfterVo){
        log.info("进入查询支付信息接口:{}",orderAfterVo.toString());
        BaseContextHandler.set(SecurityConstants.SHARDING_KEY,orderAfterVo.getUserId());
        EntityWrapper<OrderPayment> orderPaymentEntityWrapper = new EntityWrapper<>();
        orderPaymentEntityWrapper.eq("ORDER_NO",orderAfterVo.getOrderNo());
        orderPaymentEntityWrapper.eq("CREATER_ID",orderAfterVo.getUserId());
        return initSuccessObjectResult(orderPaymentService.selectOne(orderPaymentEntityWrapper));
    }
}

