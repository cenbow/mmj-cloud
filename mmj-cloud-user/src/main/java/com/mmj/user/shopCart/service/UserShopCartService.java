package com.mmj.user.shopCart.service;


import com.baomidou.mybatisplus.service.IService;
import com.mmj.user.shopCart.model.UserShopCart;
import com.mmj.user.shopCart.model.dto.ShopCartsListDto;
import com.mmj.user.shopCart.model.vo.*;

/**
 * <p>
 * 购物车表 服务类
 * </p>
 *
 * @author zhangyicao
 * @since 2019-06-03
 */
public interface UserShopCartService extends IService<UserShopCart> {

    /**
     * 购物车列表
     * @param userid
     * @return
     */
    ShopCartsListDto carts();

    /**
     * 加入购物车
     * @param addVo
     */
    void addCarts(ShopCartsAddVo addVo);


    /**
     * 编辑购物车
     * @param editVo
     */
    void editCarts(ShopCartsEditVo editVo);

    /**
     * 删除购物车
     * @param removeVo
     */
    void removes(ShopCartsRemoveVo removeVo);

    /**
     * 选中商品
     * @param marksVo
     */
    void marks(ShopCartMarksVo marksVo);

    /**
     * 购物车下单前校验
     * @param cartsVaildVo
     */
    boolean valid(ShopCartsValidVo cartsVaildVo);
}
