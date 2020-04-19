package com.mmj.pay.controller;


import com.alibaba.fastjson.JSON;
import com.mmj.common.controller.BaseController;
import com.mmj.common.model.ReturnData;
import com.mmj.common.utils.MD5Util;
import com.mmj.pay.model.WxpayRefund;
import com.mmj.pay.sdk.weixin.WXPayUtil;
import com.mmj.pay.service.WxpayRefundService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.security.Security;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 微信退款表 前端控制器
 * </p>
 *
 * @author shenfuding
 * @since 2019-07-15
 */
@RestController
@RequestMapping("/wxpayRefund")
@Slf4j
public class WxpayRefundController extends BaseController {

    @Autowired
    WxpayRefundService wxpayRefundService;

    /**
     * 微信退款接口
     * @param wxpayRefund
     * @return
     */
    @RequestMapping("refund")
    public ReturnData<WxpayRefund> refund(@RequestBody WxpayRefund wxpayRefund){
        log.info("退款接口接受参数======》:"  + JSON.toJSONString(wxpayRefund));
        wxpayRefund = wxpayRefundService.refund(wxpayRefund);
        return initSuccessObjectResult(wxpayRefund);
    }

    @ApiOperation(value="退款微信回调地址")
    @RequestMapping(value = "notifyUrl", produces = "application/xml")
    public Object notify(HttpServletRequest request){
        try {
            InputStream inStream = request.getInputStream();
            ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = inStream.read(buffer)) != -1) {
                outSteam.write(buffer, 0, len);
            }
            outSteam.close();
            inStream.close();
            String xml = new String(outSteam.toByteArray(), "utf-8");
            log.info("退款xml========" + xml);
            wxpayRefundService.success(xml);
            Map<String, String> result = new HashMap<>();
            result.put("return_code", "SUCCESS");
            result.put("return_msg", "OK");
            return WXPayUtil.mapToXml(result);
        } catch (Exception e) {
            log.error("微信回调错误:" , new Throwable(e));
        }
        return null;
    }
}

