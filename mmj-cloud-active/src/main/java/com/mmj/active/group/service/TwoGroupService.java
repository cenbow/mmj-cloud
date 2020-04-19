package com.mmj.active.group.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.mmj.active.group.model.dto.BossTwoGroupListDto;
import com.mmj.active.group.model.vo.BossTwoGroupGoodsAddVo;
import com.mmj.active.group.model.vo.BossTwoGroupGoodsEditVo;
import com.mmj.active.group.model.vo.BossTwoGroupGoodsVo;
import com.mmj.active.group.model.vo.BossTwoGroupListVo;

import java.util.List;

/**
 * @description: 二人团
 * @auther: KK
 * @date: 2019/7/22
 */
public interface TwoGroupService {
    /**
     * 添加二人团商品
     *
     * @param twoGroupGoodsAddVos
     */
    void addGoods(List<BossTwoGroupGoodsAddVo> twoGroupGoodsAddVos);

    /**
     * 编辑二人团商品
     *
     * @param twoGroupGoodsEditVos
     */
    void editGoods(List<BossTwoGroupGoodsEditVo> twoGroupGoodsEditVos);

    /**
     * 编辑二人团商品状态
     *
     * @param bossTwoGroupGoodsEditVos
     */
    void editGoodsStatus(List<BossTwoGroupGoodsEditVo> bossTwoGroupGoodsEditVos);

    /**
     * 删除二人团商品
     *
     * @param bossTwoGroupGoodsVo
     */
    void removeGoods(BossTwoGroupGoodsVo bossTwoGroupGoodsVo);

    /**
     * 二人团商品
     *
     * @param bossTwoGroupListVo
     * @return
     */
    Page<BossTwoGroupListDto> twoGroupGoods(BossTwoGroupListVo bossTwoGroupListVo);
}
