package com.mmj.good.mapper;

import com.baomidou.mybatisplus.plugins.Page;
import com.mmj.good.model.*;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 商品表 Mapper 接口
 * </p>
 *
 * @author lyf
 * @since 2019-06-03
 */
public interface GoodInfoMapper extends BaseMapper<GoodInfo> {

    /**
     * 分页查询商品基础信息
     * @param page
     * @param entityEx
     * @return
     */
    List<GoodInfoBaseQueryEx> queryList(Page<GoodInfoBaseQueryEx> page, GoodInfoBaseQueryEx entityEx);

    /**
     * 分页查询商品基础信息 简化
     * @param page
     * @param entityEx
     * @return
     */
    List<GoodInfoBaseQueryEx> queryBaseList(Page<GoodInfoBaseQueryEx> page, GoodInfoBaseQueryEx entityEx);

    /**
     * 自定义查询条件
     * @param goodInfo
     * @return
     */
    List<GoodInfo> select(GoodInfo goodInfo);

    /**
     * 自定义排序查询
     * @param page
     * @param entityEx
     * @return
     */
    List<GoodInfoEx> queryOrderList(Page<GoodInfoEx> page, GoodInfoEx entityEx);

    /**
     * 查询商品的封面图片
     * @return
     */
    String queryGoodFile(@Param("goodId") Integer goodId);

    /**
     * 根据商品id查询商品分类
     * @param goodIds
     * @return
     */
    List<String> queryGoodClasses(@Param("goodIds") List<Integer> goodIds);

    /**
     * 查询销量前十商品
     * @return
     */
    List<Map<String, Object>> queryTopGood();

    /**
     * 商品搜索查询
     * @param content
     * @return
     */
    List<GoodInfoEx> searchGoods(Page<GoodInfoEx> page, String content);

    /**
     * 查询商品库存
     * @param goodIds
     * @return
     */
    List<GoodNum> queryGoodNumTotal(@Param("goodIds") List<Integer> goodIds);

    List<GoodInfoProperty> loadGoodInfo(GoodInfoBaseQueryEx goodInfoBaseQueryEx);

    List<GoodSaleProperty> loadGoodSale(@Param("goodIds") List<Integer> goodIds);

    List<GoodImageProperty> loadGoodImage(@Param("goodIds") List<Integer> goodIds);
}
