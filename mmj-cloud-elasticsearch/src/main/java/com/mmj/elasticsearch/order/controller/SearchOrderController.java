package com.mmj.elasticsearch.order.controller;

import com.baomidou.mybatisplus.plugins.Page;
import com.mmj.common.controller.BaseController;
import com.mmj.common.model.ReturnData;
import com.mmj.common.model.order.OrderSearchConditionDto;
import com.mmj.common.model.order.OrderSearchResultDto;
import com.mmj.elasticsearch.order.service.OrdersDocumentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @description: 订单es
 * @auther: KK
 * @date: 2019/8/10
 */
@RestController
@RequestMapping("/orders")
@Api(value = "订单ES接口", description = "订单ES接口")
public class SearchOrderController extends BaseController {
    @Autowired
    private OrdersDocumentService ordersDocumentService;

    @PostMapping("/search")
    @ApiOperation(value = "订单搜索")
    public ReturnData<Page<OrderSearchResultDto>> search(@RequestBody OrderSearchConditionDto orderSearchConditionDto) {
        return initSuccessObjectResult(ordersDocumentService.search(orderSearchConditionDto));
    }
}
