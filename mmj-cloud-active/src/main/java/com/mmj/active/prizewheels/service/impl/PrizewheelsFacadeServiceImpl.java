package com.mmj.active.prizewheels.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.mmj.active.common.MQProducer;
import com.mmj.active.common.config.GzhNotifyConfig;
import com.mmj.active.common.constants.CouponConstants;
import com.mmj.active.common.constants.MMKingShareType;
import com.mmj.active.common.constants.WxMedia;
import com.mmj.active.common.feigin.CouponUserFeignClient;
import com.mmj.active.common.feigin.GoodFeignClient;
import com.mmj.active.common.feigin.NoticeFeignClient;
import com.mmj.active.common.feigin.UserFeignClient;
import com.mmj.active.common.feigin.WxConfigFeignClient;
import com.mmj.active.common.feigin.WxMessageFeignClient;
import com.mmj.active.common.feigin.WxpayTransfersFeignClient;
import com.mmj.active.common.model.FocusInfo;
import com.mmj.active.common.model.vo.UserCouponVo;
import com.mmj.active.common.service.FocusInfoService;
import com.mmj.active.coupon.model.CouponInfo;
import com.mmj.active.coupon.service.CouponInfoService;
import com.mmj.active.prizewheels.constants.PrizewheelsConstant;
import com.mmj.active.prizewheels.dto.MyCoinsChangeDetail;
import com.mmj.active.prizewheels.dto.MyPrizeDto;
import com.mmj.active.prizewheels.dto.PrizeTypeDto;
import com.mmj.active.prizewheels.dto.WithdrawRecordDto;
import com.mmj.active.prizewheels.model.PrizewheelsAccessRecord;
import com.mmj.active.prizewheels.model.PrizewheelsAccount;
import com.mmj.active.prizewheels.model.PrizewheelsCoinsRecord;
import com.mmj.active.prizewheels.model.PrizewheelsPrizeProbability;
import com.mmj.active.prizewheels.model.PrizewheelsPrizeRecord;
import com.mmj.active.prizewheels.model.PrizewheelsPrizeType;
import com.mmj.active.prizewheels.model.PrizewheelsRedpacketRecord;
import com.mmj.active.prizewheels.model.PrizewheelsTemplate;
import com.mmj.active.prizewheels.model.PrizewheelsWithdrawRecord;
import com.mmj.active.prizewheels.service.PrizewheelsAccessRecordService;
import com.mmj.active.prizewheels.service.PrizewheelsAccountService;
import com.mmj.active.prizewheels.service.PrizewheelsCoinsRecordService;
import com.mmj.active.prizewheels.service.PrizewheelsFacadeService;
import com.mmj.active.prizewheels.service.PrizewheelsPrizeProbabilityService;
import com.mmj.active.prizewheels.service.PrizewheelsPrizeRecordService;
import com.mmj.active.prizewheels.service.PrizewheelsPrizeTypeService;
import com.mmj.active.prizewheels.service.PrizewheelsRedpacketRecordService;
import com.mmj.active.prizewheels.service.PrizewheelsTemplateService;
import com.mmj.active.prizewheels.service.PrizewheelsWithdrawRecordService;
import com.mmj.active.prizewheels.util.PrizewheelsUtil;
import com.mmj.common.constants.CommonConstant;
import com.mmj.common.constants.MQTopicConstant;
import com.mmj.common.exception.CustomException;
import com.mmj.common.exception.CustomMessageException;
import com.mmj.common.model.BaseUser;
import com.mmj.common.model.JwtUserDetails;
import com.mmj.common.model.PrizewheelsShare;
import com.mmj.common.model.ReturnData;
import com.mmj.common.model.UserLogin;
import com.mmj.common.model.UserReceiveCouponDto;
import com.mmj.common.model.WxpayTransfers;
import com.mmj.common.properties.SecurityConstants;
import com.mmj.common.utils.CommonUtil;
import com.mmj.common.utils.DateUtils;
import com.mmj.common.utils.DoubleUtil;
import com.mmj.common.utils.SecurityUserUtil;

@Slf4j
@Service
public class PrizewheelsFacadeServiceImpl implements PrizewheelsFacadeService {

    @Autowired
    private PrizewheelsTemplateService prizewheelsTemplateService;

    @Autowired
    private PrizewheelsRedpacketRecordService prizewheelsRedpacketRecordService;

    @Autowired
    private PrizewheelsCoinsRecordService prizewheelsCoinsRecordService;

    @Autowired
    private PrizewheelsAccessRecordService prizewheelsAccessRecordService;

    @Autowired
    private PrizewheelsPrizeTypeService prizewheelsPrizeTypeService;

    @Autowired
    private PrizewheelsPrizeProbabilityService prizewheelsPrizeProbabilityService;

    @Autowired
    private PrizewheelsPrizeRecordService prizewheelsPrizeRecordService;

    @Autowired
    private PrizewheelsWithdrawRecordService prizewheelsWithdrawRecordService;

    @Autowired
    private PrizewheelsAccountService prizewheelsAccountService;

    @Autowired
    private WxConfigFeignClient wxConfigService;

    @Autowired
    private GoodFeignClient goodService;

    @Autowired
    private UserFeignClient userFeignClient;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private WxpayTransfersFeignClient wxpayTransfersService;

    @Autowired
    private CouponUserFeignClient couponUserFeignClient;

    @Autowired
    private CouponInfoService couponInfoService;

    @Autowired
    private MQProducer mqProducer;
    
    @Autowired
    private WxMessageFeignClient wxMessageFeignClient;

    @Autowired
    private NoticeFeignClient noticeFeignClient;
    
    @Autowired
    private FocusInfoService focusInfoService;
    
    @Autowired
    private GzhNotifyConfig notifyConfig;
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> checkNewUser() {
        long userId = SecurityUserUtil.getUserDetails().getUserId();
        log.info("-->转盘-->检查新用户，userId:{}", userId);
        // 要返回的map对象
        Map<String, Object> resultMap = new HashMap<String, Object>();

        PrizewheelsAccount account = prizewheelsAccountService.selectById(userId);
        boolean isNewUser = account == null ? true : false;
        resultMap.put("isNewUser", isNewUser);
        log.info("-->转盘-->判断用户{}是否为转盘新用户：{}.", userId, isNewUser);

        if (isNewUser) {

            log.info("-->转盘-->用户{}为新用户，开始发放红包和买买币", userId);

            // 获取转盘规则配置
            PrizewheelsTemplate template = prizewheelsTemplateService.load();

            /******************** 保存红包增加记录 ***********************/
            PrizewheelsRedpacketRecord redpacketRecord = this.getPrizewheelsRedpacketRecordInitBean();
            redpacketRecord.setUserId(userId);
            redpacketRecord.setIncreaseMoney(template.getNewUserRedpacket());
            redpacketRecord.setGotWays(PrizewheelsConstant.RedpacketGotWays.NEW_USER);
            // 红包状态为待领取，用户必须手动点击"放入我的余额"才可变为已领取
            redpacketRecord.setStatus(PrizewheelsConstant.GotStatus.PENDING);
            prizewheelsRedpacketRecordService.insert(redpacketRecord);
            log.info("-->转盘-->保存新用户{}的红包增加记录，状态为待领取，红包金额：{}",
                    userId, template.getNewUserRedpacket());

            /******************* 保存买买币增加记录 **********************/
            PrizewheelsCoinsRecord coinsRecord = getPrizewheelsCoinsRecordInitBean(userId);
            coinsRecord.setIncreaseCoins(template.getNewUserGetCoinsAmount());
            coinsRecord.setBlanceCoins(template.getNewUserGetCoinsAmount());
            coinsRecord.setGotWays(PrizewheelsConstant.CoinsGotWays.NEW_USER);
            // 已和唐超确认， 新人实时获得买买币，不需要用户再额外点击什么领取
            coinsRecord.setStatus(PrizewheelsConstant.GotStatus.GOT);
            prizewheelsCoinsRecordService.insert(coinsRecord);
            log.info("-->转盘-->保存新用户{}的买买币增加记录，买买币金额：{}.",
                    userId, template.getNewUserGetCoinsAmount());

            /******************* 初始化账户信息并保存 **********************/
            account = this.getPrizewheelsAccountInitBean(userId, isNewUser);
            // 由于此时红包还未领取，用户必须点击“放入红包余额”才可到我的余额中，所以创建时余额为0
            account.setRedpacketBalance(0d);
            account.setCoinsBalance(template.getNewUserGetCoinsAmount());
            // 记录翻倍奖励状态（流量池相关）
            account.setTenPrize(PrizewheelsConstant.DoubleReward.INIT);
            this.prizewheelsAccountService.insert(account);
            log.info("-->转盘-->初始化新用户{}账户信息，买买币余额：{}，红包余额：{}.",
                    userId, template.getNewUserGetCoinsAmount(), 0);
            // 更新转盘账户是否存在的缓存值
            String cacheKey = getAccountExistsKey(userId);
            this.redisTemplate.delete(cacheKey);
            
            resultMap.put("newUserRedpacketMoney", template.getNewUserRedpacket());
            resultMap.put("status", redpacketRecord.getStatus());

        } else {

            log.info("-->转盘-->用户{}非新用户", userId);
            if (PrizewheelsConstant.DoubleReward.OFFICIALED_ACCOUNTS == account.getTenPrize()) {
                log.info("-->转盘-->检查到用户{}关注了公众号，等待用户领取翻倍红包", userId);
                // 获取待领取的翻倍红包
                // 注：这个翻倍不是指10元红包金额翻倍，而是用户在领到一个10红包后，再给用户一个随机红包，只要关注了公众号就可以领取
                Wrapper<PrizewheelsRedpacketRecord> wrapper = new EntityWrapper<PrizewheelsRedpacketRecord>();
                wrapper.eq("USER_ID", userId);
                wrapper.eq("GOT_WAYS", PrizewheelsConstant.RedpacketGotWays.DOUBLE_REWARD_REDPACKET);
                wrapper.eq("STATUS", PrizewheelsConstant.GotStatus.PENDING);
                PrizewheelsRedpacketRecord record = prizewheelsRedpacketRecordService.selectOne(wrapper);
                if (record != null) {
                    log.info("-->转盘-->用户{}有翻倍的随机红包待领取，红包金额：{}", userId, record.getIncreaseMoney());
                    resultMap.put("flowcell", true);
                    double totalMoney = DoubleUtil.add(record.getIncreaseMoney(), 10d, DoubleUtil.SCALE_3);
                    resultMap.put("flowcellMoney", totalMoney);
                    resultMap.put("increateMoney", record.getIncreaseMoney());

                    // 将翻倍红包显示给前端，然后再将tenPize改为已提醒
                    PrizewheelsAccount accountForUpdate = new PrizewheelsAccount();
                    accountForUpdate.setUserId(userId);
                    accountForUpdate.setTenPrize(PrizewheelsConstant.DoubleReward.REMINDED);
                    prizewheelsAccountService.updateById(accountForUpdate);
                    log.info("-->转盘-->修改用户{}的翻倍红包状态为已前端已提醒", userId);
                }
            }

        }
        return resultMap;
    }
    
    private String getAvatarUrl(JwtUserDetails user) {
    	String avatarUrl = user.getImagesUrl();
        if(StringUtils.isBlank(avatarUrl)) {
        	BaseUser baseUser = userFeignClient.getUserById(user.getUserId());
        	if(baseUser != null) {
        		avatarUrl = baseUser.getImagesUrl();
        	}
        }
        return avatarUrl;
    }
    
    private String getNickname(JwtUserDetails user) {
    	String nickname = user.getUserFullName();
        if(StringUtils.isBlank(nickname)) {
        	BaseUser baseUser = userFeignClient.getUserById(user.getUserId());
        	if(baseUser != null) {
        		nickname = baseUser.getUserFullName();
        	}
        }
        return nickname;
    }

