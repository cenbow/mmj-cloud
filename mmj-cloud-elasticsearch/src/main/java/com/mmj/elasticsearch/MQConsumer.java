package com.mmj.elasticsearch;

import com.alibaba.fastjson.JSONObject;
import com.mmj.common.constants.MQTopicConstant;
import com.mmj.common.model.order.OrderPayDto;
import com.mmj.common.model.order.OrderStatusMQDto;
import com.mmj.common.model.order.OrdersMQDto;
import com.mmj.common.model.order.OrdersPackageMQDto;
import com.mmj.elasticsearch.order.domain.OrdersDocument;
import com.mmj.elasticsearch.order.service.OrdersDocumentService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Optional;


@Slf4j
@Component
public class MQConsumer {
    @Autowired
    private OrdersDocumentService ordersDocumentService;

    /**
     * 下单
     *
     * @param records
     */
    @KafkaListener(topics = {MQTopicConstant.SYNC_ORDER_TO_ES_TOPIC})
    public void listen(List<ConsumerRecord<?, ?>> records) {
        for (ConsumerRecord<?, ?> record : records) {
            Optional<?> kafkaMessage = Optional.ofNullable(record.value());
            log.info("Received: " + record);
            if (kafkaMessage.isPresent()) {
                Object message = record.value();
                String topic = record.topic();
                log.info("=> 下单订单同步ES -> topic:{},message:{}", topic, message);
                OrdersMQDto ordersMQDto = JSONObject.parseObject(message.toString(), OrdersMQDto.class);
                if (Objects.nonNull(ordersMQDto)) {
                    OrdersDocument ordersDocument = new OrdersDocument();
                    BeanUtils.copyProperties(ordersMQDto, ordersDocument);
                    ordersDocument.setOrderTime(ordersMQDto.getOrderDate().getTime());
                    ordersDocument.setConsigneeName(ordersMQDto.getConsignee().getName());
                    ordersDocument.setConsigneeTelNumber(ordersMQDto.getConsignee().getTelNumber());
                    ordersDocument.setChannel(ordersMQDto.getChannel());
                    ordersDocument.setSource(ordersMQDto.getSource());
                    if (Objects.nonNull(ordersMQDto.getPay())) {
                        ordersDocument.setPayAmount(ordersMQDto.getPay().getPayAmount());
                        ordersDocument.setPayTime(ordersMQDto.getPay().getPayTime().getTime());
                    }
                    ordersDocumentService.create(ordersDocument);
                }
            }
        }
    }

    @KafkaListener(topics = {MQTopicConstant.SYNC_PACKAGE_TO_ES_TOPIC})
    public void packageListen(List<ConsumerRecord<?, ?>> records) {
        for (ConsumerRecord<?, ?> record : records) {
            Optional<?> kafkaMessage = Optional.ofNullable(record.value());
            log.info("Received: " + record);
            if (kafkaMessage.isPresent()) {
                Object message = record.value();
                String topic = record.topic();
                log.info("=> 拆单后包裹订单同步ES -> topic:{},message:{}", topic, message);
                List<OrdersPackageMQDto> ordersPackageMQDtoList = JSONObject.parseArray(message.toString(), OrdersPackageMQDto.class);
                if (Objects.nonNull(ordersPackageMQDtoList)) {
                    ordersDocumentService.batchCreatePackage(ordersPackageMQDtoList);
                }
            }
        }
    }

    @KafkaListener(topics = {MQTopicConstant.SYNC_ORDER_STATUS_TO_ES_TOPIC})
    public void listenOrderStatus(List<ConsumerRecord<?, ?>> records) {
        for (ConsumerRecord<?, ?> record : records) {
            Optional<?> kafkaMessage = Optional.ofNullable(record.value());
            log.info("Received: " + record);
            if (kafkaMessage.isPresent()) {
                Object message = record.value();
                String topic = record.topic();
                log.info("=> 订单状态同步ES -> topic:{},message:{}", topic, message);
                OrderStatusMQDto orderStatusMQDto = JSONObject.parseObject(message.toString(), OrderStatusMQDto.class);
                if (Objects.nonNull(orderStatusMQDto)) {
                    System.out.println(orderStatusMQDto);
                    ordersDocumentService.updateOrderStatusByOrderNo(orderStatusMQDto.getOrderNo(), orderStatusMQDto.getOrderStatus());
                }
            }
        }
    }

    @KafkaListener(topics = {MQTopicConstant.MMJ_SYNC_ORDER_PAY_TO_ES_TOPIC})
    public void listenOrderPay(List<ConsumerRecord<?, ?>> records) {
        for (ConsumerRecord<?, ?> record : records) {
            Optional<?> kafkaMessage = Optional.ofNullable(record.value());
            log.info("Received: " + record);
            if (kafkaMessage.isPresent()) {
                Object message = record.value();
                String topic = record.topic();
                log.info("=> 订单支付同步ES -> topic:{},message:{}", topic, message);
                OrderPayDto orderPayDto = JSONObject.parseObject(message.toString(), OrderPayDto.class);
                if (Objects.nonNull(orderPayDto)) {
                    ordersDocumentService.updateOrderPayByOrderNo(orderPayDto, 0);
                }
            }
        }
    }

    @KafkaListener(topics = {MQTopicConstant.SYNC_PACKAGE_STATUS_TO_ES_TOPIC})
    public void listenPackageStatus(List<ConsumerRecord<?, ?>> records) {
        for (ConsumerRecord<?, ?> record : records) {
            Optional<?> kafkaMessage = Optional.ofNullable(record.value());
            log.info("Received: " + record);
            if (kafkaMessage.isPresent()) {
                Object message = record.value();
                String topic = record.topic();
                log.info("=> 包裹状态同步ES -> topic:{},message:{}", topic, message);
                List<OrderStatusMQDto> orderStatusMQDtoList = JSONObject.parseArray(message.toString(), OrderStatusMQDto.class);
                if (Objects.nonNull(orderStatusMQDtoList)) {
                    ordersDocumentService.updateOrderStatusByPackageNo(orderStatusMQDtoList);
                }
            }
        }
    }

    /**
     * 上传ERP状态同步
     *
     * @param records
     */
    @KafkaListener(topics = {MQTopicConstant.ORDER_UPLOAD_STATUS_TO_BE_DELIVERED_TOPIC})
    public void listenToBeDeliveredStatus(List<ConsumerRecord<?, ?>> records) {
        for (ConsumerRecord<?, ?> record : records) {
            Optional<?> kafkaMessage = Optional.ofNullable(record.value());
            log.info("Received: " + record);
            if (kafkaMessage.isPresent()) {
                Object message = record.value();
                String topic = record.topic();
                log.info("=> 上传ERP状态同步ES -> topic:{},message:{}", topic, message);
                List<String> uploadPackageNoList = JSONObject.parseArray(message.toString(), String.class);
                if (Objects.nonNull(uploadPackageNoList) && uploadPackageNoList.size() > 0) {
                    ordersDocumentService.updateUploadErpStatusByPackageNo(uploadPackageNoList);
                }
            }
        }
    }

}
