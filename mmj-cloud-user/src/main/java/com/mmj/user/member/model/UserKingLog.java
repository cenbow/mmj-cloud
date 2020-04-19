package com.mmj.user.member.model;

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
 * 买买金日志表
 * </p>
 *
 * @author zhangyicao
 * @since 2019-07-10
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_user_king_log")
@ApiModel(value="UserKingLog对象", description="买买金日志表")
public class UserKingLog extends BaseModel {

    private static final long serialVersionUID = 6296098415802143326L;
    @ApiModelProperty(value = "主键，日志ID")
    @TableId(value = "LOG_ID", type = IdType.AUTO)
    private Integer logId;

    @ApiModelProperty(value = "买买金变动后的实时总数")
    @TableField("KING_NUM")
    private Integer kingNum;

    @ApiModelProperty(value = "用户ID")
    @TableField("USER_ID")
    private Long userId;

    @ApiModelProperty(value = "变更内容，文字描述")
    @TableField("KING_CONTEXT")
    private String kingContext;

    @ApiModelProperty(value = "变更数量")
    @TableField("UPDATE_NUM")
    private Integer updateNum;

    @ApiModelProperty(value = "分享的类型：")
    @TableField("SHARE_TYPE")
    private String shareType;

    @ApiModelProperty(value = "商品ID")
    @TableField("GOOD_ID")
    private Integer goodId;

    @ApiModelProperty(value = "如果是订单使用，则对应订单号")
    @TableField("ORDER_NO")
    private String orderNo;

    @ApiModelProperty(value = "状态 0:未使用 1:已使用")
    @TableField("STATUS")
    private Integer status;

    @ApiModelProperty(value = "剩余数量:扣掉已使用的")
    @TableField("SURPLUS")
    private Integer surplus;

    @ApiModelProperty(value = "排序 主要用于扣减买买金 0:活动获得 1:订单获得 2:买送活动获得")
    @TableField("SORT")
    private Integer sort;

    @ApiModelProperty(value = "创建时间")
    @TableField("CREATE_TIME")
    private Date createTime;

    /**
     * 好友的ID
     */
    @TableField(exist = false)
    private Long friendUserId;
}
