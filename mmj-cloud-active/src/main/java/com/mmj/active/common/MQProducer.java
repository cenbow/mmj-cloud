package com.mmj.active.common;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mmj.common.constants.MQCommonTopic;
import com.mmj.common.constants.MQTopicConstant;
import com.mmj.common.model.TemplateMessage;
import com.mmj.common.model.order.OrderProduceDto;
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


    public void sendTemplateMessage(TemplateMessage msg) {
        Long key = snowflakeIdWorker.nextId();
        kafkaTemplate.send(MQCommonTopic.SEND_TEMPLATE_MESSAGE, key + "", JSON.toJSONString(msg));
        log.info("active发送模板消息 {} success", JSON.toJSONString(msg));
    }


    /**
     * 退款方法
     *
     * @param object
     * @param topic
     */

    public void send(JSONObject object, String topic) {
        Long key = snowflakeIdWorker.nextId();
        kafkaTemplate.send(topic, key.toString(), JSONObject.toJSONString(object));
        log.info("MQProducer send msg key {} success", key.toString());
    }

    /**
     * 批量
     *
     * @param object
     * @param topic
     */
    public void send(List<JSONObject> object, String topic) {
        Long key = snowflakeIdWorker.nextId();
        kafkaTemplate.send(topic, key.toString(), JSONObject.toJSONString(object));
    }

    public void addMMKing(Long userId, String type) {
        Long key = snowflakeIdWorker.nextId();
        JSONObject object = new JSONObject();
        object.put("userId", userId);
        object.put("type", type);
        kafkaTemplate.send(MQCommonTopic.MMKING_TOPIC, key.toString(), object.toJSONString());
        log.info("参与活动新增买买金 key {} success", key.toString());
    }

    public void send(String topic, String jsonString) {
        Long key = snowflakeIdWorker.nextId();
        kafkaTemplate.send(topic, key.toString(), jsonString);
        log.info("MQProducer send msg key {} success", key.toString());
    }

    /**
     * 同步活动订单
     *
     * @param orderProduceDto
     */
    public void produceOrder(OrderProduceDto orderProduceDto) {
        Long key = snowflakeIdWorker.nextId();
        String content = JSONObject.toJSONString(orderProduceDto);
        kafkaTemplate.send(MQTopicConstant.SYNC_ACTIVE_ORDER_TOPIC, key.toString(), content);
        log.info("活动生成订单 send msg success content:{}", content);
    }

    /**
     * 话费订单充值成功后状态同步
     *
     * @param orderNo
     */
    public void syncOrderStatus(String orderNo) {
        Long key = snowflakeIdWorker.nextId();
        String content = orderNo;
        kafkaTemplate.send(MQTopicConstant.SYNC_RECHARGE_ORDER_STATUS, key.toString(), content);
        log.info("同步话费充值成功状态 send msg success content:{}", content);
    }
}