    @Override
    @Transactional
    public Map<String, Object> loadPrizewheelsInitData() {
        JwtUserDetails user = SecurityUserUtil.getUserDetails();
        Long userId = user.getUserId();

        log.info("-->转盘-->获取页面初始化数据，userId:{}", userId);

        // 要返回的map对象
        Map<String, Object> resultMap = new HashMap<String, Object>();

        log.info("-->转盘-->保存用户{}访问记录.", userId);
        prizewheelsAccessRecordService.save(userId);

        log.info("-->转盘-->查询用户{}转盘页面初始化所需数据.", userId);
        resultMap.put("avatarUrl", getAvatarUrl(user)); // 头像地址

        PrizewheelsAccount account = prizewheelsAccountService.selectById(userId);
        double redpacketBalance = account != null ? account.getRedpacketBalance() : 0d;
        int coinsBalance = account != null ? account.getCoinsBalance() : 0;
        resultMap.put("redpacketBalance", redpacketBalance); // 账户余额
        resultMap.put("coinsBalance", coinsBalance); // 买买币余额

        PrizewheelsTemplate template = prizewheelsTemplateService.load();
        StringBuilder coinsRuleDesc = new StringBuilder();
        coinsRuleDesc.append("每次抽奖消耗");
        coinsRuleDesc.append(template.getConsumingCoinsAmount());
        coinsRuleDesc.append("买买币，每");
        coinsRuleDesc.append(template.getIncrementCoinsMinutes());
        coinsRuleDesc.append("分钟获取");
        coinsRuleDesc.append(template.getIncrementCoinsAmount());
        coinsRuleDesc.append("买买币");
        // 买买币规则描述：每次抽奖消耗10买买币，每10分钟获得2买买币
        resultMap.put("coinsRuleDesc", coinsRuleDesc.toString());
        String ruleDesc = template.getRuleDesc();
        String[] ruleArr = ruleDesc.split("<br/>");
        resultMap.put("activeRuleDesc", ruleArr);

        // 获取奖品类型数据
        List<PrizewheelsPrizeType> prizeList = prizewheelsPrizeTypeService.loadAllPrize();
        List<PrizeTypeDto> prizeTypeDtoList = new ArrayList<PrizeTypeDto>();
        if (prizeList != null) {
            PrizeTypeDto dto = null;
            for (PrizewheelsPrizeType prize : prizeList) {
                dto = new PrizeTypeDto();
                dto.setPrizeName(prize.getPrizeName());
                dto.setPrizeCode(prize.getPrizeCode());
                dto.setSort(prize.getSort());
                dto.setIconUrl(prize.getIconUrl());
                prizeTypeDtoList.add(dto);
            }
        }
        resultMap.put("allPrize", prizeTypeDtoList);
        return resultMap;
    }

    private PrizewheelsRedpacketRecord getLastPendingRedpacketRecord(Wrapper<PrizewheelsRedpacketRecord> wrapper) {
        List<PrizewheelsRedpacketRecord> list = prizewheelsRedpacketRecordService.selectList(wrapper);
        PrizewheelsRedpacketRecord redpacketRecord = list.isEmpty() ? null : list.get(0);
        return redpacketRecord;
    }

