
package com.mmj.common.model;

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
 * 用户表
 * </p>
 *
 * @author lyf
 * @since 2019-06-06
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_base_user")
@ApiModel(value="BaseUser对象", description="用户表")
public class BaseUser extends BaseModel {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "用户ID")
    @TableId(value = "USER_ID",type=IdType.INPUT)
    private Long userId;
    
    @ApiModelProperty(value = "微信openId")
    @TableField("OPEN_ID")
    private String openId;

    @ApiModelProperty(value = "用户全称")
    @TableField("USER_FULL_NAME")
    private String userFullName;

    @ApiModelProperty(value = "用户状态")
    @TableField("USER_STATUS")
    private Integer userStatus;

    @ApiModelProperty(value = "用户性别")
    @TableField("USER_SEX")
    private Integer userSex;

    @ApiModelProperty(value = "头像URL")
    @TableField("IMAGES_URL")
    private String imagesUrl;

    @ApiModelProperty(value = "盐")
    @TableField("USER_SALT")
    private String userSalt;

    @ApiModelProperty(value = "密码")
    @TableField("USER_PASSWORD")
    private String userPassword;

    @ApiModelProperty(value = "用户渠道")
    @TableField("USER_CHANNEL")
    private String userChannel;

    @ApiModelProperty(value = "客户端来源")
    @TableField("USER_SOURCE")
    private String userSource;

    @ApiModelProperty(value = "国家")
    @TableField("USER_COUNTRY")
    private String userCountry;

    @ApiModelProperty(value = "省")
    @TableField("USER_PROVINCE")
    private String userProvince;

    @ApiModelProperty(value = "市")
    @TableField("USER_CITY")
    private String userCity;

    @ApiModelProperty(value = "区")
    @TableField("USER_AREA")
    private String userArea;

    @ApiModelProperty(value = "微信ID")
    @TableField("UNION_ID")
    private String unionId;

    @ApiModelProperty(value = "电话号码")
    @TableField("USER_MOBILE")
    private String userMobile;

    @ApiModelProperty(value = "关注时间")
    @TableField("ATTENTION_TIME")
    private Date attentionTime;

    @ApiModelProperty(value = "用户来源")
    @TableField("USER_FROM")
    private String userFrom;
    
    @ApiModelProperty(value = "关注公众号的状态，0:未关注, 1:已关注, 2:已取消, 3:取消后再关注, 4:未授权已关注;")
    @TableField("SUBSCRIBE")
    private Integer subscribe;

    @ApiModelProperty(value = "关注公众号的时间")
    @TableField("SUBSCRIBE_TIME")
    private Date subscribeTime;

    @ApiModelProperty(value = "关注场景")
    @TableField("SUBSCRIBE_SCENE")
    private String subscribeScene;

    @ApiModelProperty(value = "关注渠道")
    @TableField("QR_SCENE_STR")
    private String qrSceneStr;

    @ApiModelProperty(value = "用户被打上的标签ID列表")
    @TableField("TAGID_LIST")
    private String tagidList;
    
    @ApiModelProperty(value = "标签名称")
    @TableField("TAG_NAME")
    private String tagName;

    @ApiModelProperty(value = "标签组")
    @TableField("GROUPID")
    private Integer groupid;

    @ApiModelProperty(value = "关注渠道")
    @TableField("QR_SCENE")
    private String qrScene;

    @ApiModelProperty(value = "创建人")
    @TableField("CREATER_ID")
    private Long createrId;

    @ApiModelProperty(value = "创建时间")
    @TableField("CREATER_TIME")
    private Date createrTime;

    @ApiModelProperty(value = "修改人")
    @TableField("MODIFY_ID")
    private Long modifyId;

    @ApiModelProperty(value = "修改时间")
    @TableField("MODIFY_TIME")
    private Date modifyTime;
    
    @TableField(exist=false)
    private String appId;


}
