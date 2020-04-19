package com.mmj.pay.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.mmj.common.constants.CommonConstant;
import com.mmj.common.constants.OrderType;
import com.mmj.common.context.BaseContextHandler;
import com.mmj.common.exception.WxException;
import com.mmj.common.model.JwtUserDetails;
import com.mmj.common.properties.SecurityConstants;
import com.mmj.common.utils.EnvUtil;
import com.mmj.common.utils.OrderUtils;
import com.mmj.common.utils.SecurityUserUtil;
import com.mmj.pay.mapper.WxpayOrderMapper;
import com.mmj.pay.model.WxBills;
import com.mmj.pay.model.WxMchInfo;
import com.mmj.pay.model.WxpayOrder;
import com.mmj.pay.model.WxpayOrderEx;
import com.mmj.pay.mq.MQProducer;
import com.mmj.pay.sdk.weixin.WXPayConstants;
import com.mmj.pay.service.WxBillsService;
import com.mmj.pay.service.WxMchInfoService;
import com.mmj.pay.service.WxpayOrderService;
import com.mmj.pay.sdk.weixin.WXPayUtil;
import com.mmj.pay.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 微信支付订单表 服务实现类
 * </p>
 *
 * @author lyf
 * @since 2019-06-05
 */
@Slf4j
@Service
public class WxpayOrderServiceImpl extends ServiceImpl<WxpayOrderMapper, WxpayOrder> implements WxpayOrderService {


    @Value("${app.pay.weixin.notify}")
    private String notify;

    @Value("${app.h5.weixin.wapurl}")
    private String wapUrl;

    @Value("${spring.cloud.config.profile}")
    private String profile;

    @Autowired
    private WxapiService wxapiService;

    @Autowired
    private MQProducer mqProducer;

    @Autowired
    WxMchInfoService wxMchInfoService;

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Autowired
    WxBillsService wxBillsService;

