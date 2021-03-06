package com.mmj.aftersale.common.model;

import lombok.Data;

/**
 * 会员降级入参
 */
@Data
public class DegradeVo {
    private String remark;

    private Long userId;

    @Override
    public String toString() {
        return "DegradeVo{" +
                "remark='" + remark + '\'' +
                ", userId=" + userId +
                '}';
    }
}
