package com.mmj.pay.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.mmj.common.constants.CommonConstant;
import com.mmj.common.utils.StringUtils;
import com.mmj.pay.sdk.weixin.MmjPayWxPayConfig;
import com.mmj.pay.sdk.weixin.WXPay;
import com.mmj.pay.sdk.weixin.WXPayConstants;
import com.mmj.pay.sdk.weixin.WXPayUtil;
import com.mmj.common.utils.HttpTools;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

/**
 * 对微信接口操作的service
 */
@Slf4j
@Service
public class WxapiService {

    /**
     * 获取sign
     * @param map
     * @return
     */
    public String getSign(Map<String, String> map){
        try {
            String key = map.get("key");
            map.remove("key");
            return WXPayUtil.generateSignature(map, key, WXPayConstants.SignType.MD5);
        } catch (Exception e) {
            log.error("加密参数错误===" , new Throwable(e));
        }
        return null;
    }

    /**
     * 下单接口
     * @param map
     * @return
     */
    public Map<String, String> unifiedOrder(Map<String, String> map){
        try {
            MmjPayWxPayConfig mmjPayWxPayConfig = new MmjPayWxPayConfig();
            mmjPayWxPayConfig.setMchID(map.get("mchId"));
            mmjPayWxPayConfig.setKey(map.get("mchKey"));
            map.remove("mchId");
            map.remove("mchKey");
            WXPay wxPay = new WXPay(mmjPayWxPayConfig);
            map.put("sign_type", WXPayConstants.MD5);
            log.info("商户信息"+JSONObject.toJSONString(mmjPayWxPayConfig)+"微信下单入参:" + JSONObject.toJSONString(map));
            Map<String, String> refund = wxPay.unifiedOrder(map);
            String errCodeDes = refund.get("err_code_des");
            if(StringUtils.isNotEmpty(errCodeDes) && errCodeDes.contains("商户订单号重复")){ //这种情况是垮端支付了 那么要更改订单号重新支付
                String appid = map.get("appid");
                String outTradeNo = map.get("out_trade_no");
                map.put("out_trade_no", outTradeNo + CommonConstant.orderSuffix + appid.substring(appid.length() -2, appid.length()));
                log.info("订单号" + outTradeNo + "跨端支付了, 新订单号是:" + map.get("out_trade_no"));
                refund = wxPay.unifiedOrder(map);
            }
            log.info("微信下单出参:" + JSONObject.toJSONString(refund));
            return refund;
        } catch (Exception e) {
            log.error("向微信下单失败," , new Throwable(e));
        }
        return null;
    }

    /**
     * 查询订单
     * @param map
     * @return
     */
    public Map<String, String> queryOrder(Map<String, String> map) throws Exception{
        MmjPayWxPayConfig mmjPayWxPayConfig = new MmjPayWxPayConfig();
        String mchId = map.get("mchId");
        String mchKey = map.get("mchKey");
        mmjPayWxPayConfig.setMchID(mchId);
        mmjPayWxPayConfig.setKey(mchKey);
        map.remove("mchId");
        map.remove("mchKey");
        map.put("sign_type", WXPayConstants.MD5);
        WXPay wxPay = new WXPay(mmjPayWxPayConfig);
        log.info("商户信息"+JSONObject.toJSONString(mmjPayWxPayConfig)+ "微信订单查询入参:" + JSONObject.toJSONString(map));
        Map<String, String> refund = wxPay.orderQuery(map);
        log.info("微信订单查询出参:" + JSONObject.toJSONString(refund));
        return refund;
    }

