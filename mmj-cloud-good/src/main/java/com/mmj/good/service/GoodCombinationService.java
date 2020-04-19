package com.mmj.good.service;

import com.baomidou.mybatisplus.service.IService;
import com.mmj.good.model.GoodCombination;
import com.mmj.good.model.GoodCombinationExcel;

import java.util.List;

/**
 * <p>
 * 组合商品表 服务类
 * </p>
 *
 * @author zhangyicao
 * @since 2019-06-12
 */
public interface GoodCombinationService extends IService<GoodCombination> {

    void upload(List<GoodCombinationExcel> goodCombinationExcels);

    void initCombination(List<GoodCombination> goodCombinations);

    void synGoodsStockZh();

    void initCombinationNum(List<GoodCombination> goodCombinations);

    boolean isCombination(String goodSku);
}
