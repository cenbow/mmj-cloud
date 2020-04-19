package com.mmj.active.cut.model;

import java.math.BigDecimal;
import java.util.Date;
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
 * 砍价信息表
 * </p>
 *
 * @author KK
 * @since 2019-06-10
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_cut_info")
@ApiModel(value="CutInfo对象", description="砍价信息表")
public class CutInfo extends BaseModel {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "砍价ID")
    @TableId(value = "CUT_ID", type = IdType.AUTO)
    private Integer cutId;

    @ApiModelProperty(value = "活动名称")
    @TableField("CUT_NAME")
    private String cutName;

    @ApiModelProperty(value = "底价")
    @TableField("BASE_PRICE")
    private BigDecimal basePrice;

    @ApiModelProperty(value = "首砍设置 RATE 比例 MONEY 固定金额")
    @TableField("FRIST_CUT_TYPE")
    private String fristCutType;

    @ApiModelProperty(value = "首砍起始值")
    @TableField("FRIST_CUT_START")
    private Integer fristCutStart;

    @ApiModelProperty(value = "首砍最高值")
    @TableField("FRIST_CUT_END")
    private Integer fristCutEnd;

    @ApiModelProperty(value = "新用户可砍比例")
    @TableField("NEW_FRIST_RATE")
    private BigDecimal newFristRate;

    @ApiModelProperty(value = "新用户所需帮砍次数")
    @TableField("NEW_FRIST_TIMES")
    private Integer newFristTimes;

    @ApiModelProperty(value = "新用户可砍比例")
    @TableField("NEW_SECOND_RATE")
    private BigDecimal newSecondRate;

    @ApiModelProperty(value = "新用户所需帮砍次数")
    @TableField("NEW_SECOND_TIMES")
    private Integer newSecondTimes;

    @ApiModelProperty(value = "新用户可砍比例")
    @TableField("NEW_THIRD_RATE")
    private BigDecimal newThirdRate;

    @ApiModelProperty(value = "新用户所需帮砍次数")
    @TableField("NEW_THIRD_TIMES")
    private Integer newThirdTimes;

    @ApiModelProperty(value = "老用户可砍比例")
    @TableField("OLD_FRIST_RATE")
    private BigDecimal oldFristRate;

    @ApiModelProperty(value = "老用户所需帮砍次数")
    @TableField("OLD_FRIST_TIMES")
    private Integer oldFristTimes;

    @ApiModelProperty(value = "老用户可砍比例")
    @TableField("OLD_SECOND_RATE")
    private BigDecimal oldSecondRate;

    @ApiModelProperty(value = "老用户所需帮砍次数")
    @TableField("OLD_SECOND_TIMES")
    private Integer oldSecondTimes;

    @ApiModelProperty(value = "老用户可砍比例")
    @TableField("OLD_THIRD_RATE")
    private BigDecimal oldThirdRate;

    @ApiModelProperty(value = "老用户所需帮砍次数")
    @TableField("OLD_THIRD_TIMES")
    private Integer oldThirdTimes;

    @ApiModelProperty(value = "删除标志")
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
