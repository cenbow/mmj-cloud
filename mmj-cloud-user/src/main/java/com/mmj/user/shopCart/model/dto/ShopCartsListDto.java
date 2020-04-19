package com.mmj.user.shopCart.model.dto;


import java.util.List;

/**
 * @Description: 购物车列表
 * @Auther: zhangyicao
 * @Date: 2019/06/11
 */
public class ShopCartsListDto {
    /**
     * 有效商品
     */
    private List<ShopCartsDto> normals;
    /**
     * 无效商品
     */
    private List<ShopCartsDto> invalids;

    /**
     * 总价
     */
    private double totalPrice;

    public ShopCartsListDto() {
    }

    public ShopCartsListDto(List<ShopCartsDto> normals, List<ShopCartsDto> invalids,double totalPrice) {
        this.normals = normals;
        this.invalids = invalids;
        this.totalPrice = totalPrice;
    }

    public List<ShopCartsDto> getNormals() {
        return normals;
    }

    public void setNormals(List<ShopCartsDto> normals) {
        this.normals = normals;
    }

    public List<ShopCartsDto> getInvalids() {
        return invalids;
    }

    public void setInvalids(List<ShopCartsDto> invalids) {
        this.invalids = invalids;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }
}
