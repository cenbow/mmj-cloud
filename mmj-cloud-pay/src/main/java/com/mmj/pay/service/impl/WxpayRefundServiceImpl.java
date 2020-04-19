package com.mmj.pay.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.mmj.common.constants.CommonConstant;
import com.mmj.common.constants.MQTopicConstant;
import com.mmj.common.context.BaseContextHandler;
import com.mmj.common.exception.WxException;
import com.mmj.common.model.ReturnData;
import com.mmj.common.model.WxConfig;
import com.mmj.common.model.wx.RefundSuccess;
import com.mmj.common.properties.SecurityConstants;
import com.mmj.common.utils.MD5Util;
import com.mmj.common.utils.OrderUtils;
import com.mmj.pay.common.feign.UserFeignClient;
import com.mmj.pay.mapper.WxpayRefundMapper;
import com.mmj.pay.model.WxMchInfo;
import com.mmj.pay.model.WxpayOrder;
import com.mmj.pay.model.WxpayRefund;
import com.mmj.pay.sdk.weixin.WXPayUtil;
import com.mmj.pay.service.ViewWxpayOrderService;
import com.mmj.pay.service.WxMchInfoService;
import com.mmj.pay.service.WxpayOrderService;
import com.mmj.pay.service.WxpayRefundService;
import com.mmj.pay.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Security;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 微信退款表 服务实现类
 * </p>
 *
 * @author shenfuding
 * @since 2019-07-15
 */
@Service
@Slf4j
public class WxpayRefundServiceImpl extends ServiceImpl<WxpayRefundMapper, WxpayRefund> implements WxpayRefundService {

    @Autowired
    WxpayOrderService wxpayOrderService;

    @Autowired
    WxapiService wxapiService;

    @Autowired
    ViewWxpayOrderService viewWxpayOrderService;

    @Autowired
    UserFeignClient userFeignClient;

    @Autowired
    WxMchInfoService wxMchInfoService;

    @Autowired
    KafkaTemplate kafkaTemplate;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Value("${app.context}")
    private String context;

    /**
     * 微信退款接口
     *
     * @param wxpayRefund
     * @return
     */
    @Override
    public WxpayRefund refund(WxpayRefund wxpayRefund) {
        if(!OrderUtils.isNewOrOld(wxpayRefund.getOutTradeNo())){ //不是新版本订单 那么就直接返回成功 实际处理手动退款
            return null;
        }
        long newDelivery = OrderUtils.getNewDelivery(wxpayRefund.getOutTradeNo());
        BaseContextHandler.set(SecurityConstants.SHARDING_KEY, newDelivery);
        WxMchInfo wxMchInfo = wxMchInfoService.getMchInfo(wxpayRefund.getOutTradeNo());
        EntityWrapper<WxpayOrder> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("OUT_TRADE_NO", wxpayRefund.getOutTradeNo());
        WxpayOrder wxpayOrder = wxpayOrderService.selectOne(entityWrapper);
        if(null == wxpayOrder || StringUtils.isEmpty(wxpayOrder.getTransactionId())){
            throw  new WxException("订单未支付");
        }
        Map<String, String> map = new HashMap();
        map.put("transaction_id", wxpayOrder.getTransactionId());
        map.put("refund_fee", wxpayRefund.getRefundFee()+"");
        map.put("total_fee", wxpayOrder.getTotalFee()+"");
        if(StringUtils.isNotEmpty(wxpayRefund.getRefundDesc())){
            map.put("refund_desc", wxpayRefund.getRefundDesc());
        }
        map.put("out_refund_no", StringUtils.getUUID());
        map.put("notify_url", context + "wxpayRefund/notifyUrl");
        map.put("appid", wxpayOrder.getAppId());
        map.put("mchId", wxMchInfo.getMchId());
        map.put("mchKey", wxMchInfo.getMchKey());
        Map<String, String> result =  wxapiService.refund(map);
        if(WXPayUtil.checkResult(result)){ //微信结果正确返回
            wxpayRefund.setTransactionId(result.get("transaction_id"));
            wxpayRefund.setOutTradeNo(result.get("out_trade_no"));
            wxpayRefund.setOutRefundNo(result.get("out_refund_no"));
            wxpayRefund.setTotalFee(wxpayOrder.getTotalFee());
            wxpayRefund.setState(0);
            wxpayRefund.setCreateTime(new Date());
            wxpayRefund.setAppid(wxpayOrder.getAppId());
            insert(wxpayRefund);
        }else {
            wxpayRefund.setOutTradeNo(wxpayRefund.getOutTradeNo());
            wxpayRefund.setState(1);
            wxpayRefund.setErrorDesc(result.get("err_code_des"));
            wxpayRefund.setCreateTime(new Date());
            insert(wxpayRefund);
            throw new WxException(result.get("err_code_des"));
        }
        return wxpayRefund;
    }

