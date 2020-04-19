package com.mmj.order.model.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;


/**
 * 确认收货
 */
@Data
@EqualsAndHashCode()
@Accessors(chain = true)
public class ReceiveOrderVo {



    private String userId;
    
    private String orderNo;

    private String packageNo;

    private String remark;


}
