package com.mmj.user.member.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.mmj.common.constants.LoginType;
import com.mmj.common.constants.MemberConstant;
import com.mmj.common.constants.OrderClassify;
import com.mmj.common.constants.OrderType;
import com.mmj.common.context.BaseContextHandler;
import com.mmj.common.exception.CustomException;
import com.mmj.common.model.JwtUserDetails;
import com.mmj.common.model.ReturnData;
import com.mmj.common.model.UserMerge;
import com.mmj.common.model.UserSharedParam;
import com.mmj.common.properties.SecurityConstants;
import com.mmj.common.utils.OrderUtils;
import com.mmj.common.utils.SecurityUserUtil;
import com.mmj.user.common.feigin.ActiveFeignClient;
import com.mmj.user.common.feigin.OrderFeignClient;
import com.mmj.user.common.feigin.PayFeignClient;
import com.mmj.user.manager.model.UserLogin;
import com.mmj.user.manager.service.UserLoginService;
import com.mmj.user.member.dto.SaveUserMemberDto;
import com.mmj.user.member.mapper.UserMemberMapper;
import com.mmj.user.member.model.UserMember;
import com.mmj.user.member.model.Vo.DegradeVo;
import com.mmj.user.member.service.MemberConfigService;
import com.mmj.user.member.service.UserMemberService;
import com.mmj.user.recommend.service.UserShardService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 会员表 服务实现类
 * </p>
 *
 * @author shenfuding
 * @since 2019-07-11
 */
@Slf4j
@Service
public class UserMemberServiceImpl extends ServiceImpl<UserMemberMapper, UserMember> implements UserMemberService {
    private static final String OLD_USER_IDENTITY = "old";
    private static final String MEMBER_USER_IDENTITY = "member";

    @Autowired
    private UserMemberMapper userMemberMapper;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private OrderFeignClient orderFeignClient;

    @Autowired
    private PayFeignClient payFeignClient;

    @Autowired
    private MemberConfigService memberConfigService;

    @Autowired
    private UserLoginService userLoginService;

    @Autowired
    private UserShardService userShardService;