    private Wrapper<PrizewheelsRedpacketRecord> getLastPendingRedpacketWrapper(Long userId, String from) {
        Wrapper<PrizewheelsRedpacketRecord> wrapper = new EntityWrapper<PrizewheelsRedpacketRecord>();
        wrapper.eq("USER_ID", userId);
        wrapper.eq("GOT_WAYS", from);
        wrapper.eq("STATUS", PrizewheelsConstant.GotStatus.PENDING);
        Collection<String> columns = new ArrayList<String>();
        columns.add("UPDATE_TIME");
        wrapper.orderDesc(columns);
        return wrapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> addRedpacketBalance(String from) {

        Long userId = SecurityUserUtil.getUserDetails().getUserId();

        log.info("-->转盘-->用户{}点击放入我的余额，from:{}", userId, from);

        if (!PrizewheelsConstant.RedpacketGotWays.NEW_USER.equalsIgnoreCase(from) &&
                !PrizewheelsConstant.RedpacketGotWays.FIXED_REDPACKET_5.equalsIgnoreCase(from) &&
                !PrizewheelsConstant.RedpacketGotWays.FIXED_REDPACKET_10.equalsIgnoreCase(from) &&
                !PrizewheelsConstant.RedpacketGotWays.FIXED_REDPACKET_100.equalsIgnoreCase(from) &&
                !PrizewheelsConstant.RedpacketGotWays.RANDOM_REDPACKET.equalsIgnoreCase(from) &&
                !PrizewheelsConstant.RedpacketGotWays.INVITE.equalsIgnoreCase(from) &&
                !PrizewheelsConstant.RedpacketGotWays.SHARE.equalsIgnoreCase(from) &&
                !PrizewheelsConstant.RedpacketGotWays.DOUBLE_REWARD_REDPACKET.equalsIgnoreCase(from)) {

            throw new CustomException("参数错误");
        }

        // 根据from查询用户待领取的红包
        Wrapper<PrizewheelsRedpacketRecord> wrapper = this.getLastPendingRedpacketWrapper(userId, from);
        PrizewheelsRedpacketRecord redpacketRecord = getLastPendingRedpacketRecord(wrapper);
        PrizewheelsAccount account = null;
        if (redpacketRecord == null) {
            log.info("-->转盘-->用户{}没有可放入余额的待领取红包, from:{}，直接返回实时余额.", userId, from);
            account = prizewheelsAccountService.selectById(userId);
        } else {
            log.info("-->转盘-->用户{}有待领取的红包，金额：{}", userId, redpacketRecord.getIncreaseMoney());
            account = this.addRedpacket(redpacketRecord, userId, from);
            // 将奖品的状态置为已领取
            if (StringUtils.isNotEmpty(redpacketRecord.getPrizeRecordId())) {
                PrizewheelsPrizeRecord prizeRecordForUpdate = new PrizewheelsPrizeRecord();
                prizeRecordForUpdate.setId(redpacketRecord.getPrizeRecordId());
                prizeRecordForUpdate.setStatus(PrizewheelsConstant.GotStatus.GOT);
                prizewheelsPrizeRecordService.updateById(prizeRecordForUpdate);
                log.info("-->转盘-->放入我的余额-->将用户{}奖品的状态置为已领取，奖品记录ID: {}",
                        userId, redpacketRecord.getPrizeRecordId());
            }

            if (PrizewheelsConstant.RedpacketGotWays.DOUBLE_REWARD_REDPACKET.equalsIgnoreCase(from)) {
                // 如果获得方式是DOUBLE_REWARD_REDPACKET
                // 则refPrizeId对应的是关联的10元红包奖励在t_prizewheels_redpacket_record表的ID，
                // 其它情况一率为t_prizewheels_prize_record表中ID
                String refPrizeId = redpacketRecord.getPrizeRecordId();
                wrapper = new EntityWrapper<PrizewheelsRedpacketRecord>();
                wrapper.eq("ID", refPrizeId);
                redpacketRecord = prizewheelsRedpacketRecordService.selectOne(wrapper);
                if (redpacketRecord != null && PrizewheelsConstant.GotStatus.PENDING.equalsIgnoreCase(redpacketRecord.getStatus())) {
                    log.info("-->转盘-->用户{}有一个关联的10元红包待领取,id:{}，下面开始领取", userId, refPrizeId);
                    account = addRedpacket(redpacketRecord, userId, redpacketRecord.getGotWays());
                }
            }
        }

        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put("coinsBalance", account.getCoinsBalance());
        resultMap.put("redpacketBalance", account.getRedpacketBalance());

        return resultMap;
    }

    /**
     * 增加用户的账户红包余额，并将红包状态改为已领取
     *
     * @param redpacketRecord
     * @param userId
     * @param from
     * @return
     */
    private PrizewheelsAccount addRedpacket(PrizewheelsRedpacketRecord redpacketRecord, Long userId, String from) {
        String id = redpacketRecord.getId();
        double increaseMoney = redpacketRecord.getIncreaseMoney();

        PrizewheelsAccount account = prizewheelsAccountService.updateRedpacketBalance(userId, increaseMoney, true);
        log.info("-->转盘-->放入我的余额-->更新用户{}红包余额，更新后实时余额为：{}", userId, account.getRedpacketBalance());
        PrizewheelsRedpacketRecord recordForUpdate = new PrizewheelsRedpacketRecord();
        recordForUpdate.setId(id);
        recordForUpdate.setStatus(PrizewheelsConstant.GotStatus.GOT);
        recordForUpdate.setBlanceRedpacket(account.getRedpacketBalance());
        recordForUpdate.setUpdateTime(new Date());
        prizewheelsRedpacketRecordService.updateById(recordForUpdate);
        log.info("-->转盘-->放入我的余额-->更新用户{}的{}类型红包记录状态为已领取", userId, from);

        // 处理翻倍红包
        if (PrizewheelsConstant.RedpacketGotWays.DOUBLE_REWARD_REDPACKET.equalsIgnoreCase(from)) {
            account = new PrizewheelsAccount();
            account.setUserId(account.getUserId());
            account.setTenPrize(PrizewheelsConstant.DoubleReward.GOT_10_YUAN);
            account.setUpdateTime(new Date());
            prizewheelsAccountService.updateById(account);
            log.info("-->转盘-->等待用户{}关注公众号", userId);
        }

        return account;
    }

    private PrizewheelsCoinsRecord getLastPendingCoinsRecord(Wrapper<PrizewheelsCoinsRecord> wrapper) {
        List<PrizewheelsCoinsRecord> list = prizewheelsCoinsRecordService.selectList(wrapper);
        PrizewheelsCoinsRecord coinsRecord = list.size() > 0 ? list.get(0) : null;
        return coinsRecord;
    }

    private Wrapper<PrizewheelsCoinsRecord> getLastPendingCoinsRecordWrapper(Long userId, String from) {
        Wrapper<PrizewheelsCoinsRecord> wrapper = new EntityWrapper<PrizewheelsCoinsRecord>();
        wrapper.eq("USER_ID", userId);
        wrapper.eq("GOT_WAYS", from);
        wrapper.eq("STATUS", PrizewheelsConstant.GotStatus.PENDING);
        Collection<String> columns = new ArrayList<String>();
        columns.add("UPDATE_TIME");
        wrapper.orderDesc(columns);
        return wrapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> addCoinsBalance(String from) {

        long userId = SecurityUserUtil.getUserDetails().getUserId();

        log.info("-->转盘买买币-->用户{}点击放入我的余额，from:{}", userId, from);

        if (!PrizewheelsConstant.CoinsGotWays.COINS_BAG.equalsIgnoreCase(from) &&
                !PrizewheelsConstant.CoinsGotWays.COINS_BOX.equalsIgnoreCase(from)) {
            throw new CustomException("参数错误");
        }

        // 先从用户的买买币记录表找到了新一条的status为PENDING，并且got_ways为PRIZEWHEELS，
        // 表示转盘抽奖所得，但还没放入到余额里，此时用户点击了放入余额

        Wrapper<PrizewheelsCoinsRecord> wrapper = this.getLastPendingCoinsRecordWrapper(userId, from);
        PrizewheelsCoinsRecord coinsRecord = this.getLastPendingCoinsRecord(wrapper);
        PrizewheelsAccount account = null;
        if (coinsRecord == null) {
            log.info("-->转盘-->用户{}没有可放入余额的待领取买买币, from:{}，直接返回实时余额.", userId, from);
            account = prizewheelsAccountService.selectById(userId);
        } else {
            int increaseCoins = coinsRecord.getIncreaseCoins();
            String prizeRecordId = coinsRecord.getPrizeRecordId();

            account = prizewheelsAccountService.updateCoinsBalance(userId, increaseCoins, true);
            log.info("-->转盘-->买买币放入我的余额-->更新用户{}买买币余额，更新后实时余额为：{}", userId, account.getCoinsBalance());
            String id = coinsRecord.getId();
            coinsRecord = new PrizewheelsCoinsRecord();
            coinsRecord.setId(id);
            coinsRecord.setStatus(PrizewheelsConstant.GotStatus.GOT);
            coinsRecord.setBlanceCoins(account.getCoinsBalance());
            coinsRecord.setUpdateTime(new Date());
            prizewheelsCoinsRecordService.updateById(coinsRecord);
            log.info("-->转盘-->买买币放入我的余额-->更新用户{}的买买币记录为已领取", userId);

            // 如果该买买币是通过抽奖获得，则需要将奖品的状态置为已领取
            if (StringUtils.isNotEmpty(prizeRecordId)) {
                PrizewheelsPrizeRecord prizeRecord = new PrizewheelsPrizeRecord();
                prizeRecord.setId(prizeRecordId);
                prizeRecord.setStatus(PrizewheelsConstant.GotStatus.GOT);
                prizeRecord.setUpdateTime(new Date());
                prizewheelsPrizeRecordService.updateById(prizeRecord);
                log.info("-->转盘-->买买币放入我的余额-->更新用户{}奖品的状态置为已领取，奖品记录ID: {}", userId, prizeRecordId);
            }
        }

        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put("coinsBalance", account.getCoinsBalance());
        resultMap.put("redpacketBalance", account.getRedpacketBalance());

        return resultMap;
    }

    @Override
    public Map<String, Object> sign() {

        // 如果当天有一条【新人】领取买买币的记录，则表示当天已签到（转盘新用户进来直接就领了买买币，不用签到）
        // 否则就查询当天的签到记录，如果有则表示当天已签到

        long userId = SecurityUserUtil.getUserDetails().getUserId();

        log.info("-->转盘签到-->用户{}点击签到", userId);

        PrizewheelsAccount returnAccount = null;

        boolean currentDayHasSign = prizewheelsCoinsRecordService.queryUserCurrentDayHasSign(userId);
        if (!currentDayHasSign) {
            // 当前可签到，签到可获得买买币，且直接入账
            log.info("-->转盘签到-->用户{}今天没有签过到，开始签到...", userId);

            /******************* 更新账户中的买买币余额 **********************/
            PrizewheelsTemplate template = prizewheelsTemplateService.load();
            returnAccount = prizewheelsAccountService.updateCoinsBalance(userId, template.getSignGetCoinsAmount(), true);

            /******************* 保存买买币增加记录 **********************/
            PrizewheelsCoinsRecord coinsRecord = getPrizewheelsCoinsRecordInitBean(userId);
            coinsRecord.setIncreaseCoins(template.getSignGetCoinsAmount());
            coinsRecord.setBlanceCoins(returnAccount.getCoinsBalance()); // 记录实时余额
            coinsRecord.setGotWays(PrizewheelsConstant.CoinsGotWays.SIGN);
            coinsRecord.setStatus(PrizewheelsConstant.GotStatus.GOT);
            prizewheelsCoinsRecordService.insert(coinsRecord);
            log.info("-->转盘签到-->用户{}保存买买币增加记录，增加买买币：{}，获得方式：{}.",
                    userId, template.getSignGetCoinsAmount(), coinsRecord.getGotWays());
        } else {
            log.info("-->转盘签到-->用户已签到，直接返回实时余额", userId);
            returnAccount = prizewheelsAccountService.selectById(userId);
        }

        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put("coinsBalance", returnAccount.getCoinsBalance());
        resultMap.put("redpacketBalance", returnAccount.getRedpacketBalance());

        return resultMap;
    }

    @Override
    public Map<String, Object> prepareWithdraw() {

        JwtUserDetails user = SecurityUserUtil.getUserDetails();
        long userId = user.getUserId();

        log.info("-->转盘预提现-->用户{}准备提现", userId);

        PrizewheelsAccount account = prizewheelsAccountService.selectById(userId);
        Double redpacketBalance = account.getRedpacketBalance();
        String headimgurl = getAvatarUrl(user);

        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put("redpacketBalance", redpacketBalance);
        resultMap.put("avatarUrl", headimgurl);
        return resultMap;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> withdraw(Double withdrawMoney) {

        JwtUserDetails user = SecurityUserUtil.getUserDetails();
        long userId = user.getUserId();
        log.info("-->转盘-->用户{}进行提现操作", userId);

        PrizewheelsTemplate template = prizewheelsTemplateService.load();
        // 初始提现门槛, 如100元
        double withdrawThresholdMinMoney = template.getWithdrawThresholdMinMoney();
        // 提现门槛最高值, 如400
        double withdrawThresholdMaxMoney = template.getWithdrawThresholdMaxMoney();

        // 计算此次提现的门槛
        Double thresholdMoney = 0d;
        Wrapper<PrizewheelsWithdrawRecord> wrapper = new EntityWrapper<PrizewheelsWithdrawRecord>();
        wrapper.eq("USER_ID", userId);
        int recordCount = prizewheelsWithdrawRecordService.selectCount(wrapper);
        if (recordCount == 0) {
            thresholdMoney = withdrawThresholdMinMoney;
            log.info("-->转盘提现-->用户{}当前没有提现记录，使用的提现门槛为{}元", userId, withdrawThresholdMinMoney);
        } else {
            thresholdMoney = withdrawThresholdMinMoney * Math.pow(2, recordCount);
            log.info("-->转盘提现-->用户{}已有过提现记录，提现次数：{}次，此次计算的提现门槛为：{}元",
                    userId, recordCount, thresholdMoney);
            if (thresholdMoney > withdrawThresholdMaxMoney) {
                thresholdMoney = withdrawThresholdMaxMoney;
                log.info("-->转盘提现-->计算出的门槛{}元大于最大门槛{}元，此次使用最大门槛.",
                        thresholdMoney, withdrawThresholdMaxMoney);
            }
        }

        // 前端传过来的不能是三位小数，只能允许整数，以及一位和两位小数，方便转换为[分]的单位，此处获取小数点后的数字长度进行判断
        String temp = String.valueOf(withdrawMoney);
        int length = temp.substring(temp.indexOf(".") + 1, temp.length()).length();
        if (length > 2) {
            throw new CustomException("提现金额不能为三位小数");
        }
        // 获取用户的余额
        PrizewheelsAccount account = prizewheelsAccountService.selectById(userId);
        if (account == null) {
            throw new CustomException("用户数据异常");
        }

        if (account.getRedpacketBalance() < withdrawMoney) {
            log.info("-->转盘提现-->用户{}提现金额{}元大于当前余额{}元，余额不足无法提现",
                    userId, withdrawMoney, account.getRedpacketBalance());
            throw new CustomException("余额不足不可以提现哦");
        }

        if (account.getRedpacketBalance() < thresholdMoney) {
            log.info("-->转盘提现-->用户{}余额{}元小于提现门槛{}元，无法提现",
                    userId, withdrawMoney, thresholdMoney);
            String thresholdMoneyStr = String.valueOf(thresholdMoney);
            if (thresholdMoneyStr.endsWith(".0")) {
                thresholdMoneyStr = thresholdMoneyStr.substring(0, thresholdMoneyStr.indexOf(".0"));
            }
            throw new CustomException("满" + thresholdMoneyStr + "元才可以提现哦");
        }


        // 到此处校验通过，允许提现

        Map<String, Object> result = new HashMap<String, Object>();

        log.info("-->转盘提现-->校验通过，用户{}可以进行提现操作，提现：{}", userId, withdrawMoney);

        // 封装发送零钱的参数
        WxpayTransfers transfer = new WxpayTransfers();
        transfer.setMchAppid(user.getAppId());
        transfer.setOpenid(user.getOpenId());
        double withdrawActualMoney = withdrawMoney;
        transfer.setAmount((int) (withdrawActualMoney * 100)); // 以分为单位 
        transfer.setDesc("买买家转盘余额提现");
        String tradeNo = CommonUtil.getRandomUUID();
        transfer.setPartnerTradeNo(tradeNo);

        // 发送零钱
        WxpayTransfers transferResult = null;
        try {
            transferResult = wxpayTransfersService.transfers(transfer).getData();
            int successState = 0;
            if (transferResult.getState() != successState) {
                log.error("-->用户{}提现失败，金额:{}元", userId, withdrawMoney);
                throw new CustomException("系统出了点问题，请联系客服");
            }
        } catch (Exception e) {
            log.error("-->调用提现接口发生错误，用户{}提现失败，金额:{}元，错误信息：", userId, withdrawMoney, e.getMessage());
            throw new CustomException("系统出了点问题，请联系客服");
        }

        // 表明提现成功
        log.info("-->转盘提现-->用户{}提现成功，提现金额：{}元", userId, withdrawMoney);
        /****************************** 修改余额 ******************************/
        // 提现前的红包余额
        double lastBalance = account.getRedpacketBalance();
        // 修改余额
        account = prizewheelsAccountService.updateRedpacketBalance(userId, withdrawMoney, false);
        // 减去提现后的红包余额
        Double realtimeBalance = account.getRedpacketBalance();

        // 保存提现记录
        PrizewheelsWithdrawRecord record = new PrizewheelsWithdrawRecord();
        record.setUserId(userId);
        record.setNickname(getNickname(user));
        record.setCreateTime(new Date());
        record.setLastBalance(lastBalance);
        record.setRealtimeBalance(realtimeBalance);
        record.setWithdrawMoney(withdrawMoney);
        record.setTradeNo(tradeNo);
        prizewheelsWithdrawRecordService.insert(record);
        log.info("-->转盘提现-->保存提现记录， userId:{}, 提现:{}元", userId, withdrawMoney);

        // 保存红包变动记录
        PrizewheelsRedpacketRecord redpacketRecord = getPrizewheelsRedpacketRecordInitBean();
        redpacketRecord.setUserId(userId);
        redpacketRecord.setBlanceRedpacket(realtimeBalance);
        redpacketRecord.setGotWays(PrizewheelsConstant.RedpacketGotWays.WITHDRAW);
        redpacketRecord.setIncreaseMoney(-withdrawMoney);
        redpacketRecord.setStatus(PrizewheelsConstant.GotStatus.WITHDRAW);
        prizewheelsRedpacketRecordService.insert(redpacketRecord);
        log.info("-->转盘提现-->保存余额变动记录，userId:{}, 变动数：{}元", userId, withdrawMoney);

        result.put("coinsBalance", account.getCoinsBalance());
        result.put("redpacketBalance", account.getRedpacketBalance());
        return result;
    }
    
    private boolean isFirstPrizeDraw(long userId) {
    	boolean isFirstPrizeDraw = false;
    	String cacheKey = "PRIZEWHEELS:ISFIRST:" + userId;
    	String cacheValue = redisTemplate.opsForValue().get(cacheKey);
    	if(StringUtils.isNotBlank(cacheValue)) {
    		isFirstPrizeDraw = Boolean.valueOf(cacheValue);
    	} else {
    		Wrapper<PrizewheelsCoinsRecord> wrapper = new EntityWrapper<PrizewheelsCoinsRecord>();
	        wrapper.eq("USER_ID", userId);
	        wrapper.eq("GOT_WAYS", PrizewheelsConstant.CoinsGotWays.CLICK_DRAW);
	        wrapper.eq("STATUS", PrizewheelsConstant.GotStatus.CONSUMED);
	        int prizeDrawCount = prizewheelsCoinsRecordService.selectCount(wrapper);
	        isFirstPrizeDraw = prizeDrawCount < 1 ? true : isFirstPrizeDraw;
	        redisTemplate.opsForValue().set(cacheKey, String.valueOf(isFirstPrizeDraw), 7, TimeUnit.DAYS);
    	}
    	return isFirstPrizeDraw;
    }
    
    private void updateUserFirstPrizeDrawCache(long userId) {
    	String cacheKey = "PRIZEWHEELS:ISFIRST:" + userId;
    	redisTemplate.opsForValue().set(cacheKey, String.valueOf(false), 7, TimeUnit.DAYS);
    }

    @Override
    @Transactional
    public Map<String, Object> prizeDraw() {

        long userId = SecurityUserUtil.getUserDetails().getUserId();

        log.info("-->转盘-->用户{}点击抽奖", userId);

        // 检查当前买买币是否充足
        PrizewheelsTemplate template = prizewheelsTemplateService.load();
        PrizewheelsAccount account = prizewheelsAccountService.selectById(userId);
        if (account == null || account.getCoinsBalance() == null || account.getCoinsBalance() < template.getConsumingCoinsAmount()) {
            log.error("-->转盘抽奖-->用户{}买买币不足", userId);//此提示信息由前端自己声明显示
            throw new CustomMessageException(SecurityConstants.FAIL_CODE, "买买币余额不足");
        }


        // 判断是否首次参与转盘抽奖
        // 抽奖获取奖品，用户首次抽奖获得10元固定红包，以后根据概率获取奖品
        boolean isFirstPrizeDraw = isFirstPrizeDraw(userId);

        // 获取转盘抽奖区的所有奖品
        List<PrizewheelsPrizeType> prizeList = prizewheelsPrizeTypeService.loadAllPrize();
        // 根据余额获取区间概率
        List<PrizewheelsPrizeProbability> prizeProbabilityList = prizewheelsPrizeProbabilityService.loadPrizeRangeList(account.getRedpacketBalance());
        // 得到此次抽到的奖品
        PrizewheelsPrizeType prize = PrizewheelsUtil.getRandomPrize(prizeList, prizeProbabilityList, isFirstPrizeDraw);
        log.info("-->转盘抽奖-->用户{}抽到的奖品为:{}, {}, 概率:{}", userId, prize.getPrizeCode(), prize.getPrizeName(), prize.getProbability());

        if (PrizewheelsConstant.PrizeCode.RANDOM_REDPACKET.equalsIgnoreCase(prize.getPrizeCode())) {
            prize.setAmount(PrizewheelsUtil.getRandomMoney(prize.getRandomRedpacketRange()));
            log.info("-->转盘抽奖-->用户{}抽到随机红包，随机金额为：{}", userId, prize.getAmount());
        } else if (PrizewheelsConstant.PrizeCode.FIXED_REDPACKET_10.equalsIgnoreCase(prize.getPrizeCode())) {
            // 此时获得额外红包奖励， 但是需要关注公众号
            if (account.getTenPrize() != null && account.getTenPrize() == PrizewheelsConstant.DoubleReward.INIT) {
                account.setTenPrize(PrizewheelsConstant.DoubleReward.GOT_10_YUAN);
                PrizewheelsAccount accountForUpdate = new PrizewheelsAccount();
                accountForUpdate.setUserId(account.getUserId());
                accountForUpdate.setTenPrize(PrizewheelsConstant.DoubleReward.GOT_10_YUAN);
                prizewheelsAccountService.updateById(accountForUpdate);
                log.info("-->转盘抽奖-->用户{}因抽到10元红包，等待其关注公众号", userId);
            }
        }

        // 待返回的信息
        String prizeName = prize.getPrizeName();
        String couponName = null;
        String couponInvalidTime = null;
        String couponCode = null;
        String couponUrl = null;

        // 待保存的获取奖励的记录
        PrizewheelsPrizeRecord prizeRecord = getPrizewheelsPrizeRecordInitBean();
        prizeRecord.setUserId(userId);
        prizeRecord.setPrizeName(prize.getPrizeName());
        prizeRecord.setPrizeCode(prize.getPrizeCode());
        prizeRecord.setPrizeType(prize.getPrizeType());
        prizeRecord.setIconUrl(prize.getSmallIconUrl());

        if (PrizewheelsConstant.PrizeType.COUPON.equalsIgnoreCase(prize.getPrizeType())) {
            //	record.setIncreaseAmount(prize.getAmount());// 注意优惠券不需设置此值
            // 奖品为优惠券，需要取出优惠券模版ID，并给用户发送优惠券，以及返回过期时间
            Integer couponTemplateid = prize.getCouponTemplateid();
            if (couponTemplateid != null) {
                JwtUserDetails user = SecurityUserUtil.getUserDetails();
                // 处理抽到优惠券的逻辑
                UserCouponVo vo = new UserCouponVo();
                vo.setCouponId(couponTemplateid);
                vo.setCouponSource(CouponConstants.CouponSource.PRIZEWHEELS);
                vo.setUserId(user.getUserId());
                UserReceiveCouponDto resultCoupon = couponUserFeignClient.receive(vo).getData();
                if (resultCoupon == null) {
                    throw new CustomException("抽到优惠券，但系统发生了点小错误，请联系客服");
                }
                couponCode = resultCoupon.getUserCoupon().getCouponCode().toString();
                CouponInfo coupon = couponInfoService.selectById(couponTemplateid);
                if (coupon != null) {
                    couponName = coupon.getCouponTitle();
                    prizeName = "【" + couponName + "】";
                    couponInvalidTime = DateUtils.SDF9.format(resultCoupon.getUserCoupon().getEndTime());
                    couponUrl = coupon.getHrafArg();
                    log.info("-->prizeDraw-->抽奖-->抽到优惠券，优惠券编码：{}，优惠券名称：{}，失效时间：{}，使用地址:{}",
                            resultCoupon.getUserCoupon().getCouponCode(), couponName, couponInvalidTime, couponUrl);
                    prizeRecord.setPrizeName(couponName);
                    prizeRecord.setCouponCode(couponCode);
                    prizeRecord.setCouponUrl(couponUrl);
                    prizeRecord.setInvalidTime(resultCoupon.getUserCoupon().getEndTime());
                    prizeRecord.setStatus(PrizewheelsConstant.GotStatus.GOT);
                } else {
                    // 运营误配置了限量的优惠券，且发放时余量不足，导致用户没有领到优惠券
                    prizeRecord.setStatus(PrizewheelsConstant.GotStatus.PENDING);
                }
            }
        } else {
            prizeRecord.setIncreaseAmount(prize.getAmount());
            // 奖品是红包或买买币都需要用户手动再点击放入才可真正领取
            prizeRecord.setStatus(PrizewheelsConstant.GotStatus.PENDING);
        }

        prizewheelsPrizeRecordService.insert(prizeRecord);
        log.info("-->转盘抽奖-->保存用户{}的奖品记录", userId);

        log.info("-->转盘抽奖-->增加买买金记录，userId:{}", userId);
        mqProducer.addMMKing(userId, MMKingShareType.WHEELS);

        if (PrizewheelsConstant.PrizeType.REDPACKET.equalsIgnoreCase(prize.getPrizeType())) {
            /******************* 保存买买币变动记录 **********************/
            PrizewheelsCoinsRecord coinsRecord = getPrizewheelsCoinsRecordInitBean(userId);
            coinsRecord.setIncreaseCoins(-template.getConsumingCoinsAmount());
            coinsRecord.setBlanceCoins(account.getCoinsBalance() - template.getConsumingCoinsAmount());
            coinsRecord.setGotWays(PrizewheelsConstant.CoinsGotWays.CLICK_DRAW);
            coinsRecord.setStatus(PrizewheelsConstant.GotStatus.CONSUMED);
            prizewheelsCoinsRecordService.insert(coinsRecord);
            log.info("-->转盘抽奖-->保存新用户{}买买币消耗记录，变动数为：{}.",
                    userId, template.getConsumingCoinsAmount());

            /******************** 保存红包增加记录 ***********************/
            PrizewheelsRedpacketRecord redpacketRecord = this.getPrizewheelsRedpacketRecordInitBean();
            redpacketRecord.setUserId(userId);
            redpacketRecord.setIncreaseMoney(prize.getAmount());
            redpacketRecord.setPrizeRecordId(prizeRecord.getId());
            if (PrizewheelsConstant.PrizeCode.FIXED_REDPACKET_5.equalsIgnoreCase(prize.getPrizeCode()) ||
                    PrizewheelsConstant.PrizeCode.FIXED_REDPACKET_10.equals(prize.getPrizeCode()) ||
                    PrizewheelsConstant.PrizeCode.FIXED_REDPACKET_100.equals(prize.getPrizeCode()) ||
                    PrizewheelsConstant.PrizeCode.RANDOM_REDPACKET.equals(prize.getPrizeCode())) {
                redpacketRecord.setGotWays(prize.getPrizeCode());
            }
            // 红包状态为待领取，用户必须手动点击"放入我的余额"才可变为已领取
            redpacketRecord.setStatus(PrizewheelsConstant.GotStatus.PENDING);
            prizewheelsRedpacketRecordService.insert(redpacketRecord);
            log.info("-->转盘抽奖-->保存用户{}红包增加记录，变动数为：{}.",
                    userId, prize.getAmount());
        } else if (PrizewheelsConstant.PrizeType.COINS.equalsIgnoreCase(prize.getPrizeType())) {

            /******************* 保存买买币变动记录：包含消耗的，以及抽到的，共2条记录 **********************/
            int coinsBalance = account.getCoinsBalance() - template.getConsumingCoinsAmount();
            PrizewheelsCoinsRecord coinsRecord = getPrizewheelsCoinsRecordInitBean(userId);
            coinsRecord.setIncreaseCoins(-template.getConsumingCoinsAmount());
            coinsRecord.setBlanceCoins(coinsBalance);
            coinsRecord.setGotWays(PrizewheelsConstant.CoinsGotWays.CLICK_DRAW);
            coinsRecord.setStatus(PrizewheelsConstant.GotStatus.CONSUMED);
            prizewheelsCoinsRecordService.insert(coinsRecord);
            log.info("-->转盘抽奖-->保存用户{}买买币消耗记录，变动数为：{}.",
                    userId, template.getConsumingCoinsAmount());

            coinsRecord = getPrizewheelsCoinsRecordInitBean(userId);
            coinsRecord.setIncreaseCoins(prize.getAmount().intValue());
            // 抽到的买买币需点击放入才会增加余额, 此时为待领取, 不用保存实时余额记录
//			int coinsBalance2 = coinsBalance1 + prize.getAmount().intValue();
//			coinsRecord.setBlanceCoins(coinsBalance2);
            coinsRecord.setGotWays(prize.getPrizeCode());
            // 抽到的买买币为待领取
            coinsRecord.setStatus(PrizewheelsConstant.GotStatus.PENDING);
            coinsRecord.setPrizeCode(prize.getPrizeCode());
            coinsRecord.setPrizeName(prize.getPrizeName());
            coinsRecord.setPrizeRecordId(prizeRecord.getId());
            prizewheelsCoinsRecordService.insert(coinsRecord);
            log.info("-->转盘抽奖-->保存用户{}买买币增加记录，变动数为：{}.",
                    userId, prize.getAmount().intValue());

            prizeName = "【" + prizeName + "】";
        } else {
            // 奖品为优惠券

            /******************* 保存买买币变动记录 **********************/
            PrizewheelsCoinsRecord coinsRecord = getPrizewheelsCoinsRecordInitBean(userId);
            coinsRecord.setIncreaseCoins(-template.getConsumingCoinsAmount());
            coinsRecord.setBlanceCoins(account.getCoinsBalance() - template.getConsumingCoinsAmount());
            coinsRecord.setGotWays(PrizewheelsConstant.CoinsGotWays.CLICK_DRAW);
            coinsRecord.setStatus(PrizewheelsConstant.GotStatus.CONSUMED);
            prizewheelsCoinsRecordService.insert(coinsRecord);
            log.info("-->转盘抽奖-->保存用户{}买买币消耗记录，变动数为：{}.",
                    userId, template.getConsumingCoinsAmount());
        }
        // 减去消耗的金币
        account = prizewheelsAccountService.updateCoinsBalance(userId, template.getConsumingCoinsAmount(), false);

        String amountStr = !PrizewheelsConstant.PrizeType.COUPON.equalsIgnoreCase(prize.getPrizeType()) ? String.valueOf(prize.getAmount()) : "";
        if (amountStr.endsWith(".0")) {
            amountStr = amountStr.substring(0, amountStr.indexOf(".0"));
        }
        
        if(isFirstPrizeDraw) {
        	//如果是第一次抽奖，此时已经完成了第一次抽奖，所以需要更新缓存，下次再抽奖时从缓存读的就是false了
        	updateUserFirstPrizeDrawCache(userId);
        }

        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put("prizeName", prizeName);
        resultMap.put("prizeCode", prize.getPrizeCode());
        resultMap.put("prizeType", prize.getPrizeType());
        resultMap.put("amount", amountStr);
        resultMap.put("coinsBalance", account.getCoinsBalance());
        resultMap.put("redpacketBalance", account.getRedpacketBalance());
        resultMap.put("gotPrizeTime", DateUtils.SDF9.format(prizeRecord.getCreateTime()));
        resultMap.put("consumeCoins", template.getConsumingCoinsAmount());
        resultMap.put("couponName", couponName);
        resultMap.put("couponInvalidTime", couponInvalidTime);
        resultMap.put("couponCode", couponCode);
        resultMap.put("couponUrl", couponUrl);
        resultMap.put("iconUrl", prize.getSmallIconUrl());

        return resultMap;
    }

    @Override
    public List<MyPrizeDto> getMyPrizes() {

        long userId = SecurityUserUtil.getUserDetails().getUserId();

        log.info("-->转盘-->用户{}获取已经领取到手的奖品记录", userId);

        // 要返回的list
        List<MyPrizeDto> resultList = new ArrayList<MyPrizeDto>();

        Wrapper<PrizewheelsPrizeRecord> wrapper = new EntityWrapper<PrizewheelsPrizeRecord>();
        wrapper.eq("USER_ID", userId);
        wrapper.eq("STATUS", PrizewheelsConstant.GotStatus.GOT);
        Collection<String> columns = new ArrayList<String>();
        columns.add("UPDATE_TIME");
        wrapper.orderDesc(columns);
        List<PrizewheelsPrizeRecord> myPrizes = prizewheelsPrizeRecordService.selectList(wrapper);
        if (myPrizes.isEmpty()) {
            log.info("-->转盘-->获取用户{}奖品数据为空 ", userId);
            return resultList;
        }

        MyPrizeDto dto = null;
        String amount = null;
        for (PrizewheelsPrizeRecord record : myPrizes) {
            dto = new MyPrizeDto();
            dto.setGotPrizeTime(DateUtils.SDF9.format(record.getUpdateTime()));
            dto.setPrizeName(record.getPrizeName());
            if (!PrizewheelsConstant.PrizeType.COUPON.equalsIgnoreCase(record.getPrizeType())) {
                amount = String.valueOf(record.getIncreaseAmount());
                if (amount.endsWith(".0")) {
                    amount = amount.substring(0, amount.indexOf(".0"));
                }
            }
            dto.setAmount(amount);
            dto.setPrizeType(record.getPrizeType());
            dto.setIconUrl(record.getIconUrl());
            resultList.add(dto);

            // 重置amount
            amount = null;
        }
        return resultList;
    }

    @Override
    public List<MyCoinsChangeDetail> getMyCoinsDetail() {
        long userId = SecurityUserUtil.getUserDetails().getUserId();
        log.info("-->转盘-->用户{}获取买买币明细", userId);
        Wrapper<PrizewheelsCoinsRecord> wrapper = new EntityWrapper<PrizewheelsCoinsRecord>();
        wrapper.eq("USER_ID", userId);
        Collection<String> columns = new ArrayList<String>();
        columns.add("UPDATE_TIME");
        wrapper.orderDesc(columns);
        wrapper.last("LIMIT 200");
        List<PrizewheelsCoinsRecord> list = prizewheelsCoinsRecordService.selectList(wrapper);
        List<MyCoinsChangeDetail> detailList = new ArrayList<MyCoinsChangeDetail>();
        if (!list.isEmpty()) {
            String gotWay = null;
            String amount = null;
            MyCoinsChangeDetail detail = null;
            for (PrizewheelsCoinsRecord record : list) {
                detail = new MyCoinsChangeDetail();
                if (PrizewheelsConstant.CoinsGotWays.NEW_USER.equalsIgnoreCase(record.getGotWays()) ||
                        PrizewheelsConstant.CoinsGotWays.SIGN.equalsIgnoreCase(record.getGotWays())) {
                    gotWay = "签到获得";
                } else if (PrizewheelsConstant.CoinsGotWays.COINS_BAG.equalsIgnoreCase(record.getGotWays())
                        || PrizewheelsConstant.CoinsGotWays.COINS_BOX.equalsIgnoreCase(record.getGotWays())) {
                    gotWay = record.getPrizeName();
                } else if (PrizewheelsConstant.CoinsGotWays.CLICK_DRAW.equalsIgnoreCase(record.getGotWays())) {
                    gotWay = "转盘消耗";
                } else {
                    gotWay = "任务获得";
                }
                detail.setGotWay(gotWay);
                detail.setGotTime(DateUtils.SDF9.format(record.getUpdateTime()));
                detail.setCoinsBalance(record.getBlanceCoins());
                if (record.getIncreaseCoins() > 0) {
                    amount = "+" + record.getIncreaseCoins();
                } else {
                    amount = String.valueOf(record.getIncreaseCoins());
                }
                detail.setIncreaseAmount(amount);
                detailList.add(detail);
            }
        }
        return detailList;
    }

    @Override
    public Map<String, Object> getMyTaskData() {
        long userId = SecurityUserUtil.getUserDetails().getUserId();
        log.info("-->转盘-->获取任务数据，用户：{}", userId);

        // 要返回的map
        Map<String, Object> resultMap = new HashMap<String, Object>();

        PrizewheelsTemplate template = prizewheelsTemplateService.load();

        /********************************* 处理任务一数据 *****************************************/

        // 任务一-->签到任务描述
        String signTaskDesc = "每天签到可获得{0}买买币。";
        signTaskDesc = signTaskDesc.replace(CommonConstant.REPLACE_INDEX_0, String.valueOf(template.getSignGetCoinsAmount()));
        resultMap.put("signTaskDesc", signTaskDesc.toString());

        // 任务一-->签到任务完成状态，也对应按钮状态，枚举值： NO_SIGN -未签到，此时按钮显示[去签到]； DONE - 已完成，此时显示[完成]图标
        boolean hasSign = prizewheelsCoinsRecordService.queryUserCurrentDayHasSign(userId);
        String signTaskSatus = hasSign ? PrizewheelsConstant.SignStatus.DONE : PrizewheelsConstant.SignStatus.NO_SIGN;
        resultMap.put("signTaskSatus", signTaskSatus);
        resultMap.put("signCoins", template.getSignGetCoinsAmount());

        /********************************* 处理任务二数据 *****************************************/

        // 任务二-->邀请好友任务描述，其中1、10在数据库表中配置
        String inviteTaskDesc = "邀请好友参与转盘抽奖，每邀请一个好友可获得{0}买买币，每邀请{1}个好友，可获得一个红包。";
        inviteTaskDesc = inviteTaskDesc.replace(CommonConstant.REPLACE_INDEX_0, String.valueOf(template.getInviteFriendGetCoinsAmount())).
                replace(CommonConstant.REPLACE_INDEX_1, String.valueOf(template.getInviteFriendCountForGetRedpacket()));
        resultMap.put("inviteTaskDesc", inviteTaskDesc);

        // 任务二-->邀请好友的当前进度，1/10-->10/10-->10/20-->11/20
        // 任务二-->邀请好友的任务完成状态，也对应按钮状态，是否可领取红包，枚举值：SHARE - 显示分享按钮； REDPACKET - 显示领取红包按钮
        String inviteTaskStatus = PrizewheelsConstant.Task.SHARE; // 默认显示分享按钮

        Wrapper<PrizewheelsCoinsRecord> coinsRecordWrapper = new EntityWrapper<PrizewheelsCoinsRecord>();
        coinsRecordWrapper.eq("USER_ID", userId);
        coinsRecordWrapper.eq("GOT_WAYS", PrizewheelsConstant.CoinsGotWays.INVITE);
        coinsRecordWrapper.eq("STATUS", PrizewheelsConstant.GotStatus.GOT);
        int invitedFriendsCount = prizewheelsCoinsRecordService.selectCount(coinsRecordWrapper);

        boolean hasMarked = false;
        // 例：10/10这种相等的情况，如果没领取红包，则显示10/10，领取过红包，则显示10/20
        int inviteTargetCount = PrizewheelsUtil.getTargetCount(invitedFriendsCount, template.getInviteFriendCountForGetRedpacket(), hasMarked);
        double inviteFriendsRedpacketMoney = 0d;

        PrizewheelsRedpacketRecord redpacketRecord = null;
        Wrapper<PrizewheelsRedpacketRecord> redpacketWrapper = new EntityWrapper<PrizewheelsRedpacketRecord>();
        if (invitedFriendsCount == inviteTargetCount) {
            redpacketWrapper.eq("USER_ID", userId);
            redpacketWrapper.eq("GOT_WAYS", PrizewheelsConstant.RedpacketGotWays.INVITE);
            Collection<String> collection = new ArrayList<String>();
            collection.add("UPDATE_TIME");
            redpacketWrapper.orderDesc(collection);
            List<PrizewheelsRedpacketRecord> redpacketRecordList = prizewheelsRedpacketRecordService.selectList(redpacketWrapper);
            redpacketRecord = redpacketRecordList.size() > 0 ? redpacketRecordList.get(0) : null;
            if (redpacketRecord != null) {
                // 判断红包是否待领取
                if (PrizewheelsConstant.GotStatus.PENDING.equalsIgnoreCase(redpacketRecord.getStatus())) {
                    // 没有领取过，显示领取红包按钮， 目标数不用改
                    inviteTaskStatus = PrizewheelsConstant.Task.REDPACKET;
                    inviteFriendsRedpacketMoney = redpacketRecord.getIncreaseMoney();
                } else {
                    // 已领取过红包，则更改目标数为下一阶段的目标数，inviteTaskStatus还是默认值，不用改
                    hasMarked = true;
                    inviteTargetCount = PrizewheelsUtil.getTargetCount(invitedFriendsCount, template.getInviteFriendCountForGetRedpacket(), hasMarked);
                    //此时还需判断是否还有之前任务阶段未领取的红包
                    redpacketWrapper.eq("STATUS", PrizewheelsConstant.GotStatus.PENDING);
                    redpacketRecordList = prizewheelsRedpacketRecordService.selectList(redpacketWrapper);
                    redpacketRecord = redpacketRecordList.size() > 0 ? redpacketRecordList.get(0) : null;
                    if (redpacketRecord != null) {
                        inviteTaskStatus = PrizewheelsConstant.Task.REDPACKET;
                        inviteFriendsRedpacketMoney = redpacketRecord.getIncreaseMoney();
                    }
                }
            }
        } else {
            if (invitedFriendsCount > template.getInviteFriendCountForGetRedpacket()) {
                //此时需判断是否还有之前任务阶段未领取的红包
                redpacketWrapper.eq("USER_ID", userId);
                redpacketWrapper.eq("GOT_WAYS", PrizewheelsConstant.RedpacketGotWays.INVITE);
                redpacketWrapper.eq("STATUS", PrizewheelsConstant.GotStatus.PENDING);
                Collection<String> collection = new ArrayList<String>();
                collection.add("UPDATE_TIME");
                redpacketWrapper.orderDesc(collection);
                List<PrizewheelsRedpacketRecord> redpacketRecordList = prizewheelsRedpacketRecordService.selectList(redpacketWrapper);
                redpacketRecord = redpacketRecordList.size() > 0 ? redpacketRecordList.get(0) : null;

                if (redpacketRecord != null) {
                    log.info("-->转盘-->获取任务数据-->用户{}还有之前任务阶段未领取的红包:{}", userId, redpacketRecord.getId());
                    inviteTaskStatus = PrizewheelsConstant.Task.REDPACKET;
                    inviteFriendsRedpacketMoney = redpacketRecord.getIncreaseMoney();
                }
            }

        }

        resultMap.put("inviteFriendsProgress", invitedFriendsCount + "/" + inviteTargetCount);
        resultMap.put("inviteTaskStatus", inviteTaskStatus);
        resultMap.put("inviteFriendsRedpacketMoney", inviteFriendsRedpacketMoney);


        /********************************* 处理任务三数据 *****************************************/
        String shareGoodTaskDesc = "分享商品，每有一个好友进入您分享的商品详情页即获得{0}买买币，同一好友只能为您增加一次，每天最多获得{1}买买币，满{2}个好友奖励一个红包。";
        shareGoodTaskDesc = shareGoodTaskDesc.replace(CommonConstant.REPLACE_INDEX_0, String.valueOf(template.getShareGoodsGetCoinsAmount()))
                .replace(CommonConstant.REPLACE_INDEX_1, String.valueOf(template.getShareGoodsGetCoinsAmount() * template.getGetCoinsShareGoodsMaxCount()))
                .replace(CommonConstant.REPLACE_INDEX_2, String.valueOf(template.getShareGoodsCountForGetRedpacket()));
        resultMap.put("shareGoodTaskDesc", shareGoodTaskDesc);

        // 任务三-->分享商品任务状态，也对应按钮状态，枚举值：REDPACKET - 显示领取红包按钮；PROGRESS - 按钮显示当前进度，如1/10，即shareGoodProgress
        // 当天不领就相当于放弃了，第二天任务状态重置
        String shareGoodTaskStatus = PrizewheelsConstant.Task.PROGRESS;// 默认显示当前进度

        coinsRecordWrapper = new EntityWrapper<PrizewheelsCoinsRecord>();
        coinsRecordWrapper.eq("USER_ID", userId);
        coinsRecordWrapper.eq("GOT_WAYS", PrizewheelsConstant.CoinsGotWays.SHARE_GOODS);
        coinsRecordWrapper.eq("STATUS", PrizewheelsConstant.GotStatus.GOT);
        Date now = new Date();
        String nowStr = DateUtils.SDF10.format(now);
        coinsRecordWrapper.le("CREATE_TIME", nowStr + " 23:59:59"); //小于等于
        coinsRecordWrapper.ge("CREATE_TIME", nowStr + " 00:00:01"); // 大于等于
        int shareGoodCount = prizewheelsCoinsRecordService.selectCount(coinsRecordWrapper);
        log.info("-->获取任务数据-->用户{}任务三shareGoodCount: {}", userId, shareGoodCount);

        double shareGoodRedpacketMoney = 0d;
        // 例：10/10这种相等的情况，如果没领取红包，则显示10/10，领取过红包，则还是显示10/10，这是和任务二不同的地方
        int shareGoodTargetCount = template.getShareGoodsCountForGetRedpacket();
        if (shareGoodCount == shareGoodTargetCount) {
            log.info("-->获取任务数据--用户{}任务三-->当天分享商品数已达到目标数，shareGoodTargetCount: {}, shareGoodTargetCount: {}",
                    userId, shareGoodCount, shareGoodTargetCount);
            redpacketWrapper = new EntityWrapper<PrizewheelsRedpacketRecord>();
            redpacketWrapper.eq("USER_ID", userId);
            redpacketWrapper.eq("GOT_WAYS", PrizewheelsConstant.RedpacketGotWays.SHARE);
            redpacketWrapper.eq("REACHED_COUNT", shareGoodTargetCount);
            redpacketWrapper.le("CREATE_TIME", nowStr + " 23:59:59"); //小于等于
            redpacketWrapper.ge("CREATE_TIME", nowStr + " 00:00:01"); // 大于等于
            Collection<String> collection = new ArrayList<String>();
            collection.add("UPDATE_TIME");
            List<PrizewheelsRedpacketRecord> redpacketRecordList = prizewheelsRedpacketRecordService.selectList(redpacketWrapper);
            redpacketRecord = redpacketRecordList.size() > 0 ? redpacketRecordList.get(0) : null;
            if (redpacketRecord != null) {
                // 判断红包是否已领取
                if (PrizewheelsConstant.GotStatus.PENDING.equalsIgnoreCase(redpacketRecord.getStatus())) {
                    // 没有领取过，显示领取红包按钮， 目标数不用改
                    shareGoodTaskStatus = PrizewheelsConstant.Task.REDPACKET;
                    shareGoodRedpacketMoney = redpacketRecord.getIncreaseMoney();
                } else {
                    // 只要领取过红包，就算已完成
                    shareGoodTaskStatus = PrizewheelsConstant.Task.DONE;
                }
            }
        }
        // 任务三-->分享进度，商品已分享的数量/分享商品的目标个数
        resultMap.put("shareGoodProgress", shareGoodCount + "/" + shareGoodTargetCount);
        resultMap.put("shareGoodTaskStatus", shareGoodTaskStatus);
        resultMap.put("shareGoodRedpacketMoney", shareGoodRedpacketMoney);


        // 任务四
        String officialAccountsTaskDesc = "去【{0}公众号】回复【{1}】即可获得{2}个买买币，每天限领一次";
        officialAccountsTaskDesc = officialAccountsTaskDesc.replace(CommonConstant.REPLACE_INDEX_0, template.getOfficialAccountName());
        officialAccountsTaskDesc = officialAccountsTaskDesc.replace(CommonConstant.REPLACE_INDEX_1, template.getReplyKeyword());
        officialAccountsTaskDesc = officialAccountsTaskDesc.replace(CommonConstant.REPLACE_INDEX_2, String.valueOf(template.getOfficialAccountReplyGetCoins()));
        String officialAccountsTaskStatus = PrizewheelsConstant.Task.COLLECT;

        coinsRecordWrapper = new EntityWrapper<PrizewheelsCoinsRecord>();
        coinsRecordWrapper.eq("USER_ID", userId);
        coinsRecordWrapper.eq("GOT_WAYS", PrizewheelsConstant.CoinsGotWays.REPLY);
        coinsRecordWrapper.eq("STATUS", PrizewheelsConstant.GotStatus.GOT);
        coinsRecordWrapper.le("CREATE_TIME", nowStr + " 23:59:59"); //小于等于
        coinsRecordWrapper.ge("CREATE_TIME", nowStr + " 00:00:01"); // 大于等于
        int record = prizewheelsCoinsRecordService.selectCount(coinsRecordWrapper);
        if (record > 0) {
            // 表示当天已通过在公众号回复得到过买买币
            officialAccountsTaskStatus = PrizewheelsConstant.Task.DONE;
        }
        resultMap.put("officialAccountsTaskDesc", officialAccountsTaskDesc);
        resultMap.put("officialAccountsTaskStatus", officialAccountsTaskStatus);
        return resultMap;
    }

    @Override
    public Set<String> getWithdrawRecord() {
        Set<String> recordSet = new LinkedHashSet<String>();
        List<WithdrawRecordDto> finalList = new ArrayList<WithdrawRecordDto>();

        // 先根据提现的创建时间取出前40条提现记录
        Wrapper<PrizewheelsWithdrawRecord> wrapper = new EntityWrapper<PrizewheelsWithdrawRecord>();
        Collection<String> collection = new ArrayList<String>();
        collection.add("CREATE_TIME");
        wrapper.orderDesc(collection);
        wrapper.last("LIMIT 40");
        List<PrizewheelsWithdrawRecord> dbRecordList = prizewheelsWithdrawRecordService.selectList(wrapper);
        // 提现时间大于一个星期的不用展示，只返回40条内的，另外加10条假数据

        // 获取10条假数据
        List<WithdrawRecordDto> virtualWithdrawRecordList = null;
        String key = "PRIZEWHEELS:VIRTUAL_DATA";
        String cacheValue = redisTemplate.opsForValue().get(key);
        if (StringUtils.isEmpty(cacheValue)) {
            log.info("-->转盘-->生成虚拟提现记录放入缓存");
            virtualWithdrawRecordList = PrizewheelsUtil.getVirtualWithdrawRecord();
            redisTemplate.opsForValue().set(key, JSONObject.toJSONString(virtualWithdrawRecordList,
                    SerializerFeature.WriteMapNullValue), 7, TimeUnit.DAYS);
        } else {
            log.info("-->转盘-->从缓存获取虚拟提现记录");
            virtualWithdrawRecordList = JSONArray.parseArray(cacheValue, WithdrawRecordDto.class);
            if (virtualWithdrawRecordList.size() > 0) {
                // 缓存中的虚拟数据都是同一天的，如果最后一条的提现时间大于一个星期，则重新生成虚拟数据
                if (PrizewheelsUtil.isExceedOneWeek(virtualWithdrawRecordList.get(9).getCreateTime())) {
                    log.info("-->getWithdrawRecord-->缓存中的虚拟记录已经大于7天，重新生成新的虚拟记录.");
                    virtualWithdrawRecordList = PrizewheelsUtil.getVirtualWithdrawRecord();
                    redisTemplate.opsForValue().set(key, JSONObject.toJSONString(virtualWithdrawRecordList,
                            SerializerFeature.WriteMapNullValue), 7, TimeUnit.DAYS);
                }
            }
        }


        if (dbRecordList == null || dbRecordList.isEmpty()) {
            log.info("-->转盘-->没有真实提现记录, 返回虚拟数据");
            finalList.addAll(virtualWithdrawRecordList);
        } else {
            log.info("-->转盘-->获取真实提现记录条数:{}条", dbRecordList.size());

            List<WithdrawRecordDto> dtoList = new ArrayList<WithdrawRecordDto>();
            WithdrawRecordDto dto = null;

            for (PrizewheelsWithdrawRecord record : dbRecordList) {
                // 提现时间大于一个星期的不用展示
                if (PrizewheelsUtil.isExceedOneWeek(record.getCreateTime())) {
                    continue;
                }
                dto = new WithdrawRecordDto();
                dto.setNickname(record.getNickname());
                dto.setWithdrawMoney(String.valueOf(record.getWithdrawMoney()));
                dto.setCreateTime(record.getCreateTime());
                dtoList.add(dto);
            }

            finalList.addAll(virtualWithdrawRecordList);
            finalList.addAll(dtoList);
        }

        Collections.sort(finalList);
        StringBuilder sb = null;
        for (WithdrawRecordDto dto : finalList) {
            sb = new StringBuilder();
            sb.append(dto.getNickname());
            sb.append(PrizewheelsUtil.getTimeIntervalStr(dto.getCreateTime()));
            sb.append("前提现");
            sb.append(PrizewheelsUtil.getMoneyToShow(dto.getWithdrawMoney()));
            sb.append("元");
            recordSet.add(sb.toString());
        }

        return recordSet;
    }

    @Override
    public Boolean hasParticipateInPrizewheels(Long userId) {
        // 活动是否开启
        boolean isOpen = prizewheelsTemplateService.load().getIsOpen();
        log.info("-->转盘活动是否开启：{}", isOpen);
        if (!isOpen) {
            return false;
        }
        // 查询用户账户表，如果有数据，则表示参与过转盘活动
        PrizewheelsAccount account = prizewheelsAccountService.selectById(userId);
        return account != null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void clickGoodsShare(Long userId, Long shareUserId, Integer goodId) {
        // 分享商品，每有一个好友进入您分享的商品详情页即获得1买买币，同一好友只能为您增加一次，每天最多获得10买买币，满10个好友奖励一个红包。
        // 每有一个带分享者标识的用户进入商品详情页，买买币账户加1，同一个用户每天只能为分享者增加一次买买币，无论进入多少个/次商品详情页。
        // 商品每天只有前10次分享增加买买币，达到次数10可获取红包
        // 同一天最多只能有10位不同的好友点击分享进入才有效并可加买买币
        // 另外当天给同一好友分享多个不同的商品也不算，即当天内用户B只能通过点击商品分享帮助用户A加一次买买币
        // 每天满10个买买币(当天通过分享商品获得的买买币满10个，即10条，当前配置是1个分享得1个买买币)可获得红包，

        log.info("-->转盘-->{}点击好友{}分享的商品{}", userId, shareUserId, goodId);

        // 判断活动是否开启
        PrizewheelsTemplate template = prizewheelsTemplateService.load();
        if (template == null || !template.getIsOpen()) {
            log.info("-->clickGoodsShare-->活动未开启，返回");
            return;
        }

        // 判断当前用户是不是分享人
        if (userId.equals(shareUserId)) {
            log.info("-->clickGoodsShare-->当前用户就是分享人，userId:{}, shareUserId:{}", userId, shareUserId);
            return;
        }

        // 判断分享人有没有参与转盘活动
        if (!this.accountIsExists(shareUserId)) {
            log.info("-->分享人{}没有参与过转盘活动，返回", userId);
            return;
        }
        // 判断商品goodId是否存在
        if (goodService.getById(goodId) == null) {
            log.info("-->转盘-->点击好友分享的商品{}不存在", goodId);
            return;
        }

        Wrapper<PrizewheelsCoinsRecord> coinsWrapper = new EntityWrapper<PrizewheelsCoinsRecord>();
        coinsWrapper.eq("USER_ID", shareUserId);
        coinsWrapper.eq("GOT_WAYS", PrizewheelsConstant.CoinsGotWays.SHARE_GOODS);
        coinsWrapper.eq("STATUS", PrizewheelsConstant.GotStatus.GOT);
        Date now = new Date();
        String nowStr = DateUtils.SDF10.format(now);
        coinsWrapper.le("CREATE_TIME", nowStr + " 23:59:59"); //小于等于
        coinsWrapper.ge("CREATE_TIME", nowStr + " 00:00:01"); // 大于等于
        // 判断分享人当天通过分享商品获得的记录数是不是10条，超过10条则不再加买买币
        int sharedCount = prizewheelsCoinsRecordService.selectCount(coinsWrapper);
        log.info("-->转盘-->用户{}当天通过分享商品获得的记录条数为：{}", shareUserId, sharedCount);
        if (sharedCount >= template.getGetCoinsShareGoodsMaxCount()) {// 此处的10表示10条记录，表示前10次分享，不表示10个买买币
            log.info("-->clickGoodsShare-->分享人{}在当天已经通过分享商品获得了{}个买买币，有{}条买买币获取记录",
                    shareUserId, template.getShareGoodsGetCoinsAmount() * sharedCount, sharedCount);
            return;
        }

        // 判断当前用户在当天有没有点击过该分享人分享的商品
        // 同一个用户每天只能为分享者增加一次买买币，无论进入多少个/次商品详情页
        coinsWrapper.eq("FRIENDS_USER_ID", userId);
        if (prizewheelsCoinsRecordService.selectCount(coinsWrapper) > 0) {
            log.info("-->clickGoodsShare-->当前用户在当天已经点击过该分享人分享的该商品");
            return;
        }

        /******************* 更新分享人账户中的买买币余额  **********************/
        PrizewheelsAccount returnAccount = prizewheelsAccountService.updateCoinsBalance(shareUserId, template.getShareGoodsGetCoinsAmount(), true);
        log.info("-->clickGoodsShare-->分享商品-->用户{}点击分享人{}的分享，分享人增加买买币：{}，余额：{}.",
                userId, shareUserId, template.getShareGoodsGetCoinsAmount(), returnAccount.getCoinsBalance());

        /******************* 保存分享人买买币增加记录 **********************/
        PrizewheelsCoinsRecord coinsRecord = getPrizewheelsCoinsRecordInitBean(shareUserId);
        coinsRecord.setFriendsUserId(userId);
        coinsRecord.setIncreaseCoins(template.getShareGoodsGetCoinsAmount());
        // 记录当前实时买买币余额
        coinsRecord.setBlanceCoins(returnAccount.getCoinsBalance());
        coinsRecord.setGotWays(PrizewheelsConstant.CoinsGotWays.SHARE_GOODS);
        // 分享商品获得的买买币直接入账
        coinsRecord.setStatus(PrizewheelsConstant.GotStatus.GOT);
        coinsRecord.setGoodId(goodId);
        prizewheelsCoinsRecordService.insert(coinsRecord);
        log.info("-->clickGoodsShare-->分享商品-->用户{}点击分享人{}的分享，保存分享人买买币增加记录，增加买买币: {}，获得方式：{}， 实时余额: {}.",
                userId, shareUserId, template.getShareGoodsGetCoinsAmount(), coinsRecord.getGotWays(), returnAccount.getCoinsBalance());

        // 判断当前操作下，分享人当天获得的买买币记录是不是达到了获取红包的10条目标，如果达到了，则给分享人增加获取随机红包的记录，分享人的账户余额不增加
        // 已和唐超确认：达到10个好友奖励一个红包，一天不可以有多个红包
        if ((sharedCount + 1) == template.getShareGoodsCountForGetRedpacket()) {
            /******************** 保存红包增加记录 ***********************/
            PrizewheelsRedpacketRecord redpacketRecord = this.getPrizewheelsRedpacketRecordInitBean();
            redpacketRecord.setUserId(shareUserId);
            Double randomMoney = PrizewheelsUtil.getRandomMoney(template.getRandomRedpacketRange());
            redpacketRecord.setIncreaseMoney(randomMoney);
            redpacketRecord.setGotWays(PrizewheelsConstant.RedpacketGotWays.SHARE);
            // 红包状态为待领取，只要没有领取成功，下次还可以看到该红包
            redpacketRecord.setStatus(PrizewheelsConstant.GotStatus.PENDING);
            redpacketRecord.setReachedCount(template.getShareGoodsCountForGetRedpacket());
            prizewheelsRedpacketRecordService.insert(redpacketRecord);
            log.info("-->clickGoodsShare-->保存分享人{}红包增加记录，状态为待领取，红包金额：{}.",
                    shareUserId, randomMoney);
        }

    }
    
    private boolean isFirstClickPrizewheelsShare(long userId) {
    	boolean isFirstClickPrizewheelsShare = false;
    	String cacheKey = "PRIZEWHEELS:ISFIRSTCLICKSHARE:" + userId;
    	String cacheValue = this.redisTemplate.opsForValue().get(cacheKey);
    	if(StringUtils.isNotBlank(cacheValue)) {
    		isFirstClickPrizewheelsShare = Boolean.valueOf(cacheValue);
    	} else {
    		Wrapper<PrizewheelsCoinsRecord> coinsWrapper = new EntityWrapper<PrizewheelsCoinsRecord>();
            coinsWrapper.eq("FRIENDS_USER_ID", userId);
            coinsWrapper.eq("GOT_WAYS", PrizewheelsConstant.CoinsGotWays.INVITE);
            isFirstClickPrizewheelsShare = prizewheelsCoinsRecordService.selectCount(coinsWrapper) == 0 ? true : false;
            this.redisTemplate.opsForValue().set(cacheKey, String.valueOf(isFirstClickPrizewheelsShare), 1, TimeUnit.DAYS);
    	}
    	return isFirstClickPrizewheelsShare;
    }
    
    private void updateUserFirstClickPrizewheelsShareCache(long userId) {
    	String cacheKey = "PRIZEWHEELS:ISFIRSTCLICKSHARE:" + userId;
    	this.redisTemplate.opsForValue().set(cacheKey, String.valueOf(false), 1, TimeUnit.DAYS);
    }

    
    @Override
    public void preClickFriendShare(Long shareUserId) {
    	PrizewheelsShare entity = new PrizewheelsShare();
		long userId = SecurityUserUtil.getUserDetails().getUserId();
		entity.setShareUserId(shareUserId);
		entity.setUserId(userId);
		try {
			mqProducer.send(MQTopicConstant.TOPIC_PRIZEWHEELS_INVITE, JSONObject.toJSONString(entity));
		} catch (Exception e) {
			log.error("-->点击好友分享的转盘，发送消息时发生错误：", e);
			this.clickFriendShare(userId, shareUserId);
		}
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void clickFriendShare(long userId, long shareUserId) {

        log.info("-->转盘分享-->用户{}点击了{}的转盘分享", userId, shareUserId);

        // 判断活动是否开启
        PrizewheelsTemplate template = prizewheelsTemplateService.load();
        if (template == null || !template.getIsOpen()) {
            log.info("-->转盘分享-->活动未开启，返回");
            return;
        }
        // 判断当前用户是不是分享人
        if (userId == shareUserId) {
            log.info("-->转盘分享-->当前用户就是分享人，返回");
            return;
        }

        // 判断分享人的转盘账户是否存在
        if (!this.accountIsExists(shareUserId)) {
            log.error("-->转盘分享-->分享人{}的转盘账户不存在", shareUserId);
            return;
        }
        
        // 同一个用户只能为一个分享者增加一次买买币，不论是给哪个分享者，也不论哪天
        boolean isFirstClickPrizewheelsShare = this.isFirstClickPrizewheelsShare(userId);
        if (!isFirstClickPrizewheelsShare) {
            log.info("-->转盘分享-->当前用户{}已经点击过好友的转盘分享，返回", userId);
            return;
        }

        /******************* 更新分享人账户中的买买币余额  **********************/
        PrizewheelsAccount returnAccount = prizewheelsAccountService.updateCoinsBalance(shareUserId, template.getInviteFriendGetCoinsAmount(), true);
        log.info("-->转盘分享-->用户{}点击分享人{}的分享，分享人增加买买币：{}，余额：{}.",
                userId, shareUserId, template.getInviteFriendGetCoinsAmount(), returnAccount.getCoinsBalance());

        /******************* 保存分享人买买币增加记录 **********************/
        PrizewheelsCoinsRecord coinsRecord = getPrizewheelsCoinsRecordInitBean(shareUserId);
        coinsRecord.setFriendsUserId(userId);
        coinsRecord.setIncreaseCoins(template.getInviteFriendGetCoinsAmount());
        // 记录当前实时买买币余额
        coinsRecord.setBlanceCoins(returnAccount.getCoinsBalance());
        coinsRecord.setGotWays(PrizewheelsConstant.CoinsGotWays.INVITE);
        // 邀请好友获得的买买币直接入账
        coinsRecord.setStatus(PrizewheelsConstant.GotStatus.GOT);
        prizewheelsCoinsRecordService.insert(coinsRecord);
        log.info("-->clickFriendShare-->邀请好友-->用户{}点击分享人{}的分享，保存分享人买买币增加记录，增加买买币: {}，获得方式：{}， 实时余额: {}.",
                userId, shareUserId, template.getInviteFriendGetCoinsAmount(), coinsRecord.getGotWays(), returnAccount.getCoinsBalance());

        /*************** 查询目前为止分享人通过分享获得的买买币记录 ***********/
        Wrapper<PrizewheelsCoinsRecord> coinsWrapper = new EntityWrapper<PrizewheelsCoinsRecord>();
        coinsWrapper.eq("USER_ID", shareUserId);
        coinsWrapper.eq("GOT_WAYS", PrizewheelsConstant.CoinsGotWays.INVITE);
        coinsWrapper.eq("STATUS", PrizewheelsConstant.GotStatus.GOT);
        int sharedCount = prizewheelsCoinsRecordService.selectCount(coinsWrapper);
        
        log.info("-->转盘分享-->用户{}当前通过分享转盘活动获得的买买币记录条数为：{}条", shareUserId, sharedCount);

        // 已和唐超确认：每满10个好友奖励一个红包，一天可以有多个红包
        if (sharedCount % template.getInviteFriendCountForGetRedpacket() == 0) {

            double randomMoney = 0d;

            // 判断风险
            // 在完成任务给用户发送红包时，判断今天有没有>=1000的人帮其刷活动分享，如果有，设置其得到的红包金额为1分钱，
            Date now = new Date();
            String nowStr = DateUtils.SDF10.format(now);
            coinsWrapper.le("CREATE_TIME", nowStr + " 23:59:59"); //小于等于
            coinsWrapper.ge("CREATE_TIME", nowStr + " 00:00:01"); // 大于等于
            sharedCount = prizewheelsCoinsRecordService.selectCount(coinsWrapper);
            int riskCount = 1000;
            double riskMoney = 0.01;
            if (sharedCount >= riskCount) {
                randomMoney = riskMoney;
                log.info("-->转盘分享-->因当天给{}增加的分享买买币条数达到或超过{}个，故红包分配{}元", shareUserId, riskCount, randomMoney);
            } else {
                randomMoney = PrizewheelsUtil.getRandomMoney(template.getRandomRedpacketRange());
            }
            /******************** 保存红包增加记录 ***********************/
            PrizewheelsRedpacketRecord redpacketRecord = this.getPrizewheelsRedpacketRecordInitBean();
            redpacketRecord.setUserId(shareUserId);
            redpacketRecord.setIncreaseMoney(randomMoney);
            redpacketRecord.setGotWays(PrizewheelsConstant.RedpacketGotWays.INVITE);
            // 红包状态为待领取，只要没有领取成功，下次还可以看到该红包
            redpacketRecord.setStatus(PrizewheelsConstant.GotStatus.PENDING);
            redpacketRecord.setReachedCount(sharedCount);
            prizewheelsRedpacketRecordService.insert(redpacketRecord);
            log.info("-->转盘分享-->保存分享人{}红包增加记录，状态为待领取，红包金额：{}.",
                    shareUserId, randomMoney);
        }
        if(isFirstClickPrizewheelsShare) {
        	updateUserFirstClickPrizewheelsShareCache(userId);
        }

    }
    
    private String getAccountExistsKey(long userId) {
    	return "PRIZEWHEELS:ACCOUNT:EXISTS:" + userId;
    }
    
    private boolean accountIsExists(long userId) {
    	boolean exists = false;
    	String cacheKey = this.getAccountExistsKey(userId);
    	String cacheValue = this.redisTemplate.opsForValue().get(cacheKey);
    	if(StringUtils.isNotBlank(cacheValue)) {
    		exists = Boolean.valueOf(cacheValue);
    	} else {
    		PrizewheelsAccount account = prizewheelsAccountService.selectById(userId);
    		exists = account != null ? true : false;
    		this.redisTemplate.opsForValue().set(cacheKey, String.valueOf(exists), 7, TimeUnit.DAYS);
    	}
    	return exists;
    }

    @Override
    public void sendDoubleRewardOfTenYuan(Long userId) {
        PrizewheelsTemplate template = prizewheelsTemplateService.load();
        
        PrizewheelsAccount accountForUpdate = new PrizewheelsAccount();
        accountForUpdate.setUserId(userId);
        accountForUpdate.setTenPrize(PrizewheelsConstant.DoubleReward.OFFICIALED_ACCOUNTS);
        prizewheelsAccountService.updateById(accountForUpdate);
        log.info("-->转盘-->修改用户{}的状态为已关注公众号", userId);

        // 获取上一个10元红包
        Wrapper<PrizewheelsRedpacketRecord> wrapper = new EntityWrapper<PrizewheelsRedpacketRecord>();
        wrapper.eq("USER_ID", userId);
        wrapper.eq("GOT_WAYS", PrizewheelsConstant.RedpacketGotWays.FIXED_REDPACKET_10);
        wrapper.eq("STATUS", PrizewheelsConstant.GotStatus.PENDING);
        Collection<String> collection = new ArrayList<String>();
        collection.add("UPDATE_TIME");
        wrapper.orderDesc(collection);
        List<PrizewheelsRedpacketRecord> list = prizewheelsRedpacketRecordService.selectList(wrapper);
        PrizewheelsRedpacketRecord queryRecord = list.size() > 0 ? list.get(0) : null;
        if (queryRecord == null) {
            log.info("-->sendDoubleRewardOfTenYuan-->用户{}没有待领取的10元红包", userId);
            return;
        }

        /******************** 保存红包增加记录 ***********************/
        PrizewheelsRedpacketRecord redpacketRecord = this.getPrizewheelsRedpacketRecordInitBean();
        redpacketRecord.setUserId(userId);
        double money = PrizewheelsUtil.getRandomMoney(template.getRandomRedpacketRange());
        redpacketRecord.setIncreaseMoney(money);
        redpacketRecord.setGotWays(PrizewheelsConstant.RedpacketGotWays.DOUBLE_REWARD_REDPACKET);
        redpacketRecord.setStatus(PrizewheelsConstant.GotStatus.PENDING);
        redpacketRecord.setPrizeRecordId(queryRecord.getId());
        prizewheelsRedpacketRecordService.insert(redpacketRecord);
        log.info("-->sendDoubleRewardOfTenYuan-->保存用户{}红包增加记录，变动数为：{}.",
                userId, money);

    }

    @Override
    public Map<Long, Integer> getSignStatus(Set<Long> useridSet) {
        Map<Long, Integer> resultMap = new HashMap<Long, Integer>();
        String endDate = DateUtils.SDF10.format(new Date());
        boolean currentDayHasSign = false;
        for (Long userId : useridSet) {
            // 查询用户当天是否已签到，如果没有，则再查询其之前连续签到的天数（不算当天）
            currentDayHasSign = prizewheelsCoinsRecordService.queryUserCurrentDayHasSign(userId);
            if (!currentDayHasSign) {
                int signDays = prizewheelsCoinsRecordService.getContinuousSigninDays(userId, endDate);
                resultMap.put(userId, signDays);
            }
        }
        return resultMap;
    }

    @Override
    public void officialAccountsReply(String appid, String openId, String keyword) {
    	UserLogin userLogin = userFeignClient.getUserLoginInfoByUserName(openId).getData();
    	if(userLogin == null) {
    		log.error("-->用户{}在公众号回复关键词：{}发生错误，用户在登录表不存在.", openId, keyword);
    		throw new CustomException("用户不存在");
    	}
    	long userId = userLogin.getUserId();
    	log.info("-->转盘-->用户{}回复关键词{},appid:{}", userId, keyword, appid);
        //判断关键词
        PrizewheelsTemplate template = prizewheelsTemplateService.load();
        if(!template.getOfficialAccountAppid().equalsIgnoreCase(appid)) {
        	log.error("-->转盘-->公众号回复-->appid不匹配：{}", appid);
        	return;
        }
        String matchWords = template.getReplyKeyword();
        if (!matchWords.equals(keyword)) {
            log.info("-->转盘-->公众号回复-->关键词[{}]不匹配，返回", keyword);
            return;
        }

        // 判断分享人的转盘账户是否存在
        PrizewheelsAccount account = prizewheelsAccountService.selectById(userId);
        if (account == null) {
            log.error("-->转盘-->公众号回复-->用户{}没有参与过转盘活动，返回", userId);
            return;
        }

        int increaseCoins = template.getOfficialAccountReplyGetCoins();
        // 判断当天是否已领取过
        Wrapper<PrizewheelsCoinsRecord> wrapper = new EntityWrapper<PrizewheelsCoinsRecord>();
        wrapper.eq("USER_ID", userId);
        wrapper.eq("GOT_WAYS", PrizewheelsConstant.CoinsGotWays.REPLY);
        wrapper.eq("STATUS", PrizewheelsConstant.GotStatus.GOT);
        Date now = new Date();
        String nowStr = DateUtils.SDF10.format(now);
        wrapper.le("CREATE_TIME", nowStr + " 23:59:59"); //小于等于
        wrapper.ge("CREATE_TIME", nowStr + " 00:00:01"); // 大于等于
        int record = prizewheelsCoinsRecordService.selectCount(wrapper);
        if (record > 0) {
        	String message = "您今天已获得" + increaseCoins + "枚买买币，分享商品也能获得买买币哦";
        	sendCustomMessage(message, openId, appid);
            return;
        }

        account = prizewheelsAccountService.updateCoinsBalance(userId, increaseCoins, true);
        log.info("-->转盘-->公众号回复-->更新用户{}买买币余额，增加买买币{}个，实时余额{}个", userId, increaseCoins, account.getCoinsBalance());

        /******************* 保存买买币增加记录 **********************/
        PrizewheelsCoinsRecord coinsRecord = getPrizewheelsCoinsRecordInitBean(userId);
        coinsRecord.setIncreaseCoins(increaseCoins);
        coinsRecord.setBlanceCoins(account.getCoinsBalance());
        coinsRecord.setGotWays(PrizewheelsConstant.CoinsGotWays.REPLY);
        coinsRecord.setStatus(PrizewheelsConstant.GotStatus.GOT);
        prizewheelsCoinsRecordService.insert(coinsRecord);
        log.info("-->转盘-->公众号回复-->保存用户{}买买币增加记录，变动的买买币个数：{}，余额：{}个.",
                userId, increaseCoins, account.getCoinsBalance());
        String message = "您已获得" + increaseCoins + "枚买买币，快去试试手气吧";
        sendCustomMessage(message, openId, appid);
        
    }

    private void sendCustomMessage(String message, String openId, String appId) {
    	JSONObject json = new JSONObject();
    	json.put("touser", openId);
    	json.put("msgtype", "text");
    	JSONObject text = new JSONObject();
    	text.put("content", message);
    	json.put("text", text);
    	json.put("appid", appId);
    	wxMessageFeignClient.sendCustom(JSONObject.toJSONString(json));

    	log.info("com.mmj.active.prizewheels.service.impl.PrizewheelsFacadeServiceImpl.sendCustomMessage发送客服消息");
        //获取素材
        WxMedia wxMedia = new WxMedia();
        wxMedia.setAppid(appId);
        wxMedia.setBusinessName("大转盘");
        wxMedia.setMediaType("forever");
        wxMedia.setMediaUrl("https://cdn.polynome.cn/turntable_share.png");
        ReturnData<WxMedia> wxMediaReturnData = noticeFeignClient.wxMediaUpload(wxMedia);
        //发送小程序卡片
        JSONObject msgJson = new JSONObject();
        msgJson.put("touser", openId);
        msgJson.put("msgtype", "miniprogrampage");
        msgJson.put("appid", appId);
        JSONObject miniJson = new JSONObject();
        miniJson.put("title", "能赚钱，能省钱的小游>>");
        miniJson.put("appid", "wx7a01aef90c714fe2");
        miniJson.put("pagepath", "pkgTurntable/main");
        miniJson.put("thumb_media_id", wxMediaReturnData.getData().getMediaId());
        msgJson.put("miniprogrampage", miniJson);
        wxMessageFeignClient.sendCustom(JSONObject.toJSONString(msgJson));
    }
    
    @Override
    public void autoIncrementCoins() {
        Date now = new Date();
        log.info("-->定时任务执行：转盘活动增加买买币，当前时间：{}----------", now);
        PrizewheelsTemplate template = prizewheelsTemplateService.load();
        int minutes = template.getIncrementCoinsMinutes();
        int increateAmount = template.getIncrementCoinsAmount();
        log.info("-->规则：每{}分钟增加{}买买币", minutes, increateAmount);
        long start = System.currentTimeMillis();
        // 查出在2小时（120分钟）内访问过转盘的用户(从访问记录表作为判断依据)
        List<PrizewheelsAccessRecord> list = prizewheelsAccessRecordService.getLatestActiveUser(org.apache.commons.lang.time.DateUtils.addMinutes(now, -template.getExceedMinutes()));
        if (list == null || list.isEmpty()) {
            log.info("-->没有活跃用户，返回...");
            return;
        }
        log.info("-->{}分钟内有{}个用户访问过转盘活动", template.getExceedMinutes(), list.size());

        int i = 0;
        for (PrizewheelsAccessRecord record : list) {
            if (incrementCoins(record, template)) {
                i++;
            }
        }
        long end = System.currentTimeMillis();
        log.info("-->定时任务-->给用户增加买买币处理完毕，成功处理个数：{}，耗时：{}毫秒", i, (end - start));

    }

    private boolean incrementCoins(PrizewheelsAccessRecord record, PrizewheelsTemplate template) {
        boolean addSuccess = false;
        Date tempDate = null;
        Long userId = null;
        try {
            PrizewheelsAccount account = null;
            PrizewheelsCoinsRecord coinsRecord = null;
            boolean match = false;
            userId = record.getUserId();
            account = prizewheelsAccountService.selectById(userId);
            if (account == null) {
                log.info("-->用户{}在转盘活动中没有账户信息，跳过.", userId);
                return addSuccess;
            }

            Wrapper<PrizewheelsCoinsRecord> wrapper = new EntityWrapper<PrizewheelsCoinsRecord>();
            wrapper.eq("USER_ID", userId);
            wrapper.eq("GOT_WAYS", PrizewheelsConstant.CoinsGotWays.INCREMENT);
            Set<String> columns = new HashSet<String>(1);
            columns.add("CREATE_TIME");
            wrapper.orderDesc(columns);
            wrapper.last("LIMIT 1");
            List<PrizewheelsCoinsRecord> coinsRecordList = prizewheelsCoinsRecordService.selectList(wrapper);
            Date now = new Date();
            if (coinsRecordList.isEmpty()) {
                // 用户刚开始参与转盘活动，定时任务还没有给其增加过买买币，此时需要判断用户创建账号的时间，间隔时间过了指定时间之后再加买买币，不能一开始就给其增加
                tempDate = org.apache.commons.lang.time.DateUtils.addMinutes(account.getCreateTime(), template.getIncrementCoinsMinutes());
                if(tempDate.compareTo(now) < 0) {
					match = true;
				}
            } else {
                coinsRecord = coinsRecordList.get(0);
                tempDate = org.apache.commons.lang.time.DateUtils.addMinutes(coinsRecord.getCreateTime(), template.getIncrementCoinsMinutes());
                if(tempDate.compareTo(now) < 0) {
					match = true;
				}
            }

            if (!match) {
                return addSuccess;
            }

            account = prizewheelsAccountService.updateCoinsBalance(userId, template.getIncrementCoinsAmount(), true);

            /********** 买买币增加记录 ****************/
            coinsRecord = new PrizewheelsCoinsRecord();
            coinsRecord.setId(CommonUtil.getRandomUUID());
            coinsRecord.setUserId(userId);
            coinsRecord.setIncreaseCoins(template.getIncrementCoinsAmount());
            coinsRecord.setBlanceCoins(account.getCoinsBalance());
            coinsRecord.setGotWays(PrizewheelsConstant.CoinsGotWays.INCREMENT);
            coinsRecord.setStatus(PrizewheelsConstant.GotStatus.GOT);
            now = new Date();
            coinsRecord.setCreateTime(now);
            coinsRecord.setUpdateTime(now);
            prizewheelsCoinsRecordService.insert(coinsRecord);
            addSuccess = true;
            log.info("-->定时任务-->给用户{}增加买买币{}个，当前时间：{}", userId, template.getIncrementCoinsAmount(), now);
        } catch (Exception e) {
            log.error("-->定时任务给用户" + userId + "增加买买币，发生异常：", e);
        }
        return addSuccess;
    }

    private PrizewheelsRedpacketRecord getPrizewheelsRedpacketRecordInitBean() {
        PrizewheelsRedpacketRecord record = new PrizewheelsRedpacketRecord();
        record.setId(CommonUtil.getRandomUUID());
        Date now = new Date();
        record.setCreateTime(now);
        record.setUpdateTime(now);
        return record;
    }

    private PrizewheelsCoinsRecord getPrizewheelsCoinsRecordInitBean(Long userId) {
        PrizewheelsCoinsRecord record = new PrizewheelsCoinsRecord();
        record.setId(CommonUtil.getRandomUUID());
        Date now = new Date();
        record.setUserId(userId);
        record.setCreateTime(now);
        record.setUpdateTime(now);
        return record;
    }

    private PrizewheelsAccount getPrizewheelsAccountInitBean(Long userId, boolean isNewUser) {
        PrizewheelsAccount account = new PrizewheelsAccount();
        account.setUserId(userId);
        Date now = new Date();
        if (isNewUser) {
            account.setCreateTime(now);
        }
        account.setUpdateTime(now);
        return account;
    }

    private PrizewheelsPrizeRecord getPrizewheelsPrizeRecordInitBean() {
        PrizewheelsPrizeRecord record = new PrizewheelsPrizeRecord();
        record.setId(CommonUtil.getRandomUUID());
        Date now = new Date();
        record.setCreateTime(now);
        record.setUpdateTime(now);
        return record;
    }

	@Override
	public void updateUserId(long oldUserId, long newUserId) {
		// 先判断newUserId在t_prizewheels_account表是否存在
		PrizewheelsAccount oldAccount = prizewheelsAccountService.selectById(oldUserId);
		PrizewheelsAccount newAccount = prizewheelsAccountService.selectById(newUserId);
		if(oldAccount != null && newAccount != null) {
			log.error("-->用户{}的新ID {}在转盘账户表也有数据，请人工处理", oldUserId, newUserId);
			return;
		}
		prizewheelsAccountService.updateUserId(oldUserId, newUserId);
		log.info("-->t_prizewheels_account表切换userId, {}改为{}", oldUserId, newUserId);
		prizewheelsAccessRecordService.updateUserId(oldUserId, newUserId);
		log.info("-->t_prizewheels_access_record表切换userId, {}改为{}", oldUserId, newUserId);
		prizewheelsCoinsRecordService.updateUserId(oldUserId, newUserId);
		log.info("-->t_prizewheels_coins_record表切换userId, {}改为{}", oldUserId, newUserId);
		prizewheelsPrizeRecordService.updateUserId(oldUserId, newUserId);
		log.info("-->t_prizewheels_prize_record表切换userId, {}改为{}", oldUserId, newUserId);
		prizewheelsRedpacketRecordService.updateUserId(oldUserId, newUserId);
		log.info("-->t_prizewheels_redpacket_record表切换userId, {}改为{}", oldUserId, newUserId);
		prizewheelsWithdrawRecordService.updateUserId(oldUserId, newUserId);
		log.info("-->t_prizewheels_withdraw_record表切换userId, {}改为{}", oldUserId, newUserId);
	}

	@Override
	public void sendSignNotice() {
		Wrapper<FocusInfo> wrapper = new EntityWrapper<FocusInfo>();
		wrapper.eq("MODULE", 6);
		List<FocusInfo> list = this.focusInfoService.selectList(wrapper);
		if(list.isEmpty()) {
			return;
		}
		Set<Long> userIdSet = new HashSet<Long>();
		Map<Long, String> userIdToOpenIdMap = new HashMap<Long, String>();
		for(FocusInfo info : list) {
			userIdSet.add(info.getUserId());
			userIdToOpenIdMap.put(info.getUserId(), info.getOpenId());
		}
		log.info("-->通过转盘关注公众号的用户有：{}个", userIdSet.size());
		
		// 获取签到状态
		Map<Long, Integer> resultMap = getSignStatus(userIdSet);
		if(resultMap.isEmpty()) {
			return;
		}
		String openId;
		for (Map.Entry<Long, Integer> entry : resultMap.entrySet()) {
			openId = userIdToOpenIdMap.get(entry.getKey());
			log.info("用户{}签到天数：{}", entry.getKey(), entry.getValue());
			notifyConfig.sendAfter5(openId, entry.getValue().toString());
		}
	}
	
	

}
