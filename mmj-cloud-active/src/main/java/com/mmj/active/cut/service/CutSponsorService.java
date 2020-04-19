package com.mmj.active.cut.service;

import com.mmj.active.cut.model.CutSponsor;
import com.baomidou.mybatisplus.service.IService;

import java.util.List;

/**
 * <p>
 * 用户发起砍价表 服务类
 * </p>
 *
 * @author KK
 * @since 2019-07-26
 */
public interface CutSponsorService extends IService<CutSponsor> {
    /**
     * 获取用户发起砍价成功数
     *
     * @param userId
     * @return
     */
    int getSuccessCutNumberByUserId(Long userId);

    /**
     * 通过砍价号获取砍价信息
     *
     * @param cutNo
     * @return
     */
    CutSponsor getCutSponsorByCutNo(String cutNo);

    /**
     * 批量获取发起砍价信息
     *
     * @param sponsorIds
     * @return
     */
    List<CutSponsor> batchGetCutSponsor(List<Integer> sponsorIds);

    /**
     * 编辑砍价状态
     *
     * @param sponsorId
     * @param cutFlag
     */
    void editCutFlagBySponsorId(Integer sponsorId, Integer cutFlag);

    /**
     * 新增砍价订单
     *
     * @param cutNo
     * @param orderNo
     * @param orderStatus
     * @param userId
     */
    void addOrderInfoByCutNo(String cutNo, String orderNo, Integer orderStatus, Long userId);

    /**
     * 编辑砍价订单状态状态
     *
     * @param orderNo
     * @param orderStatus
     */
    void editOrderStatusByOrderNo(String orderNo, Integer orderStatus);

}
