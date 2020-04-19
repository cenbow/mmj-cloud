package com.mmj.order.model.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode()
@Accessors(chain = true)
public class PayResultDto {

    private String groupNo;
    private String goodsTitle;
    private Integer goodsId;
    private boolean isLauncher = false;
    //是否有成团
    private boolean groupStatus;

    public PayResultDto() {
    }

    public PayResultDto(String groupNo, boolean isLauncher, boolean groupStatus) {
        this.groupNo = groupNo;
        this.isLauncher = isLauncher;
        this.groupStatus = groupStatus;
    }
}
