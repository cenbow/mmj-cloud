package com.mmj.active.common.constants;

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
 * 微信素材表
 * </p>
 *
 * @author shenfuding
 * @since 2019-07-22
 */
@Data
public class WxMedia extends BaseModel {

    private static final long serialVersionUID = 1L;

    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    @TableField("APPID")
    private String appid;

    @ApiModelProperty(value = "业务id")
    @TableField("BUSINESS_ID")
    private String businessId;

    @ApiModelProperty(value = "业务名称")
    @TableField("BUSINESS_NAME")
    private String businessName;

    @ApiModelProperty(value = "媒体id")
    @TableField("MEDIA_ID")
    private String mediaId;

    @ApiModelProperty(value = "媒体类型(forever:永久;temporary:临时)")
    @TableField("MEDIA_TYPE")
    private String mediaType;

    @ApiModelProperty(value = "素材原始路径")
    @TableField("MEDIA_URL")
    private String mediaUrl;

    @ApiModelProperty(value = "创建时间")
    @TableField("CREATE_TIME")
    private Date createTime;


}
