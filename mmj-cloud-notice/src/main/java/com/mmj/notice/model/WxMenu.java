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
 * 公众号菜单栏配置表
 * </p>
 *
 * @author shenfuding
 * @since 2019-07-26
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_wx_menu")
@ApiModel(value="WxMenu对象", description="公众号菜单栏配置表")
public class WxMenu extends BaseModel {

    private static final long serialVersionUID = 1L;

    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "公众号名称")
    @TableField("APP_NAME")
    private String appName;

    @ApiModelProperty(value = "公众号appid")
    @TableField("APPID")
    private String appid;

    @ApiModelProperty(value = "公众号菜单栏内容")
    @TableField("CONTENT")
    private String content;

    @ApiModelProperty(value = "创建时间")
    @TableField("CREATE_TIME")
    private Date createTime;

    @ApiModelProperty(value = "创建人名称")
    @TableField("CREATE_USER_NAME")
    private String createUserName;

    @ApiModelProperty(value = "创建人id")
    @TableField("CREATE_USER_ID")
    private Long createUserId;


}
