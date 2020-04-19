package com.mmj.active.freeorder.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.google.common.collect.Maps;
import com.mmj.active.common.constants.ActiveGoodsConstants;
import com.mmj.active.common.feigin.GoodFeignClient;
import com.mmj.active.common.feigin.OrderFeignClient;
import com.mmj.active.common.feigin.UserFeignClient;
import com.mmj.active.common.model.*;
import com.mmj.active.common.service.ActiveGoodService;
import com.mmj.active.freeorder.mapper.FreeInfoMapper;
import com.mmj.active.freeorder.model.FreeInfo;
import com.mmj.active.freeorder.model.vo.FreeOrderInfoVo;
import com.mmj.active.freeorder.model.vo.FreeOrderRelationsVo;
import com.mmj.active.freeorder.service.FreeInfoService;
import com.mmj.active.freeorder.utils.SoldNumUtils;
import com.mmj.common.constants.CommonConstant;
import com.mmj.common.model.BaseUser;
import com.mmj.common.utils.PriceConversion;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.*;

/**
 * <p>
 * 免费送活动表 服务实现类
 * </p>
 *
 * @author 陈光复
 * @since 2019-06-19
 */
@Service
@Slf4j
public class FreeInfoServiceImpl extends ServiceImpl<FreeInfoMapper, FreeInfo> implements FreeInfoService {

    private final OrderFeignClient orderFeignClient;

    private final UserFeignClient userFeignClient;

    private final ActiveGoodService activeGoodService;

    private final GoodFeignClient goodFeignClient;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public FreeInfoServiceImpl(OrderFeignClient orderFeignClient, UserFeignClient userFeignClient, ActiveGoodService activeGoodService, GoodFeignClient goodFeignClient) {
        this.orderFeignClient = orderFeignClient;
        this.userFeignClient = userFeignClient;
        this.activeGoodService = activeGoodService;
        this.goodFeignClient = goodFeignClient;
    }

    @Override
    public FreeOrderInfoVo info(String orderNo) {
        log.info("进入查询免费送信息接口:{}", orderNo);

        FreeOrderInfoVo vo = new FreeOrderInfoVo();
        OrderGroup group = new OrderGroup();
        group.setLaunchOrderNo(orderNo);
        group.setGroupType(6);
        group = orderFeignClient.getGroupInfo(group);
        Assert.notNull(group, "团订单不存在");

        log.info("查询的团信息:{}", group);
        OrderInfo info = new OrderInfo();
        info.setOrderNo(orderNo);
        info = orderFeignClient.getForFeign(orderNo);
        log.info("查询的订单信息:{}", info);
        Assert.notNull(info, "订单不存在!");
        Assert.notNull(info.getOrderAmount(), "订单价格异常!");

        vo.setGroupPeople(group.getGroupPeople());
        vo.setCurrentPeople(group.getCurrentPeople());
        vo.setUserId(group.getLaunchUserId());
        vo.setGroupStatus(group.getGroupStatus());
        vo.setOrderNo(group.getLaunchOrderNo());
        vo.setRedMoney(PriceConversion.intToString(info.getOrderAmount()));
        vo.setOrderAmount(PriceConversion.intToString(info.getOrderAmount()));

        OrderGroupJoin join = new OrderGroupJoin();
        join.setActiveType(6);
        join.setLaunchUserId(info.getCreaterId());
        join.setGroupNo(group.getGroupNo());
        List<OrderGroupJoin> joins = orderFeignClient.getGroupJoin(join);
        log.info("参与人集合:{}", joins);
        List<FreeOrderRelationsVo> relationsVos = new ArrayList<>();
        for (OrderGroupJoin j : joins) {
            FreeOrderRelationsVo relationsVo = new FreeOrderRelationsVo();
            relationsVo.setLaunchOrderNo(j.getLaunchOrderNo());
            BaseUser baseUser = userFeignClient.getUserById(j.getJoinUserId());
            if (null == baseUser)
                continue;
            relationsVo.setHeadImgUrl(baseUser.getImagesUrl());
            relationsVo.setNickName(baseUser.getUserFullName());
            relationsVo.setOrderNo(j.getJoinOrderNo());
            relationsVos.add(relationsVo);
        }
        vo.setRelationsVos(relationsVos);

        //查询红包
        if (group.getGroupStatus() == 1) {
            //已完成
            BaseUser user = userFeignClient.getUserById(group.getLaunchUserId());
            if (null == user) {
                return vo;
            }
            vo.setNickName(user.getUserFullName());
            vo.setUnionId(user.getUnionId());
            RedPackageUser redPackageUser = new RedPackageUser();
            redPackageUser.setActiveType(8);
            redPackageUser.setUserId(user.getUserId());
            redPackageUser.setUnionId(user.getUnionId());
            redPackageUser.setPackageSource("FREE_ORDER");
            RedPackageUser redPacket = userFeignClient.redPacketInfo(redPackageUser);
            log.info("查询到的红包:{}", redPacket);
            if (null == redPacket) {
                return vo;
            }
            vo.setRedCode(redPacket.getPackageCode());
            vo.setRedStatus(redPacket.getPackageStatus());
            vo.setNickName(user.getUserFullName());
        }
        return vo;
    }

