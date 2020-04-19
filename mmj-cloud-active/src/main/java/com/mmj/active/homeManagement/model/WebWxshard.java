package com.mmj.active.homeManagement.model;

import java.util.Date;

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

/**
 * <p>
 * 小程序分享配置
 * </p>
 *
 * @author dashu
 * @since 2019-06-05
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_web_wxshard")
@ApiModel(value="WebWxshard对象", description="小程序分享配置")
public class WebWxshard extends BaseModel {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "分享ID")
    @TableId(value = "SHARD_ID", type = IdType.AUTO)
    private Integer shardId;

    @ApiModelProperty(value = "分类编码")
    @TableField("GOOD_CLASS")
    private String goodClass;

    @ApiModelProperty(value = "图片地址")
    @TableField("IMAGE_URL")
    private String imageUrl;

    @ApiModelProperty(value = "链接地址")
    @TableField("HRAF_URL")
    private String hrafUrl;

    @ApiModelProperty(value = "开始时间")
    @TableField("START_TIME")
    private Date startTime;

    @ApiModelProperty(value = "结束时间")
    @TableField("END_TIME")
    private Date endTime;

    @ApiModelProperty(value = "是否永久有效")
    @TableField("FOREVER_FLAG")
    private Integer foreverFlag;

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
