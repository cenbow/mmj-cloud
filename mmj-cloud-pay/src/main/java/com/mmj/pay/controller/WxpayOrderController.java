package com.mmj.pay.controller;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.mmj.common.context.BaseContextHandler;
import com.mmj.common.controller.BaseController;
import com.mmj.common.model.JwtUserDetails;
import com.mmj.common.model.ReturnData;
import com.mmj.common.properties.SecurityConstants;
import com.mmj.common.utils.SecurityUserUtil;
import com.mmj.pay.model.WxpayOrder;
import com.mmj.pay.model.WxpayOrderEx;
import com.mmj.pay.sdk.weixin.WXPayUtil;
import com.mmj.pay.service.WxpayOrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 微信支付订单表 前端控制器
 * </p>
 *
 * @author lyf
 * @since 2019-06-05
 */
@Slf4j
@RestController
@RequestMapping("/wxpayOrder")
@Api(value = "微信支付")
public class WxpayOrderController extends BaseController {

    @Autowired
    private WxpayOrderService wxpayOrderService;

    @ApiOperation(value="获取支付信息")
    @RequestMapping(value="/getPayInfo",method = RequestMethod.POST)
    public ReturnData<Map> getPayInfo(@RequestBody WxpayOrderEx wxpayOrderEx, HttpServletRequest request) {
        String appType = request.getHeader(SecurityConstants.APP_TYPE);
        wxpayOrderEx.setPayType(appType);
        Map map = wxpayOrderService.getPayInfo(wxpayOrderEx);
        return initSuccessObjectResult(map);
    }

    @ApiOperation(value="微信回调地址")
    @RequestMapping(value = "notifyUrl", produces = "application/xml")
    public Object notifyUrl(HttpServletRequest request){
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
            log.info("xml========" + xml);
            Map<String, String> xmlToMap = WXPayUtil.xmlToMap(xml);
            wxpayOrderService.notifyUrl(xmlToMap);
            Map<String, String> result = new HashMap<>();
            result.put("return_code", "SUCCESS");
            result.put("return_msg", "OK");
            return WXPayUtil.mapToXml(result);
        } catch (Exception e) {
           log.error("微信回调错误:" , new Throwable(e));
        }
        return null;
    }

    @ApiOperation(value="主动从微信平台拉取订单数据")
    @PostMapping("pullOrder")
    public ReturnData<Map> pullOrder(@RequestBody WxpayOrder wxpayOrder){
        JwtUserDetails userDetails = SecurityUserUtil.getUserDetails();
        BaseContextHandler.set(SecurityConstants.SHARDING_KEY, userDetails.getUserId());
        EntityWrapper<WxpayOrder> wrapper = new EntityWrapper<>();
        wrapper.eq("OUT_TRADE_NO", wxpayOrder.getOutTradeNo());
        wxpayOrder = wxpayOrderService.selectOne(wrapper);
        if(null == wxpayOrder){
            return initErrorObjectResult("系统表订单号不存在");
        }
        if(StringUtils.isEmpty(wxpayOrder.getTransactionId())){
            log.info("主动拉取订单信息,订单号:" + wxpayOrder.getOutTradeNo());
            wxpayOrderService.pullOrder(wxpayOrder);
        }
        return initSuccessResult();
    }


}

