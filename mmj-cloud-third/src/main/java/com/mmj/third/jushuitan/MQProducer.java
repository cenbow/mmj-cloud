package com.mmj.third.jushuitan;

import com.alibaba.fastjson.JSONObject;
import com.mmj.common.constants.MQTopicConstant;
import com.mmj.common.utils.SnowflakeIdWorker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class MQProducer {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    private SnowflakeIdWorker snowflakeIdWorker;

    public void send(Object object, String topic) {
        Long key = snowflakeIdWorker.nextId();
        String content = JSONObject.toJSONString(object);
        kafkaTemplate.send(topic, key + "", content);
        log.info("MQProducer send msg topic:{},content:{}", topic, content);
    }

    /**
     * 同步上传聚水潭状态
     *
     * @param packageNos
     */
    public void sendUploadStatus(List<String> packageNos) {
        Long key = snowflakeIdWorker.nextId();
        String content = JSONObject.toJSONString(packageNos);
        kafkaTemplate.send(MQTopicConstant.ORDER_UPLOAD_STATUS_TO_BE_DELIVERED_TOPIC, key + "", content);
        log.info("=> 同步上传ERP状态 content:{}", content);
    }

}