    /**
     * 获取支付信息
     *
     * @param wxpayOrderEx
     * @return
     */
    @Override
    public Map getPayInfo(WxpayOrderEx wxpayOrderEx) {
        JwtUserDetails userDetails = SecurityUserUtil.getUserDetails();
        BaseContextHandler.set(SecurityConstants.SHARDING_KEY, userDetails.getUserId());
        Map<String, String> map = new HashMap();
        wxpayOrderEx.setNotifyUrl(notify);
        map.put("body", wxpayOrderEx.getGoodDesc());  //商品描述
        map.put("out_trade_no", wxpayOrderEx.getOutTradeNo()); //系统订单号
        Integer totalFee = wxpayOrderEx.getTotalFee();
        if (!EnvUtil.isPro(profile)) { //测试环境
            totalFee = 1;
            wxpayOrderEx.setTotalFee(1);
        }
        map.put("total_fee", totalFee+"");  //支付金额 单位分
        map.put("spbill_create_ip", StringUtils.getServerIp());  //服务端ip
        if(StringUtils.isNotEmpty(wxpayOrderEx.getDeviceInfo())){
            map.put("device_info", wxpayOrderEx.getDeviceInfo()); //自定义参数 现在用作订单渠道
        }
        map.put("notify_url", wxpayOrderEx.getNotifyUrl()); //回调地址
        String payType = wxpayOrderEx.getPayType();
        String tradeType = WxpayOrderEx.tradeType.JSAPI.name(); //交易类型
        if(WxpayOrderEx.paySource.H5.name().equalsIgnoreCase(payType)){ //微信外h5支付
            tradeType = WxpayOrderEx.tradeType.MWEB.name();
            JSONObject param = new JSONObject();
            param.put("type", "Wap");
            param.put("wap_url", wapUrl); //支付回调页面
            param.put("wap_name", "买买家");
            JSONObject sceneInfoObj = new JSONObject();
            sceneInfoObj.put("h5_info", param);
            map.put("scene_info", sceneInfoObj.toJSONString());
        }
        map.put("trade_type", tradeType);
        map.put("openid", wxpayOrderEx.getOpenId());
        map.put("appid", wxpayOrderEx.getAppId());
        WxMchInfo mchInfo = wxMchInfoService.getMchInfo(wxpayOrderEx.getOutTradeNo());
        map.put("mchId", mchInfo.getMchId());
        map.put("mchKey", mchInfo.getMchKey());
        Map<String, String> result = wxapiService.unifiedOrder(map);
        if(WXPayUtil.checkResult(result)){
            String timestamp = System.currentTimeMillis()+ "";
            result.put("timestamp", timestamp);
            wxpayOrderEx.setMchId(result.get("mch_id"));
            wxpayOrderEx.setNotifyUrl(notify);
            wxpayOrderEx.setTradeStatus(WxpayOrderEx.tradeStatus.order.name());
            wxpayOrderEx.setCreaterId(userDetails.getUserId());
            wxpayOrderEx.setCreaterTime(new Date());
            EntityWrapper<WxpayOrder> wrapper = new EntityWrapper<>();
            wrapper.eq("out_trade_no", map.get("out_trade_no"));
            WxpayOrder wxpayOrder = selectOne(wrapper);
            if(null == wxpayOrder){
                insert(wxpayOrderEx);
            }else {
                wxpayOrderEx.setPayId(wxpayOrder.getPayId());
                updateById(wxpayOrderEx);
            }
            Map<String, String> signMap = new HashMap<>();
            signMap.put("appId", wxpayOrderEx.getAppId());
            signMap.put("timeStamp", timestamp);
            signMap.put("nonceStr", result.get("nonce_str"));
            signMap.put("package", "prepay_id=" + result.get("prepay_id"));
            signMap.put("signType", WXPayConstants.MD5);
            signMap.put("key", mchInfo.getMchKey());
            result.put("sign", wxapiService.getSign(signMap));  //前端需要的加密参数
            if(StringUtils.isNotEmpty(result.get("mweb_url"))){ //微信外h5支付的时候有此返回值
                result.put("mwebUrl", result.get("mweb_url"));
            }
        }else {
            throw new WxException(result.get("err_code_des"));
        }
        //删除不必要的删除返回给调用方
        result.remove("appid");
        result.remove("mch_id");
        result.remove("result_code");
        result.remove("return_code");
        result.remove("return_msg");
        result.remove("trade_type");
        //参数返回值下划线变驼峰
        String nonceStr = result.get("nonce_str");
        result.remove("nonce_str");
        result.put("nonceStr", nonceStr);
        String prepayId = result.get("prepay_id");
        result.remove("prepay_id");
        result.put("prepayId", prepayId);
        result.put("orderNo", map.get("out_trade_no"));
        return result;
    }

    /**
     * 主动从微信拉取订单号信息，拉取成功就往mq里面塞数据
     *
     * @param wxpayOrder
     */
    @Override
    public void pullOrder(WxpayOrder wxpayOrder) {
        EntityWrapper<WxpayOrder> wrapper = new EntityWrapper<>();
        wrapper.eq("OUT_TRADE_NO", wxpayOrder.getOutTradeNo());
        wxpayOrder = selectOne(wrapper);
        if(null != wxpayOrder && StringUtils.isEmpty(wxpayOrder.getTransactionId())){
            Map<String, String> result = null;
            try {
                Map<String, String> map = new HashMap<>();
                map.put("out_trade_no", wxpayOrder.getOutTradeNo());
                map.put("appid", wxpayOrder.getAppId());
                WxMchInfo wxMchInfo = wxMchInfoService.getMchInfo(wxpayOrder.getOutTradeNo());
                map.put("mchId", wxMchInfo.getMchId());
                map.put("mchKey", wxMchInfo.getMchKey());
                result = wxapiService.queryOrder(map);
                if(WXPayUtil.checkResult(result)){
                    wxpayOrder.setTransactionId(result.get("transaction_id"));
                    doSuccessOrder(wxpayOrder);
                }else {
                    throw new WxException(result.get("err_code_des"));
                }
            } catch (Exception e) {
                log.error("查询微信订单错误, 信息是" + e.getMessage());
            }
        }
    }

