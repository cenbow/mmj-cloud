package com.mmj.common.model.order;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 包裹快递信息
 */
@Data
@EqualsAndHashCode()
@Accessors(chain = true)
public class OrderPackageLogDto {


    // 包裹编号
    private String packageNo;

    private String orderNo;

    private String LogisticsName; // 快递公司名称

    private String LogisticsNo;  // 快递单号

    private String sendTime;   // 发货时间

    private String checkTime;  // 收货时间

    private  String LogisticsCode; // 快递公司简称


}
