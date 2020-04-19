package com.mmj.active.group.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.mmj.active.group.model.dto.BossRelayGroupListDto;
import com.mmj.active.group.model.dto.BossTwoGroupListDto;
import com.mmj.active.group.model.vo.*;

import java.util.List;

public interface RelayGroupService {

    /**
     * 添加接力购商品
     *
     * @param relayGroupGoodsAddVos
     */
    void addGoods(List<BossRelayGroupGoodsAddVo> relayGroupGoodsAddVos);

    /**
     * 编辑接力购商品
     *
     * @param relayGroupGoodsEditVos
     */
    void editGoods(List<BossRelayGroupGoodsEditVo> relayGroupGoodsEditVos);

    /**
     * 编辑接力购商品状态
     *
     * @param bossRelayGroupGoodsEditVo
     */
    void editGoodsStatus(List<BossRelayGroupGoodsEditVo> bossRelayGroupGoodsEditVo);

    /**
     * 删除接力购商品
     *
     * @param bossTwoGroupGoodsVo
     */
    void removeGoods(BossTwoGroupGoodsVo bossTwoGroupGoodsVo);

    /**
     * 接力购商品
     *
     * @param bossTwoGroupListVo
     * @return
     */
    Page<BossRelayGroupListDto> relayGroupGoods(BossTwoGroupListVo bossTwoGroupListVo);
}
