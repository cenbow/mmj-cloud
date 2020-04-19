package com.mmj.user.recommend.model;


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
 * 分享附件表
 * </p>
 *
 * @author dashu
 * @since 2019-06-18
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_user_recommend_file")
@ApiModel(value="UserRecommendFile对象", description="分享附件表")
public class UserRecommendFile extends BaseModel {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键，附件ID")
    @TableId(value = "FILE_ID", type = IdType.AUTO)
    private Integer fileId;

    @ApiModelProperty(value = "推荐ID")
    @TableField("RECOMMEND_ID")
    private Integer recommendId;

    @ApiModelProperty(value = "附件名称")
    @TableField("FILE_NAME")
    private String fileName;

    @ApiModelProperty(value = "附件URL")
    @TableField("FILE_URL")
    private String fileUrl;

    @ApiModelProperty(value = "排序码")
    @TableField("SORT_ORDER")
    private Integer sortOrder;

    @ApiModelProperty(value = "格式: 1:图片  2:视频")
    @TableField("FILE_FORMAT")
    private Integer fileFormat;

    @ApiModelProperty(value = "视频封面")
    @TableField("COVER_URL")
    private String coverUrl;

    @ApiModelProperty(value = "创建人的用户ID")
    @TableField("CREATER_ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long createrId;

    @ApiModelProperty(value = "创建时间")
    @TableField("CREATER_TIME")
    private Date createrTime;


}
