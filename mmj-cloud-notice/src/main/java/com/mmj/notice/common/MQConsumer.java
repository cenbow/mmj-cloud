package com.mmj.notice.common;

import com.alibaba.fastjson.JSONObject;
import com.mmj.common.constants.MQCommonTopic;
import com.mmj.common.model.SmsDto;
import com.mmj.notice.common.utils.SMSUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class MQConsumer {

    @Autowired
    private SMSUtils smsUtils;

    @KafkaListener(topics = {MQCommonTopic.SMS_TOPIC})
    public void listen(List<ConsumerRecord<?, ?>> records) {
        for (ConsumerRecord<?, ?> record : records) {
            Optional<?> kafkaMessage = Optional.ofNullable(record.value());
            if (kafkaMessage.isPresent()) {
                SmsDto dto = JSONObject.parseObject(record.value().toString(), SmsDto.class);
                smsUtils.send(dto);
            }
        }
    }
}
