package com.mmj.pay.controller;


import com.alibaba.fastjson.JSON;
import com.mmj.common.controller.BaseController;
import com.mmj.common.model.ReturnData;
import com.mmj.pay.model.WxpayTransfers;
import com.mmj.pay.service.WxpayTransfersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 微信发送零钱表 前端控制器
 * </p>
 *
 * @author shenfuding
 * @since 2019-07-16
 */
@RestController
@RequestMapping("/wxpayTransfers")
@Slf4j
public class WxpayTransfersController extends BaseController {

    @Autowired
    WxpayTransfersService wxpayTransfersService;

    /**
     * 发送零钱
     * @param wxpayTransfers
     * @return
     */
    @RequestMapping("transfers")
    public ReturnData<WxpayTransfers> transfers(@RequestBody WxpayTransfers wxpayTransfers){
        log.info("发送零钱接口接受:"  + JSON.toJSONString(wxpayTransfers));
        wxpayTransfers = wxpayTransfersService.transfers(wxpayTransfers);
        return initSuccessObjectResult(wxpayTransfers);
    }
}

