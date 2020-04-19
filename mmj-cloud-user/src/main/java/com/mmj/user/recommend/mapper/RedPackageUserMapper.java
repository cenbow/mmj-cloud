package com.mmj.user.recommend.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.mmj.user.recommend.model.RedPackageUser;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 用户红包表 Mapper 接口
 * </p>
 *
 * @author dashu
 * @since 2019-06-20
 */
@Repository
public interface RedPackageUserMapper extends BaseMapper<RedPackageUser> {

    RedPackageUser getRedPacket(@Param("unionId") String unionId, @Param("packageCode") String packageCode);

    void updateUserId(@Param("oldUserId") long oldUserId, @Param("newUserId") long newUserId);
}
