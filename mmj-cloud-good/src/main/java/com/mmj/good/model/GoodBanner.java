package com.mmj.good.model;

import java.util.Date;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.FieldStrategy;
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
 * 分类横幅表
 * </p>
 *
 * @author lyf
 * @since 2019-06-03
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_good_banner")
@ApiModel(value="GoodBanner对象", description="分类横幅表")
public class GoodBanner extends BaseModel {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "横幅ID")
    @TableId(value = "BANNER_ID", type = IdType.AUTO)
    private Integer bannerId;

    @ApiModelProperty(value = "横幅名称")
    @TableField("BANNER_NAME")
    private String bannerName;

    @ApiModelProperty(value = "分类编码")
    @TableField("CLASS_CODE")
    private String classCode;

    @ApiModelProperty(value = "大横幅图片地址")
    @TableField(value = "BIG_FILE_URL", strategy= FieldStrategy.IGNORED)
    private String bigFileUrl;

    @ApiModelProperty(value = "小横幅图片地址")
    @TableField("SMALL_FILE_URL")
    private String smallFileUrl;

    @ApiModelProperty(value = "大横幅链接地址")
    @TableField("BIG_HRAF_URL")
    private String bigHrafUrl;

    @ApiModelProperty(value = "小横幅链接地址")
    @TableField("SMALL_HRAF_URL")
    private String smallHrafUrl;

    @ApiModelProperty(value = "是否显示 1：显示 0：隐藏")
    @TableField("SHOW_FLAG")
    private Integer showFlag;

    @ApiModelProperty(value = "创建人")
    @TableField("CREATER_ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long createrId;

    @ApiModelProperty(value = "创建时间")
    @TableField("CREATER_TIME")
    private Date createrTime;


}
