package com.mmj.active.coupon.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.mmj.active.common.feigin.NoticeFeignClient;
import com.mmj.active.common.feigin.UserFeignClient;
import com.mmj.active.coupon.mapper.CouponInfoMapper;
import com.mmj.active.coupon.model.CouponClass;
import com.mmj.active.coupon.model.CouponGood;
import com.mmj.active.coupon.model.CouponInfo;
import com.mmj.active.coupon.model.dto.BossCouponDto;
import com.mmj.active.coupon.model.dto.CouponInfoDto;
import com.mmj.active.coupon.model.dto.CouponNumDto;
import com.mmj.active.coupon.model.vo.*;
import com.mmj.active.coupon.service.CouponClassService;
import com.mmj.active.coupon.service.CouponGoodService;
import com.mmj.active.coupon.service.CouponInfoService;
import com.mmj.common.model.BaseDict;
import com.mmj.common.model.JwtUserDetails;
import com.mmj.common.model.ReturnData;
import com.mmj.common.properties.SecurityConstants;
import com.mmj.common.utils.DateUtils;
import com.mmj.common.utils.DoubleUtil;
import com.mmj.common.utils.PriceConversion;
import com.mmj.common.utils.SecurityUserUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <p>
 * 优惠券信息表 服务实现类
 * </p>
 *
 * @author KK
 * @since 2019-06-25
 */
@Slf4j
@Service
public class CouponInfoServiceImpl extends ServiceImpl<CouponInfoMapper, CouponInfo> implements CouponInfoService {
    @Autowired
    private CouponGoodService couponGoodService;
    @Autowired
    private CouponClassService couponClassService;
    @Autowired
    private CouponInfoMapper couponInfoMapper;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private UserFeignClient userFeignClient;
    @Autowired
    private NoticeFeignClient noticeFeignClient;

    private JwtUserDetails getUserDetails() {
        JwtUserDetails jwtUserDetails = SecurityUserUtil.getUserDetails();
        Assert.notNull(jwtUserDetails, "缺少用户信息");
        return jwtUserDetails;
    }

    @Override
    public List<CouponInfo> getGoodsDetailShowCouponInfo() {
        CouponInfo couponInfo = new CouponInfo();
//        couponInfo.setCouponScope("1"); //所有商品可用
        couponInfo.setDetailShow(1); //可以在商详展示
        couponInfo.setDelFlag(0); //未删除
        EntityWrapper<CouponInfo> couponInfoEntityWrapper = new EntityWrapper<>(couponInfo);
        //所有商品可用 部分商品不可用
        couponInfoEntityWrapper.in("COUPON_SCOPE", Stream.of(new String[]{"1", "3"}).collect(Collectors.toList()));
        return selectList(couponInfoEntityWrapper);
    }

