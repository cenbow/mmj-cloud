package com.mmj.order.model;

import lombok.Data;

import java.util.Date;

/**
 * @Description: 物流发货模块消息
 * @Auther: KK
 * @Date: 2018/12/11
 */

@Data
public class LogisticsShipDto {
    private Long userId;
    private String orderNo;
    private String lId;
    private String logisticsCompany;
    private Date sendTime;
    private String cozyMsg;//温馨提示

    public LogisticsShipDto() {
    }

    public LogisticsShipDto(Long userId, String orderNo, String lId, String logisticsCompany, Date sendTime, String cozyMsg) {
        this.userId = userId;
        this.orderNo = orderNo;
        this.lId = lId;
        this.logisticsCompany = logisticsCompany;
        this.sendTime = sendTime;
        this.cozyMsg = cozyMsg;
    }
}
