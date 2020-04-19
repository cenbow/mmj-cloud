package com.mmj.active.channel.model;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.mmj.common.model.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * <p>
 * 分销渠道统计表
 * </p>
 *
 * @author dashu
 * @since 2019-08-05
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_buy_bak_channel")
@ApiModel(value="Channel对象", description="分销渠道统计表")
public class Channel extends BaseModel {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键id")
    @TableField("CHANNEL_ID")
    private Integer channelId;

    @ApiModelProperty(value = "渠道名")
    @TableField("CHANNEL_NAME")
    private String channelName;

    @ApiModelProperty(value = "渠道链接")
    @TableField("CHANNEL_LINK")
    private String channelLink;

    @ApiModelProperty(value = "用户id")
    @TableField("USER_ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;

    @ApiModelProperty(value = "用户openid")
    @TableField("OPENID")
    private String openid;

    @ApiModelProperty(value = "用户unionid")
    @TableField("UNIONID")
    private String unionid;

    @ApiModelProperty(value = "创建时间")
    @TableField("CREATE_TIME")
    private Date createTime;


}
