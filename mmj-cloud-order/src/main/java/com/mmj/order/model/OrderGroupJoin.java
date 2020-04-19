package com.mmj.order.model;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import com.mmj.common.model.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

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
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_order_group_join")
@ApiModel(value="OrderGroupJoin对象", description="参团信息表")
public class OrderGroupJoin extends BaseModel {

    private static final long serialVersionUID = 4154743384555735459L;

    @ApiModelProperty(value = "参团ID")
    @TableId(value = "JOIN_ID", type = IdType.AUTO)
    private Integer joinId;

    @ApiModelProperty(value = "活动类型 1 抽奖 2 接力购 3 接力购抽奖 4十元三件 5 秒杀 ")
    @TableField("ACTIVE_TYPE")
    private Integer activeType;

    @ApiModelProperty(value = "活动ID")
    @TableField("BUSINESS_ID")
    private Integer businessId;

    @ApiModelProperty(value = "团号")
    @TableField("GROUP_NO")
    private String groupNo;

    @ApiModelProperty(value = "是否团主 1是 0否")
    @TableField("GROUP_MAIN")
    private Integer groupMain;

    @ApiModelProperty(value = "团主订单号")
    @TableField("LAUNCH_ORDER_NO")
    private String launchOrderNo;

    @ApiModelProperty(value = "团主用户ID")
    @TableField("LAUNCH_USER_ID")
    private Long launchUserId;

    @ApiModelProperty(value = "参团用户ID")
    @TableField("JOIN_USER_ID")
    private Long joinUserId;

    @ApiModelProperty(value = "参团订单号")
    @TableField("JOIN_ORDER_NO")
    private String joinOrderNo;

    @ApiModelProperty(value = "参团时间")
    @TableField("JOIN_TIME")
    private Date joinTime;

    @ApiModelProperty(value = "备注")
    @TableField("REMARK")
    private String remark;


}
