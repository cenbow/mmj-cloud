package com.mmj.active.grouplottery.model;

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
 * 用户关注抽奖信息表
 * </p>
 *
 * @author shenfuding
 * @since 2019-08-26
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_lottery_remind")
@ApiModel(value = "LotteryRemind对象", description = "用户关注抽奖信息表")
public class LotteryRemind extends BaseModel {

    private static final long serialVersionUID = -4703175784685864889L;
    @ApiModelProperty(value = "关注ID")
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "用户ID")
    @TableField("USER_ID")
    private Long userId;

    @ApiModelProperty(value = "微信openId")
    @TableField("OPEN_ID")
    private String openId;

    @ApiModelProperty(value = "开启:open,关闭:close")
    @TableField("STATUS_C")
    private String statusC;

    @ApiModelProperty(value = "开启:open,关闭:close")
    @TableField("STATUS_O")
    private String statusO;

    @ApiModelProperty(value = "创建时间")
    @TableField("CREATE_TIME")
    private Date createTime;


}
