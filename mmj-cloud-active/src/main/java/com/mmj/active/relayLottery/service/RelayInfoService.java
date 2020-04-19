package com.mmj.active.relayLottery.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import com.mmj.active.relayLottery.model.RelayInfo;
import com.mmj.active.relayLottery.model.dto.LotteryListDto;
import com.mmj.active.relayLottery.model.vo.RelayInfoVo;
import com.mmj.active.relayLottery.model.vo.RelayInfoVoList;
import com.mmj.common.model.ReturnData;

import java.util.List;

/**
 * <p>
 * 接力购抽奖表 服务类
 * </p>
 *
 * @author zhangyicao
 * @since 2019-06-04
 */
public interface RelayInfoService extends IService<RelayInfo> {

    public Object saveConfigure(RelayInfoVo relayInfoVo);

    Object onOff(String parame);

    Object del(int relayId);

    Page<RelayInfoVo> queryList(RelayInfoVo relayInfoVo);

    RelayInfoVo lotteryReleyInfo(long lotteryId);

    Integer queryLackNumber(Integer id,Integer relayNumber);

    Integer successNumber(Integer id);

    List<LotteryListDto.Member> queryHeadSculptureList(Integer actId);

    List<LotteryListDto> lotteryList(Long userId);

    List<LotteryListDto> myLotteryList(Long userId);

    Object queryLotteryResult(String parame);

    Object queryGroup(String parame);

    Object queryOpenLotteryInfo(String parame);

    int getJieligouCount();
}