    @Autowired
    private ActiveFeignClient activeFeignClient;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int save(SaveUserMemberDto entity) {

        log.info("-->保存会员信息，参数：{}", JSONObject.toJSONString(entity));

        if (!MemberConstant.BE_MEMBER_TYPE_UPGRADE.equalsIgnoreCase(entity.getBeMemberType())
                && !MemberConstant.BE_MEMBER_TYPE_ORDER.equalsIgnoreCase(entity.getBeMemberType())
                && !MemberConstant.BE_MEMBER_TYPE_BUY.equalsIgnoreCase(entity.getBeMemberType())
                && !MemberConstant.BE_MEMBER_TYPE_CALL_CHARGE.equalsIgnoreCase(entity.getBeMemberType())
                && !MemberConstant.BE_MEMBER_TYPE_IMPORT.equalsIgnoreCase(entity.getBeMemberType())) {
            throw new CustomException("会员参数非法");
        }

        long userId;
        String appId = "wx7a01aef90c714fe2"; //默认使用小程序APPID
        String openId;
        if (MemberConstant.BE_MEMBER_TYPE_BUY.equalsIgnoreCase(entity.getBeMemberType())) {
            openId = entity.getOpenId();
            // 如果是购买会员，则userId通过openId去取，因为支付回调只能拿到openId
            if (StringUtils.isBlank(openId)) {
                throw new CustomException("openId缺失");
            }
            UserLogin user = userLoginService.getUserLoginInfoByUserName(openId);
            if (user == null) {
                log.info("-->购买会员，用户{}在登录表中不存在", openId);
                throw new CustomException("用户数据异常");
            }
            userId = user.getUserId();
            appId = user.getAppId();
            log.info("-->购买会员，通过openId取到用户ID：{}，openId:{}", userId, openId);
        } else if (MemberConstant.BE_MEMBER_TYPE_UPGRADE.equalsIgnoreCase(entity.getBeMemberType()) ||
                MemberConstant.BE_MEMBER_TYPE_IMPORT.equalsIgnoreCase(entity.getBeMemberType())) {
            // 前端直接调成为会员
            JwtUserDetails userDetail = SecurityUserUtil.getUserDetails();
            userId = userDetail.getUserId();
            appId = userDetail.getAppId();
            openId = userDetail.getOpenId();
        } else {
            // 消息处理会员，userId必须要传，因为没有令牌
            if (entity.getUserId() == null) {
                throw new CustomException("userId缺失");
            }

            userId = entity.getUserId();

            // 此时有两种场景：下单成为会员会传openId和appId，但是话费充值成为会员就需要单独查openId和appId
            openId = entity.getOpenId();

            if (StringUtils.isBlank(openId)) {
                // 话费充值场景，appId还是使用小程序appId，根据appId找到openId

                Wrapper<UserLogin> wrapper = new EntityWrapper<UserLogin>();
                wrapper.eq("USER_ID", userId);
                BaseContextHandler.set(SecurityConstants.SHARDING_KEY, userId);
                List<UserLogin> list = userLoginService.selectList(wrapper);
                for (UserLogin userLogin : list) {
                    if (LoginType.openid.toString().equalsIgnoreCase(userLogin.getLoginType())
                            && appId.equalsIgnoreCase(userLogin.getAppId())) {
                        openId = userLogin.getUserName();
                        break;
                    }
                }
                if (StringUtils.isBlank(openId)) {
                    log.error("-->成为会员-->根据appId:{}未找到openId， userId:{}", appId, userId);
//					throw new CustomException("未找到openId");
                }
            } else {
                // 下单成为会员，openId是传过来的，appId也使用传过来的
                appId = entity.getAppId();
                if (StringUtils.isBlank(appId)) {
                    throw new CustomException("appId缺失");
                }
            }
        }

        if (MemberConstant.BE_MEMBER_TYPE_UPGRADE.equalsIgnoreCase(entity.getBeMemberType())) {

            // 如果是升级成为会员，则进行二次校验，防止前台校验出错

            // 历史消费金额
            double consumeMoney = orderFeignClient.getConsumeMoney(userId).getData();
            // 成为会员的门槛
            int mmjMemberCumulativeConsumption = memberConfigService.getMmjMemberCumulativeConsumption();
            if (consumeMoney < mmjMemberCumulativeConsumption) {
                log.error("-->用户{}进行升级会员操作，但校验不符合资格，历史消费金额:{}",
                        userId, consumeMoney);
                return -1;
            }
        }
        BaseContextHandler.set(SecurityConstants.SHARDING_KEY, userId);
        Date now = new Date();

        // 先判断会员信息是否存在
        UserMember um = this.queryUserMemberInfoByUserId(userId);
        int memberId;
        if (um != null) {
            // 只有先前成为了会员->降级->又成为会员才会更新此信息，所以进入到这里，肯定是成为会员
            log.info("-->该用户已有会员信息，此次更新会员信息，UserId:{}", userId);
            UserMember umForUpdate = new UserMember();
            umForUpdate.setMemberId(um.getMemberId());
            umForUpdate.setBeMemberType(entity.getBeMemberType());
            umForUpdate.setBeMemberTime(now);
            umForUpdate.setExpiryDate(DateUtils.addYears(now, 1));
            umForUpdate.setUpdateTime(now);
            umForUpdate.setActive(true);
            if (!StringUtils.isBlank(entity.getOrderNo())) {
                umForUpdate.setOrderNo(entity.getOrderNo());
            }
            BaseContextHandler.set(SecurityConstants.SHARDING_KEY, userId);
            this.updateById(umForUpdate);
            //删除会员缓存
            redisTemplate.delete(getMemberCacheKey(um.getUserId()));
            memberId = um.getMemberId();
        } else {
            // 该用户没有会员信息，此次新增会员信息
            log.info("-->该用户没有会员信息，此次新增会员信息，UserId:{}", userId);
            UserMember umForInsert = new UserMember();
            umForInsert.setMemberId(generateMemberId());
            umForInsert.setUserId(userId);
            umForInsert.setActive(true);
            umForInsert.setBeMemberType(entity.getBeMemberType());
            umForInsert.setBeMemberTime(now);
            umForInsert.setCreateTime(now);
            umForInsert.setExpiryDate(DateUtils.addYears(now, 1));//有效期1年
            umForInsert.setUpdateTime(now);
            if (!StringUtils.isBlank(entity.getOrderNo())) {
                umForInsert.setOrderNo(entity.getOrderNo());
            }
            this.insert(umForInsert);
            log.info("-->保存会员信息返回会员ID：{}, 会员userId：{}", umForInsert.getMemberId(), userId);
            memberId = umForInsert.getMemberId() > 0 ? umForInsert.getMemberId() : -1;
        }
        // 推荐成为会员逻辑
        UserSharedParam param = new UserSharedParam();
        param.setUserId(userId);
        param.setOpenId(entity.getOpenId());
        param.setAppId(appId);
        param.setOrderNo(entity.getOrderNo());
        param.setBeMemberType(entity.getBeMemberType());
        log.info("-->推荐成为会员逻辑：{}", JSON.toJSONString(param));
        userShardService.saveUserSharedInfo(param);

        try {
            //清除首页缓存, 修改版本号
            activeFeignClient.updateIndexCode(OLD_USER_IDENTITY);
            log.info("-> 老用户升级成为会员，清除缓存成功");
        } catch (Exception e) {
            e.printStackTrace();
            log.info("-> 老用户升级成为会员，清除缓存失败");
        }
        return memberId;
    }

