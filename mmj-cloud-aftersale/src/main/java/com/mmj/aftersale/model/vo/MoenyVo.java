package com.mmj.aftersale.model.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class MoenyVo {

    @NotNull
    private String afterSaleNo;

    @NotNull
    private String createrId;
}
