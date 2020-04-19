package com.mmj.statistics.channel.model;

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
 * 渠道数据统计配置表
 * </p>
 *
 * @author shenfuding
 * @since 2019-08-30
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_channel_statistics_config")
@ApiModel(value="ChannelStatisticsConfig对象", description="渠道数据统计配置表")
public class ChannelStatisticsConfig extends BaseModel {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键，自增")
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "配置类型，枚举值：ORDER - 配置的渠道无法查看订单数据; REDUCE - 配置的渠道进行数据衰减")
    @TableField("CONFIG_TYPE")
    private String configType;

    @ApiModelProperty(value = "渠道编码")
    @TableField("CHANNEL_CODE")
    private String channelCode;


}
