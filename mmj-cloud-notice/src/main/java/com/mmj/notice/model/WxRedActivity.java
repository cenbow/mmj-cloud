package com.mmj.notice.model;

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
 * @author shenfuding
 * @since 2019-08-08
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_wx_red_activity")
@ApiModel(value="WxRedActivity对象", description="")
public class WxRedActivity extends BaseModel {

    private static final long serialVersionUID = 1L;

    @TableId(value = "ID", type = IdType.AUTO)
    private String id;

    @ApiModelProperty(value = "红包码")
    @TableField("RED_CODE")
    private String redCode;

    @ApiModelProperty(value = "红包码最小金额")
    @TableField("RED_MIN_MONEY")
    private Integer redMinMoney;

    @ApiModelProperty(value = "红包码最大金额")
    @TableField("RED_MAX_MONEY")
    private Integer redMaxMoney;

    @ApiModelProperty(value = "红包码限制领的次数")
    @TableField("LIMIT_TIMES")
    private Integer limitTimes;

    @ApiModelProperty(value = "创建时间")
    @TableField("CREATE_TIME")
    private Date createTime;


}
