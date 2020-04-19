package com.mmj.active.common.service;

import com.baomidou.mybatisplus.service.IService;
import com.mmj.active.common.model.FocusInfo;
import com.mmj.common.model.UserMerge;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author shenfuding
 * @since 2019-08-31
 */
public interface FocusInfoService extends IService<FocusInfo> {

    void updateUserID(UserMerge userMerge);

}
