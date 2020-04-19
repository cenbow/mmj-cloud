package com.mmj.aftersale.model;

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
 * 售后用户提交信息表
 * </p>
 *
 * @author zhangyicao
 * @since 2019-06-17
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_after_person")
@ApiModel(value="AfterPerson对象", description="售后用户提交信息表")
public class AfterPerson extends BaseModel {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键ID")
    @TableId(value = "AFTER_ID", type = IdType.AUTO)
    private Integer afterId;

    @ApiModelProperty(value = "售后单号")
    @TableField("AFTER_SALE_NO")
    private String afterSaleNo;

    @ApiModelProperty(value = "用户备注")
    @TableField("USER_REMARK")
    private String userRemark;

    @ApiModelProperty(value = "用户上传图片（逗号隔开）")
    @TableField("AFTER_IMAGE")
    private String afterImage;

    @ApiModelProperty(value = "仓库地址")
    @TableField("WAREHOUSE_ADDR")
    private String warehouseAddr;

    @ApiModelProperty(value = "仓库人")
    @TableField("WAREHOUSE_PERSON")
    private String warehousePerson;

    @ApiModelProperty(value = "仓库电话")
    @TableField("WAREHOUSE_MOBILE")
    private String warehouseMobile;

    @ApiModelProperty(value = "快递单号")
    @TableField("LOGISTICS_NO")
    private String logisticsNo;

    @ApiModelProperty(value = "快递公司编码")
    @TableField("LOGISTICS_CODE")
    private String logisticsCode;

    @ApiModelProperty(value = "快递公司名称")
    @TableField("LOGISTICS_NAME")
    private String logisticsName;

    @ApiModelProperty(value = "拒绝原因")
    @TableField("REJECT_REMARK")
    private String rejectRemark;

    @ApiModelProperty(value = "是否删除")
    @TableField("DEL_FLAG")
    private Integer delFlag;

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


}
