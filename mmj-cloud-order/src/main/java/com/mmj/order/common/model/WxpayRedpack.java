package com.mmj.order.common.model;

import com.mmj.common.model.BaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * <p>
 * 微信红包记录表
 * </p>
 *
 * @author shenfuding
 * @since 2019-07-17
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class WxpayRedpack extends BaseModel {

    private static final long serialVersionUID = 6526209146158135249L;

    private Integer id;

    private String mchBillno;

    private String mchId;

    private String wxappid;

    private String sendName;

    private String reOpenid;

    private Integer totalAmount;

    private Integer totalNum;

    private String wishing;

    private String clientIp;

    private String actName;

    private String remark;

    private Integer state;

    private String errorDesc;

    private Date createTime;

}