    /**
     * 退款成功时候的处理
     *
     * @param xml
     */
    @Override
    public void success(String xml) {
        try {
            Map<String, String> xmlToMap = WXPayUtil.xmlToMap(xml);
            EntityWrapper<WxMchInfo> wxMchInfoEntityWrapper = new EntityWrapper<>();
            wxMchInfoEntityWrapper.eq("MCH_ID", xmlToMap.get("mch_id"));
            WxMchInfo wxMchInfo = wxMchInfoService.selectOne(wxMchInfoEntityWrapper);
            if(null == wxMchInfo){
                return;
            }
            byte[] b = org.bouncycastle.util.encoders.Base64.decode(xmlToMap.get("req_info"));
            String key =  MD5Util.MD5Encode(wxMchInfo.getMchKey(), "utf-8").toLowerCase();
            Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS7Padding");
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), "AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
            String msg = new String(cipher.doFinal(b));
            Map<String, String> map = WXPayUtil.xmlToMap(msg);
            String outTradeNo = map.get("out_trade_no");
            if(outTradeNo.contains(CommonConstant.orderSuffix)){
                outTradeNo = outTradeNo.substring(0,outTradeNo.indexOf(CommonConstant.orderSuffix));
            }
            RefundSuccess refundSuccess = new RefundSuccess();
            refundSuccess.setOutRefundNo(map.get("out_refund_no"));
            refundSuccess.setOutTradeNo(outTradeNo);
            refundSuccess.setRefundAccount(map.get("refund_account"));
            refundSuccess.setRefundFee(Integer.parseInt(map.get("refund_fee")));
            refundSuccess.setRefundId(map.get("refund_id"));
            refundSuccess.setRefundRecvAccout(map.get("refund_recv_accout"));
            refundSuccess.setRefundRequestSource(map.get("refund_request_source"));
            refundSuccess.setRefundStatus(map.get("refund_status"));
            refundSuccess.setSettlementRefundFee(Integer.parseInt(map.get("settlement_refund_fee")));
            refundSuccess.setSettlementTotalFee(Integer.parseInt(map.get("settlement_total_fee")));
            refundSuccess.setSuccessTime(map.get("success_time"));
            refundSuccess.setTotalFee(Integer.parseInt(map.get("total_fee")));
            refundSuccess.setTransactionId(map.get("transaction_id"));
            Long increment = redisTemplate.opsForValue().increment(MQTopicConstant.WX_REFUND_SUCCESS + outTradeNo, 1);
            redisTemplate.expire(MQTopicConstant.WX_REFUND_SUCCESS + outTradeNo, 60, TimeUnit.SECONDS);
            if(increment == 1){
                kafkaTemplate.send(MQTopicConstant.WX_REFUND_SUCCESS, JSON.toJSONString(refundSuccess));
                log.info("微信退款成功通知MQProducer send msg {} success", JSON.toJSONString(refundSuccess));
            }
        }catch (Exception e){
            log.error("退款通知处理异常", e);
        }
    }
}
