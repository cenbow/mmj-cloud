package com.mmj.notice.model;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import com.mmj.common.model.BaseModel;
import com.mmj.common.model.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * <p>
 * 物流箱领取红包记录
 * </p>
 *
 * @author shenfuding
 * @since 2019-08-13
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_wx_box_record")
@ApiModel(value="WxBoxRecord对象", description="物流箱领取红包记录")
public class WxBoxRecord extends BaseModel {

    private static final long serialVersionUID = 1L;

    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "用户openid")
    @TableField("OPENID")
    private String openid;

    @ApiModelProperty(value = "用户昵称")
    @TableField("NICK_NAME")
    private String nickName;

    @ApiModelProperty(value = "用户头像")
    @TableField("HEAD_IMG")
    private String headImg;

    @ApiModelProperty(value = "用户unionid")
    @TableField("UNIONID")
    private String unionid;

    @ApiModelProperty(value = "用户发送的红包码")
    @TableField("RED_CODE")
    private String redCode;

    @ApiModelProperty(value = "用户自己的专属红包码")
    @TableField("OPEN_CODE")
    private String openCode;

    @ApiModelProperty(value = "此次领取的金额")
    @TableField("AMOUNT")
    private Integer amount;

    @ApiModelProperty(value = "公众号appid")
    @TableField("APP_ID")
    private String appId;

    @ApiModelProperty(value = "创建时间")
    @TableField("CREATE_TIME")
    private Date createTime;


}
