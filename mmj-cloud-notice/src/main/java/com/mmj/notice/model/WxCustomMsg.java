package com.mmj.notice.model;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.mmj.common.model.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * <p>
 * 客服消息配置
 * </p>
 *
 * @author shenfuding
 * @since 2019-07-27
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_wx_custom_msg")
@ApiModel(value="WxCustomMsg对象", description="客服消息配置")
public class WxCustomMsg extends BaseModel {

    private static final long serialVersionUID = 1L;

    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "公众号对应的微信号")
    @TableField("WX_NO")
    private String wxNo;

    @ApiModelProperty(value = "公众号对应的appid")
    @TableField("APPID")
    private String appid;

    @ApiModelProperty(value = "公众号对应的名称")
    @TableField("APP_NAME")
    private String appName;

    @ApiModelProperty(value = "接受消息的类型(关注回复:subscribe;关注延迟回复:delay;主动推送:push;关键词回复:keyword;默认回复:default)")
    @TableField("ACCEPT_TYPE")
    private String acceptType;

    @ApiModelProperty(value = "回复类型")
    @TableField("REPLY_TYPE")
    private String replyType;

    @ApiModelProperty(value = "回复内容")
    @TableField("REPLY_CONTENT")
    private String replyContent;

    @ApiModelProperty(value = "回复的图片")
    @TableField("REPLY_IMG")
    private String replyImg;

    @ApiModelProperty(value = "关注后延迟推送的时间(单位是小时)")
    @TableField("REPLY_DELAY")
    private Integer replyDelay;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @ApiModelProperty(value = "主动推送的时间")
    @TableField("REPLY_TIME")
    private Date replyTime;

    @ApiModelProperty(value = "发送对象的标签id(多个以逗号分隔)")
    @TableField("TAG_ID")
    private String tagId;

    @ApiModelProperty(value = "发送对象的标签名字(多个以逗号分隔)")
    @TableField("TAG_NAME")
    private String tagName;

    @ApiModelProperty(value = "创建时间")
    @TableField("CREATE_TIME")
    private Date createTime;

    @ApiModelProperty(value = "创建人id")
    @TableField("CREATE_ID")
    private Long createId;

    @ApiModelProperty(value = "创建人昵称")
    @TableField("CREATE_NAME")
    private String createName;


}
