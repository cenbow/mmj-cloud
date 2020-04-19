package com.mmj.active.search.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import com.mmj.active.common.model.ActiveGood;
import com.mmj.active.common.model.GoodInfoBaseQueryEx;
import com.mmj.active.common.model.GoodInfoEx;
import com.mmj.active.search.model.SearchConfiguration;
import com.mmj.active.search.model.SearchConfigurationEx;
import com.mmj.active.search.model.SearchVo;

import java.util.List;

/**
 * <p>
 * 商品搜索配置表 服务类
 * </p>
 *
 * @author shenfuding
 * @since 2019-07-26
 */
public interface SearchConfigurationService extends IService<SearchConfiguration> {

    Long configuration(SearchConfigurationEx searchConfigurationEx) throws Exception;

    Long getConfigurationId();

    SearchConfigurationEx getConfigurationList(Long configurationId);

    String updateConfiguration(String flag);

    void upSort(ActiveGood activeGood);

    void downSort(ActiveGood activeGood);

    Page<GoodInfoEx> getSearchResult(List<String> words, SearchVo searchVo);

    /**
     * job
     * 热销前十商品更新：每周3中午12点
     */
    void resetHotSellGoods();

}