    /**
     * 退款接口
     * @param map
     * @return
     */
    public Map<String, String> refund(Map<String, String> map) {
        MmjPayWxPayConfig mmjPayWxPayConfig = new MmjPayWxPayConfig();
        String mchId = map.get("mchId");
        String mchKey = map.get("mchKey");
        mmjPayWxPayConfig.setMchID(mchId);
        mmjPayWxPayConfig.setKey(mchKey);
        map.remove("mchId");
        map.remove("mchKey");
        String CERT_NAME_PREFIX = "apiclient_cert?.p12";
        CERT_NAME_PREFIX = CERT_NAME_PREFIX.replace("?", mchId.substring(mchId.length() - 4, mchId.length()));
        log.info("商户号:"+mchId+"退款商户证书:" + CERT_NAME_PREFIX);
        try {
            InputStream inputStream = new ClassPathResource(CERT_NAME_PREFIX).getInputStream();
            mmjPayWxPayConfig.setInputStream(inputStream);
            map.put("sign_type", WXPayConstants.MD5);
            WXPay wxPay = new WXPay(mmjPayWxPayConfig);
            log.info("商户信息"+JSONObject.toJSONString(mmjPayWxPayConfig)+ "退款入参:" + JSONObject.toJSONString(map));
            Map<String, String> refund = wxPay.refund(map);
            log.info("退款出参:" + JSONObject.toJSONString(refund));
            if(null != inputStream){
                inputStream.close();
            }
            return refund;
        } catch (Exception e) {
            log.error("退款接口失败, 错误信息" , new Throwable(e));
        }
        return null;
    }


    /**
     * 发送零钱接口
     * @param map
     * @return
     */
    public Map<String, String> transfers(Map<String, String> map) {
        MmjPayWxPayConfig mmjPayWxPayConfig = new MmjPayWxPayConfig();
        String mchId = map.get("mchId");
        String mchKey = map.get("mchKey");
        mmjPayWxPayConfig.setMchID(mchId);
        mmjPayWxPayConfig.setKey(mchKey);
        map.remove("mchId");
        map.remove("mchKey");
        String CERT_NAME_PREFIX = "apiclient_cert?.p12";
        CERT_NAME_PREFIX = CERT_NAME_PREFIX.replace("?", mchId.substring(mchId.length() - 4, mchId.length()));
        try {
            map.put("mchid", mchId);
            InputStream inputStream = new ClassPathResource(CERT_NAME_PREFIX).getInputStream();
            mmjPayWxPayConfig.setInputStream(inputStream);
            mmjPayWxPayConfig.setKey(mchKey);
            WXPay wxPay = new WXPay(mmjPayWxPayConfig);
            String hostAddress = InetAddress.getLocalHost().getHostAddress();
            map.put("spbill_create_ip", hostAddress);
            log.info("商户信息"+JSONObject.toJSONString(mmjPayWxPayConfig)+ "发送零钱入参:" + JSONObject.toJSONString(map));
            Map<String, String> refund = wxPay.transfers(map);
            log.info("发送零钱出参:" + JSONObject.toJSONString(refund));
            if(null != inputStream){
                inputStream.close();
            }
            refund.put("spbill_create_ip", hostAddress);
            return refund;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("发送零钱接口失败, 错误信息" , new Throwable(e));
        }
        return null;
    }

    /**
     * 发送普通红包接口
     * @param map
     * @return
     */
    public Map<String, String> sendRedpack(Map<String, String> map) {
        MmjPayWxPayConfig mmjPayWxPayConfig = new MmjPayWxPayConfig();
        String mchId = map.get("mchId");
        String mchKey = map.get("mchKey");
        mmjPayWxPayConfig.setMchID(mchId);
        mmjPayWxPayConfig.setKey(mchKey);
        map.remove("mchId");
        map.remove("mchKey");
        String CERT_NAME_PREFIX = "apiclient_cert?.p12";
        CERT_NAME_PREFIX = CERT_NAME_PREFIX.replace("?", mchId.substring(mchId.length() - 4, mchId.length()));
        try {
            map.put("mch_id", mchId);
            InputStream inputStream = new ClassPathResource(CERT_NAME_PREFIX).getInputStream();
            mmjPayWxPayConfig.setInputStream(inputStream);
            mmjPayWxPayConfig.setKey(mchKey);
            WXPay wxPay = new WXPay(mmjPayWxPayConfig);
            String hostAddress = InetAddress.getLocalHost().getHostAddress();
            map.put("client_ip", hostAddress);
            map.put("scene_id", "PRODUCT_4");//设置这个是因为要发3毛钱的红包
            log.info("商户信息"+JSONObject.toJSONString(mmjPayWxPayConfig)+ "发送红包入参:" + JSONObject.toJSONString(map));
            Map<String, String> refund = wxPay.sendredpack(map);
            log.info("发送红包出参:" + JSONObject.toJSONString(refund));
            if(null != inputStream){
                inputStream.close();
            }
            refund.put("client_ip", hostAddress);
            return refund;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("发送红包接口失败, 错误信息" , new Throwable(e));
        }
        return null;
    }

}
