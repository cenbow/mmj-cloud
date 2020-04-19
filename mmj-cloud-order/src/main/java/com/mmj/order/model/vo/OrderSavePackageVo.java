package com.mmj.order.model.vo;

import com.mmj.order.common.model.vo.CartOrderGoodsDetails;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 拆单中转实体类
 */
@Data
@EqualsAndHashCode()
@Accessors(chain = true)
public class OrderSavePackageVo {

    private OrderSaveVo orderSaveVo;

    private String orderNo;

    private Long id;

    private CartOrderGoodsDetails cartOrderGoodsDetails;


}
