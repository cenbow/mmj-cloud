package com.mmj.active.cut.model;

import com.mmj.common.model.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 用户砍价表
 * </p>
 *
 * @author KK
 * @since 2019-06-15
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@ApiModel(value = "CutUser对象", description = "用户砍价表")
public class CutUser extends BaseModel {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "记录ID")
    private Integer logId;

    @ApiModelProperty(value = "发起砍价用户ID")
    private Long userId;

    @ApiModelProperty(value = "发起砍价ID")
    private Integer sponsorId;

    @ApiModelProperty(value = "砍价编码")
    private String cutNo;

    @ApiModelProperty(value = "砍价ID")
    private Integer cutId;

    @ApiModelProperty(value = "帮砍人ID")
    private Long cutMember;

    @ApiModelProperty(value = "帮砍时间")
    private Date cutTime;

    @ApiModelProperty(value = "帮砍金额")
    private BigDecimal cutAmount;

    @ApiModelProperty(value = "奖励金额")
    private BigDecimal rewardAmount;

    @ApiModelProperty(value = "剩余金额")
    private BigDecimal surplusAmount;

    @ApiModelProperty(value = "砍价状态 -1 己过期  0 正在进行 1 己完成")
    private Integer cutFlag;

    @ApiModelProperty(value = "发起时间")
    private Date startTime;


}
