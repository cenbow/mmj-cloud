package com.mmj.active.limit.model;

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
 * 
 * </p>
 *
 * @author shenfuding
 * @since 2019-09-02
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_active_limit_detail")
@ApiModel(value="ActiveLimitDetail对象", description="")
public class ActiveLimitDetail extends BaseModel {

    private static final long serialVersionUID = 1L;

    @TableId(value = "DETAIL_ID", type = IdType.AUTO)
    private Integer detailId;

    @TableField("LIMIT_ID")
    private Integer limitId;

    @ApiModelProperty(value = "限购模式(1 每单限购; 2 每天限购; 3 每人限购)")
    @TableField("LIMIT_TYPE")
    private Integer limitType;

    @ApiModelProperty(value = "限购数量")
    @TableField("LIMIT_NUM")
    private Integer limitNum;


}
