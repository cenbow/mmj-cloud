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
 * 订单团信息表
 * </p>
 *
 * @author zhangyicao
 * @since 2019-06-05
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_order_group")
@ApiModel(value="OrderGroup对象", description="订单团信息表")
public class OrderGroup extends BaseModel {

    private static final long serialVersionUID = -6336251602161217682L;

    @ApiModelProperty(value = "团ID")
    @TableId(value = "GROUP_ID", type = IdType.AUTO)
    private Integer groupId;

    @ApiModelProperty(value = "团号")
    @TableField("GROUP_NO")
    private String groupNo;

    @ApiModelProperty(value = "团类型 1二人团 2抽奖 3新人团 4秒杀")
    @TableField("GROUP_TYPE")
    private Integer groupType;

    @ApiModelProperty(value = "团状态 拼团状态 0进行中 1已完成 2已过期 3已取消 4已结束")
    @TableField("GROUP_STATUS")
    private Integer groupStatus;

    @ApiModelProperty(value = "关联ID （各种活动ID）")
    @TableField("BUSINESS_ID")
    private Integer businessId;

    @ApiModelProperty(value = "成团人数")
    @TableField("GROUP_PEOPLE")
    private Integer groupPeople;

    @ApiModelProperty(value = "当前人数")
    @TableField("CURRENT_PEOPLE")
    private Integer currentPeople;

    @ApiModelProperty(value = "团主订单号")
    @TableField("LAUNCH_ORDER_NO")
    private String launchOrderNo;

    @ApiModelProperty(value = "团主用户ID")
    @TableField("LAUNCH_USER_ID")
    private Long launchUserId;

    @ApiModelProperty(value = "过期时间")
    @TableField("EXPIRE_DATE")
    private Date expireDate;

    @ApiModelProperty(value = "传递参数")
    @TableField("PASSING_DATA")
    private String passingData;

    @ApiModelProperty(value = "团备注")
    @TableField("GROUP_DESC")
    private String groupDesc;

    @ApiModelProperty(value = "删除标志 0删除 1有效")
    @TableField("DELETE_FLAG")
    private Integer deleteFlag;

    @ApiModelProperty(value = "创建人")
    @TableField("CREATER_ID")
    private Long createrId;

    @ApiModelProperty(value = "创建时间")
    @TableField("CREATER_TIME")
    private Date createrTime;

    @ApiModelProperty(value = "修改人")
    @TableField("MODIFY_ID")
    private Long modifyId;

    @ApiModelProperty(value = "修改时间")
    @TableField("MODIFY_TIME")
    private Date modifyTime;

    public OrderGroup() {
    }

    public OrderGroup(String groupNo) {
        this.groupNo = groupNo;
    }
}
