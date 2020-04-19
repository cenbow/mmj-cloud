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
 * 客服消息文字关键字回复
 * </p>
 *
 * @author shenfuding
 * @since 2019-07-29
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_wx_custom_msg_txt")
@ApiModel(value="WxCustomMsgTxt对象", description="客服消息文字关键字回复")
public class WxCustomMsgTxt extends BaseModel {

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

    @ApiModelProperty(value = "规则名称")
    @TableField("RULE_NAME")
    private String ruleName;

    @ApiModelProperty(value = "关键字")
    @TableField("KEY_WORD")
    private String keyWord;

    @ApiModelProperty(value = "匹配规则(半匹配half;全匹配:full)")
    @TableField("MATCH_RULE")
    private String matchRule;

    @ApiModelProperty(value = "回复的消息类型(图文消息:news;文字消息:text;图片消息:image;小程序卡片:miniprogrampage)")
    @TableField("REPLY_TYPE")
    private String replyType;

    @ApiModelProperty(value = "回复的图片")
    @TableField("REPLY_IMG")
    private String replyImg;

    @ApiModelProperty(value = "回复的内容")
    @TableField("REPLY_CONTENT")
    private String replyContent;

    @TableField("CREATE_TIME")
    private Date createTime;


}
