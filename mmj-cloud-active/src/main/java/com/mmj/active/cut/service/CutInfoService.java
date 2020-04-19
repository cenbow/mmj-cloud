package com.mmj.active.cut.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.mmj.active.common.model.ActiveGood;
import com.mmj.active.common.model.GoodSale;
import com.mmj.active.common.model.dto.CutGoodDto;
import com.mmj.active.cut.model.CutInfo;
import com.baomidou.mybatisplus.service.IService;
import com.mmj.active.cut.model.dto.BossCutDto;
import com.mmj.active.cut.model.dto.BossCutEditDto;
import com.mmj.active.cut.model.dto.CutGoodListDto;
import com.mmj.active.cut.model.vo.BossCutAddVo;
import com.mmj.active.cut.model.vo.BossCutEditVo;
import com.mmj.active.cut.model.vo.BossCutQueryVo;

import java.util.List;

/**
 * <p>
 * 砍价信息表 服务类
 * </p>
 *
 * @author KK
 * @since 2019-06-10
 */
public interface CutInfoService extends IService<CutInfo> {

    /**
     * 获取砍价商品信息
     *
     * @param cutId
     * @return
     */
    List<ActiveGood> getActiveGood(Integer cutId);

    /**
     * 通过多个goodId查询SKU信息
     *
     * @param goodIds
     * @return
     */
    List<GoodSale> getGoodInfo(List<Integer> goodIds);

    /**
     * 新增免费拿
     *
     * @param addVo
     */
    BossCutEditDto add(BossCutAddVo addVo);

    /**
     * 编辑免费拿
     *
     * @param editVo
     */
    BossCutEditDto edit(BossCutEditVo editVo);

    /**
     * 查询
     *
     * @param queryVo
     * @return
     */
    Page<BossCutDto> query(BossCutQueryVo queryVo);

    /**
     * 砍价详情
     *
     * @param cutId
     * @return
     */
    BossCutDto queryByCutId(Integer cutId);

    /**
     * 删除砍价
     *
     * @param cutId
     */
    void deleteByCutId(Integer cutId);

    /**
     * 砍价商品列表
     *
     * @return
     */
    List<CutGoodListDto> goodList();
}
