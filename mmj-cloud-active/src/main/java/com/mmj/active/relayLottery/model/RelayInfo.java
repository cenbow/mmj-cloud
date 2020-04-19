package com.mmj.active.relayLottery.model;

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
 * 接力购抽奖表
 * </p>
 *
 * @author zhangyicao
 * @since 2019-06-05
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_relay_info")
@ApiModel(value="RelayInfo对象", description="接力购抽奖表")
public class RelayInfo extends BaseModel {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "活动ID")
    @TableId(value = "RELAY_ID", type = IdType.AUTO)
    private Integer relayId;

    @ApiModelProperty(value = "统一编码")
    @TableField("UNION_ID")
    private String unionId;

    @ApiModelProperty(value = "总期数")
    @TableField("PERIODS")
    private Integer periods;

    @ApiModelProperty(value = "当前期次")
    @TableField("PERIOD")
    private Integer period;

    @ApiModelProperty(value = "抽奖名称")
    @TableField("RELAY_NAME")
    private String relayName;

    @ApiModelProperty(value = "首次抽奖开始时间")
    @TableField("FRIST_START_TIME")
    private Date fristStartTime;

    @ApiModelProperty(value = "首次抽奖结束时间")
    @TableField("FRIST_END_TIME")
    private Date fristEndTime;

    @ApiModelProperty(value = "开始时间")
    @TableField("START_TIME")
    private Date startTime;

    @ApiModelProperty(value = "结束时间")
    @TableField("END_TIME")
    private Date endTime;

    @ApiModelProperty(value = "期数")
    @TableField("PRIODS")
    private Integer priods;

    @ApiModelProperty(value = "间隔时间")
    @TableField("INTERVAL_TIME")
    private Integer intervalTime;

    @ApiModelProperty(value = "活动时间")
    @TableField("ACTIVE_TIME")
    private Integer activeTime;

    @ApiModelProperty(value = "显示开奖人数")
    @TableField("SHOW_OPEN_NUM")
    private Integer showOpenNum;

    @ApiModelProperty(value = "虚拟人数")
    @TableField("VIRTUAL_NUM")
    private Integer virtualNum;

    @ApiModelProperty(value = "开奖人数")
    @TableField("OPEN_NUM")
    private Integer openNum;

    @ApiModelProperty(value = "接力人数")
    @TableField("RELAY_NUM")
    private Integer relayNum;

    @ApiModelProperty(value = "接力模式 1新带新 2 老带新")
    @TableField("RELAY_TYPE")
    private Integer relayType;

    @ApiModelProperty(value = "状态 -1 删除 0 关闭 1开启 2开启不显示 ")
    @TableField("RELAY_STATUS")
    private Integer relayStatus;

    @ApiModelProperty(value = "开奖时间")
    @TableField("OPEN_TIME")
    private Date openTime;

    @ApiModelProperty(value = "是否开奖")
    @TableField("OPEN_FLAG")
    private Integer openFlag;

    @ApiModelProperty(value = "中奖码")
    @TableField("CHECK_CODE")
    private String checkCode;

    @ApiModelProperty(value = "中奖人ID")
    @TableField("CHECK_MAN")
    private Long checkMan;

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
