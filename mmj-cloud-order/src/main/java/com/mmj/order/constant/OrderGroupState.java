package com.mmj.order.constant;

import lombok.Data;

@Data
public class OrderGroupState {

    private Integer status;

    private String statusDesc;

    public OrderGroupState() {
    }

    public OrderGroupState(Integer status, String statusDesc) {
        this.status = status;
        this.statusDesc = statusDesc;
    }
}