    /**
     * 生成会员ID<br/>
     * 会员ID不可重复，当前对会员表进行了分表，如果采用自增，则不同会员的ID会出现相同的情况
     *
     * @return
     */
    private synchronized int generateMemberId() {
        int memberId = 0;
        String cacheKey = "USERMEMBER_MAXID";
        String cacheValue = redisTemplate.opsForValue().get(cacheKey);
        if (StringUtils.isBlank(cacheValue)) {
            memberId = userMemberMapper.getMaxMemberId() + 1;
            redisTemplate.opsForValue().set(cacheKey, String.valueOf(memberId));
            log.info("-->生成会员ID:{}", memberId);
        } else {
            memberId = Integer.valueOf(redisTemplate.opsForValue().increment(cacheKey, 1).toString());
            log.info("-->从redis生成会员ID:{}", memberId);
        }
        return memberId;
    }

    private String getMemberCacheKey(Long userid) {
        return "USERMEMBER:" + userid;
    }

    @Override
    public UserMember queryUserMemberInfoByUserId(Long userId) {
        BaseContextHandler.set(SecurityConstants.SHARDING_KEY, userId);
        UserMember um = null;
        String cacheKey = getMemberCacheKey(userId);
        String cacheValue = redisTemplate.opsForValue().get(cacheKey);
        if (StringUtils.isNotBlank(cacheValue)) {
            um = JSONArray.parseObject(cacheValue, UserMember.class);
        } else {
            Wrapper<UserMember> wrapper = new EntityWrapper<UserMember>();
            wrapper.eq("USER_ID", userId);
            um = this.selectOne(wrapper);
            if (um != null) {
                redisTemplate.opsForValue().set(cacheKey, JSONObject.toJSONString(um,
                        SerializerFeature.WriteMapNullValue), 12, TimeUnit.HOURS);
            }
        }

        if (um == null) {
            log.error("-->未查询到用户{}的会员信息", userId);
            return null;
        }

        if (um.getActive()) {
            // 如果会员有效，则判断是否已过期
            Date now = new Date();
            if (now.after(um.getExpiryDate())) {
                // 如果会员已过期，但会员标识还是1，则更新当前表的会员标识为0
                UserMember umForUpdate = new UserMember();
                umForUpdate.setMemberId(um.getMemberId());
                umForUpdate.setActive(false);
                this.updateById(umForUpdate);
                um.setActive(false);
                redisTemplate.delete(cacheKey);
                log.info("-->根据userId获取会员，当前会员已过期，但会员标识还是1，此次修改为0，userId:{}", userId);
            }
        }
        BaseContextHandler.set(SecurityConstants.SHARDING_KEY, null);
        return um;
    }

    @Override
    public boolean degrade(DegradeVo degradeVo) {
        long userId = 0;
        if (degradeVo.getUserId() == null) {
            userId = SecurityUserUtil.getUserDetails().getUserId();
        } else {
            userId = degradeVo.getUserId();
        }

        log.error("---->会员降级，userId：{}，降级原因：{}", userId, degradeVo.getRemark());
        UserMember um = this.queryUserMemberInfoByUserId(userId);
        if (um == null) {
            log.error("-->未查询到该用户的会员信息，userId：{}", userId);
            return false;
        }
        // 花钱购买的会员不能进行降级，因为没有此类型订单无法取消无法退款
        if (MemberConstant.BE_MEMBER_TYPE_BUY.equalsIgnoreCase(um.getBeMemberType())) {
            return false;
        }
        UserMember umForUpdate = new UserMember();
        umForUpdate.setMemberId(um.getMemberId());
        Date now = new Date();
        umForUpdate.setActive(false);
        umForUpdate.setDegradeTime(now);
        umForUpdate.setUpdateTime(now);
        umForUpdate.setRemark(degradeVo.getRemark());
        // 过期时间也更新为当前时间，即立即过期
        umForUpdate.setExpiryDate(now);
        BaseContextHandler.set(SecurityConstants.SHARDING_KEY, userId);
        boolean success = this.updateById(umForUpdate);
        redisTemplate.delete(getMemberCacheKey(userId));
        //删除历史消费金额缓存
        String cacheKey = "COUSUMEMONEY:" + userId;
        redisTemplate.delete(cacheKey);
        try {
            //清除首页缓存, 修改版本号
            activeFeignClient.updateIndexCode(MEMBER_USER_IDENTITY);
            log.info("-->会员降级成为普通老用户，清除首页缓存成功");
        } catch (Exception e) {
            e.printStackTrace();
            log.info("-->会员降级成为普通老用户，清除首页缓存失败");
        }
        return success;
    }

