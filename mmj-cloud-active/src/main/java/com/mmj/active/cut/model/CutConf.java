package com.mmj.active.cut.model;

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
 * 砍价公共配置表
 * </p>
 *
 * @author KK
 * @since 2019-06-10
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_cut_conf")
@ApiModel(value="CutConf对象", description="砍价公共配置表")
public class CutConf extends BaseModel {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "公共配置ID")
    @TableId(value = "CONF_ID", type = IdType.AUTO)
    private Integer confId;

    @ApiModelProperty(value = "榜单公众号")
    @TableField("WEIXN_NAME")
    private String weixnName;

    @ApiModelProperty(value = "活动规则")
    @TableField("RULE_CINTEXT")
    private String ruleCintext;


}
