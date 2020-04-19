package com.mmj.active.cut.service;

import com.baomidou.mybatisplus.service.IService;
import com.mmj.active.cut.model.CutBlackList;

/**
 * <p>
 * 砍价黑名单 服务类
 * </p>
 *
 * @author KK
 * @since 2019-09-24
 */
public interface CutBlackListService extends IService<CutBlackList> {
    int blackList(Long userId, String openId);
}
