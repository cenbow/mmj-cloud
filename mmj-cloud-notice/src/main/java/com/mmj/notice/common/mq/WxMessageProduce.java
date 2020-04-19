package com.mmj.notice.common.mq;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mmj.common.constants.MQTopicConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class WxMessageProduce {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * 公众号客服消息kafka
     * @param msg
     */
    public void sendWxH5Msg(JSONObject msg) {
        kafkaTemplate.send(MQTopicConstant.WX_H5_MSG, JSON.toJSONString(msg));
        log.info("公众号客服消息mq消息MQProducer send msg {} success", JSON.toJSONString(msg));
    }

    /**
     * 小程序客服消息kafka
     * @param msg
     */
    public void sendWxMinMsg(JSONObject msg) {
        kafkaTemplate.send(MQTopicConstant.WX_MIN_MSG, JSON.toJSONString(msg));
        log.info("小程序客服消息mq消息MQProducer send msg {} success", JSON.toJSONString(msg));
    }
}
