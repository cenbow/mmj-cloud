package com.mmj.active.callCharge.controller;


import com.mmj.active.callCharge.model.dto.RechargeGoodsDto;
import com.mmj.active.callCharge.service.CallChargeGoodsService;
import com.mmj.common.controller.BaseController;
import com.mmj.common.model.ReturnData;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 话费商品
 * </p>
 *
 * @author KK
 * @since 2019-08-31
 */
@RestController
@RequestMapping("/callCharge/goods")
@Api(value = "话费商品", description = "话费商品")
public class CallChargeGoodsController extends BaseController {
    @Autowired
    private CallChargeGoodsService callChargeGoodsService;

    @PostMapping("/list")
    @ApiOperation("话费商品列表")
    public ReturnData<List<RechargeGoodsDto>> list() {
        return initSuccessObjectResult(callChargeGoodsService.getRechargeGoods());
    }
}

