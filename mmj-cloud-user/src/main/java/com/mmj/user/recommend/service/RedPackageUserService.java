package com.mmj.user.recommend.service;

import com.baomidou.mybatisplus.service.IService;
import com.mmj.user.recommend.model.RedPackageUser;

/**
 * <p>
 * 用户红包表 服务类
 * </p>
 *
 * @author dashu
 * @since 2019-06-20
 */
public interface RedPackageUserService extends IService<RedPackageUser> {

    RedPackageUser getRedPacket(String unionId, String packageCode);

    void getRedPacketFromMQ(String params);

    void updateAllUser(Long oldUserId, Long newUserId);
}
