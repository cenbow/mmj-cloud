package com.mmj.active.threeSaleTenner.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.pagination.PageHelper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.mmj.active.common.feigin.OrderGoodFeignClient;
import com.mmj.active.common.feigin.UserFeignClient;
import com.mmj.active.common.feigin.WxMessageFeignClient;
import com.mmj.active.common.feigin.WxpayTransfersFeignClient;
import com.mmj.active.common.model.OrderInfo;
import com.mmj.active.common.model.vo.UserOrderVo;
import com.mmj.active.threeSaleTenner.constant.ThreeSaleFissionConstant;
import com.mmj.active.threeSaleTenner.mapper.ThreeSaleFissionMapper;
import com.mmj.active.threeSaleTenner.mapper.ThreeSaleTennerMapper;
import com.mmj.active.threeSaleTenner.model.ThreeSaleFission;
import com.mmj.active.threeSaleTenner.model.ThreeSaleFissionEx;
import com.mmj.active.threeSaleTenner.model.ThreeSaleTenner;
import com.mmj.active.threeSaleTenner.service.ThreeSaleFissionService;
import com.mmj.common.context.BaseContextHandler;
import com.mmj.common.model.*;
import com.mmj.common.properties.SecurityConstants;
import com.mmj.common.utils.DateUtils;
import com.mmj.common.utils.MD5Util;
import com.mmj.common.utils.SecurityUserUtil;
import com.xiaoleilu.hutool.collection.CollectionUtil;
import jodd.util.StringUtil;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * <p>
 * 十元三件红包裂变 服务实现类
 * </p>
 *
 * @author dashu
 * @since 2019-07-11
 */
@Service
public class ThreeSaleFissionServiceImpl extends ServiceImpl<ThreeSaleFissionMapper, ThreeSaleFission> implements ThreeSaleFissionService {
    Logger logger = LoggerFactory.getLogger(ThreeSaleFissionServiceImpl.class);
    @Autowired
    private ThreeSaleFissionMapper threeSaleFissionMapper;
    @Autowired
    private OrderGoodFeignClient orderGoodFeignClient;
    @Autowired
    private ThreeSaleTennerMapper threeSaleTennerMapper;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private WxpayTransfersFeignClient wxpayTransfersFeignClient;
    @Autowired
    private WxMessageFeignClient wxMessageFeignClient;
    @Autowired
    private UserFeignClient userFeignClient;




    /**
     * 十元三件,分享用户下订单 - 保存
     * @param threeSaleFissionEx
     * @return
     */
    @Override
    public Object save(ThreeSaleFissionEx threeSaleFissionEx) {
        JwtUserDetails userDetails = SecurityUserUtil.getUserDetails();
        //查询用户是否已经保存
        EntityWrapper<ThreeSaleFission> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("FROM_USERID",userDetails.getUserId());
        entityWrapper.eq("FROM_ORDER_NO",threeSaleFissionEx.getFromOrderNo());
        List<ThreeSaleFission> threeSaleFissions = threeSaleFissionMapper.selectList(entityWrapper);
        if(CollectionUtil.isNotEmpty(threeSaleFissions)){
            return "已经保存过!";
        }
        threeSaleFissionEx.setCreateTime(new Date());
        threeSaleFissionEx.setFromUserid(userDetails.getUserId());
        if(null == userDetails.getUserFullName() || null == userDetails.getImagesUrl()) { //用户基本信息
            BaseContextHandler.set(SecurityConstants.SHARDING_KEY, userDetails.getUserId());
            BaseUser baseUser = userFeignClient.getUserById(userDetails.getUserId());
            threeSaleFissionEx.setFromNickName(baseUser.getUserFullName());
            threeSaleFissionEx.setFromHeadImg(baseUser.getImagesUrl());
        }else{
            threeSaleFissionEx.setFromNickName(userDetails.getUserFullName());
            threeSaleFissionEx.setFromHeadImg(userDetails.getImagesUrl());
        }

        //获取用户登录信息,如openId
        JSONObject params = new JSONObject();
        params.put("userId",userDetails.getUserId());
        params.put("appId", userDetails.getAppId());
        BaseUserDto data = userFeignClient.queryUserInfoByUserId(params.toJSONString()).getData();
        threeSaleFissionEx.setFromOpenid(data.getOpenId());
        threeSaleFissionMapper.insert(threeSaleFissionEx);
        return threeSaleFissionEx.getFissionId();
    }

