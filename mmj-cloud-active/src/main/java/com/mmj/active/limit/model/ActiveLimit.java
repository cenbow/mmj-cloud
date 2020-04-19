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

import java.util.Date;

/**
 * <p>
 * 活动商品限购表
 * </p>
 *
 * @author shenfuding
 * @since 2019-08-29
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_active_limit")
@ApiModel(value="ActiveLimit对象", description="活动商品限购表")
public class ActiveLimit extends BaseModel {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "配置ID")
    @TableId(value = "LIMIT_ID", type = IdType.AUTO)
    private Integer limitId;

    @ApiModelProperty(value = "活动类型 1 抽奖 2 接力购 3 接力购抽奖 4十元三件 5 秒杀  6 优惠券 7 砍价 8 主题 9 猜你喜欢 10 免邮热卖 11 分类商品")
    @TableField("ACTIVE_TYPE")
    private String activeType;

    @ApiModelProperty(value = "限购数量")
    @TableField("LIMIT_NUM")
    private Integer limitNum;

    @ApiModelProperty(value = "限购数量")
    @TableField("LIMIT_GOOD")
    private Integer limitGood;

    @ApiModelProperty(value = "限购时段")
    @TableField("LIMIT_TIME_START")
    private Date limitTimeStart;

    @ApiModelProperty(value = "限购时段")
    @TableField("LIMIT_TIME_END")
    private Date limitTimeEnd;

    @ApiModelProperty(value = "是否限购(1 是; 0 否)")
    @TableField("STATUS")
    private Integer status;

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
