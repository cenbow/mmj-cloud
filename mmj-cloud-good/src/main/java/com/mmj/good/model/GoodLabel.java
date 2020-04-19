package com.mmj.good.model;

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
 * 商品标签表
 * </p>
 *
 * @author lyf
 * @since 2019-06-03
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_good_label")
@ApiModel(value="GoodLabel对象", description="商品标签表")
public class GoodLabel extends BaseModel {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "标签ID")
    @TableId(value = "LABEL_ID", type = IdType.AUTO)
    private Integer labelId;

    @ApiModelProperty(value = "标签名称")
    @TableField("LABEL_NAME")
    private String labelName;

    @ApiModelProperty(value = "链接类型 0：无 1：专题 2：自定义链接")
    @TableField("HRAF_TYPE")
    private Integer hrafType;

    @ApiModelProperty(value = "标签状态")
    @TableField("LABEL_STATUS")
    private Integer labelStatus;

    @ApiModelProperty(value = "链接地址")
    @TableField("HREF_URL")
    private String hrefUrl;

    @ApiModelProperty(value = "创建人")
    @TableField("CREATER_ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long createrId;

    @ApiModelProperty(value = "创建时间")
    @TableField("CREATER_TIME")
    private Date createrTime;


}