    /**
     * 查询详情 - 拆红包使用
     * @return
     */
    @Override
    public ThreeSaleFissionEx query() {
        JwtUserDetails userDetails = SecurityUserUtil.getUserDetails();
        Date twoHourse = new Date(System.currentTimeMillis() - 7200000);  //当前时间往前推2个小时
        EntityWrapper<ThreeSaleFission> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("FROM_USERID",userDetails.getUserId());
        entityWrapper.gt("CREATE_TIME",twoHourse);
        entityWrapper.orderBy("CREATE_TIME DESC");
        List<ThreeSaleFission> fissionList = threeSaleFissionMapper.selectList(entityWrapper);
        ThreeSaleFissionEx threeSaleFissionEx = new ThreeSaleFissionEx();
        if(CollectionUtil.isEmpty(fissionList)){  //2个小时内,没有人助力
            threeSaleFissionEx.setTotalAmout(0); //返回价格为0
            threeSaleFissionEx.setRemainTime(0L);  //返回时间为0
        }else{  //2小时内,有人助力
            threeSaleFissionEx.setFromOrderNo(fissionList.get(0).getFromOrderNo());
            threeSaleFissionEx.setRedMoney(fissionList.get(0).getRedMoney());
            //获取助力金额, 订单状态要是 2:已支付, 3:已确认收货
            int totalAmout = fissionList.stream().filter(
                    n -> ThreeSaleFissionConstant.toOrderStatus.FINISH_PAY.equals(n.getToOrderStatus()) ||
                         ThreeSaleFissionConstant.toOrderStatus.CONFIRM_GOOD.equals(n.getToOrderStatus())
            ).mapToInt(ThreeSaleFission::getRedMoney).sum();
            threeSaleFissionEx.setTotalAmout(totalAmout);
            //获取分享人订单创建时间
            long remainTime = fissionList.get(0).getCreateTime().getTime();
            threeSaleFissionEx.setRemainTime((7200000 - new Date().getTime() + remainTime)); //剩余时间(当前时间 - 创建时间 = 过去时间)

            //获取朋友助力的数据, 订单状态要是 2:已支付, 3:已确认收货
            List<ThreeSaleFission> collect = fissionList.stream().filter(
                    n -> ThreeSaleFissionConstant.toOrderStatus.FINISH_PAY.equals(n.getToOrderStatus()) ||
                            ThreeSaleFissionConstant.toOrderStatus.CONFIRM_GOOD.equals(n.getToOrderStatus())).collect(Collectors.toList());
            threeSaleFissionEx.setFriendFreeSaleFissionExes(collect);
        }

        //获取跑马灯数据
        EntityWrapper<ThreeSaleFission> entity = new EntityWrapper<>();
        entity.orderBy("TO_ORDER_CONFIRM_TIME DESC");
        PageHelper.setPagination(new Pagination(1, 10)); //分页条件
        entity.eq("TO_ORDER_STATUS",ThreeSaleFissionConstant.toOrderStatus.CONFIRM_GOOD);
        List<ThreeSaleFission> list = threeSaleFissionMapper.selectList(entity);//跑马灯数据
        threeSaleFissionEx.setFreeSaleFissionExes(list);
        threeSaleFissionEx.setFromUserid(userDetails.getUserId());
        return threeSaleFissionEx;
    }

