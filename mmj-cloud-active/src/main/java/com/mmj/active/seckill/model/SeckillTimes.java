package com.mmj.active.seckill.model;

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
 * 秒杀期次表
 * </p>
 *
 * @author zhangyicao
 * @since 2019-06-14
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_seckill_times")
@ApiModel(value="SeckillTimes对象", description="秒杀期次表")
public class SeckillTimes extends BaseModel {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "秒杀期次ID")
    @TableId(value = "TIMES_ID", type = IdType.AUTO)
    private Integer timesId;

    @ApiModelProperty(value = "秒杀期次")
    @TableField("SECKILL_PRIOD")
    private Integer seckillPriod;

    @ApiModelProperty(value = "秒杀配置ID")
    @TableField("SECKILL_ID")
    private Integer seckillId;

    @ApiModelProperty(value = "开始时间")
    @TableField("START_TIME")
    private Date startTime;

    @ApiModelProperty(value = "结束时间")
    @TableField("END_TIME")
    private Date endTime;

    @ApiModelProperty(value = "是否当前期次")
    @TableField("IS_ACTIVE")
    private Integer isActive;


}
