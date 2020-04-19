package com.mmj.pay.controller;


import com.alibaba.fastjson.JSON;
import com.mmj.common.controller.BaseController;
import com.mmj.common.model.ReturnData;
import com.mmj.pay.model.WxpayRedpack;
import com.mmj.pay.service.WxpayRedpackService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 微信红包记录表 前端控制器
 * </p>
 *
 * @author shenfuding
 * @since 2019-07-17
 */
@RestController
@RequestMapping("/wxpayRedpack")
@Slf4j
public class WxpayRedpackController extends BaseController {

    @Autowired
    WxpayRedpackService wxpayRedpackService;

    /**
     * 发送普通红包
     * @param wxpayRedpack
     * @return
     */
    @RequestMapping(value = "sendRedpack",method = RequestMethod.POST)
    public ReturnData<WxpayRedpack> sendRedpack(@RequestBody WxpayRedpack wxpayRedpack) {
        log.info("发送红包接口接受参数:"  + JSON.toJSONString(wxpayRedpack));
        wxpayRedpack = wxpayRedpackService.sendRedpack(wxpayRedpack);
        return initSuccessObjectResult(wxpayRedpack);
    }
}

