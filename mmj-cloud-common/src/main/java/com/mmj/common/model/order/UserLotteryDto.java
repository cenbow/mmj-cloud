package com.mmj.common.model.order;

import lombok.Data;

import java.io.Serializable;


@Data
public class UserLotteryDto implements Serializable {
    private static final long serialVersionUID = 8317478485776727409L;

    private Long userId;

    private String orderNo;
}
