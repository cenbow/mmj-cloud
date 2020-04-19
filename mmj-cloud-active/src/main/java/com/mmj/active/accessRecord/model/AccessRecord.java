package com.mmj.active.accessRecord.model;

import com.baomidou.mybatisplus.annotations.TableField;
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
@TableName("t_access_record")
@ApiModel(value="AccessRecord对象", description="访问环境数据上报表")
public class AccessRecord extends BaseModel {

    private static final long serialVersionUID = 1L;

    @TableField("URL")
    private String url;

    @ApiModelProperty(value = "请求参数")
    @TableField("REQ")
    private String req;

    @ApiModelProperty(value = "响应参数")
    @TableField("RES")
    private String res;

    @ApiModelProperty(value = "响应时间")
    @TableField("REPONSE_TIME")
    private Integer reponseTime;

    @ApiModelProperty(value = "创建时间")
    @TableField("CREATE_TIME")
    private Date createTime;

    @ApiModelProperty(value = "请求类型(get/post)")
    @TableField("TYPE")
    private String type;

    @ApiModelProperty(value = "用户id")
    @TableField("USER_ID")
    private String userId;

    @ApiModelProperty(value = "网络类型(wifi/4g)")
    @TableField("INTERNET_TYPE")
    private String internetType;

    @ApiModelProperty(value = "状态码(200/404)")
    @TableField("STATE")
    private String state;


}