    @Override
    public List<Map<String, Object>> gotRedPackList() {
/*        List<Map<String, Object>> list = orderFeignClient.getRedPackList();

        if (null != list && list.size() > 10)
            return list;

        int size;
        if (null == list) {
            list = new ArrayList<>();
        }
        size = list.size();
        for (int i = 0; i < 10 - size; i++) {
            Map<String, Object> map = Maps.newHashMap();
            map.put("nickname", nickNames[genIndex(nickNames.length)]);
            map.put("money", 10);
            list.add(map);
        }
        */
        List<Map<String, Object>> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Map<String, Object> map = Maps.newHashMap();
            map.put("nickname", nickNames[genIndex(nickNames.length)]);
            map.put("money", 10);
            list.add(map);
        }
        return list;
    }

    @Override
    public Map<String, Object> queryRedPack(String redCode, String unionId) {
        Map<String, Object> result = Maps.newHashMapWithExpectedSize(4);
        RedPackageUser redPackageUser = userFeignClient.getRedPackage(redCode, unionId);
        if (null == redPackageUser) {
            result.put("code", -1);
            result.put("desc", "暂未获得红包");
            return result;
        }
        if (1 == redPackageUser.getPackageStatus()) {
            result.put("code", -1);
            result.put("desc", "已经领过红包");
            return result;
        }

        result.put("code", 1);
        result.put("desc", "成功");
        result.put("id", redPackageUser.getPackageId());
        result.put("amount", redPackageUser.getPackageAmount());
        return result;
    }

    @Override
    @Transactional
    public void updateRedPack(Integer id) {
        RedPackageUser redPackageUser = new RedPackageUser();
        redPackageUser.setPackageId(id);
        redPackageUser.setPackageStatus(1);
        userFeignClient.updateRedPackage(redPackageUser);
    }

    @Override
    public List<ActiveGoodEx> goodsList(Integer id) {
        FreeInfo info = selectById(id);
        Assert.notNull(info, "活动暂未创建");

        ActiveGood activeGood = new ActiveGood();
        activeGood.setActiveType(ActiveGoodsConstants.ActiveType.FREE_ORDER);
        activeGood.setBusinessId(id);
        activeGood.setGoodStatus("1");
        EntityWrapper<ActiveGood> wrapper = new EntityWrapper<>(activeGood);
        wrapper.groupBy("GOOD_ID,BUSINESS_ID");
        List<ActiveGood> activeGoodList = activeGoodService.selectList(wrapper);
        List<ActiveGoodEx> list = new ArrayList<>();
        if (null == activeGoodList || activeGoodList.size() == 0)
            return list;
        for (ActiveGood ag : activeGoodList) {
            ActiveGoodEx ex = new ActiveGoodEx();
            BeanUtils.copyProperties(ag, ex);
            ex.setGotNum(SoldNumUtils.genSoldNum(ag.getGoodSpu(), 0));
            list.add(ex);
        }
        return list;
    }

    @Override
    public Page<ActiveGood> bossGoodsList(ActiveGood activeGood) {
        Page<ActiveGood> page = new Page<>(activeGood.getCurrentPage(), activeGood.getPageSize());
        Integer busId = activeGood.getBusinessId();
        if (null == busId)
            busId = 1;
        FreeInfo info = selectById(busId);
        Assert.notNull(info, "活动暂未创建");

        activeGood.setActiveType(ActiveGoodsConstants.ActiveType.FREE_ORDER);
        activeGood.setBusinessId(busId);
        EntityWrapper<ActiveGood> wrapper = new EntityWrapper<>();

        if (StringUtils.isNotBlank(activeGood.getGoodName()))
            wrapper.like("GOOD_NAME", activeGood.getGoodName());
        if (StringUtils.isNotBlank(activeGood.getGoodSpu()))
            wrapper.like("GOOD_SPU", activeGood.getGoodSpu());
        activeGood.setGoodSpu(null);
        activeGood.setGoodName(null);
        wrapper.setEntity(activeGood);
        Page<ActiveGood> list = activeGoodService.selectPage(page, wrapper);
        for (ActiveGood ag : list.getRecords()) {
            Object usedStock = redisTemplate.opsForValue().get(CommonConstant.GOOD_STOCK_OCCUPY + ag.getGoodSku());
            Object sumStock = redisTemplate.opsForValue().get(ActiveGoodsConstants.SKU_STOCK + ag.getGoodSku());
            log.info("查询免费送商品总库存:{},已使用库存:{}", sumStock, usedStock);
            if (null == sumStock) {
                ag.setActiveStore(0);
                continue;
            }
            if (null == usedStock) {
                ag.setActiveStore(Integer.parseInt(sumStock.toString()));
                continue;
            }
            ag.setActiveStore(Integer.parseInt(sumStock.toString()) - Integer.parseInt(usedStock.toString()));
        }
        return list;
    }

    @Override
    public void saveOrUpdate(List<ActiveGood> list) {
        FreeInfo info = selectOne(new EntityWrapper<>());
        if (null == info || null == info.getActiveId()) {
            //创建
            info = new FreeInfo();
            info.setActiveName("第一个免费送活动");
            info.setActiveStatus(1);
            info.setCreaterId(1L);
            info.setCreaterTime(new Date());
            insert(info);
        }
        Assert.notNull(info.getActiveId(), "获取免费送活动失败");
        for (ActiveGood good : list) {
            if (null == good.getMapperyId()) {
                //新增
                good.setActiveType(ActiveGoodsConstants.ActiveType.FREE_ORDER);
                good.setBusinessId(info.getActiveId());
            }
            Assert.notNull(good.getGoodId(), "商品id不能为空");
            Assert.notNull(good.getGoodImage(), "商品图片不能为空");
            Assert.notNull(good.getGoodName(), "商品名不能为空");
            Assert.notNull(good.getGoodSpu(), "商品SPU不能为空");
            Assert.notNull(good.getActivePrice(), "商品活动价不能为空");
            Assert.notNull(good.getBasePrice(), "商品市场价不能为空");
            Assert.notNull(good.getMemberPrice(), "商品会员价不能为空");
        }
        activeGoodService.insertOrUpdateBatch(list);
    }

    @Override
    @Transactional
    public boolean deleteGood(List<Long> mapperId) {
        return activeGoodService.deleteBatchIds(mapperId);
    }

    @Override
    @Transactional
    public boolean onOrOff(List<Long> mapperId, boolean bool) {
        List<ActiveGood> goodList = new ArrayList<>();
        for (Long mapperyId : mapperId) {
            ActiveGood ag = new ActiveGood();
            ag.setMapperyId(mapperyId);
            ag.setGoodStatus(bool ? "1" : "0");
            goodList.add(ag);
        }
        return activeGoodService.updateBatchById(goodList);
    }


    private static final String[] nickNames = {"小东", "不辜负最美的自己", "明眸", "sonder", "花败须相依",
            "Boyfriend.", "浅夏微涼彡", "A0000海鸥", "the shy", "江改改", "深深", "地头蛇", "向精致的自己出发",
            "你的胖虎.", "丶叶子_", "悦悦越越", "THE  Profiteer", "Sweet", "荟荟吖.", "稍纵即逝。", "海龙",
            "芭比米琪儿", "李贝贝", "吴晓敏、", "倔强的猫", "陌生人", "梅", "feel小雨", "爱你一辈子", "迷鹿", "小小",
            "兮", "祖滢", "A美得不像话", "释怀。", "芳", "念念不忘", "人生如戏，要学会演戏", "闹闹", "阿H", "小刚",
            "酷酷的我", "心仪.", "初颜", "田秀兰", "Y    晴", "仙女儿", "阿拉丁神灯", "小可爱", "妞妞", "雾",
            "阿星", "心若阳光", "wulanx", "真爱永恒李远", "AAA纯银饰品批发〜佩佩", "往后余生^O^", "嘉嘉", "小赵",
            "哆哖以銗", "回忆", "X", "琪琪琪啊", "CHANYEOL", "Oxygenゞ", "无为", "路痴", "nabalalalala", "寂梦"
    };

    private int genIndex(int max) {
        return new Random().nextInt(max);
    }
}