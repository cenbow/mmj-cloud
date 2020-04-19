package com.mmj.user.recommend.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.mmj.common.model.UserMerge;
import com.mmj.user.recommend.model.UserRecommend;
import com.mmj.user.recommend.model.UserRecommendEx;
import com.mmj.user.recommend.model.vo.UserRecommendVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 用户推荐表 Mapper 接口
 * </p>
 *
 * @author dashu
 * @since 2019-06-18
 */
@Repository
public interface UserRecommendMapper extends BaseMapper<UserRecommend> {

    List<UserRecommendEx> queryList(Page<UserRecommendEx> page, UserRecommend entity);

    List<UserRecommendVo> selectRecommendAllList(Page<UserRecommendVo> page, UserRecommendVo userRecommendVo);

    List<UserRecommendVo> selectRecommendList(Page<UserRecommendVo> page, UserRecommendVo userRecommendVo);

    Integer selectAlltotal(UserRecommendVo userRecommendVo);

    Integer selectPictureOrVideoTotal(UserRecommendVo userRecommendVo);

    void updateUserId(@Param("oldUserId") long oldUserId, @Param("newUserId") long newUserId);
}
