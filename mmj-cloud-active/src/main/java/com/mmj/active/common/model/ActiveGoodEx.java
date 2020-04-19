package com.mmj.active.common.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class ActiveGoodEx extends ActiveGood {

    private static final long serialVersionUID = -1464486679601490304L;
    /**
     * 原始库存
     */
    private Integer activeStoreOld;

    /**
     * 免费送已经活动红包的人数
     */
    private Integer gotNum;

    private List<Integer> goodIds;

    private List<Integer> noGoodIds;

    private List<Integer> saleIds;

    private List<String> goodClasses;

    private List<Integer> activeTypes;

    private String orderSql;

    private Integer oldGoodId;
}
