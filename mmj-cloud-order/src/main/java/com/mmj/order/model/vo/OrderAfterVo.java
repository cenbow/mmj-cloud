package com.mmj.order.model.vo;

import lombok.Data;

@Data
public class OrderAfterVo {
    private  String orderNo;


    private  String userId;

    @Override
    public String toString() {
        return "OrderAfterVo{" +
                "orderNo='" + orderNo + '\'' +
                ", userId='" + userId + '\'' +
                '}';
    }
}
