package com.mmj.order.common.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 用户活动参与表
 * </p>
 *
 * @author lyf
 * @since 2019-06-06
 */
@Data
public class UserActive implements Serializable {


    private static final long serialVersionUID = -5688501841245385180L;

    private Integer checkId;

    private Integer activeType;

    private Integer businessId;

    private Long userId;

    private String lotteryCode;

    private Long orderId;

    private String orderNo;

    private Date createrTime;


}
