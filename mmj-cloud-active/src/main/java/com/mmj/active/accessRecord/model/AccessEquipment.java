package com.mmj.active.accessRecord.model;

import com.baomidou.mybatisplus.annotations.TableName;
import com.mmj.common.model.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * <p>
 * 访问环境数据上报表
 * </p>
 *
 * @author shenfuding
 * @since 2019-07-12
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_access_equipment")
@ApiModel(value="AccessEquipment对象", description="访问环境数据上报表")
public class AccessEquipment extends BaseModel {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "用户id")
    private String userId;

    @ApiModelProperty(value = "网络类型")
    private String internetType;

    @ApiModelProperty(value = "手机品牌")
    private String brand;

    @ApiModelProperty(value = "手机型号")
    private String model;

    @ApiModelProperty(value = "微信版本号")
    private String version;

    @ApiModelProperty(value = "操作系统版本号")
    private String system;

    @ApiModelProperty(value = "客户端基础库版本")
    private String sdkVersion;

    @ApiModelProperty(value = "可使用窗口高度")
    private String windowWidth;

    @ApiModelProperty(value = "状态栏的高度")
    private String statusBarHeight;

    @ApiModelProperty(value = "微信设置的语言")
    private String language;

    @ApiModelProperty(value = "屏幕宽度")
    private String screenWidth;

    @ApiModelProperty(value = "屏幕高度")
    private String screenHeight;

    @ApiModelProperty(value = "设备像素比")
    private String pixelRatio;

    @ApiModelProperty(value = "用户字体大小设置")
    private String fontSizeSetting;

    @ApiModelProperty(value = "创建时间")
    private Date createdTime;


}
