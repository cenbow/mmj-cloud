package com.mmj.active.seckill.model;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
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
 * 秒杀信息表
 * </p>
 *
 * @author zhangyicao
 * @since 2019-06-13
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_seckill_info")
@ApiModel(value="SeckillInfo对象", description="秒杀信息表")
public class SeckillInfo extends BaseModel {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "秒杀活动ID")
    @TableId(value = "SECKILL_ID", type = IdType.AUTO)
    private Integer seckillId;

    @ApiModelProperty(value = "秒杀类型 1站内秒杀 2独立秒杀")
    @TableField("SECKILL_TYPE")
    private Integer seckillType;

    @ApiModelProperty(value = "活动名称")
    @TableField("SECKILL_NAME")
    private String seckillName;

    @ApiModelProperty(value = "横幅图片地址")
    @TableField("SECKILL_BANNER")
    private String seckillBanner;

    @ApiModelProperty(value = "横幅链接地址")
    @TableField("BANNER_HRAF")
    private String bannerHraf;

    @ApiModelProperty(value = "每天开始时间")
    @TableField("EVERY_START_TIME")
    private Date everyStartTime;

    @ApiModelProperty(value = "每天结束时间")
    @TableField("EVERY_END_TIME")
    private Date everyEndTime;

    @ApiModelProperty(value = "间隔时间")
    @TableField("INTERVAL_TIME")
    private Integer intervalTime;

    @ApiModelProperty(value = "持续天数")
    @TableField("CONTINUE_DAYS")
    private Integer continueDays;

    @ApiModelProperty(value = "每人每天限购数量")
    @TableField("EVERYONE_LIMIT")
    private Integer everyoneLimit;

    @ApiModelProperty(value = "创建人")
    @TableField("CREATER_ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long createrId;

    @ApiModelProperty(value = "创建时间")
    @TableField("CREATER_TIME")
    private Date createrTime;

    @ApiModelProperty(value = "修改人")
    @TableField("MODIFY_ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long modifyId;

    @ApiModelProperty(value = "修改时间")
    @TableField("MODIFY_TIME")
    private Date modifyTime;


}
