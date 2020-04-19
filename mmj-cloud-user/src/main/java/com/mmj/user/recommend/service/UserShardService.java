package com.mmj.user.recommend.service;

import java.util.List;

import com.baomidou.mybatisplus.service.IService;
import com.mmj.common.model.ReturnData;
import com.mmj.common.model.UserMerge;
import com.mmj.common.model.UserSharedParam;
import com.mmj.user.recommend.model.UserShard;
import com.mmj.user.recommend.model.UserShardEx;

/**
 * <p>
 * 分享关联表 服务类
 * </p>
 *
 * @author dashu
 * @since 2019-06-18
 */
public interface UserShardService extends IService<UserShard> {

    void add(UserShard shard);

    boolean del(Long shardTo);

    UserShard getByToUserId(Long toUserId);

    /**
     * 推荐分享返现 - 保存
     * @param userShard
     * @return
     */
    Object recommendSharedSave(UserShard userShard);

    /**
     *生成订单， 支付成功更新订单信息
     * @param userId
     * @param orderNo
     */
    void updateRecommendShared(Long userId,String orderNo,String appId,Integer orderStatus);

    /**
     * 被推荐人确定收货时调用 用来发零钱
     * @param orderNo
     * @param userid
     */
    void updateConfirm(String orderNo, long userid,String appId);

    /**
     * 返现中心提现明细查询
     * @param type
     * @return
     */
    List<UserShardEx> queryCash(String type,Long userId);

    /**
     * 可提现小计
     * @param userId
     * @return
     */
    Integer queryCanCashAmount(Long userId);

    /**
     * 即将到账小计
     * @param userId
     * @return
     */
    Integer querySoonCashAmount(Long userId);

    /**
     * 未支付小计
     * @param userId
     * @return
     */
    Integer queryUnPayCashAmount(Long userId);

    /**
     * 用户退款, 更新退款信息, 售后调用
     * @param userId
     * @param orderNo
     */
    boolean updateRefundByOrderNo(Long userId, String orderNo);

    /**
     * 查询退款金额, 售后调用
     * @param userId
     * @param orderNo
     * @return
     */
    int queryRefundByOrderNo(Long userId, String orderNo);

    /**
     * 提现, 批量更新红包状态
     * @param list
     * @return
     */
    boolean updateBachCash(List<UserShardEx> list);

    /**
     * 待支付状态下 取消付款
     * @param userId
     * @param orderNo
     * @return
     */
    boolean cancelOrder(Long userId, String orderNo);

    /**
     * 推荐成为会员保存
     * @param userShard
     * @return
     */
    Object saveUserShardInfo(UserShard userShard);

    /**
     * 成为会员后要保存分享信息，并发送零钱
     * @param param
     * @return
     */
    UserShard saveUserSharedInfo(UserSharedParam param);


    /**
     * 用户点击提现
     * @return
     */
    ReturnData<Object> doCash();

    /**
     * 被分享人确定收货10天, 发送零钱
     * @return
     */
    Object userShardSendMoney();

    /**
     * 查询用户被谁绑定成为会员
     * @return
     */
    Object queryBindMember();

    /**
     * 合并用户数据
     * @param userMerge
     */
    void updateUserId(UserMerge userMerge);
}
