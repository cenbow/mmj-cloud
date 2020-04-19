package com.mmj.user.common.model.vo;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode()
@Accessors(chain = true)
public class UserOrderVo {

    private String userId;

    private  String orderNo;

}
