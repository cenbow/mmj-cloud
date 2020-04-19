package com.mmj.oauth.channel.service.impl;

import com.mmj.oauth.channel.model.ChannelConfig;
import com.mmj.oauth.channel.mapper.ChannelConfigMapper;
import com.mmj.oauth.channel.service.ChannelConfigService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 第三方渠道配置表 服务实现类
 * </p>
 *
 * @author shenfuding
 * @since 2019-07-01
 */
@Service
public class ChannelConfigServiceImpl extends ServiceImpl<ChannelConfigMapper, ChannelConfig> implements ChannelConfigService {

}
