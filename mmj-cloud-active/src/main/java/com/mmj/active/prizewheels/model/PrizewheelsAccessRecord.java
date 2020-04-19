package com.mmj.active.prizewheels.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

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
 * 用户访问转盘活动的时间记录表
 * </p>
 *
 * @author shenfuding
 * @since 2019-06-27
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_prizewheels_access_record")
@ApiModel(value="PrizewheelsAccessRecord对象", description="用户访问转盘活动的时间记录表")
public class PrizewheelsAccessRecord extends BaseModel {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "用户ID")
    @TableId(value = "USER_ID", type = IdType.INPUT)
    private Long userId;

    @ApiModelProperty(value = "访问转盘活动的时间")
    @TableField("ACCESS_TIME")
    private Date accessTime;

    @ApiModelProperty(value = "访问次数")
    @TableField("ACCESS_COUNT")
    private Integer accessCount;


}
