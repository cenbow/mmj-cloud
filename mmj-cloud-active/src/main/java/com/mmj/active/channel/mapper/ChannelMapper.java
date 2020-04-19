package com.mmj.active.channel.mapper;

import com.mmj.active.channel.model.Channel;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.mmj.active.channel.model.dto.ChannelDayDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 分销渠道统计表 Mapper 接口
 * </p>
 *
 * @author dashu
 * @since 2019-08-05
 */
public interface ChannelMapper extends BaseMapper<Channel> {

    List<ChannelDayDto> selectChannelBy15(@Param(value = "channelName")String channelName, @Param(value = "startTime")String startTime, @Param(value ="endTime" )String endTime);

    Integer selectScanSumCount(@Param(value = "channelName")String channelName);

    Integer selectpersonSumCount(@Param(value = "channelName")String channelName);

    Integer selectScanDaySumCount(@Param(value = "channelName")String channelName, @Param(value = "startTime")String startTime, @Param(value ="endTime" )String endTime);

    Integer selectPersonDaySumCount(@Param(value = "channelName")String channelName, @Param(value = "startTime")String startTime, @Param(value ="endTime" )String endTime);
}
