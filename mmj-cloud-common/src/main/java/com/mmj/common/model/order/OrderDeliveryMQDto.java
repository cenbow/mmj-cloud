package com.mmj.common.model.order;

import lombok.Data;

import java.util.Date;

//订单发货信息
@Data
public class OrderDeliveryMQDto {

    private String orderNo;
    private Date deliveryDate;  //发货时间
    private String logisticsNo; //快递单号
    private String logisticsCompany;//物流公司
    private String logisticsSimpleName;//物流公司简称
    private String packageNo; //包裹号
}
