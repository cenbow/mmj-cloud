package com.mmj.active.common.model;

import lombok.Data;
import java.util.Date;

/**
 * <p>
 * 订单团信息表
 * </p>
 *
 * @author zhangyicao
 * @since 2019-06-05
 */
@Data
public class OrderGroup{

    private static final long serialVersionUID = -6336251602161217682L;

    private Integer groupId;

    private String groupNo;

    private Integer groupType;

    private Integer groupStatus;

    private Integer businessId;

    private Integer groupPeople;

    private Integer currentPeople;

    private String launchOrderNo;

    private Long launchUserId;

    private Date expireDate;

    private String groupDesc;

    private Integer deleteFlag;

    private Long createrId;

    private Date createrTime;

    private Long modifyId;

    private Date modifyTime;

}
