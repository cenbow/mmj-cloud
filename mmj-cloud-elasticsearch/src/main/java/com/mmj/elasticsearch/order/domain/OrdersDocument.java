package com.mmj.elasticsearch.order.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldIndex;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;
import java.util.List;

/**
 * @description: 订单
 * @auther: KK
 * @date: 2019/8/8
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@Document(indexName = "idx_orders", type = "orders_docs")
public class OrdersDocument implements Serializable {

    @Id
    @Field(type = FieldType.String, index = FieldIndex.not_analyzed)
    private String orderNo;

    @Field(type = FieldType.String, index = FieldIndex.not_analyzed)
    private String parentNo;

    @Field(type = FieldType.Long)
    private Long userId;

    @Field(type = FieldType.Integer)
    private Integer orderType;

    @Field(type = FieldType.Integer)
    private Integer orderStatus;

    @Field(type = FieldType.Long)
    private Long orderTime;

    @Field(type = FieldType.Long)
    private Long payTime;

    @Field(type = FieldType.Integer, index = FieldIndex.no)
    private Integer orderAmount;

    @Field(type = FieldType.Integer, index = FieldIndex.no)
    private Integer payAmount;

    @Field(type = FieldType.Integer)
    private Integer virtualGood;

    @Field(type = FieldType.Boolean)
    private Boolean uploadErp;

    private List<Goods> goods;

    @Field(type = FieldType.String, index = FieldIndex.not_analyzed)
    private String consigneeName;

    @Field(type = FieldType.String, index = FieldIndex.not_analyzed)
    private String consigneeTelNumber;

    @Field(type = FieldType.String, index = FieldIndex.not_analyzed)
    private String channel;

    @Field(type = FieldType.String, index = FieldIndex.not_analyzed)
    private String source;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @EqualsAndHashCode
    @ToString
    public static class Goods {
        @Field(type = FieldType.String)
        private String goodName;
        @Field(type = FieldType.String, index = FieldIndex.no)
        private String goodImage;
        @Field(type = FieldType.String)
        private String goodSku;
    }
}