    @Override
    public int queryMemberTotalCount() {
        int total = userMemberMapper.getTotalCount();
        log.info("-->查询会员有效数量，结果：{}", total);
        return total;
    }

    @Override
    public Map<String, Object> queryUserMemberInfoForUC() {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        long userId = SecurityUserUtil.getUserDetails().getUserId();
        UserMember um = this.queryUserMemberInfoByUserId(userId);
        if (um != null) {
            resultMap.put("memberId", um.getMemberId());
            long day = (um.getExpiryDate().getTime() - new Date().getTime()) / 86400000;
            resultMap.put("expiryDate", day);
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DATE, Integer.parseInt(day + ""));
            String format = com.mmj.common.utils.DateUtils.SDF10.format(calendar.getTime());
            resultMap.put("expiryDateStr", format);
        }
        return resultMap;
    }

    /**
     * 直接购买会员
     *
     * @return
     */
    @Override
    public Map<String, String> buy(String appType) {
        JSONObject params = new JSONObject();
        JwtUserDetails userDetails = SecurityUserUtil.getUserDetails();
        String orderNo = OrderUtils.gainOrderNo(userDetails.getUserId(), OrderType.BUYORDER, appType, OrderClassify.MAIN);
        String outTradeNo = "hy_" + orderNo;
        params.put("outTradeNo", outTradeNo);
        params.put("appId", userDetails.getAppId());
        params.put("goodDesc", "会员购买");
        params.put("openId", userDetails.getOpenId());
        params.put("totalFee", memberConfigService.getMmjMemberWorth() * 100);
        ReturnData<Map<String, String>> payInfo = payFeignClient.getPayInfo(params);
        Map<String, String> data = payInfo.getData();
        data.put("orderNo", outTradeNo);
        return payInfo.getData();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUserID(UserMerge userMerge) {
        long oldUserId = userMerge.getOldUserId();
        long newUserId = userMerge.getNewUserId();
        log.info("-->会员表合并-->oldUserId:{}, newUserId:{}", oldUserId, newUserId);
        if (oldUserId == newUserId) {
            log.info("-->会员表合并-->新旧userId相等，不用合并");
            return;
        }

        BaseContextHandler.set(SecurityConstants.SHARDING_KEY, oldUserId);
        Wrapper<UserMember> wrapper = new EntityWrapper<UserMember>();
        wrapper.eq("USER_ID", oldUserId);
        UserMember um = this.selectOne(wrapper);
        if (um == null) {
            log.info("-->会员表合并-->根据oldUserId:{}未查到会员信息，不用合并", oldUserId);
            return;
        }
        // 判断是否需要切换表
        int oldTableIndex = (int) (oldUserId % 10);
        int newTableIndex = (int) (newUserId % 10);
        log.info("-->会员表合并-->oldUserId:{}所在表t_user_member_{}，newUserId:{}所在表t_user_member_{}", oldUserId, oldTableIndex, newUserId, newTableIndex);
        if (oldTableIndex != newTableIndex) {
            // 需要切换表

            // 1.插入到新表
            um.setUserId(newUserId);
            BaseContextHandler.set(SecurityConstants.SHARDING_KEY, newUserId);
            this.insert(um);

            // 2.删除旧数据
            BaseContextHandler.set(SecurityConstants.SHARDING_KEY, oldUserId);
            this.delete(wrapper);

        } else {
            // 两个userId在同一张表，直接修改userId
            log.info("-->会员表合并-->新旧ID都在同一张表：t_user_member_{}，直接修改用户ID：{}为{}", oldTableIndex, oldUserId, newUserId);
            BaseContextHandler.set(SecurityConstants.SHARDING_KEY, oldUserId);
            userMemberMapper.updateUserId(oldUserId, newUserId);
        }

        // 删除缓存
        redisTemplate.delete(getMemberCacheKey(oldUserId));
        redisTemplate.delete(getMemberCacheKey(newUserId));

        BaseContextHandler.set(SecurityConstants.SHARDING_KEY, null);
    }

}
