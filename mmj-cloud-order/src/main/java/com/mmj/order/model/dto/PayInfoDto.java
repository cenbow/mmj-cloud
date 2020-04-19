package com.mmj.order.model.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode()
@Accessors(chain = true)
public class PayInfoDto {

    private String sign;

    private String prepayId;

    private String nonceStr;

    private String timestamp;

    private String mwebUrl;

    private boolean isPaid = false;
}
