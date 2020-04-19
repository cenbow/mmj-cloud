package com.mmj.active.freeorder.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import com.mmj.active.common.model.ActiveGood;
import com.mmj.active.common.model.ActiveGoodEx;
import com.mmj.active.freeorder.model.FreeInfo;
import com.mmj.active.freeorder.model.vo.FreeOrderInfoVo;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 免费送活动表 服务类
 * </p>
 *
 * @author 陈光复
 * @since 2019-06-19
 */
public interface FreeInfoService extends IService<FreeInfo> {

    FreeOrderInfoVo info(String orderNo);

    List<Map<String, Object>> gotRedPackList();

    Map<String, Object> queryRedPack(String redCode, String unionId);

    void updateRedPack(Integer id);

    List<ActiveGoodEx> goodsList(Integer id);

    Page<ActiveGood> bossGoodsList(ActiveGood activeGood);

    void saveOrUpdate(List<ActiveGood> list);

    boolean deleteGood(List<Long> mapperId);

    boolean onOrOff(List<Long> mapperId, boolean bool);
}