    /**
     * 好友助力下单
     * @param threeSaleFissionEx
     * @return
     */
    @Override
    public Object assist(ThreeSaleFissionEx threeSaleFissionEx) {
        JwtUserDetails userDetails = SecurityUserUtil.getUserDetails();
        //获取助力人的用户信息
        BaseContextHandler.set(SecurityConstants.SHARDING_KEY, userDetails.getUserId());
        BaseUser baseUser = userFeignClient.getUserById(userDetails.getUserId());
        threeSaleFissionEx.setToUserid(userDetails.getUserId());
        threeSaleFissionEx.setToOpenid(userDetails.getOpenId());
        threeSaleFissionEx.setToNickName(baseUser.getUserFullName());
        threeSaleFissionEx.setToHeadImg(baseUser.getImagesUrl());

        UserOrderVo userOrderVo = new UserOrderVo();
        userOrderVo.setUserId(userDetails.getUserId().toString());
        List<OrderInfo> orderInfoList = orderGoodFeignClient.getUserAllOrderNos(userOrderVo).getData();
        //过滤出不是十元三件红包裂变的订单, 如果存在订单则用户不是新用户, 只有新用户才能参与助力
        List<OrderInfo> collect = orderInfoList.stream().filter(orderInfo -> !orderInfo.getOrderNo().equals(threeSaleFissionEx.getToOrderNo())).collect(Collectors.toList());
        if(CollectionUtil.isNotEmpty(collect)){
            logger.info("-->十元三件红包裂变,不是新用户不能助力,用户id:{}",userDetails.getUserId());
            return null;
        }

        EntityWrapper<ThreeSaleFission> entityWrapper = new EntityWrapper<>();
        entityWrapper.orderBy("CREATE_TIME");
        entityWrapper.eq("FROM_ORDER_NO",threeSaleFissionEx.getFromOrderNo());
        List<ThreeSaleFission> fissionList = threeSaleFissionMapper.selectList(entityWrapper);
        if(CollectionUtil.isEmpty(fissionList)){
            logger.info("--> 十元三件红包裂变,订单号:{},不存在,",threeSaleFissionEx.getFromOrderNo());
            return null;
        }

        ThreeSaleFission record = fissionList.get(0);
        long time = record.getCreateTime().getTime();  //分享人订单创建时间
        if(System.currentTimeMillis() - time > 7200000){
            logger.info("-->十元三件红包裂变,超过2小时不能再助力了,用户id{}",userDetails.getUserId());
            return null;
        }

        if(record.getFromUserid().equals(threeSaleFissionEx.getToUserid())){
            logger.info("--> 十元三件红包裂变,自己不能帮自己助力,用户id:{}",threeSaleFissionEx.getToUserid());
            return null;
        }

        //查询该用户之前有没有帮其他人助力过,一个新用户只能助力一次
        List<ThreeSaleFission> list = fissionList.stream().filter(n -> threeSaleFissionEx.getToUserid().equals(n.getToUserid()) && !ThreeSaleFissionConstant.toOrderStatus.CANCEL_PAY.equals(n.getToOrderStatus())).collect(Collectors.toList());
        if(CollectionUtil.isNotEmpty(list)){
            logger.info("--> 十元三件红包裂变,你已经帮其他好友助力过, 不能再助力,用户id{}",userDetails.getUserId());
            return null;
        }


        String toOrderNo = record.getToOrderNo();
        ThreeSaleTenner threeSaleTenner = threeSaleTennerMapper.selectOne(new ThreeSaleTenner());
        BaseContextHandler.set(SecurityConstants.SHARDING_KEY, record.getFromUserid());
        BaseUser fromUser = userFeignClient.getUserById(record.getFromUserid());
        if(StringUtil.isNotEmpty(toOrderNo)){  //分享人被助力过, 创建一条新的记录
            threeSaleFissionEx.setFromNickName(fromUser.getUserFullName());
            threeSaleFissionEx.setFromHeadImg(fromUser.getImagesUrl());
            threeSaleFissionEx.setFromOpenid(record.getFromOpenid());
            threeSaleFissionEx.setFromUserid(record.getFromUserid());
            threeSaleFissionEx.setFromUnionid(record.getFromUnionid());
            threeSaleFissionEx.setFromOrderNo(record.getFromOrderNo());
            threeSaleFissionEx.setToOrderStatus(ThreeSaleFissionConstant.toOrderStatus.WAIT_PAY);  //支付状态:待支付
            threeSaleFissionEx.setToOderTime(new Date());
            threeSaleFissionEx.setRedMoney(threeSaleTenner.getRedMoney());
            threeSaleFissionEx.setRedStatus(ThreeSaleFissionConstant.redStatus.WAIT_GET);  //红包状态://未发送
            threeSaleFissionEx.setCreateTime(record.getCreateTime());
            logger.info("-->十元三件红包裂变,好友助力参数:{}",JSONObject.toJSONString(threeSaleFissionEx));
            threeSaleFissionMapper.insert(threeSaleFissionEx);
            return threeSaleFissionEx.getFissionId();
        }else{  //分享人没有被助力过,更新分享人的信息即可
            record.setFromNickName(fromUser.getUserFullName());
            record.setFromHeadImg(fromUser.getImagesUrl());
            record.setToOpenid(threeSaleFissionEx.getToOpenid());
            record.setToUserid(threeSaleFissionEx.getToUserid());
            record.setToUnionid(threeSaleFissionEx.getToUnionid());
            record.setToNickName(threeSaleFissionEx.getToNickName());
            record.setToHeadImg(threeSaleFissionEx.getToHeadImg());
            record.setToOrderNo(threeSaleFissionEx.getToOrderNo());
            record.setToOrderStatus(ThreeSaleFissionConstant.toOrderStatus.WAIT_PAY);  //支付状态:待支付
            record.setRedMoney(threeSaleTenner.getRedMoney());
            record.setRedStatus(ThreeSaleFissionConstant.redStatus.WAIT_GET);  //红包状态://未发送
            record.setToOderTime(new Date());
            logger.info("-->十元三件红包裂变,好友助力参数:{}",JSONObject.toJSONString(record));
            threeSaleFissionMapper.updateById(record);
            return record.getFissionId();
        }
    }