    /**
     * 微信回调触发业务处理
     *
     * @param xmlToMap
     */
    @Override
    public void notifyUrl(Map<String, String> xmlToMap) {
        EntityWrapper<WxpayOrder> entityWrapper = new EntityWrapper<>();
        String outTradeNo = xmlToMap.get("out_trade_no");
        if(outTradeNo.contains(CommonConstant.orderSuffix)){
            outTradeNo = outTradeNo.substring(0, outTradeNo.indexOf(CommonConstant.orderSuffix));
        }
        long newDelivery = OrderUtils.getNewDelivery(outTradeNo);
        BaseContextHandler.set(SecurityConstants.SHARDING_KEY, newDelivery);
        entityWrapper.eq("OUT_TRADE_NO", outTradeNo);
        WxpayOrder wxpayOrder = selectOne(entityWrapper);
        wxpayOrder.setTransactionId(xmlToMap.get("transaction_id"));
        doSuccessOrder(wxpayOrder);
    }

    /**
     * 订单成功处理
     * @param wxpayOrder
     */
    private void doSuccessOrder(WxpayOrder wxpayOrder){
        Long increment = redisTemplate.opsForValue().increment("com.mmj.pay.service.impl.WxpayOrderServiceImpl.doSuccessOrder:" + wxpayOrder.getOutTradeNo(), 1);
        redisTemplate.expire("com.mmj.pay.service.impl.WxpayOrderServiceImpl.doSuccessOrder:" + wxpayOrder.getOutTradeNo(), 60, TimeUnit.SECONDS);
        if(increment > 1){
            return;
        }
        String outTradeNo = wxpayOrder.getOutTradeNo();
        wxpayOrder.setTradeStatus(WxpayOrderEx.tradeStatus.pay.name());
        EntityWrapper<WxpayOrder> wrapper = new EntityWrapper<>();
        wrapper.eq("OUT_TRADE_NO", outTradeNo);
        String tempOrderNo = outTradeNo;
        if(tempOrderNo.contains(CommonConstant.orderSuffix)){
            tempOrderNo = tempOrderNo.substring(0,tempOrderNo.indexOf(CommonConstant.orderSuffix));
        }
        long newDelivery = OrderUtils.getNewDelivery(tempOrderNo);
        BaseContextHandler.set(SecurityConstants.SHARDING_KEY, newDelivery);
        update(wxpayOrder, wrapper);
        WxBills wxBills = new WxBills();
        wxBills.setAppid(wxpayOrder.getAppId());
        wxBills.setAttach(wxpayOrder.getPayAttach());
        wxBills.setMchId(wxpayOrder.getMchId());
        wxBills.setOpenid(wxpayOrder.getOpenId());
        wxBills.setOutTradeNo(wxpayOrder.getOutTradeNo());
        wxBills.setTransactionId(wxpayOrder.getTransactionId());
        wxBills.setTotalFee(wxpayOrder.getTotalFee());
        wxBills.setCreateTime(new Date());
        wxBillsService.insert(wxBills);
        if(outTradeNo.contains(CommonConstant.orderSuffix)){ //这种情况下说明有过垮端支付 那么就要处理下订单号 返给业务模块正确的
            outTradeNo = outTradeNo.substring(0 , outTradeNo.indexOf(CommonConstant.orderSuffix));
            wxpayOrder.setOutTradeNo(outTradeNo);
        }
        redisTemplate.opsForValue().set("wx_order:" +outTradeNo , JSON.toJSONString(wxpayOrder));
        mqProducer.sendOrderPay(wxpayOrder);
    }
}