    @Override
    public Page<BossCouponDto> query(BossCouponQueryVo queryVo) {
        getUserDetails();
        CouponInfo queryCouponInfo = new CouponInfo();
        if (Objects.isNull(queryVo.getDelFlag())) {
            queryCouponInfo.setDelFlag(0);
        } else {
            queryCouponInfo.setDelFlag(queryVo.getDelFlag());
        }
        EntityWrapper<CouponInfo> couponInfoEntityWrapper = new EntityWrapper<>(queryCouponInfo);
        if (StringUtils.isNotBlank(queryVo.getCouponTitle())) {
            couponInfoEntityWrapper.like("COUPON_TITLE", queryVo.getCouponTitle());
        }
        if (StringUtils.isNotBlank(queryVo.getMaketingDesc())) {
            couponInfoEntityWrapper.like("MAKETING_DESC", queryVo.getMaketingDesc());
        }
        if (Objects.nonNull(queryVo.getCouponMain())) {
            couponInfoEntityWrapper.eq("COUPON_MAIN", queryVo.getCouponMain());
        }
        if (StringUtils.isNotBlank(queryVo.getApplyPrice())) {
            couponInfoEntityWrapper.eq("APPLY_PRICE", queryVo.getApplyPrice().trim());
        }
        if (StringUtils.isNotBlank(queryVo.getCouponStart()) && StringUtils.isNotBlank(queryVo.getCouponEnd())) {
            couponInfoEntityWrapper.ge("COUPON_START", DateUtils.getDate(queryVo.getCouponStart(), DateUtils.SDF1));
            couponInfoEntityWrapper.le("COUPON_END", DateUtils.getDate(queryVo.getCouponEnd(), DateUtils.SDF1));
        }
        couponInfoEntityWrapper.orderBy("COUPON_ID", false);
        Page<CouponInfo> results = new Page(queryVo.getCurrentPage(), queryVo.getPageSize());
        results = selectPage(results, couponInfoEntityWrapper);
        List<BossCouponDto> bossCouponDtos = Lists.newArrayListWithCapacity(results.getRecords().size());
        results.getRecords().stream().forEach(couponInfo -> {
            BossCouponDto bossCouponDto = new BossCouponDto();
            BeanUtils.copyProperties(couponInfo, bossCouponDto);
            if ("2".equalsIgnoreCase(couponInfo.getCouponScope()) || "3".equalsIgnoreCase(couponInfo.getCouponScope())) { //部分商品可用|部分商品不可用
                CouponGood queryCouponGood = new CouponGood();
                queryCouponGood.setCouponId(couponInfo.getCouponId());
                EntityWrapper<CouponGood> couponGoodEntityWrapper = new EntityWrapper<>(queryCouponGood);
                List<CouponGood> couponGoods = couponGoodService.selectList(couponGoodEntityWrapper);
                List<BossCouponGoodAddVo> bossCouponGoodAddVos = Lists.newArrayListWithCapacity(couponGoods.size());
                couponGoods.stream().forEach(couponGood -> {
                    BossCouponGoodAddVo bossCouponGoodAddVo = new BossCouponGoodAddVo();
                    BeanUtils.copyProperties(couponGood, bossCouponGoodAddVo);
                    bossCouponGoodAddVos.add(bossCouponGoodAddVo);
                });
                bossCouponDto.setGoodItems(bossCouponGoodAddVos);
            } else if ("4".equalsIgnoreCase(couponInfo.getCouponScope())) { //指定分类可用
                CouponClass queryCouponClass = new CouponClass();
                queryCouponClass.setCouponId(couponInfo.getCouponId());
                EntityWrapper<CouponClass> couponClassEntityWrapper = new EntityWrapper<>(queryCouponClass);
                List<CouponClass> couponClasses = couponClassService.selectList(couponClassEntityWrapper);
                List<BossCouponClassAddVo> bossCouponClassAddVos = Lists.newArrayListWithCapacity(couponClasses.size());
                couponClasses.stream().forEach(couponClass -> {
                    BossCouponClassAddVo bossCouponClassAddVo = new BossCouponClassAddVo();
                    BeanUtils.copyProperties(couponClass, bossCouponClassAddVo);
                    bossCouponClassAddVos.add(bossCouponClassAddVo);
                });
                bossCouponDto.setClassItems(bossCouponClassAddVos);
            }
            bossCouponDto.setToDaySendNumber(toDayNum(bossCouponDto.getCouponId()).getNum());
            bossCouponDtos.add(bossCouponDto);
        });
        Page<BossCouponDto> bossCouponDtoPage = new Page<>();
        bossCouponDtoPage.setTotal(results.getTotal());
        bossCouponDtoPage.setCurrent(results.getCurrent());
        bossCouponDtoPage.setSize(results.getSize());
        bossCouponDtoPage.setCondition(results.getCondition());
        bossCouponDtoPage.setRecords(bossCouponDtos);
        return bossCouponDtoPage;
    }

