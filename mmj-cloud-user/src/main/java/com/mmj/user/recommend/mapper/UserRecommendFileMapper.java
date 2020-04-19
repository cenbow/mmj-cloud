package com.mmj.user.recommend.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.mmj.common.model.UserMerge;
import com.mmj.user.recommend.model.UserRecommendFile;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 分享附件表 Mapper 接口
 * </p>
 *
 * @author dashu
 * @since 2019-06-18
 */
@Repository
public interface UserRecommendFileMapper extends BaseMapper<UserRecommendFile> {

    List<String> selectFileUrl(Integer recommendId);

    List<UserRecommendFile> selectRecommendFileUrl(Integer recommendId);

    void updateUserId(@Param("oldUserId") long oldUserId, @Param("newUserId") long newUserId);
}
