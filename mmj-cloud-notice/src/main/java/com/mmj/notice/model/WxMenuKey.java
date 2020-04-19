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

/**
 * <p>
 * 公众号菜单栏事件配置
 * </p>
 *
 * @author shenfuding
 * @since 2019-07-26
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_wx_menu_key")
@ApiModel(value="WxMenuKey对象", description="公众号菜单栏事件配置")
public class WxMenuKey extends BaseModel {

    private static final long serialVersionUID = 1L;

    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "公众号微信号")
    @TableField("WX_NO")
    private String wxNo;

    @ApiModelProperty(value = "公众号appid")
    @TableField("APPID")
    private String appid;

    @ApiModelProperty(value = "公众号名称")
    @TableField("APP_NAME")
    private String appName;

    @ApiModelProperty(value = "菜单栏点击传递过来的关键字")
    @TableField("KEY_WORD")
    private String keyWord;

    @ApiModelProperty(value = "回复类型")
    @TableField("REPLY_TYPE")
    private String replyType;

    @ApiModelProperty(value = "回复的图片")
    @TableField("REPLY_IMG")
    private String replyImg;

    @ApiModelProperty(value = "实际回复的内容")
    @TableField("REPLY_CONTENT")
    private String replyContent;

    @ApiModelProperty(value = "关联的wx_menu表里面的id")
    @TableField("MENU_ID")
    private Integer menuId;


}
