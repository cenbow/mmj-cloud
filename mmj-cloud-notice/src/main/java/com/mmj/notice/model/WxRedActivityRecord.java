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
 * 
 * </p>
 *
 * @author shenfuding
 * @since 2019-08-09
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_wx_red_activity_record")
@ApiModel(value="WxRedActivityRecord对象", description="")
public class WxRedActivityRecord extends BaseModel {

    private static final long serialVersionUID = 1L;

    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "用户openid")
    @TableField("OPENID")
    private String openid;

    @ApiModelProperty(value = "用户unionid")
    @TableField("UNIONID")
    private String unionid;

    @ApiModelProperty(value = "用户领的金额")
    @TableField("RED_MONEY")
    private Integer redMoney;

    @ApiModelProperty(value = "用户昵称")
    @TableField("NICKNAME")
    private String nickname;

    @ApiModelProperty(value = "性别")
    @TableField("SEX")
    private Integer sex;

    @ApiModelProperty(value = "市")
    @TableField("CITY")
    private String city;

    @ApiModelProperty(value = "省")
    @TableField("PROVICE")
    private String provice;

    @ApiModelProperty(value = "领取红包码关联的红包信息")
    @TableField("RED_ACTIVITY_ID")
    private String redActivityId;

    @TableField("CREATE_TIME")
    private Date createTime;


}
