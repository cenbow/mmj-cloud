package com.mmj.aftersale.common.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 订单信息表
 * </p>
 *
 * @author lyf
 * @since 2019-06-04
 */
@Data
public class OrderInfo implements Serializable {


    private static final long serialVersionUID = 9103605430819578694L;

    private Long orderId;

    private String orderNo;

    private String groupNumber;

    private String orderType;

    private Integer businessId;

    private Integer orderStatus;

    private BigDecimal orderAmount;

    private BigDecimal goodAmount;

    private BigDecimal discountAmount;

    private BigDecimal couponAmount;

    private BigDecimal expressAmount;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date expirtTime;

    private String orderSource;

    private Integer addrId;

    private String consumerDesc;

    private Integer delFlag;

    private Long createrId;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date createrTime;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date modifyTime;

}
