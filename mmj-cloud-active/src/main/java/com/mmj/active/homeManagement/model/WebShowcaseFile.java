package com.mmj.active.homeManagement.model;


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

/**
 * <p>
 * 橱窗商品图片表
 * </p>
 *
 * @author dashu
 * @since 2019-06-09
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_web_showcase_file")
@ApiModel(value="WebShowcaseFile对象", description="橱窗商品图片表")
public class WebShowcaseFile extends BaseModel {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "附件ID")
    @TableId(value = "FILE_ID", type = IdType.AUTO)
    private Integer fileId;

    @ApiModelProperty(value = "附件名称")
    @TableField("FILE_NAME")
    private String fileName;

    @ApiModelProperty(value = "橱窗ID")
    @TableField("SHOWECASE_ID")
    private Integer showecaseId;

    @ApiModelProperty(value = "图片地址")
    @TableField("IMAGES_URL")
    private String imagesUrl;

    @ApiModelProperty(value = "图片链接地址")
    @TableField("HRAF_URL")
    private String hrafUrl;

    @ApiModelProperty(value = "排序")
    @TableField("ORDER_ID")
    private Integer orderId;


}
