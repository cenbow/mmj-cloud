package com.mmj.order.service;

import java.util.Map;

/**
 * 库存接口
 */
public interface GoodsStockService {

    /***
     *    设置库存
     * @param sku
     * @param num
     * @return
     */
    boolean setStock(String sku, int num);

    /**
     * 批量设置库存
     *
     * @param stockMap
     * @return
     */
    boolean setStock(Map<String, String> stockMap);


    /**
     * @Description: 得到库存
     * @author: KK
     * @date: 2018/10/24
     * @param: [sku]
     * @return: int
     */
    int getStock(String sku);


    /**
     * 判断是否还有num个库存
     *
     * @param sku
     * @param num
     * @return
     */
    boolean hasStock(String sku, int num);


    /**
     * 通过skuId查询库存
     *
     * @param skuId
     * @param num
     * @return
     */
    boolean hasSkuIdStock(Integer skuId, int num);

    /**
     * 减去库存
     *
     * @param orderNo
     * @param sku
     * @param num
     * @return
     */
    boolean decr(String orderNo, String sku, int num);

    /**
     * 通过skuId 减去库存
     *
     * @param orderNo
     * @param skuId
     * @param num
     * @return
     */
    boolean decrSkuId(String orderNo, Integer skuId, int num);

    /**
     * 加上库存
     *
     * @param orderNo
     * @param sku
     * @param num
     * @return
     */
    boolean addStock(String orderNo, String sku, int num);


    /**
     * 添加组合商品
     *
     * @param combineSku
     */
    void addCombineGoods(String combineSku);


    /**
     * 批量添加组合商品
     *
     * @param combineSku
     */
    void addCombineGoods(String... combineSku);


    /**
     * 判断是否组合商品
     *
     * @param combineSku
     * @return
     */
    Map<Object, Object> existCombineGoods(String combineSku);

    /**
     * 添加组合商品的关联sku
     *
     * @param sku
     * @param combineSku
     */
    void addCombineSingleGoods(String sku, String combineSku);

    /**
     * 批量添加组合商品的关联sku
     *
     * @param sku
     */
    void addCombineSingleGoods(String... sku);


    /**
     * 判断sku是否被组合商品关联
     *
     * @param sku
     * @return
     */
    boolean existSingleGoods(String sku);

    /**
     * 根据sku同步所属组合商品的库存
     *
     * @param sku
     * @return
     */
    boolean synCombineStockBySingle(String sku);

    /**
     * 同步组合商品库存
     * @param sku
     * @return
     */
    boolean synCombineStockBySku(String sku);


    boolean addStockSkuId(String orderNo,  Integer skuId, int num);



    /**
     * @Description: 添加2人团创建时记录
     * @author: KK
     * @date: 2018/11/1
     * @param: [sku, orderNo, num]
     * @return: boolean
     */
    boolean addUngrouped(String sku,String orderNo,int num);

    /**
     * @Description: 2人团拼团成功后删除记录
     * @author: KK
     * @date: 2018/11/1
     * @param: [sku, orderNo, num]
     * @return: boolean
     */
    boolean removeUngrouped(String sku,String orderNo,int num);

    /**
     * @Description: 2人团未成团购买数量
     * @author: KK
     * @date: 2018/11/1
     * @param: [sku]
     * @return: int
     */
    int getUngroupedNum(String sku);


}
