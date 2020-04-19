package com.mmj.order.model.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;


/**
 *  订单详情之会员，返现，商品推荐
 */
@Data
@EqualsAndHashCode()
@Accessors(chain = true)
public class OrderDetaisMemberDto {



    //是否有推荐 0默认 1待评价 2已评价待分享
    private int hasRecommend = 0;

    //推荐id
    private Integer recommendId;

    /**
     * 买买金备注
     */
    private String kingRemarks;


    private String goldAmount;


}
