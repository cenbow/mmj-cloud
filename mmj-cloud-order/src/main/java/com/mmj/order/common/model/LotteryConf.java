package com.mmj.order.common.model;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 抽奖配置表
 * </p>
 *
 * @author cgf
 * @since 2019-06-05
 */
@Data
public class LotteryConf implements Serializable {

    private static final long serialVersionUID = 1821087064096915512L;

    private Integer lotteryId;

    private String lotteryName;

    private Date startTime;

    private Date endTime;

    private Date openTime;

    private String shardTitle;

    private String shardImage;

    private String maxEveryone;

    private Integer needOpneNum;

    private String lotteryCodeStart;

    private String lotteryCodeEnd;

    private Integer tzRondHb;

    private BigDecimal hbStart;

    private BigDecimal hbEnd;

    private Integer tuanBuildNum;

    private Integer openType;

    private Integer showFlag;

    private String lotteryRule;

    private String bannerUrl;

    private String bannerHraf;

    private Integer openFlag;

    private String checkCode;

    private String openDetail;

    private Long checkMan;

    private String orderNo;

    private String couponId;

    private Long createrId;

    private Date createrTime;
}
