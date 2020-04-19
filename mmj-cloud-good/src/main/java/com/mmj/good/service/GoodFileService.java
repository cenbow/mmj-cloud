package com.mmj.good.service;

import com.mmj.good.model.GoodFile;
import com.baomidou.mybatisplus.service.IService;

import java.util.List;

/**
 * <p>
 * 商品附件表 服务类
 * </p>
 *
 * @author lyf
 * @since 2019-06-03
 */
public interface GoodFileService extends IService<GoodFile> {
    /**
     * 删除商品附件
     * @param goodId
     * @param fileTypes
     */
    Integer delByGoodId(Integer goodId, Integer activeType, List<String> fileTypes);

    List<GoodFile> queryByGoodId(Integer goodId, Integer activeType, List<String> fileTypes);

}
