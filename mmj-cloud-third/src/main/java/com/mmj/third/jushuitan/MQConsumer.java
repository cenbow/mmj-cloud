package com.mmj.third.jushuitan;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.mmj.common.constants.MQTopicConstant;
import com.mmj.third.jushuitan.model.request.OrdersUploadRequest;
import com.mmj.third.jushuitan.service.JushuitanService;
import com.mmj.third.jushuitan.utils.JushuiTanConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Component
public class MQConsumer {
    @Autowired
    private JushuitanService jushuitanService;
    @Autowired
    private MQProducer mqProducer;
    @Value("${jushuitan.shopId}")
    private Integer shopId;

    @KafkaListener(topics = {JushuiTanConstant.TOPIC_ORDER_UPLOAD})
    public void listen(String params) {
        log.info("=>kafka-listen:{} -> {}", JushuiTanConstant.TOPIC_ORDER_UPLOAD, params);
        OrdersUploadRequest ordersUploadRequest = JSONObject.parseObject(params, OrdersUploadRequest.class);
        if (Objects.nonNull(ordersUploadRequest)) {
            ordersUploadRequest.setShopId(shopId);
            ordersUploadRequest.setShopStatus("WAIT_SELLER_SEND_GOODS");
            ordersUploadRequest.getPay().setBuyerAccount("111");
            ordersUploadRequest.getPay().setSellerAccount("222");
            ordersUploadRequest.getItems().forEach(item -> {
                item.setOuterOiId(ordersUploadRequest.getSoId() + "-" + item.getSkuId());
            });
            jushuitanService.jushuitanOrderUpload(ordersUploadRequest);
        }
    }

    /**
     * 上传聚水潭
     *
     * @param records
     */
    @KafkaListener(topics = {MQTopicConstant.ORDER_UPLOAD_JST_TOPIC})
    public void orderUploadListen(List<ConsumerRecord<?, ?>> records) {
        for (ConsumerRecord<?, ?> record : records) {
            Optional<?> kafkaMessage = Optional.ofNullable(record.value());
            log.info("Received: " + record);
            if (kafkaMessage.isPresent()) {
                Object message = record.value();
                String topic = record.topic();
                log.info("上传订单 topic={},message={}", topic, message);
                List<OrdersUploadRequest> ordersUploadRequests = JSONObject.parseArray(message.toString(), OrdersUploadRequest.class);
                if (Objects.nonNull(ordersUploadRequests) && ordersUploadRequests.size() > 0) {
                    ordersUploadRequests.forEach(ordersUploadRequest -> {
                        ordersUploadRequest.setShopId(shopId);
                    });
                    try {
                        List<String> packageNoList = Lists.newArrayListWithCapacity(ordersUploadRequests.size());
                        ordersUploadRequests.forEach(ordersUploadRequest -> packageNoList.add(ordersUploadRequest.getSoId()));
                        jushuitanService.jushuitanOrderUpload(ordersUploadRequests.toArray(new OrdersUploadRequest[ordersUploadRequests.size()]));
                        mqProducer.sendUploadStatus(packageNoList);
                    } catch (Exception e) {
                        log.error("上传聚水潭错误,error:{},content:{}", e.getMessage(), message);
                    }
                }
            }
        }

    }

}
