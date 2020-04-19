package com.mmj.active.freeorder.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class FreeOrderInfoVo implements Serializable {

    private static final long serialVersionUID = -6392199087365318566L;

    private String orderNo;

    private Long userId;

    private String unionId;

    private Integer goodsId;

    private Integer groupPeople;

    private Integer currentPeople;

    private String orderAmount;

    private Integer orderStatus;

    private Integer groupStatus;

    private String redCode;

    private Integer redStatus;

    private String redMoney;

    private String nickName;

    private List<FreeOrderRelationsVo> relationsVos;
}