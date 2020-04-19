package com.mmj.order.common.model.vo;

import com.mmj.common.model.BaseModel;
import lombok.Data;

import java.util.Date;

/**
 * <p>
 * 分享关联表
 * </p>
 *
 * @author zhangyicao
 * @since 2019-06-21
 */
@Data
public class UserShardVo extends BaseModel {

    private static final long serialVersionUID = 4866552346563596571L;

    private Integer shardId;

    private Long shardFrom;

    private String fromHeadImg;

    private String fromNiclName;

    private String fromShardOpenid;

    private Long shardTo;

    private String toHeadImg;

    private String toNickName;

    private String toShardOpenid;

    private String shardType;

    private String shardSource;

    private String orderNo;

    private String businessValue;

    private Integer orderStatus;

    private Integer orderAmount;

    private Date orderCreateTime;

    private Date orderEndTime;

    private Integer userFlag;

    private Date createrTime;

}
