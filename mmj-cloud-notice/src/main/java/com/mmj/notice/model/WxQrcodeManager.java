package com.mmj.notice.model;

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
 * 公众号二维码
 * </p>
 *
 * @author shenfuding
 * @since 2019-08-15
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_wx_qrcode_manager")
@ApiModel(value="WxQrcodeManager对象", description="公众号二维码")
public class WxQrcodeManager extends BaseModel {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键id")
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "二维码名称")
    @TableField("QRCODE_NAME")
    private String qrcodeName;

    @ApiModelProperty(value = "公众号名称")
    @TableField("APP_NAME")
    private String appName;

    @ApiModelProperty(value = "公众号id")
    @TableField("APP_ID")
    private String appId;

    @ApiModelProperty(value = "标签名称,多个以逗号分隔")
    @TableField("USER_TAG_NAMES")
    private String userTagNames;

    @ApiModelProperty(value = "标签id，多个已逗号分隔")
    @TableField("USER_TAG_IDS")
    private String userTagIds;

    @ApiModelProperty(value = "渠道id")
    @TableField("CHANNEL_ID")
    private String channelId;

    @TableField("CHANNEL_NAME")
    private String channelName;

    @ApiModelProperty(value = "扫码人数")
    @TableField("PERSON_COUNT")
    private Integer personCount;

    @ApiModelProperty(value = "二维码图片地址")
    @TableField("PATH")
    private String path;

    @ApiModelProperty(value = "回复1的类型")
    @TableField("REPLY_ONE_TYPE")
    private String replyOneType;

    @ApiModelProperty(value = "回复1的内容")
    @TableField("REPLY_ONE_CONTENT")
    private String replyOneContent;

    @ApiModelProperty(value = "回复1前端存的数据")
    @TableField("REPLY_ONE_DATA")
    private String replyOneData;

    @ApiModelProperty(value = "回复1保存的图片")
    @TableField("REPLY_ONE_IMG")
    private String replyOneImg;

    @ApiModelProperty(value = "回复二的类型")
    @TableField("REPLY_TWO_TYPE")
    private String replyTwoType;

    @ApiModelProperty(value = "回复二的内容")
    @TableField("REPLY_TWO_CONTENT")
    private String replyTwoContent;

    @ApiModelProperty(value = "回复二前端存的数据")
    @TableField("REPLY_TWO_DATA")
    private String replyTwoData;

    @ApiModelProperty(value = "回复二的图片")
    @TableField("REPLY_TWO_IMG")
    private String replyTwoImg;

    @ApiModelProperty(value = "回复3的类型")
    @TableField("REPLY_THRID_TYPE")
    private String replyThridType;

    @ApiModelProperty(value = "回复3的内容")
    @TableField("REPLY_THRID_CONTENT")
    private String replyThridContent;

    @ApiModelProperty(value = "回复3前端存储的数据")
    @TableField("REPLY_THRID_DATA")
    private String replyThridData;

    @ApiModelProperty(value = "回复3的图片")
    @TableField("REPLY_THRID_IMG")
    private String replyThridImg;

    @ApiModelProperty(value = "创建时间")
    @TableField("CREATE_TIME")
    private Date createTime;

    @ApiModelProperty(value = "创建人id")
    @TableField("CREATE_ID")
    private Long createId;

    @ApiModelProperty(value = "创建人昵称")
    @TableField("CREATE_NAME")
    private String createName;

    @ApiModelProperty(value = "更新人id")
    @TableField("UPDATE_ID")
    private Long updateId;

    @ApiModelProperty(value = "更新时间")
    @TableField("UPDATE_TIME")
    private Date updateTime;

    @ApiModelProperty(value = "更新人名称")
    @TableField("UPDATE_NAME")
    private String updateName;


}
