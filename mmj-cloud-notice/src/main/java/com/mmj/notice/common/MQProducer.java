package com.mmj.notice.common;

import com.alibaba.fastjson.JSON;
import com.mmj.common.constants.MQCommonTopic;
import com.mmj.common.model.SmsDto;
import com.mmj.common.utils.SnowflakeIdWorker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MQProducer {

    @Autowired
    SnowflakeIdWorker snowflakeIdWorker;
    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    public void sendSmsMsg(SmsDto msg) {
        Long key = snowflakeIdWorker.nextId();
        kafkaTemplate.send(MQCommonTopic.SMS_TOPIC, key + "", JSON.toJSONString(msg));
        log.info("发送及时短信消息 {} success", JSON.toJSONString(msg));
    }
}

