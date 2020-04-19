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
 * 商品附件表
 * </p>
 *
 * @author lyf
 * @since 2019-06-03
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_good_file")
@ApiModel(value="GoodFile对象", description="商品附件表")
public class GoodFile extends BaseModel {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "文件ID")
    @TableId(value = "FILE_ID", type = IdType.AUTO)
    private Integer fileId;

    @ApiModelProperty(value = "组编码")
    @TableField("GROUP_CODE")
    private String groupCode;

    @ApiModelProperty(value = "商品ID")
    @TableField("GOOD_ID")
    private Integer goodId;

    @ApiModelProperty(value = "销售ID")
    @TableField("SALE_ID")
    private Integer saleId;

    @ApiModelProperty(value = "规格ID")
    @TableField("MODEL_ID")
    private Integer modelId;

    @ApiModelProperty(value = "文件服务商 ALIYUN TENGXUN")
    @TableField("FILE_SERVER")
    private String fileServer;

    @ApiModelProperty(value = "附件类型 SELLING_POINT：卖点 IMAGE：商品图片 MAINVIDEO：主视频 VIDEOTITLE：视频封面 WECHAT：小程序分享 H5：H5分享 DETAIL：详情 DETAILVIDEO 详情视频 DETAILTITLE：视频封面 SALEMODEL:规格图片 ACTIVE:活动图片")
    @TableField("FILE_TYPE")
    private String fileType;

    @ApiModelProperty(value = "文件路由")
    @TableField("FILE_URL")
    private String fileUrl;

    @ApiModelProperty(value = "是否封面")
    @TableField("TITLE_FLAG")
    private Integer titleFlag;

    @ApiModelProperty(value = "排序")
    @TableField("FILE_ORDER")
    private Integer fileOrder;

    @ApiModelProperty(value = "附件标签 逗号隔开")
    @TableField("FILE_LABEL")
    private String fileLabel;

    @ApiModelProperty(value = "创建人")
    @TableField("CREATER_ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long createrId;

    @ApiModelProperty(value = "创建时间")
    @TableField("CREATER_TIME")
    private Date createrTime;

    @ApiModelProperty(value = "活动类型 0 商品 1 抽奖 2 接力购 3 接力购抽奖 4十元三件 5 秒杀  6 优惠券 7 砍价")
    @TableField("ACTIVE_TYPE")
    private Integer activeType;

    @ApiModelProperty(value = "活动ID")
    @TableField("BUSINESS_ID")
    private Integer businessId;


}
