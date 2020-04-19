package com.mmj.user.shopCart.mapper;


import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.mmj.user.shopCart.model.UserShopCart;

/**
 * <p>
 * 购物车表 Mapper 接口
 * </p>
 *
 * @author zhangyicao
 * @since 2019-06-03
 */
public interface UserShopCartMapper extends BaseMapper<UserShopCart> {

    public Integer sumByGoodsNum(Long createrId);
}
