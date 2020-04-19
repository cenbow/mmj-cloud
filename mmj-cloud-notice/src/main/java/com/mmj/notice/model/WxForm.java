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
 * 微信模板消息
 * </p>
 *
 * @author shenfuding
 * @since 2019-07-19
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_wx_form")
@ApiModel(value="WxForm对象", description="微信模板消息")
public class WxForm extends BaseModel {

    private static final long serialVersionUID = 1L;

    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "小程序appid")
    @TableField("APPID")
    private String appid;

    @ApiModelProperty(value = "用户openid")
    @TableField("OPENID")
    private String openid;

    @ApiModelProperty(value = "formid(用于发模板消息)")
    @TableField("FORM_ID")
    private String formId;

    @ApiModelProperty(value = "剩余次数")
    @TableField("REMAIN_NUMBER")
    private Integer remainNumber;

    @TableField("CREATE_TIME")
    private Date createTime;


}
