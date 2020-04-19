package com.mmj.third.kuaidi100.controller;

import com.mmj.common.controller.BaseController;
import com.mmj.common.model.ReturnData;
import com.mmj.third.kuaidi100.model.LogisticsVo;
import com.mmj.third.kuaidi100.model.PollQueryResponse;
import com.mmj.third.kuaidi100.service.KuaiDi100Service;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @Description: 物流查询接口信息
 * @Auther: KK
 * @Date: 2018/10/15
 */
@RestController
@RequestMapping("/api/logistics")
@Api(value = "物流查询", description = "物流查询")
public class LogisticsController extends BaseController {
    @Autowired
    private KuaiDi100Service kuaiDi100Service;

    /**
     * @Description: 查询快递
     * @author: KK
     * @date: 2018/10/16
     * @param: [logisticsVo]
     * @return: com.mmj.ecommerce.utils.R
     */
    @ApiOperation(value = "查询快递")
    @PostMapping("/query")
    public ReturnData<PollQueryResponse> query(@Valid @RequestBody LogisticsVo logisticsVo) {
        return initSuccessObjectResult(kuaiDi100Service.query(logisticsVo.getOrderNo(), logisticsVo.getlId(), logisticsVo.getLcCode()));
    }

}
