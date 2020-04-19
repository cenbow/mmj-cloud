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
 * 二维码扫描用户记录
 * </p>
 *
 * @author shenfuding
 * @since 2019-08-15
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_wx_qrcode_record")
@ApiModel(value="WxQrcodeRecord对象", description="二维码扫描用户记录")
public class WxQrcodeRecord extends BaseModel {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键id")
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "对应的公众号id")
    @TableField("APPID")
    private String appid;

    @ApiModelProperty(value = "关联的id")
    @TableField("REF_ID")
    private Integer refId;

    @ApiModelProperty(value = "用户openid")
    @TableField("OPEN_ID")
    private String openId;

    @ApiModelProperty(value = "用户unionid")
    @TableField("UNION_ID")
    private String unionId;

    @ApiModelProperty(value = "用户昵称")
    @TableField("NICK_NAME")
    private String nickName;

    @ApiModelProperty(value = "创建时间")
    @TableField("CREATE_TIME")
    private Date createTime;


}
