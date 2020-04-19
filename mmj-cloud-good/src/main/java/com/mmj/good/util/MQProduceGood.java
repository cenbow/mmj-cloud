package com.mmj.good.util;

import com.mmj.common.constants.MQTopicConstant;
import org.springframework.kafka.core.KafkaTemplate;

public class MQProduceGood {

    /**
     * 商品基本信息更新消息
     * @param kafkaTemplate
     * @param jsonString
     */
    public static void goodInfoUpdate(KafkaTemplate kafkaTemplate, String jsonString){
        kafkaTemplate.send(MQTopicConstant.GOOD_INFO_UPDATE, jsonString);
    }

    /**
     * 商品销售信息更新消息
     * @param kafkaTemplate
     * @param jsonString
     */
    public static void goodSaleUpdate(KafkaTemplate kafkaTemplate, String jsonString){
        kafkaTemplate.send(MQTopicConstant.GOOD_SALE_UPDATE, jsonString);
    }

    /**
     * 商品状态变更消息-批量
     * @param kafkaTemplate
     * @param jsonString
     */
    public static void goodStatusUpdate(KafkaTemplate kafkaTemplate, String jsonString){
        kafkaTemplate.send(MQTopicConstant.GOOD_STATUS_UPDATE, jsonString);
    }
}
