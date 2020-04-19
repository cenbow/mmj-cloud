package com.mmj.good.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import com.mmj.good.model.*;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 商品表 服务类
 * </p>
 *
 * @author lyf
 * @since 2019-06-03
 */
public interface GoodInfoService extends IService<GoodInfo> {

    /**
     * 保存基本信息
     * @return
     */
    Integer saveInfo(GoodInfoBaseEx entityEx);

    /**
     * 保存商品详情
     * @param entityEx
     * @throws Exception
     */
    void saveDetailInfo(GoodInfoBaseEx entityEx) throws Exception;

    /**
     * 分页查询商品
     * @param entityEx
     * @return
     */
    Page<GoodInfoBaseQueryEx> queryList(GoodInfoBaseQueryEx entityEx);

    /**
     * 分页查询商品
     * @param entityEx
     * @return
     */
    Page<GoodInfoBaseQueryEx> queryBaseList(GoodInfoBaseQueryEx entityEx);

    /**
     * 批量修改状态
     * @param goodIds
     * @return
     */
    void onshelve(List<Integer> goodIds);

    /**
     * 批量验证商品编号是否存在
     * @param spuList
     * @return
     */
    Map<String,Object> batchVerifyGoodSpu(List<String> spuList);

    /**
     * 自定义查询条件
     * @param goodInfo
     * @return
     */
    List<GoodInfo> select(GoodInfo goodInfo);

    /**
     * 商品下架
     * @param goodIds
     */
    void unshelve(List<Integer> goodIds);

    /**
     * 自定义排序查询 (猜你喜欢，免邮热卖，商品排序)
     * @param entityEx
     * @return
     */
    Page<GoodInfoEx> queryOrderList(GoodInfoEx entityEx);

    /**
     * 自动上架
     */
    void autoOnshelve();

    /**
     * 查询商品的封面图片
     * @param goodId
     * @return
     */
    String queryGoodFile(Integer goodId);

    /**
     * 根据商品id查询商品分类
     * @param goodIds
     * @return
     */
    List queryGoodClasses(List<Integer> goodIds);

    /**
     * 查询销量前十商品
     * @return
     */
    List<Map<String, Object>> queryTopGood();

    /**
     * 商品搜索查询
     * @param param
     * @return
     */
    Page<GoodInfoEx> searchGoods(String param);

    /**
     * 查询商品库存
     * @param goodIds
     * @return
     */
    List<GoodNum> queryGoodNumTotal(List<Integer> goodIds);

    GoodInfo getById(Integer id);

    GoodInfo getBySku(String goodSku);

    List<GoodInfoProperty> loadGoodInfo(GoodInfoBaseQueryEx goodInfoBaseQueryEx);

    List<GoodSaleProperty> loadGoodSale(List<Integer> goodIds);

    List<GoodImageProperty> loadGoodImage(List<Integer> goodIds);
}
