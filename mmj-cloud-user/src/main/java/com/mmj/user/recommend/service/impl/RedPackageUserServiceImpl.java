package com.mmj.user.recommend.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.mmj.common.context.BaseContextHandler;
import com.mmj.common.model.ReturnData;
import com.mmj.common.properties.SecurityConstants;
import com.mmj.user.common.feigin.NoticeFeignClient;
import com.mmj.user.common.feigin.PayFeignClient;
import com.mmj.user.common.model.WxpayRedpack;
import com.mmj.user.recommend.mapper.RedPackageUserMapper;
import com.mmj.user.recommend.model.RedPackageUser;
import com.mmj.user.recommend.service.RedPackageUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 用户红包表 服务实现类
 * </p>
 *
 * @author dashu
 * @since 2019-06-20
 */
@Service
@Slf4j
public class RedPackageUserServiceImpl extends ServiceImpl<RedPackageUserMapper, RedPackageUser> implements RedPackageUserService {

    @Autowired
    private RedPackageUserMapper mapper;

    @Autowired
    private PayFeignClient payFeignClient;
    @Autowired
    private RedPackageUserMapper redPackageUserMapper;

    @Autowired
    private NoticeFeignClient noticeFeignClient;

    @Override
    public RedPackageUser getRedPacket(String unionId, String packageCode) {
        return mapper.getRedPacket(unionId, packageCode);
    }

    @Override
    public void getRedPacketFromMQ(String params) {
        log.info("通过公众号领红包:{}", params);
        JSONObject msg = JSONObject.parseObject(params);
        if (null == msg)
            return;
        if (!"text".equals(msg.getString("MsgType"))) {
            //非文本消息，不是领红包消息
            return;
        }
        String code = msg.getString("Content");
        if (!msg.containsKey("ex")) {
            log.error("参数【ex】不存在");
            return;
        }

        JSONObject object = msg.getJSONObject("ex");
        String unionId = object.getString("unionid");

        RedPackageUser redPackageUser = this.getRedPacket(unionId, code);
        if (null == redPackageUser) {
            log.info("用户无红包:{}", object);
            return;
        }
        if (redPackageUser.getPackageStatus() ==1){
            JSONObject msgJson = new JSONObject();
            msgJson.put("appid", object.getString("appid"));
            msgJson.put("touser", object.getString("openid"));
            msgJson.put("msgtype", "text");
            JSONObject textJson = new JSONObject();
            textJson.put("content", "你已经领取过了该红包");
            msgJson.put("text", textJson);
            noticeFeignClient.sendCustom(msgJson.toJSONString());
            return;
        }
        log.info("查询到红包码:{}", redPackageUser);

        WxpayRedpack redpack = new WxpayRedpack();
        redpack.setActName(redPackageUser.getPackageSource());
        redpack.setMchBillno(redPackageUser.getOrderNo());
        redpack.setSendName("买买家");
        redpack.setReOpenid(object.getString("openid"));
        redpack.setRemark("红包");
        redpack.setTotalAmount(redPackageUser.getPackageAmount());
        redpack.setWishing("恭喜获得红包");
        redpack.setWxappid(object.getString("appid"));
        ReturnData data = payFeignClient.sendRedpack(redpack);
        log.info("公众号领红包结果:{}", data);
        if (data.getCode() == 1) {
            //领红包成功
            RedPackageUser rpu = new RedPackageUser();
            rpu.setPackageId(redPackageUser.getPackageId());
            rpu.setPackageStatus(1);
            rpu.setAccountTime(new Date());
            rpu.setModifyTime(new Date());
            rpu.setModifyId(redPackageUser.getUserId());
            BaseContextHandler.set(SecurityConstants.SHARDING_KEY, redPackageUser.getUserId());
            this.updateById(rpu);
        }
    }

    @Override
    public void updateAllUser(Long oldUserId, Long newUserId) {
        EntityWrapper<RedPackageUser> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("USER_ID", oldUserId);
        BaseContextHandler.set(SecurityConstants.SHARDING_KEY, oldUserId);
        List<RedPackageUser> redPackageUserList = redPackageUserMapper.selectList(entityWrapper);
        if (CollectionUtils.isEmpty(redPackageUserList)) {
            log.info("-->推荐返现红包表合并-->根据oldUserId:{}未查到返现信息，不用合并", oldUserId);
        } else {
            //判断是否需要切换表
            int oldTableIndex = (int) (oldUserId % 10);
            int newTableIndex = (int) (newUserId % 10);
            log.info("-->推荐返现红包表合并-->分享人,oldUserId:{}所在表t_redpackage_user_{}，newUserId:{}所在表t_redpackage_user_{}", oldUserId, oldTableIndex, newUserId, newTableIndex);

            if (oldTableIndex != newTableIndex) {
                redPackageUserList.forEach(redPackageUser -> {
                    redPackageUser.setUserId(newUserId);
                    redPackageUser.setCreaterId(newUserId);
                });

                BaseContextHandler.set(SecurityConstants.SHARDING_KEY, newUserId);
                this.insertBatch(redPackageUserList);

                this.deleteAllRedPackage(oldUserId);
                log.info("-->推荐返现红包表合并-->修改数据成功,newUserId:{}所在表t_redpackage_user_{}", newUserId, newTableIndex);
            } else {
                log.info("-->推荐返现红包表合并-->新旧ID都在同一张表：t_redpackage_user_{}，直接修改用户ID：{}为{}", oldTableIndex, oldUserId, newUserId);
                BaseContextHandler.set(SecurityConstants.SHARDING_KEY, oldUserId);
                redPackageUserMapper.updateUserId(oldUserId, newUserId);
                log.info("-->推荐返现红包表合并-->修改数据成功,newUserId:{}所在表t_redpackage_user_{}", newUserId, newTableIndex);
            }
        }
    }


    public void deleteAllRedPackage(Long oldUserId) {
        EntityWrapper<RedPackageUser> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("USER_ID", oldUserId);
        BaseContextHandler.set(SecurityConstants.SHARDING_KEY, oldUserId);
        redPackageUserMapper.delete(entityWrapper);
    }

}