    /**
     * 好友助力 - 确定支付
     * @param orderNo
     * @return
     */
    @Override
    public Object updatePay(String orderNo) {
        logger.info("-->十元三件红包裂变,确定支付,订单号：{}",orderNo);
        EntityWrapper<ThreeSaleFission> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("TO_ORDER_NO",orderNo);
        List<ThreeSaleFission> fissionList = threeSaleFissionMapper.selectList(entityWrapper);
        if(CollectionUtil.isNotEmpty(fissionList)){
            logger.info(" --> 十元三件红包裂变,确认支付,订单号:{}",orderNo);
            ThreeSaleFission threeSaleFission = new ThreeSaleFission();
            threeSaleFission.setToOrderStatus(ThreeSaleFissionConstant.toOrderStatus.FINISH_PAY);  //支付状态:已支付
            threeSaleFission.setToOderTime(new Date());  //设置支付时间
            return threeSaleFissionMapper.update(threeSaleFission,entityWrapper);
        }
        return "用户订单不存在";
    }

    /**
     * 好友助力 - 确定收货
     * @param orderNo
     * @return
     */
    @Override
    public Object updateConfirm(String orderNo,String appId) {
        logger.info("-->十元三件红包裂变,确定收货,订单号：{},AppId:{}",orderNo,appId);
        EntityWrapper<ThreeSaleFission> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("TO_ORDER_NO",orderNo);
        List<ThreeSaleFission> fissionList = threeSaleFissionMapper.selectList(entityWrapper);
        if(CollectionUtil.isNotEmpty(fissionList)){
            ThreeSaleFission record = fissionList.get(0);
            if(ThreeSaleFissionConstant.toOrderStatus.FINISH_PAY.equals(record.getToOrderStatus())){  //已支付的状态下才会有确定收货
                //修改订单状态,为确定收货
                ThreeSaleFission threeSaleFission = new ThreeSaleFission();
                threeSaleFission.setToOrderStatus(ThreeSaleFissionConstant.toOrderStatus.CONFIRM_GOOD);  //确定收货
                threeSaleFission.setToOrderConfirmTime(new Date());  //确定收货时间
                threeSaleFissionMapper.update(threeSaleFission,entityWrapper);
                logger.info("-->十元三件红包裂变:助力好友确定收货,修改订单状态成功,被分享人订单号:{}",orderNo);

                //给分享人发送奖励模板消息
                try {
                    JSONObject tempParams = new JSONObject();
                    tempParams.put("appid",appId);  //同一端的appid一样
                    tempParams.put("touser", record.getFromOpenid());
                    tempParams.put("page", "/pkgProbationFree/cashBackCenter/main");
                    tempParams.put("template_id", "AuH2RwmJDM7rGwNwRrvQLIz4sQwyzeM0UpC-8eeRlIk"); //其他小程序也能接收到这个模板id

                    JSONObject data = new JSONObject();
                    JSONObject keyword1 = new JSONObject();
                    keyword1.put("value", (record.getRedMoney() / 100f) + "元");

                    JSONObject keyword2 = new JSONObject();
                    keyword2.put("value", DateUtils.getNowDate("yyyy-MM-dd"));

                    JSONObject keyword3 = new JSONObject();
                    keyword3.put("value", "十元三件推荐下单返现");

                    JSONObject keyword4 = new JSONObject();
                    EntityWrapper<ThreeSaleFission> entity = new EntityWrapper<>();  //查询红包总金额
                    entity.eq("FROM_USERID",record.getFromUserid());
                    entity.eq("TO_ORDER_STATUS",ThreeSaleFissionConstant.toOrderStatus.CONFIRM_GOOD);  //确定收货
                    List<ThreeSaleFission> list = threeSaleFissionMapper.selectList(entity);
                    int amount = 0;
                    if(CollectionUtil.isNotEmpty(list)){
                        amount = list.stream().mapToInt(ThreeSaleFission::getRedMoney).sum();
                    }
                    keyword4.put("value", "累计收益为"+ (amount / 100f) + "元");

                    JSONObject keyword5 = new JSONObject();
                    keyword5.put("value","你有"+(record.getRedMoney() / 100f)+"元现金1小时后过期，点击查看详情>>");

                    data.put("keyword1", keyword1);
                    data.put("keyword2", keyword2);
                    data.put("keyword3", keyword3);
                    data.put("keyword4", keyword4);
                    data.put("keyword5", keyword5);
                    tempParams.put("data", data);
                    //todo 发送奖励的模板消息
                    wxMessageFeignClient.sendTemplateM(tempParams.toJSONString()); //发送模板消息
                    logger.info("-->十元三件红包裂变:助力好友确定收货,发送模板消息成功,用户id:{}",record.getFromUserid());
                } catch (Exception e) {
                    logger.info("-->十元三件红包裂变:助力好友确定收货,发送模板消息失败,用户id:{},报错信息:{}",record.getFromUserid(),e.getMessage());
                }
                return "模板消息发送成功!";
            }
        }
        return "订单不存在!";
    }

