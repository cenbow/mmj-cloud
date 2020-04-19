package com.mmj.active.advertisement.model;

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
 * @author zhangyicao
 * @since 2019-07-24
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_advertisement_manage")
@ApiModel(value="AdvertisementMaage对象", description="")
public class AdvertisementManage extends BaseModel {

    private static final long serialVersionUID = 1L;

    @TableId(value = "ID", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "页面名称")
    @TableField("PAGE_NAME")
    private String pageName;

    @ApiModelProperty(value = "页面类型")
    @TableField("PAGE_TYPE")
    private String pageType;

    @ApiModelProperty(value = "页面地址")
    @TableField("URL")
    private String url;

    @ApiModelProperty(value = "图片")
    @TableField("IMG")
    private String img;

    @ApiModelProperty(value = "是否展示新用户")
    @TableField("IS_NEW_USER")
    private String isNewUser;

    @ApiModelProperty(value = "创建时间")
    @TableField("CREATE_TIME")
    private Date createTime;


}
