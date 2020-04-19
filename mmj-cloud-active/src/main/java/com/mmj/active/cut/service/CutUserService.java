package com.mmj.active.cut.service;

import com.mmj.active.cut.model.dto.*;
import com.mmj.active.cut.model.vo.CutAssistVo;
import com.mmj.active.cut.model.vo.CutDetailsVo;
import com.mmj.active.cut.model.vo.CutUserVo;

import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 * 用户砍价表 服务类
 * </p>
 *
 * @author KK
 * @since 2019-06-10
 */
public interface CutUserService {
    /**
     * 发起砍价
     *
     * @param cutUserVo
     */
    CutUserDto bargain(CutUserVo cutUserVo);

    /**
     * 帮砍
     *
     * @param cutAssistVo
     */
    CutAssistDto assistBargain(CutAssistVo cutAssistVo);

    /**
     * 砍价详情
     *
     * @param cutDetailsVo
     * @return
     */
    CutDetailsDto details(CutDetailsVo cutDetailsVo);

    /**
     * 获取砍价地址
     *
     * @param cutDetailsVo
     * @return
     */
    CutAddressDto address(CutDetailsVo cutDetailsVo);

    /**
     * 我的砍价
     *
     * @return
     */
    List<MyCutListDto> myCutList();

    /**
     * 免费拿榜单(统计)
     *
     * @param sponsorUserId
     */
    void addCutFreeList(long sponsorUserId);

    /**
     * 免费拿榜单
     *
     * @return
     */
    List<CutFreeListDto> cutFreeList();

    /**
     * 砍价榜单
     *
     * @return
     */
    List<AssistCutListDto> assistCutList();

    /**
     * 人脉榜单
     *
     * @return
     */
    List<PeopleCutListDto> peopleCutList();

    /**
     * 获取发起砍价未砍金额
     *
     * @param cutId
     * @param cutNo
     * @return
     */
    BigDecimal getSurplusAmount(Integer cutId, String cutNo);

    /**
     * 下单获取下单价格和已砍金额
     *
     * @param cutId
     * @param cutNo
     * @return
     */
    CutOrderDto checkOrder(Integer cutId, String cutNo);

}
