package com.mmj.common.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import com.mmj.common.model.BaseModel;

/**
 * <p>
 * 微信信息表
 * </p>
 *
 * @author zhangyicao
 * @since 2019-06-17
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_wx_config")
@ApiModel(value="WxConfig对象", description="微信信息表")
public class WxConfig extends BaseModel {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键ID")
    @TableId(value = "CONFIG_ID", type = IdType.AUTO)
    private Integer configId;

    @ApiModelProperty(value = "公众号")
    @TableField("WX_NO")
    private String wxNo;

    @ApiModelProperty(value = "公众号名称")
    @TableField("WX_NAME")
    private String wxName;

    @ApiModelProperty(value = "APPID")
    @TableField("APP_ID")
    private String appId;

    @ApiModelProperty(value = "密钥")
    @TableField("SECRET")
    private String secret;

    @ApiModelProperty(value = "功能类型")
    @TableField("TYPE")
    private String type;


}
