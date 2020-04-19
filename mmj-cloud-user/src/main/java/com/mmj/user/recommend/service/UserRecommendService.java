package com.mmj.user.recommend.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import com.mmj.common.model.UserMerge;
import com.mmj.user.recommend.model.UserRecommend;
import com.mmj.user.recommend.model.UserRecommendEx;
import com.mmj.user.recommend.model.vo.UserRecommendOrder;
import com.mmj.user.recommend.model.vo.UserRecommendVo;

import java.util.List;

/**
 * <p>
 * 用户推荐表 服务类
 * </p>
 *
 * @author dashu
 * @since 2019-06-18
 */
public interface UserRecommendService extends IService<UserRecommend> {

    Object save(UserRecommendVo userRecommendVo);

    Object updateUserRecommend(UserRecommend userRecommend);

    Page<UserRecommendEx> queryList(UserRecommend userRecommend);

    Object selectRecommendList(UserRecommendVo userRecommendVo);

    List<UserRecommendOrder> selectByOrderNo(List<String> orderNoList,Long createrId);

    List<UserRecommendOrder> selectByGoodSku(List<String> goodSku, String orderNo, Long createrId);

    Integer selectNORecommendOrderCont(Long createrId);

    UserRecommendVo selectByRecommendId(Integer recommendId);

    Object getRecommendByUserid(Long userId, Integer goodId);

    Object updateByRecommendId(List<UserRecommend> list);

    void updateUserId(UserMerge userMerge);
}