    /**
     * 好友助力 - 取消订单
     * @param orderNo
     * @return
     */
    @Override
    public Object cancelled(String orderNo) {
        logger.info("-->十元三件红包裂变,取消订单,订单号：{}",orderNo);
        EntityWrapper<ThreeSaleFission> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("TO_ORDER_NO",orderNo);
        List<ThreeSaleFission> fissionList = threeSaleFissionMapper.selectList(entityWrapper);
        if(CollectionUtil.isNotEmpty(fissionList)){
            ThreeSaleFission threeSaleFission = new ThreeSaleFission();
            threeSaleFission.setToOrderStatus(ThreeSaleFissionConstant.toOrderStatus.CANCEL_PAY);  //订单状态: 已取消
            threeSaleFission.setRedStatus(ThreeSaleFissionConstant.redStatus.PAST_DUE);  //红包状态: 已经过期
            threeSaleFissionMapper.update(threeSaleFission,entityWrapper);
            return null;
        }
        return "订单不存在!";
    }

    /**
     * 根据用户id查询用户明细
     * @param type
     * @return
     */
    @Override
    public Map<String, Object> queryList(String type) {
        JwtUserDetails userDetails = SecurityUserUtil.getUserDetails();
        Map<String,Object> map = new HashMap();
        EntityWrapper<ThreeSaleFission> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("FROM_USERID",userDetails.getUserId());
        entityWrapper.eq("TO_ORDER_STATUS",ThreeSaleFissionConstant.toOrderStatus.FINISH_PAY);  //查询好友已经支付的订单
        entityWrapper.orderBy("TO_ODER_TIME DESC");  //支付时间
        List<ThreeSaleFission> soonFissionList = threeSaleFissionMapper.selectList(entityWrapper);
        if(CollectionUtil.isEmpty(soonFissionList)){
            map.put("soonCashAmount", 0); //即将到账金额
        }else{
            Integer soonCashAmount = soonFissionList.stream().mapToInt(ThreeSaleFission::getRedMoney).sum();
            map.put("soonCashAmount",(double)soonCashAmount/100);  //即将到账金额
        }

        entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("FROM_USERID",userDetails.getUserId());
        entityWrapper.eq("TO_ORDER_STATUS",ThreeSaleFissionConstant.toOrderStatus.CONFIRM_GOOD);  //查询好友已确定收货的订单
        entityWrapper.isNull("RED_TIME");
        entityWrapper.orderBy("TO_ODER_TIME DESC");  //支付时间
        List<ThreeSaleFission> canFissionList = threeSaleFissionMapper.selectList(entityWrapper);
        if(CollectionUtil.isEmpty(canFissionList)){
            map.put("canCashAmount", 0); //可提现金额
        }else{
            int canCashAmount = canFissionList.stream().mapToInt(ThreeSaleFission::getRedMoney).sum();
            map.put("canCashAmount",(double)canCashAmount/100 ); //可提现金额
        }

        switch (type){
            case "1": //即将到账(助友已付款,未收货)
                map.put("list", soonFissionList);
                break;

            case "2":  //可提现(助友确定收货)
                map.put("list", canFissionList);
                break;

            case "3":  //已失效(助友取消付款)
                entityWrapper = new EntityWrapper<>();
                entityWrapper.eq("FROM_USERID",userDetails.getUserId());
                entityWrapper.eq("TO_ORDER_STATUS",ThreeSaleFissionConstant.toOrderStatus.CANCEL_PAY);  //查询好友已取消的订单
                entityWrapper.eq("red_status",ThreeSaleFissionConstant.redStatus.PAST_DUE);  //红包状态:已过期
                entityWrapper.orderBy("CREATE_TIME DESC");  //支付时间
                List<ThreeSaleFission> noUseFissionList = threeSaleFissionMapper.selectList(entityWrapper);
                List<ThreeSaleFission> target = noUseFissionList.stream()
                        .collect(
                                Collectors.collectingAndThen(
                                        Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(ThreeSaleFission::getFromOrderNo))),
                                        n -> new ArrayList<>(n)
                                )
                        ); //根据fromOrderNo去重
                //针对from_order_no的推荐订单部分失效，部分不失效的处理
                List<ThreeSaleFission> threeSaleFissionList = target.parallelStream().filter(new Predicate<ThreeSaleFission>() {
                    @Override
                    public boolean test(ThreeSaleFission threeSaleFission) {
                        EntityWrapper<ThreeSaleFission> entity = new EntityWrapper<>();
                        entity.eq("FROM_ORDER_NO",threeSaleFission.getFromOrderNo());
                        entity.ne("TO_ORDER_STATUS",ThreeSaleFissionConstant.toOrderStatus.CANCEL_PAY);
                        List<ThreeSaleFission> fissionList = threeSaleFissionMapper.selectList(entity);
                        return fissionList.isEmpty();
                    }
                }).collect(Collectors.toList());
                map.put("list", threeSaleFissionList);
                break;

