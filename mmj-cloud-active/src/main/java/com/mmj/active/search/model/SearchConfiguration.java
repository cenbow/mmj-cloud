package com.mmj.active.search.model;

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
 * 商品搜索配置表
 * </p>
 *
 * @author shenfuding
 * @since 2019-07-26
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_search_configuration")
@ApiModel(value="SearchConfiguration对象", description="商品搜索配置表")
public class SearchConfiguration extends BaseModel {

    private static final long serialVersionUID = 1L;

    @TableId(value = "ID", type = IdType.INPUT)
    private Long id;

    @ApiModelProperty(value = "是否有效(0:否 1:有效)")
    @TableField("ACTIVE")
    private Boolean active;

    @ApiModelProperty(value = "配置id")
    @TableField("CONFIGURATION_ID")
    private Long configurationId;

    @ApiModelProperty(value = "关键词")
    @TableField("KEYWORD")
    private String keyword;

    @ApiModelProperty(value = "商品id")
    @TableField("GOOD_ID")
    private Integer goodId;

    @ApiModelProperty(value = "商品名称")
    @TableField("GOOD_NAME")
    private String goodName;

    @ApiModelProperty(value = "默认标识(0:否 1：是)")
    @TableField("DEFAULT_FLAG")
    private Boolean defaultFlag;

    @ApiModelProperty(value = "开始时间")
    @TableField("BEGIN_DATE")
    private Date beginDate;

    @ApiModelProperty(value = "结束时间")
    @TableField("END_DATE")
    private Date endDate;

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

    @ApiModelProperty(value = "当前索引")
    @TableField("SEARCH_INDEX")
    private Integer searchIndex;


}
