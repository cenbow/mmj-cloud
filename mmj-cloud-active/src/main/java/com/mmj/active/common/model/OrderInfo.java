package com.mmj.active.common.model;

import lombok.Data;

import java.io.Serializable;
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


    private static final long serialVersionUID = -4246601864367227501L;

    private Long orderId;

    private String orderNo;

    private Integer orderType;

    private Integer businessId;

    private Integer orderStatus;

    private Integer orderAmount;

    private Integer goodAmount;

    private Integer discountAmount;

    private Integer couponAmount;

    private Integer expressAmount;

    private Date expirtTime;

    private String orderSource;

    private String consumerDesc;

    private Integer goldPrice;

    private Boolean memberOrder;

    private Boolean hasAfterSale;

    private Integer goldNum;

    private Integer delFlag;

    private String passingData;

    private Long createrId;

    private Date createrTime;

    private Date modifyTime;

}
