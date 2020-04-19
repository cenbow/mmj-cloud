package com.mmj.third.recharge.controller;

import com.google.common.collect.Maps;
import com.mmj.common.controller.BaseController;
import com.mmj.common.model.ReturnData;
import com.mmj.common.model.third.recharge.*;
import com.mmj.third.recharge.service.RechargeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;
import java.util.Map;

/**
 * @description: 话费充值服务
 * @auther: KK
 * @date: 2019/8/31
 */
@Slf4j
@RestController
@RequestMapping("/recharge")
@Api(value = "话费充值服务", description = "话费充值服务")
public class RechargeController extends BaseController {
    @Autowired
    private RechargeService rechargeService;

    @PostMapping("/details")
    @ApiOperation("话费充值服务-订单详情")
    public ReturnData<RechargeDto> details(@RequestBody RechargeDetailsVo rechargeDetailsVo) {
        return initSuccessObjectResult(rechargeService.getRechargeInfo(rechargeDetailsVo.getOuterId()));
    }

    @PostMapping("/produce")
    @ApiOperation("话费充值服务-话费充值")
    public ReturnData<RechargeDto> recharge(@RequestBody RechargeVo rechargeVo) {
        return initSuccessObjectResult(rechargeService.recharge(rechargeVo));
    }

    @PostMapping("/mobile/info")
    @ApiOperation("手机号归属地查询")
    public ReturnData<NumberInfoDto> mobileInfo(@RequestBody NumberInfoVo numberInfoVo) {
        return initSuccessObjectResult(rechargeService.getNumberInfo(numberInfoVo.getMobile()));
    }

    @RequestMapping(value = "/bm001/callback", method = {RequestMethod.POST, RequestMethod.GET})
    @ApiOperation("第三方异步回调")
    public String callback(HttpServletRequest request, HttpServletResponse response) {
        Enumeration keys = request.getParameterNames();
        Map<String, String> params = Maps.newHashMap();
        while (keys.hasMoreElements()) {
            String key = (String) keys.nextElement();
            String value = request.getParameter(key);
            params.put(key, value);
        }
        log.info("=> bm001Callback params:{}", params);
        try {
            rechargeService.bm001Callback(params);
            return "success";
        } catch (Exception e) {
            log.error("=> bm001Callback error", e.getCause());
        }
        return "fail";
    }
}
