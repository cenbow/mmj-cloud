package com.mmj.common.model.active;

import lombok.Data;

/**
 * @description: 活动价格验证
 * @auther: KK
 * @date: 2019/9/21
 */
@Data
public class ActiveGoodStoreResult {
    private boolean resultStatus;
    private double discountAmount = 0;

    public ActiveGoodStoreResult() {
    }

    public ActiveGoodStoreResult(boolean resultStatus, double discountAmount) {
        this.resultStatus = resultStatus;
        this.discountAmount = discountAmount;
    }

    public ActiveGoodStoreResult(boolean resultStatus) {
        this(resultStatus, 0);
    }
}
