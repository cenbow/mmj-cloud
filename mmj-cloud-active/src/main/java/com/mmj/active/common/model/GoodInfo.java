package com.mmj.active.common.model;

import com.mmj.common.model.BaseModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 商品表
 * </p>
 *
 * @author lyf
 * @since 2019-06-03
 */
@Data
public class GoodInfo extends BaseModel implements Serializable {


    @ApiModelProperty(value = "商品ID")
    private Integer goodId;

    @ApiModelProperty(value = "商品名称")
    private String goodName;

    @ApiModelProperty(value = "卖点")
    private String sellingPoint;

    @ApiModelProperty(value = "简称")
    private String shortName;

    @ApiModelProperty(value = "款式编码（SPU）")
    private String goodSpu;

    @ApiModelProperty(value = "商品分类")
    private String goodClass;

    @ApiModelProperty(value = "商品状态 -1：删除 0：暂不发布 1：立即上架 2：自动上架")
    private String goodStatus;

    @ApiModelProperty(value = "上架时间")
    private Date upTime;

    @ApiModelProperty(value = "售买天数")
    private Integer saleDays;

    @ApiModelProperty(value = "是否虚拟商品")
    private Integer virtualFlag;

    @ApiModelProperty(value = "是否会员商品")
    private Integer memberFlag;

    @ApiModelProperty(value = "是否组合商品")
    private Integer combinaFlag;

    @ApiModelProperty(value = "是否删除")
    private Integer delFlag;

    @ApiModelProperty(value = "是否自动展示")
    private Integer autoShow;

    @ApiModelProperty(value = "创建人")
    private Long createrId;

    @ApiModelProperty(value = "创建时间")
    private Date createrTime;

    @ApiModelProperty(value = "修改人")
    private Long modifyId;

    @ApiModelProperty(value = "修改时间")
    private Date modifyTime;


}
