package com.mmj.order.model.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.List;

@Data
@EqualsAndHashCode()
@Accessors(chain = true)
public class BossListVo {


    private Integer currentPage;

    private Integer pageSize;

    private String orderNo;

    private Integer orderStatus;

    private Integer orderType;

    private Integer virtualGood;

    private String consigneeName;

    private String consigneeTel;

    private String beginOrderDate;

    private String endOrderDate;

    private Long createrId;

    private List<Long> createIds;

    private String channel;

    private String source;
}