    @Override
    public void detailShow(DetailShowVo detailShowVo) {
        CouponInfo updateCouponInfo = new CouponInfo();
        updateCouponInfo.setDetailShow(detailShowVo.getDetailShow());
        CouponInfo queryCouponInfo = new CouponInfo();
        queryCouponInfo.setCouponId(detailShowVo.getCouponId());
        EntityWrapper<CouponInfo> couponInfoEntityWrapper = new EntityWrapper<>(queryCouponInfo);
        Assert.isTrue(update(updateCouponInfo, couponInfoEntityWrapper), "操作失败");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void add(BossCouponAddVo couponAddVo) {
        JwtUserDetails userDetails = getUserDetails();
        CouponInfo couponInfo = new CouponInfo();
        BeanUtils.copyProperties(couponAddVo, couponInfo);
        if ("2".equals(couponInfo.getIndateType())) { //有效期-按领取后..
            if ("DATE".equalsIgnoreCase(couponInfo.getAfterUnit())) {
                Assert.isTrue(StringUtils.isNotBlank(couponAddVo.getAfterDate()), "请输入有效时间");
                couponInfo.setAfterDate(DateUtils.getDate(couponAddVo.getAfterDate(), DateUtils.SDF10));
            }
        } else {
            Assert.isTrue(StringUtils.isNotBlank(couponAddVo.getCouponStart()) && StringUtils.isNotBlank(couponAddVo.getCouponEnd()), "请输入有效期时间区间");
            couponInfo.setCouponStart(DateUtils.getDate(couponAddVo.getCouponStart(), DateUtils.SDF1));
            couponInfo.setCouponEnd(DateUtils.getDate(couponAddVo.getCouponEnd(), DateUtils.SDF1));
        }
        if (!"3".equals(couponInfo.getWhereType()) && couponInfo.getWhereValue().intValue() > 0) { //条件类型 1 无限制 ; 2 满X元; 3 满X件
            couponInfo.setWhereValue(PriceConversion.stringToInt(couponInfo.getWhereValue().toString()));
        }
        if ("1".equals(couponInfo.getCouponAmount())) { //优惠类型  1 减X元; 2 打X拆
            couponInfo.setCouponValue(PriceConversion.stringToInt(couponInfo.getCouponValue().toString()));
        }
        couponInfo.setDelFlag(0);
        couponInfo.setCreaterId(userDetails.getUserId());
        couponInfo.setModifyId(userDetails.getUserId());
        couponInfo.setTotalSendNumber(0);
        boolean result = insert(couponInfo);
        Assert.isTrue(result, "新增优惠券失败");
        processCouponScope(couponInfo, couponAddVo);
    }

    /**
     * 根据优惠券使用范围处理
     *
     * @param couponInfo
     * @param couponAddVo
     */
    private void processCouponScope(CouponInfo couponInfo, BossCouponAddVo couponAddVo) {
        boolean result;
        if ("2".equalsIgnoreCase(couponInfo.getCouponScope())) { //部分商品可用
            List<BossCouponGoodAddVo> goodAddVos = couponAddVo.getGoodItems();
            if (Objects.nonNull(goodAddVos)) {
                List<CouponGood> couponGoods = Lists.newArrayListWithCapacity(goodAddVos.size());
                goodAddVos.stream().forEach(good -> {
                    CouponGood couponGood = new CouponGood();
                    BeanUtils.copyProperties(good, couponGood);
                    couponGood.setScopeType("1");
                    couponGood.setCouponId(couponInfo.getCouponId());
                    couponGoods.add(couponGood);
                });
                result = couponGoodService.insertBatch(couponGoods);
                Assert.isTrue(result, "新增优惠券可用商品失败");
            }
        } else if ("3".equalsIgnoreCase(couponInfo.getCouponScope())) { //部分商品不可用
            List<BossCouponGoodAddVo> goodAddVos = couponAddVo.getGoodItems();
            if (Objects.nonNull(goodAddVos)) {
                List<CouponGood> couponGoods = Lists.newArrayListWithCapacity(goodAddVos.size());
                goodAddVos.stream().forEach(goodVo -> {
                    CouponGood couponGood = new CouponGood();
                    BeanUtils.copyProperties(goodVo, couponGood);
                    couponGood.setScopeType("2");
                    couponGood.setCouponId(couponInfo.getCouponId());
                    couponGoods.add(couponGood);
                });
                result = couponGoodService.insertBatch(couponGoods);
                Assert.isTrue(result, "新增优惠券不可用商品失败");
            }
        } else if ("4".equalsIgnoreCase(couponInfo.getCouponScope())) { //指定分类可用
            List<BossCouponClassAddVo> classAddVos = couponAddVo.getClassItems();
            if (Objects.nonNull(classAddVos)) {
                List<CouponClass> couponClasses = Lists.newArrayListWithCapacity(classAddVos.size());
                classAddVos.stream().forEach(classVo -> {
                    CouponClass couponClass = new CouponClass();
                    BeanUtils.copyProperties(classVo, couponClass);
                    couponClass.setCouponId(couponInfo.getCouponId());
                    couponClasses.add(couponClass);
                });
                result = couponClassService.insertBatch(couponClasses);
                Assert.isTrue(result, "新增优惠券指定分类失败");
            }
        } else { //所有商品可用
            //不做处理
        }
    }

    @Override
    public List<CouponInfo> batchCouponInfos(List<Integer> couponIds) {
        Assert.isTrue(Objects.nonNull(couponIds) && couponIds.size() > 0, "缺少优惠券ID");
        CouponInfo couponInfo = new CouponInfo();
        couponInfo.setDelFlag(0);
        EntityWrapper<CouponInfo> entityWrapper = new EntityWrapper<>(couponInfo);
        entityWrapper.in("COUPON_ID", couponIds);
        return selectList(entityWrapper);
    }

    @Override
    public CouponInfoDto toCouponInfoDto(CouponInfo couponInfo) {
        CouponInfoDto couponInfoDto = new CouponInfoDto();
        BeanUtils.copyProperties(couponInfo, couponInfoDto);
        couponInfoDto.setToDaySendNumber(toDayNum(couponInfo.getCouponId()).getNum());
        packageCouponInfoDto(couponInfoDto);
        return couponInfoDto;
    }

    @Override
    public List<CouponInfoDto> toCouponInfoDto(List<CouponInfo> couponInfoList) {
        if (Objects.nonNull(couponInfoList) && couponInfoList.size() > 0) {
            List<CouponInfoDto> couponInfoDtoList = Lists.newArrayListWithCapacity(couponInfoList.size());
            couponInfoList.forEach(couponInfo -> {
                CouponInfoDto couponInfoDto = new CouponInfoDto();
                BeanUtils.copyProperties(couponInfo, couponInfoDto);
                couponInfoDto.setToDaySendNumber(toDayNum(couponInfo.getCouponId()).getNum());
                packageCouponInfoDto(couponInfoDto);
                couponInfoDtoList.add(couponInfoDto);
            });
            return couponInfoDtoList;
        }
        return null;
    }

    public void packageCouponInfoDto(CouponInfoDto couponInfoDto) {
        if ("4".equals(couponInfoDto.getCouponScope())) {
            couponInfoDto.setGoodClassList(getCouponClass(couponInfoDto.getCouponId()));
        } else if ("2".equals(couponInfoDto.getCouponScope()) || "3".equals(couponInfoDto.getCouponScope())) {
            couponInfoDto.setGoodIdList(getCouponGoodId(couponInfoDto.getCouponId()));
        }
    }

    public List<String> getCouponClass(Integer couponId) {
        List<CouponClass> couponClassList = couponClassService.getCouponClass(couponId);
        List<String> couponClass = Lists.newArrayListWithCapacity(couponClassList.size());
        couponClassList.forEach(cs -> {
            couponClass.add(cs.getGoodClass());
        });
        return couponClass;
    }

    public List<Integer> getCouponGoodId(Integer couponId) {
        List<CouponGood> couponGoodList = couponGoodService.getCouponGood(couponId);
        List<Integer> couponGood = Lists.newArrayListWithCapacity(couponGoodList.size());
        couponGoodList.forEach(cg -> {
            couponGood.add(cg.getGoodId());
        });
        return couponGood;
    }

    @Override
    public List<CouponNumDto> batchTodayNums(List<Integer> couponIds) {
        getUserDetails();
        Assert.isTrue(Objects.nonNull(couponIds) && couponIds.size() > 0, "缺少优惠券ID");
        List<CouponNumDto> couponNumDtos = Lists.newArrayListWithCapacity(couponIds.size());
        couponIds.forEach(couponId -> couponNumDtos.add(toDayNum(couponId)));
        return couponNumDtos;
    }

    @Override
    public CouponNumDto toDayNum(Integer couponId) {
        //获取当天发放数量
        String toDaySendNumKey = String.format(COUPON_TODAY_SEND_NUM, todayYYYYMMDD(), couponId.intValue());
        String toDaySendNum = redisTemplate.opsForValue().get(toDaySendNumKey);
        return new CouponNumDto(couponId, StringUtils.isEmpty(toDaySendNum) ? 0 : Integer.parseInt(toDaySendNum));
    }

    @Override
    public void issued(Integer couponId) {
        RLock fairLock = redissonClient.getFairLock("COUPON:LOCK:ISSUED:" + couponId);
        fairLock.lock(3, TimeUnit.SECONDS);
        try {
            //累计当天发放数量
            String toDaySendNumKey = String.format(COUPON_TODAY_SEND_NUM, todayYYYYMMDD(), couponId.intValue());
            redisTemplate.opsForValue().increment(toDaySendNumKey, 1);
            //累计发放总量
            couponInfoMapper.incrTotalSendNumber(couponId);
        } finally {
            fairLock.unlock();
        }
    }

    /**
     * 优惠券每日发放量
     */
    private final String COUPON_TODAY_SEND_NUM = "COUPON:ISSUED:%s:%d";

    /**
     * 获取当天日期
     *
     * @return
     */
    public String todayYYYYMMDD() {
        LocalDate localDate = LocalDate.now();
        int year = localDate.getYear();
        int month = localDate.getMonth().getValue();
        int day = localDate.getDayOfMonth();
        return String.format("%s%s%s", year, month < 10 ? "0" + month : month, day < 10 ? "0" + day : day);
    }

    @Override
    public boolean updateMemberDaySendTotalCount() {
        //ID：44 -> 10元无门槛优惠券 -> 5%的会员可领取
        //ID：45 ->  5元无门槛优惠券 -> 10%的会员可领到
        //ID：46 ->  2元无门槛优惠券 -> 10%的会员可领到
        //ID：47 ->  满30减3元 -> 20%的会员可领到
        //ID：48 ->  满50减8元 -> 20%的会员可领到
        //ID：49 ->  满90减20元 -> 20%的会员可领到
        ReturnData<Integer> returnData = userFeignClient.queryMemberTotalCount();
        Assert.isTrue(returnData.getCode() == SecurityConstants.SUCCESS_CODE, returnData.getDesc());
        int memberTotalCount = returnData.getData(); //会员总数量

        ReturnData<BaseDict> baseDictReturnData = noticeFeignClient.queryGlobalConfigByDictCode("memberDay.couponTemplate.id.arr");
        BaseDict baseDict = baseDictReturnData.getData();
        String memberDayCouponTemplatesStr = Objects.nonNull(baseDict) && StringUtils.isNotEmpty(baseDict.getDictValue()) ? baseDict.getDictValue() : "47,48,50,51,52";
        String[] arr = memberDayCouponTemplatesStr.split(",");
        int couponId;
        double percent;
        List<CouponInfo> updateCouponInfoList = Lists.newArrayListWithCapacity(arr.length);
        CouponInfo memberCouponInfo;
        for (String str : arr) {
            couponId = Integer.valueOf(str);
            baseDictReturnData = noticeFeignClient.queryGlobalConfigByDictCode("memberDay.couponTemplate." + couponId + ".percent");
            baseDict = baseDictReturnData.getData();
            percent = Double.parseDouble(Objects.nonNull(baseDict) && StringUtils.isNotEmpty(baseDict.getDictValue()) ? baseDict.getDictValue() : "0.2");
            //正常情况下只要sys_config表作了配置，就不会取默认值，此默认值是防止数据被误删的情况下导致数量无法被计算
            int total = (int) Math.ceil(DoubleUtil.mul((double) memberTotalCount, percent));
            memberCouponInfo = new CouponInfo();
            memberCouponInfo.setCouponId(couponId);
            memberCouponInfo.setCountNum(total);
//            memberCouponInfo.setTotalSendNumber(0);
            updateCouponInfoList.add(memberCouponInfo);
        }
        return updateBatchById(updateCouponInfoList);
    }
}
