package com.mmj.pay.mq;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mmj.common.constants.MQCommonTopic;
import com.mmj.pay.model.WxpayRefund;
import com.mmj.pay.service.WxpayRefundService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class MQConsumer {


    private final WxpayRefundService wxpayRefundService;

    public MQConsumer(WxpayRefundService wxpayRefundService) {
        this.wxpayRefundService = wxpayRefundService;
    }

    @KafkaListener(topics = {MQCommonTopic.REFUND_TOPIC})
    public void refund(String params) {
        log.info("进入抽奖退款方法... {}", params);
        List<JSONObject> array = JSONArray.parseArray(params, JSONObject.class);
        for (JSONObject object : array) {
            if (!object.containsKey("orderNo"))
                continue;
            WxpayRefund refund = new WxpayRefund();
            refund.setOutTradeNo(object.getString("orderNo"));
            refund.setRefundFee(object.containsKey("price") ? object.getInteger("price") : 1);
            refund.setRefundDesc("抽奖未中奖退款");
            wxpayRefundService.refund(refund);
        }
    }
}
