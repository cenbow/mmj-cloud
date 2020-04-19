package com.mmj.active.common.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 参团信息表
 * </p>
 *
 * @author zhangyicao
 * @since 2019-06-05
 */
@Data
public class OrderGroupJoin implements Serializable {


    private static final long serialVersionUID = 4154743384555735459L;

    private Integer joinId;

    private Integer activeType;

    private Integer businessId;

    private String groupNo;

    private Integer groupMain;

    private String launchOrderNo;

    private Long launchUserId;

    private Long joinUserId;

    private String joinOrderNo;

    private Date joinTime;

    private String remark;

}
