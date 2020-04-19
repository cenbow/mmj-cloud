package com.mmj.order.common.model;

import lombok.Data;

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
public class RelayInfo {

    private static final long serialVersionUID = 1L;

    //活动ID
    private Integer relayId;

    //统一编码
    private String unionId;

    //总期数
    private Integer periods;

    //当前期次
    private Integer period;

    //抽奖名称
    private String relayName;

    //首次抽奖开始时间
    private Date fristStartTime;

    //首次抽奖结束时间
    private Date fristEndTime;

    //开始时间
    private Date startTime;

    //结束时间
    private Date endTime;

    //期数
    private Integer priods;

    //间隔时间
    private Integer intervalTime;

    //活动时间
    private Integer activeTime;

    //显示开奖人数
    private Integer showOpenNum;

    //虚拟人数
    private Integer virtualNum;

    //开奖人数
    private Integer openNum;

    //接力人数
    private Integer relayNum;

    //接力模式 1新带新 2 老带新
    private Integer relayType;

    //状态 -1 删除 0 关闭 1开启 2开启不显示
    private Integer relayStatus;

    //开奖时间
    private Date openTime;

    //是否开奖
    private Integer openFlag;

    //中奖码
    private String checkCode;

    //中奖人ID
    private Long checkMan;

    //创建人
    private Long createrId;

    //创建时间
    private Date createrTime;

    //修改人
    private Long modifyId;

    //修改时间
    private Date modifyTime;

}