            case "4":  //已提现(用户提现成功)
                entityWrapper = new EntityWrapper<>();
                entityWrapper.eq("FROM_USERID",userDetails.getUserId());
                entityWrapper.eq("TO_ORDER_STATUS",ThreeSaleFissionConstant.toOrderStatus.CONFIRM_GOOD);  //确定收货
                entityWrapper.eq("RED_STATUS",ThreeSaleFissionConstant.redStatus.FINISH_GET);  //红包状态: 已领取
                entityWrapper.orderBy("RED_TIME DESC");
                List<ThreeSaleFission> cashFissionList = threeSaleFissionMapper.selectList(entityWrapper);
                map.put("list", cashFissionList);
                break;
        }
        return map;
    }

    /**
     * 用户点击提现
     * @param
     * @return
     */

    @Override
    @Transactional
    public ReturnData<Object> doCash() {
        JwtUserDetails userDetails = SecurityUserUtil.getUserDetails();
        ReturnData<Object> rd = new ReturnData<>();
        //防盗刷,对数量进行累加
        Long count = redisTemplate.opsForValue().increment("com.mmj.active.threeSaleTenner.service.impl.ThreeSaleFissionServiceImpl.doCash_" + userDetails.getUserId(), 1);
        if(count == 1){
            //设置60分钟过期
            redisTemplate.expire("com.mmj.active.threeSaleTenner.service.impl.ThreeSaleFissionServiceImpl.doCash_" + userDetails.getUserId(), 60, TimeUnit.SECONDS);
            //查询可提现的订单
            EntityWrapper<ThreeSaleFission> entityWrapper = new EntityWrapper<>();
            entityWrapper.eq("FROM_USERID",userDetails.getUserId());
            entityWrapper.eq("TO_ORDER_STATUS",ThreeSaleFissionConstant.toOrderStatus.CONFIRM_GOOD);  //确定收货
            entityWrapper.eq("RED_STATUS",ThreeSaleFissionConstant.redStatus.WAIT_GET);  //红包状态: 未发送
            entityWrapper.isNull("RED_TIME");
            entityWrapper.orderBy("TO_ORDER_STATUS DESC");
            List<ThreeSaleFission> fissionList = threeSaleFissionMapper.selectList(entityWrapper);
            if(CollectionUtil.isNotEmpty(fissionList)){
                //修改红包状态
                ThreeSaleFission record  = new ThreeSaleFission();
                record.setRedStatus(ThreeSaleFissionConstant.redStatus.FINISH_GET);  //红包状态: 已发送
                record.setRedTime(new Date());
                EntityWrapper<ThreeSaleFission> entity = new EntityWrapper<>();
                List<Integer> collectIds = fissionList.stream().map(ThreeSaleFission::getFissionId).collect(Collectors.toList());  //分享id集合
                entity.in("FISSION_ID",collectIds);
                threeSaleFissionMapper.update(record,entity);
                logger.info("-->十元三件红包裂变,用户点击提现,修改红包状态成功,用户id:{},", userDetails.getUserId());

                int totalAccount = fissionList.stream().mapToInt(ThreeSaleFission::getRedMoney).sum();  //获取零钱
                WxpayTransfers wxpayTransfers = new WxpayTransfers();
                wxpayTransfers.setMchAppid(userDetails.getAppId());
                wxpayTransfers.setOpenid(userDetails.getOpenId());
                wxpayTransfers.setAmount(totalAccount);
                wxpayTransfers.setDesc("10元三件裂变提现");
                List<String> collect = fissionList.stream().map(ThreeSaleFission::getToOrderNo).collect(Collectors.toList());
                String partnerTradeNo = MD5Util.MD5Encode(JSONObject.toJSONString(collect), "utf-8");
                wxpayTransfers.setPartnerTradeNo(partnerTradeNo);
                try {
                    WxpayTransfers data = wxpayTransfersFeignClient.transfers(wxpayTransfers).getData();//发送零钱
                    logger.info("-->十元三件红包裂变:用户提现,用户id:{},提现状态:{},提现描述:{}", userDetails.getUserId(),data.getState(),data.getErrorDesc());
                    rd.setCode(SecurityConstants.SUCCESS_CODE);
                    rd.setDesc("提现成功");
                    return rd;
                } catch (Exception e) {
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    JSONObject error = JSON.parseObject(e.getCause().getMessage().split("content:\n")[1]);
                    e.printStackTrace();
                    logger.info("-->十元三件红包裂变:用户提现失败,用户id:{},提现金额:{},失败原因:{}", userDetails.getUserId(),totalAccount,error.getString("desc"));
                    rd.setCode(SecurityConstants.FAIL_CODE);
                    rd.setDesc(error.getString("desc"));
                    return rd;
                }

            }
            rd.setCode(SecurityConstants.FAIL_CODE);
            rd.setDesc("没有可提现的红包");
            return rd;
        }else {
            rd.setCode(SecurityConstants.FAIL_CODE);
            rd.setDesc("请勿频繁操作,一分钟以后再试");
            return rd;
        }
    }

    /**
     * 判断该用户是否有红包
     * @return
     */
    @Override
    public boolean hasRedPackage() {
        Long userId = SecurityUserUtil.getUserDetails().getUserId();
        EntityWrapper<ThreeSaleFission> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("FROM_USERID",userId);
        List<ThreeSaleFission> fissionList = threeSaleFissionMapper.selectList(entityWrapper);
        if(CollectionUtil.isNotEmpty(fissionList)){
            return true;
        }
        return false;
    }

    /**
     * 十元三件红包裂变,定时任务 - 查询2小时内,助力好友,未支付成功的数据
     * @return
     */
    @Override
    public Object updateInvalid() {
        return threeSaleFissionMapper.updateInvalid();
    }

    @Override
    @Transactional(rollbackFor=Exception.class)
    public void updateUserId(Long oldUserId, Long newUserId) {
        EntityWrapper<ThreeSaleFission> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("FROM_USERID",newUserId).or().eq("TO_USERID",newUserId);
        List<ThreeSaleFission> list = threeSaleFissionMapper.selectList(entityWrapper);
        if(CollectionUtils.isNotEmpty(list)){
            logger.error("-->-->十元三件红包裂变表,用户{}的新ID {}在十元三件红包表也有数据，请人工处理", oldUserId, newUserId);
            return;
        }

        List<ThreeSaleFission> threeSaleFissionList = selectList("FROM_USERID", oldUserId);
        if(CollectionUtils.isEmpty(threeSaleFissionList)){
            logger.info("-->十元三件红包裂变表合并-->根据分享人oldUserId:{}未查到返现信息，不用合并", oldUserId);
        }else{
            threeSaleFissionMapper.updateFromUserId(oldUserId,newUserId);
            logger.info("-->十元三件红包裂变表合并-->分享人切换userId, {}改为{}", oldUserId, newUserId);
        }

        List<ThreeSaleFission> fissionList = selectList("TO_USERID", oldUserId);
        if(CollectionUtils.isEmpty(fissionList)){
            logger.info("-->十元三件红包裂变表合并-->根据被分享人oldUserId:{}未查到返现信息，不用合并", oldUserId);
        }else{
            threeSaleFissionMapper.updateToUserId(oldUserId,newUserId);
            logger.info("-->十元三件红包裂变表合并-->被分享人切换userId, {}改为{}", oldUserId, newUserId);
        }
    }

    private List<ThreeSaleFission>  selectList(String column,Long userId) {
        EntityWrapper<ThreeSaleFission> wrapper = new EntityWrapper<>();
        wrapper.eq(column,userId);
        return threeSaleFissionMapper.selectList(wrapper);
    }
}
