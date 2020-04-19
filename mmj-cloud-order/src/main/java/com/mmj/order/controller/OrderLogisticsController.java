package com.mmj.order.controller;


import com.mmj.common.controller.BaseController;
import com.mmj.common.model.ReturnData;
import com.mmj.order.model.OrderLogistics;
import com.mmj.order.service.OrderLogisticsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * <p>
 * 订单快递信息表 前端控制器
 * </p>
 *
 * @author lyf
 * @since 2019-06-04
 */
@RestController
@RequestMapping("/order/orderLogistics")
@Slf4j
public class OrderLogisticsController extends BaseController {

    @Autowired
    private OrderLogisticsService service;

    @RequestMapping(value = "/getUser", method = RequestMethod.POST)
    public ReturnData<Map<String, Object>> getUser(@RequestBody OrderLogistics logistics) {
        try {
            Map<String, Object> map = service.getUser(logistics);
            return initSuccessObjectResult(map);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return initExcetionObjectResult(e.getMessage());
        }
    }
}

