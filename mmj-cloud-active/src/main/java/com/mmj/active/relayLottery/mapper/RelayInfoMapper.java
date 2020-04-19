package com.mmj.active.relayLottery.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.mmj.active.relayLottery.model.RelayInfo;
import com.mmj.active.relayLottery.model.vo.RelayInfoVo;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 接力购抽奖表 Mapper 接口
 * </p>
 *
 * @author zhangyicao
 * @since 2019-06-04
 */
public interface RelayInfoMapper extends BaseMapper<RelayInfo> {

    /**
     * 关联商品查询
     * @param relayInfoVo
     * @return
     */
    List<RelayInfoVo> queryRelayInfoList(Page<RelayInfoVo> page,RelayInfoVo relayInfoVo);
}
