package com.mmj.active.callCharge.controller;


import com.baomidou.mybatisplus.plugins.Page;
import com.mmj.active.callCharge.model.CallChargeRecord;
import com.mmj.active.callCharge.model.dto.PayInfoDto;
import com.mmj.active.callCharge.model.dto.RechargeRecordDto;
import com.mmj.active.callCharge.model.vo.BossQueryVo;
import com.mmj.active.callCharge.model.vo.PayInfoVo;
import com.mmj.active.callCharge.model.vo.RechargeOrderVo;
import com.mmj.active.callCharge.service.CallChargeRecordService;
import com.mmj.common.controller.BaseController;
import com.mmj.common.model.ReturnData;
import com.mmj.common.model.active.RechargeVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * <p>
 * 话费订单
 * </p>
 *
 * @author KK
 * @since 2019-08-31
 */
@RestController
@RequestMapping("/callCharge/orders")
@Api(value = "话费订单", description = "话费订单")
public class CallChargeRecordController extends BaseController {
    @Autowired
    private CallChargeRecordService callChargeRecordService;

    @PostMapping("/produce")
    @ApiOperation("话费订单下单")
    public ReturnData<RechargeRecordDto> produce(@Valid @RequestBody RechargeOrderVo rechargeOrderVo) {
        return initSuccessObjectResult(callChargeRecordService.produceOrder(rechargeOrderVo));
    }

    @PostMapping("/list")
    @ApiOperation("话费订单记录")
    public ReturnData<Page<CallChargeRecord>> list(@Valid @RequestBody BossQueryVo bossQueryVo) {
        return initSuccessObjectResult(callChargeRecordService.getCallChargeRecordList(bossQueryVo));
    }

    @RequestMapping(value = "/getPayInfo", method = RequestMethod.POST)
    @ApiOperation(value = "订单支付")
    public ReturnData<PayInfoDto> getOrderPayInfo(@Valid @RequestBody PayInfoVo payInfoVo) {
        return initSuccessObjectResult(callChargeRecordService.getOrderPayInfo(payInfoVo.getAppId(), payInfoVo.getOpenId(), payInfoVo.getOrderNo()));
    }

    @PostMapping("/recharge/fail")
    @ApiOperation("充值失败")
    public ReturnData rechargeFail(@Valid @RequestBody RechargeVo rechargeVo) {
        callChargeRecordService.payFail(rechargeVo);
        return initSuccessResult();
    }

    @PostMapping("/recharge")
    @ApiOperation("话费充值")
    public ReturnData recharge(@RequestBody RechargeVo rechargeVo) {
        callChargeRecordService.recharge(rechargeVo.getOrderNo());
        return initSuccessResult();
    }
}

