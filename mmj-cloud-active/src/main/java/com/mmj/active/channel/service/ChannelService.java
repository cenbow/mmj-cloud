package com.mmj.active.channel.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import com.mmj.active.channel.model.Channel;
import com.mmj.active.channel.model.ChannelEx;
import com.mmj.active.channel.model.vo.ChannelVo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 * 分销渠道统计表 服务类
 * </p>
 *
 * @author dashu
 * @since 2019-08-05
 */
public interface ChannelService extends IService<Channel> {

    Page<ChannelVo> query(ChannelEx channelEx);


    Object exportChannel(ChannelEx channelEx, HttpServletRequest request, HttpServletResponse response);
}
