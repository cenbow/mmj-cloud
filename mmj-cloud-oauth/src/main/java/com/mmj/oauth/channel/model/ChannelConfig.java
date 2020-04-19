package com.mmj.oauth.channel.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import com.mmj.common.model.BaseModel;

/**
 * <p>
 * 第三方渠道配置表
 * </p>
 *
 * @author shenfuding
 * @since 2019-07-01
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_channel_config")
@ApiModel(value="ChannelConfig对象", description="第三方渠道配置表")
public class ChannelConfig extends BaseModel {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键ID")
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "类别")
    @TableField("TYPE")
    private String type;

    @ApiModelProperty(value = "第三方渠道的ID")
    @TableField("ADVERTISER_ID")
    private String advertiserId;

    @ApiModelProperty(value = "第三方渠道回调的URL")
    @TableField("URL")
    private String url;


}
