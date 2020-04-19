package com.mmj.aftersale.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mmj.common.constants.MQCommonTopic;
import com.mmj.common.constants.MQTopicConstant;
import com.mmj.common.model.TemplateMessage;
import com.mmj.common.utils.SnowflakeIdWorker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MQProducer {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    SnowflakeIdWorker snowflakeIdWorker;

    public void sendTemplateMessage(TemplateMessage msg) {
        Long key = snowflakeIdWorker.nextId();
        kafkaTemplate.send(MQCommonTopic.SEND_TEMPLATE_MESSAGE, key + "", JSON.toJSONString(msg));
        log.info("aftersale发送模板消息 {} success", JSON.toJSONString(msg));
    }

    /**
     * 冻结订单获得买买金
     *
     * @param orderNo
     * @param userId
     * @param status
     */
    public void updateMMKing(String orderNo, Long userId, Integer status) {
        Long key = snowflakeIdWorker.nextId();
        JSONObject object = new JSONObject();
        object.put("orderNo", orderNo);
        object.put("userId", userId);
        object.put("status", status);
        kafkaTemplate.send(MQCommonTopic.FROZEN_TOPIC, key + "", JSON.toJSONString(object));
        log.info("申请售后冻结买买金消息 {} success", JSON.toJSONString(object));
    }

    /**
     * 售后状态变更同步
     *
     * @param orderNo
     * @param userId
     * @param afterNo
     */
    public void updateAfterStatus(String orderNo, long userId, String afterNo) {
        Long key = snowflakeIdWorker.nextId();
        JSONObject object = new JSONObject();
        object.put("orderNo", orderNo);
        object.put("userId", userId);
        object.put("afterNo", afterNo);
        kafkaTemplate.send(MQTopicConstant.AFTER_STATUS_SYNCHRONIZATION, key + "", JSON.toJSONString(object));
    }
}

