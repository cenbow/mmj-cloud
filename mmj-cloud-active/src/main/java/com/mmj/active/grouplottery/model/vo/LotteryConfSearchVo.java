package com.mmj.active.grouplottery.model.vo;

import com.mmj.active.grouplottery.model.LotteryConf;
import lombok.Data;

@Data
public class LotteryConfSearchVo extends LotteryConf {

    private static final long serialVersionUID = -2145656220100531746L;

    private String goodsName;

    private String lotteryActivityName;

    private Integer isOpen;

    private String maxOpentime;

    private String minOpentime;

}
