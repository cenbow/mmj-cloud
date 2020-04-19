package com.mmj.active.common.service;

import com.baomidou.mybatisplus.service.IService;
import com.mmj.active.common.model.WatermarkConfigure;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author shenfuding
 * @since 2019-07-23
 */
public interface WatermarkConfigureService extends IService<WatermarkConfigure> {

    String createMark(String params);
}
