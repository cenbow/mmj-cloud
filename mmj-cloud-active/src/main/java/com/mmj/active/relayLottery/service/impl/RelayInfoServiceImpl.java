package com.mmj.active.relayLottery.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.mmj.active.common.constants.ActiveGoodsConstants;
import com.mmj.active.common.feigin.OrderFeignClient;
import com.mmj.active.common.feigin.UserFeignClient;
import com.mmj.active.common.model.ActiveGood;
import com.mmj.active.common.model.OrderGroup;
import com.mmj.active.common.model.UserActive;
import com.mmj.active.common.service.ActiveGoodService;
import com.mmj.active.relayLottery.mapper.RelayInfoMapper;
import com.mmj.active.relayLottery.model.RelayInfo;
import com.mmj.active.relayLottery.model.dto.*;
import com.mmj.active.relayLottery.model.vo.RelayInfoVo;
import com.mmj.active.relayLottery.service.RelayInfoService;
import com.mmj.common.model.BaseUser;
import com.mmj.common.model.ReturnData;
import com.mmj.common.utils.DateUtils;
import com.mmj.common.utils.ResultUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;


/**
 * <p>
 * 接力购抽奖表 服务实现类
 * </p>
 *
 * @author zhangyicao
 * @since 2019-06-04
 */
@Service
public class RelayInfoServiceImpl extends ServiceImpl<RelayInfoMapper, RelayInfo> implements RelayInfoService {

    private Logger logger = LoggerFactory.getLogger(RelayInfoServiceImpl.class);

    @Autowired
    private RelayInfoMapper relayInfoMapper;
    @Autowired
    private OrderFeignClient orderFeignClient;
    @Autowired
    private UserFeignClient userFeignClient;
    @Autowired
    private ActiveGoodService activeGoodService;


    /**
     * 保存抽奖活动配置
     * @param relayInfoVo
     * @return
     */
    @Override
    @Transactional
    public Object saveConfigure(RelayInfoVo relayInfoVo){
        //新增
        if(null == relayInfoVo.getRelayId()){
            logger.info("新增接力购抽奖：{}",relayInfoVo.toString());
            //写抽奖配置表
            save(relayInfoVo);
        }else{//编辑
            logger.info("编辑接力购抽奖：{}",relayInfoVo.toString());
            RelayInfo relayInfo = relayInfoMapper.selectById(relayInfoVo.getRelayId());
            if(relayInfo != null){
                //判断距离活动开始时间是否小于5分钟
                long secondNum = DateUtils.subInterval(new Date(),relayInfoVo.getStartTime());
                if(secondNum < 300){
                    return ResultUtil.error(-1,"距离活动开始时间不到5分钟了，不能编辑");
                }else{
                    //原配置删除
                    EntityWrapper<RelayInfo> relayInfoEntityWrapper = new EntityWrapper<>();
                    relayInfoEntityWrapper.eq("UNION_ID",relayInfo.getUnionId());
                    relayInfoMapper.delete(relayInfoEntityWrapper);
                    save(relayInfoVo);
                }
            }
        }
        return ResultUtil.success("保存成功");
    }

    /**
     * 写入配置
     */
    public void save(RelayInfoVo relayInfoVo){
        String uuid = UUID.randomUUID().toString();
        Date lastEndTime = null;
        for (int i = 0; i < relayInfoVo.getPeriods(); i++) {
            RelayInfo relayInfo = new RelayInfo();
            BeanUtils.copyProperties(relayInfoVo,relayInfo);
            relayInfo.setUnionId(uuid);
            relayInfo.setCreaterTime(new Date());
            relayInfo.setPeriod(i+1);
            relayInfo.setFristStartTime(relayInfo.getStartTime());
            relayInfo.setFristEndTime(relayInfo.getEndTime());
            relayInfo.setOpenFlag(0);
            if (i == 0) { //第一期直接写入
                lastEndTime = relayInfo.getEndTime();
                relayInfo.setOpenTime(relayInfo.getFristEndTime());
                relayInfoMapper.insert(relayInfo);
            } else {
                //计算开始时间
                Calendar cal = Calendar.getInstance();
                cal.setTime(lastEndTime);//上一期的结束时间
                cal.add(Calendar.HOUR, relayInfo.getIntervalTime());// 加上间隔时间
                Date startTime = cal.getTime();
                relayInfo.setStartTime(startTime);

                //计算结束时间
                cal.setTime(startTime);
                cal.add(Calendar.HOUR, relayInfo.getActiveTime());// 开始时间加上活动持续时间
                Date endTime = cal.getTime();
                relayInfo.setEndTime(endTime);
                lastEndTime = endTime;
                relayInfo.setOpenTime(endTime);
                relayInfoMapper.insert(relayInfo);
            }

            //写商品表
            ActiveGood activeGood = new ActiveGood();
            activeGood.setGoodId(relayInfoVo.getGoodId());
            activeGood.setSaleId(relayInfoVo.getSaleId());
            activeGood.setBusinessId(relayInfo.getRelayId());
            activeGood.setActiveType(ActiveGoodsConstants.ActiveType.GROUP_RELAY_LOTTERY);
            activeGood.setGoodName(relayInfoVo.getGoodName());
            activeGood.setGoodImage(relayInfoVo.getGoodImage());
            activeGoodService.insert(activeGood);
        }
    }

    /**
     * 开启/关闭活动
     * @param parame
     * @return
     */
    @Override
    public Object onOff(String parame) {
        JSONObject jsonObject = JSONObject.parseObject(parame);
        Long relayId = jsonObject.getLong("id");
        Integer status = jsonObject.getInteger("status");
        //关闭活动时
        RelayInfo relayInfo = relayInfoMapper.selectById(relayId);
        if(status == 0 && relayInfo != null){
            EntityWrapper<RelayInfo> relayInfoEntityWrapper = new EntityWrapper<>();
            relayInfoEntityWrapper.eq("UNION_ID",relayInfo.getUnionId());
            relayInfoEntityWrapper.ge("START_TIME",new Date());
            relayInfoEntityWrapper.gt("END_TIME",new Date());
            List<RelayInfo> infos = relayInfoMapper.selectList(relayInfoEntityWrapper);
            for(RelayInfo info : infos){
                if(info.getOpenFlag() == 1 ){
                    break;
                }
//                OrderGroup orderGroup = new OrderGroup();
//                orderGroup.setBusinessId(relayInfo.getRelayId());
//                orderGroup.setGroupType(ActiveGoodsConstants.ActiveType.GROUP_RELAY_LOTTERY);
//                List<OrderGroup> orderGroups = getOrderGroup(orderGroup);
//                Set<String> orderNoSet = orderGroups.stream().map(OrderGroup::getLaunchOrderNo).collect(Collectors.toSet());
//
//                List<String> orderNos = new ArrayList<>(orderNoSet);
                //TODO 执行退款

                //TODO 更新状态订单状态为已关闭

                //TODO 更新活动状态
                RelayInfo r = new RelayInfo();
                r.setRelayStatus(status);
                EntityWrapper<RelayInfo> infoEntityWrapper = new EntityWrapper<>();
                relayInfoEntityWrapper.eq("RELAY_ID",info.getRelayId());
                relayInfoMapper.update(r,infoEntityWrapper);
            }
        }

        //修改活动状态
        RelayInfo info = new RelayInfo();
        info.setRelayStatus(status);
        EntityWrapper<RelayInfo> relayInfoEntityWrapper = new EntityWrapper<>();
        relayInfoEntityWrapper.eq("RELAY_ID",relayId);
        relayInfoMapper.update(info,relayInfoEntityWrapper);
        return ResultUtil.success();
    }

    /**
     * 逻辑删除活动配置
     * @return
     */
    @Override
    public Object del(int relayId){
        RelayInfo info = new RelayInfo();
        info.setRelayStatus(-1);

        RelayInfo relayInfo = relayInfoMapper.selectById(relayId);

        EntityWrapper<RelayInfo> relayInfoEntityWrapper = new EntityWrapper<>();
        relayInfoEntityWrapper.eq("UNION_ID",relayInfo.getUnionId());
        relayInfoMapper.update(info,relayInfoEntityWrapper);
        return ResultUtil.success();
    }


    /**
     * 查询活动列表
     * @param relayInfoVo
     * @return
     */
    @Override
    public Page<RelayInfoVo> queryList(RelayInfoVo relayInfoVo){
        logger.info("boss后台查询接力购抽奖列表入参：{}",relayInfoVo.toString());
        Page<RelayInfoVo> page = new Page<>(relayInfoVo.getCurrentPage(), relayInfoVo.getPageSize());
        List<RelayInfoVo> relayInfos = relayInfoMapper.queryRelayInfoList(page,relayInfoVo);
        page.setRecords(relayInfos);
        return page;
    }

    /**
     * 接力购抽奖列表
     */
    @Override
    public List<LotteryListDto> lotteryList(Long userId){
        List<LotteryListDto> lotteryListDtoslist = new ArrayList<>();
        //查询活动配置子表
        EntityWrapper<RelayInfo> relayInfoEntityWrapper = new EntityWrapper<>();
        relayInfoEntityWrapper.eq("RELAY_STATUS",1);
        relayInfoEntityWrapper.le("START_TIME",new Date());
        relayInfoEntityWrapper.ge("END_TIME",new Date());
        List<RelayInfo> relayInfos = relayInfoMapper.selectList(relayInfoEntityWrapper);

        //查询商品
        for(RelayInfo info : relayInfos){
            LotteryListDto lotteryListDto = new LotteryListDto();
            lotteryListDto.setLotteryactivityid(info.getRelayId());
            lotteryListDto.setPeriodName(info.getPeriod()+"期");//期名称

            ActiveGood activeGood = new ActiveGood();
            activeGood.setActiveType(ActiveGoodsConstants.ActiveType.GROUP_RELAY_LOTTERY);
            activeGood.setBusinessId(info.getRelayId());
            EntityWrapper<ActiveGood> activeGoodEntityWrapper = new EntityWrapper<>(activeGood);
            activeGood = activeGoodService.selectOne(activeGoodEntityWrapper);

            lotteryListDto.setGoodId(activeGood.getGoodId());
            lotteryListDto.setGoodImage(activeGood.getGoodImage());//商品图片
            lotteryListDto.setGoodName(activeGood.getGoodName());//商品标题
            lotteryListDto.setRelayNumber(info.getOpenNum() + info.getVirtualNum());//活动接力人数
            lotteryListDto.setLackNumber(queryLackNumber(info.getRelayId(),info.getOpenNum() + info.getVirtualNum()));//还差多少人
            lotteryListDto.setHeadList(queryHeadSculptureList(info.getRelayId()));//头像List
            lotteryListDtoslist.add(lotteryListDto);
        }
        return lotteryListDtoslist;
    }

    /**
     * 查询活动详情
     * @param lotteryId
     * @return
     */
    @Override
    public RelayInfoVo lotteryReleyInfo(long lotteryId){
        RelayInfo relayInfo = relayInfoMapper.selectById(lotteryId);
        RelayInfoVo vo = new RelayInfoVo();
        BeanUtils.copyProperties(relayInfo,vo);

        ActiveGood activeGood = new ActiveGood();
        activeGood.setActiveType(ActiveGoodsConstants.ActiveType.GROUP_RELAY_LOTTERY);
        activeGood.setBusinessId(relayInfo.getRelayId());
        EntityWrapper<ActiveGood> activeGoodEntityWrapper = new EntityWrapper<>(activeGood);
        activeGood = activeGoodService.selectOne(activeGoodEntityWrapper);

        if(activeGood != null){
            vo.setGoodId(activeGood.getGoodId());
            vo.setGoodImage(activeGood.getGoodImage());
            vo.setGoodName(activeGood.getGoodName());
        }

        return vo;
    }

    /**
     * 我的抽奖列表
     * @param userId
     * @return
     */
    @Override
    public List<LotteryListDto> myLotteryList(Long userId){
        List<LotteryListDto> lotteryListDtoslist = new ArrayList<>();
        //查询参与信息
        OrderGroup orderGroup = new OrderGroup();
        orderGroup.setGroupType(ActiveGoodsConstants.ActiveType.GROUP_RELAY_LOTTERY);
        orderGroup.setLaunchUserId(userId);
        List<OrderGroup> orderGroups = getOrderGroup(orderGroup);

        for(OrderGroup group : orderGroups){
            RelayInfo info = relayInfoMapper.selectById(group.getBusinessId());
            LotteryListDto lotteryListDto = new LotteryListDto();
            lotteryListDto.setLotteryactivityid(info.getRelayId());
            lotteryListDto.setPeriodName(info.getPeriod()+"期");//期名称

            ActiveGood activeGood = new ActiveGood();
            activeGood.setBusinessId(info.getRelayId());
            activeGood.setActiveType(ActiveGoodsConstants.ActiveType.GROUP_RELAY_LOTTERY);
            EntityWrapper<ActiveGood> activeGoodEntityWrapper = new EntityWrapper<>(activeGood);
            activeGood = activeGoodService.selectOne(activeGoodEntityWrapper);

            lotteryListDto.setGoodId(activeGood.getGoodId());
            lotteryListDto.setGoodImage(activeGood!=null?activeGood.getGoodImage():"");//商品图片
            lotteryListDto.setGoodName(activeGood!=null?activeGood.getGoodName():"");//商品标题
            lotteryListDto.setRelayNumber(info.getOpenNum() + info.getVirtualNum());//活动接力人数
            lotteryListDto.setLackNumber(queryLackNumber(info.getRelayId(),info.getOpenNum()+info.getVirtualNum()));//还差多少人
            lotteryListDto.setHeadList(queryHeadSculptureList(info.getRelayId()));//头像List
            lotteryListDto.setStatus(group.getGroupStatus());//进行中|拼团成功

            //未成团且已过期
            if(group.getGroupStatus() == 0 && info.getEndTime().getTime() < new Date().getTime()){
                lotteryListDto.setStatus(2);
            }

            if(info.getRelayStatus() == 0){//活动已经关闭为取消状态
                lotteryListDto.setStatus(3);
            }else if(info.getRelayStatus() == 4){//开奖后结束
                lotteryListDto.setStatus(4);
            }

            lotteryListDto.setCurrentPeople(group.getCurrentPeople().longValue()-1);//当前人数
            lotteryListDto.setGroupPeople(Long.valueOf(info.getRelayNum()));//团所需人数
            lotteryListDto.setOrderNo(group.getLaunchOrderNo());
            lotteryListDto.setGroupNo(group.getGroupNo());
            lotteryListDtoslist.add(lotteryListDto);
        }
        return lotteryListDtoslist;
    }

    /**
     *
     * 查询活动开奖结果
     * @param parame
     * @return
     */
    @Override
    public Object queryLotteryResult(String parame){
        JSONObject jsonObject = JSONObject.parseObject(parame);
        Long userid = Long.valueOf(jsonObject.get("userid").toString());
        String groupNo = jsonObject.get("groupNo").toString();

        //根据团号找活动
        int actId = 0;
        OrderGroup orderGroup = new OrderGroup();
        orderGroup.setGroupNo(groupNo);
        List<OrderGroup> orderGroups = getOrderGroup(orderGroup);
        if(orderGroups.isEmpty()){
            logger.info("未找到参团信息{}",groupNo);
            return ResultUtil.error(-1,"未找到参团信息");
        }
        orderGroup = orderGroups.get(0);

        if(orderGroup != null ){
            actId = orderGroup.getBusinessId();
        }

        RelayInfo info = relayInfoMapper.selectById(actId);
        if(info == null){
            logger.info("活动不存在{}",actId);
            return ResultUtil.error(-1,"活动不存在");
        }

        LotteryResultDto lotteryResultDto = new LotteryResultDto();
        lotteryResultDto.setStatus(info.getOpenFlag());//是否开奖
        lotteryResultDto.setOpencode(info.getCheckCode());//开奖码
        lotteryResultDto.setDistanceOpenTime(DateUtils.subInterval(new Date(),info.getEndTime()));//距离开奖时间
        lotteryResultDto.setLackNumber(queryLackNumber(info.getRelayId(),info.getOpenNum()+info.getVirtualNum()));//还差多少人
        if(info.getOpenFlag() == 1 && userid.equals(info.getCheckMan())){
            lotteryResultDto.setLotteryResult(1);//中奖
        }else if(info.getOpenFlag() == 1 && !userid.equals(info.getCheckMan())){
            lotteryResultDto.setLotteryResult(2);//中奖
        }
        if(info.getOpenFlag() == 2){
            lotteryResultDto.setLotteryResult(3);//未达到参与人数
        }

        //查询是否关注公众号
        JSONObject json = new JSONObject();
        json.put("userid",jsonObject.get("userid").toString());
        json.put("module","10");//买买家公众号
        json.put("type","1");
        lotteryResultDto.setIsFollow(getIsFollow(json));

        lotteryResultDto.setActid(info.getRelayId());

        //抽奖码
        UserActive ua = new UserActive();
        ua.setActiveType(ActiveGoodsConstants.ActiveType.GROUP_RELAY_LOTTERY);
        ua.setBusinessId(actId);
        ua.setUserId(userid);
        UserActive userActive = userFeignClient.queryWinner(ua);
        if(userActive != null){
            lotteryResultDto.setWincode(!"".equals(userActive.getLotteryCode())?userActive.getLotteryCode():"");
        }
        return ResultUtil.success(lotteryResultDto);
    }


    /**
     * 接力购抽奖团信息
     * @param parame
     * @return
     */
    @Override
    public Object queryGroup(String parame){
        logger.info("接力购抽奖获取团信息入参：{}",parame);
        JSONObject jsonObject = JSONObject.parseObject(parame);
        Long userid = Long.valueOf(jsonObject.get("userid").toString());
        String groupNo = jsonObject.get("groupNo").toString();
        String orderNo = jsonObject.get("orderNo").toString();
        int actId = 0;

        //根据订单查询活动
        OrderGroup orderGroup = new OrderGroup();
        orderGroup.setGroupNo(groupNo);
        orderGroup.setGroupType(ActiveGoodsConstants.ActiveType.GROUP_RELAY_LOTTERY);
        List<OrderGroup> orderGroups = getOrderGroup(orderGroup);
        if(orderGroups.isEmpty()){
            logger.info("未找到参团信息{}",groupNo);
            return ResultUtil.error(-1,"未找到参团信息");
        }
        orderGroup = orderGroups.get(0);

        if(orderGroup != null){
            actId = orderGroup.getBusinessId();
        }

        RelayInfo info = relayInfoMapper.selectById(actId);
        if(info == null){
            logger.info("接力购抽奖活动不存在{}",actId);
            return ResultUtil.error(-1,"接力购抽奖活动不存在");
        }

        //查询团信息
        LotteryGroupDto lotteryGroupDto = new LotteryGroupDto();
        GroupInfoDto groupInfoDto = orderFeignClient.groupInfo(userid,groupNo,orderNo);
        BeanUtils.copyProperties(groupInfoDto,lotteryGroupDto);

        lotteryGroupDto.setHaveNumber(successNumber(actId));//已有多少人参与
        lotteryGroupDto.setDistanceOpenTime(DateUtils.subInterval(new Date(),info.getEndTime()));//距离开奖时间
        lotteryGroupDto.setLackNumber(queryLackNumber(actId,info.getOpenNum()+info.getVirtualNum()));

        lotteryGroupDto.setOpencode(!"".equals(info.getCheckCode()) ? info.getCheckCode():"");//开奖码

        //查询抽奖码
        UserActive ua = new UserActive();
        ua.setUserId(userid);
        ua.setActiveType(ActiveGoodsConstants.ActiveType.GROUP_RELAY_LOTTERY);
        ua.setOrderNo(orderNo);
        UserActive userActive = userFeignClient.queryWinner(ua);
        if(userActive != null){
            lotteryGroupDto.setWincode(userActive.getLotteryCode());//抽奖码
        }

        lotteryGroupDto.setActStatus(info.getRelayStatus());//活动状态
        lotteryGroupDto.setActiveId(Long.valueOf(info.getRelayId()));

        //查询是否关注公众号
        JSONObject json = new JSONObject();
        json.put("userid",jsonObject.get("userid").toString());
        json.put("module","10");//买买家公众号
        json.put("type","1");
        lotteryGroupDto.setIsFollow(getIsFollow(json));

        return ResultUtil.success();
    }


    /**
     * 开奖详情页
     * @param parame
     * @return
     */
    @Override
    public Object queryOpenLotteryInfo(String parame) {
        JSONObject jsonObject = JSONObject.parseObject(parame);
        Long userid = Long.valueOf(jsonObject.get("userid").toString());
        Long actid = Long.valueOf(jsonObject.get("actid").toString());
        RelayInfo info = relayInfoMapper.selectById(actid);
        if (info == null) {
            logger.info("活动不存在{}", actid);
            return ResultUtil.error(-1,"活动不存在");
        }
//        LotteryOpen lotteryOpen = lotteryOpenMapper.getLotteryOpenByActId(actid.intValue());
//        if (lotteryOpen == null) {
//            logger.info("活动对应开奖信息不存在{}", actid);
//            return R.error("活动对应开奖信息不存在");
//        }

        OpenLotteryInfo openLotteryInfo = new OpenLotteryInfo();
        openLotteryInfo.setLotteryResult(info.getOpenFlag());//开奖结果
        openLotteryInfo.setLackNumber(queryLackNumber(info.getRelayId(),(info.getOpenNum() + info.getVirtualNum())));//还差多少人

        //查询活动商品
//        RelayLotteryGoods rlg = relayLotteryGoodsMapper.selectByPrimaryKey(rlcs.getLotteryGoodsId());
//        if(rlg != null){
//            Goodsbase goodsbase = goodsbaseMapper.selectByPrimaryKey(rlg.getGoodsbaseid(), "2");
//            if(goodsbase!=null && null != goodsbase.getTitle()){
//                openLotteryInfo.setGoodsName(goodsbase.getTitle());//商品标题
//            }
//        }

        openLotteryInfo.setStartTime(info.getStartTime());
        openLotteryInfo.setEndTime(info.getEndTime());
        openLotteryInfo.setPeriodsName(info.getPeriod()+"期");
        openLotteryInfo.setRunLotteryNumber(info.getOpenNum() + info.getVirtualNum());
        openLotteryInfo.setRunTime(info.getEndTime());

        openLotteryInfo.setJoinNumber(successNumber(actid.intValue()));//参与人数

        openLotteryInfo.setOpenCode(!"".equals(info.getCheckCode()) ? info.getCheckCode():"");//开奖码

        openLotteryInfo.setHeadList(queryHeadSculptureList(actid.intValue()));

        openLotteryInfo.setHead(queryHeda(null!=info.getCheckMan() ? info.getCheckMan():0L));//开奖人头像

        return ResultUtil.success(openLotteryInfo);
    }


    /**
     * 根据订单类型查询是否有下过订单（接力购抽奖防刷机制）
     *      *
     * @return
     */
    @Override
    public int getJieligouCount() {
//        OrdersExample ordersExample = new OrdersExample();
//        OrdersExample.Criteria criteria = ordersExample.createCriteria();
//        criteria.andActiveEqualTo(true);
//        criteria.andOrderTypeEqualTo(orderVo.getOrderType());
//        criteria.andCreateByEqualTo(orderVo.getUserid());
//        if (orderVo.getOrderChildType() != null) {
//            criteria.andOrderChildTypeEqualTo(orderVo.getOrderChildType());
//        }
//        List<Integer> statsList = new ArrayList<>();
//        statsList.add(OrderStatus.PENDING_PAYMENT.getStatus());//待付款
//        statsList.add(OrderStatus.TO_BE_A_GROUP.getStatus());//待成团
//        statsList.add(OrderStatus.TO_BE_DELIVERED.getStatus());//待发货
//        statsList.add(OrderStatus.PENDING_RECEIPT.getStatus());//配送中
//        statsList.add(OrderStatus.COMPLETED.getStatus());//已完成
//        statsList.add(OrderStatus.CLOSED.getStatus());//已关闭
//        criteria.andOrderStatusIn(statsList);
//        List<Orders> orders = ordersMapper.selectByExample(ordersExample);
//        return orders.size();
        return 1;
    }


    /**
     * 查询还差多少人
     * @return
     */
    @Override
    public Integer queryLackNumber(Integer id,Integer relayNumber){
        Integer groupNumber = successNumber(id);
        return relayNumber - groupNumber;
    }

    /**
     * 查询成团多少人
     * @param id
     * @return
     */
    @Override
    public Integer successNumber(Integer id){
        RelayInfo relayInfo = relayInfoMapper.selectById(id);
        OrderGroup orderGroup = new OrderGroup();
        orderGroup.setBusinessId(relayInfo.getRelayId());
        orderGroup.setGroupStatus(1);
        Integer count = 0;
//        orderGroupMapper.selectFullGroupCount(orderGroup) + relayInfo.getVirtualNum()
        return count;
    }

    /**
     * 查询参团头像List
     * @return
     */
    @Override
    public List<LotteryListDto.Member> queryHeadSculptureList(Integer actId){
        List<LotteryListDto.Member> list = new ArrayList<>();
        OrderGroup orderGroup = new OrderGroup();
        orderGroup.setBusinessId(actId);
        orderGroup.setGroupType(ActiveGoodsConstants.ActiveType.GROUP_RELAY_LOTTERY);
        orderGroup.setGroupStatus(1);
        List<OrderGroup> orderGroups = getOrderGroup(orderGroup);

        //去重
        Set<OrderGroup> treeSet = new TreeSet<OrderGroup>(new Comparator<OrderGroup>(){
            @Override
            public int compare(OrderGroup r1, OrderGroup r2) {
                int compareTo = r1.getLaunchUserId().compareTo(r2.getLaunchUserId());
                return compareTo;
            }
        });
        treeSet.addAll(orderGroups);
        //放入新的list 或者把当前的list进行close
        List<OrderGroup> arrayList = new ArrayList<>(treeSet);

        for(OrderGroup group : arrayList){
            BaseUser baseUser = userFeignClient.getUserById(group.getLaunchUserId());
            list.add(getMember(baseUser));
        }
        return list;
    }

    public LotteryListDto.Member queryHeda(Long userid){
        BaseUser baseUser = userFeignClient.getUserById(userid);
        return getMember(baseUser);
    }

    /**
     * 获取团信息
     * @param orderGroup
     * @return
     */
    public List<OrderGroup> getOrderGroup(OrderGroup orderGroup){

        ReturnData<List<OrderGroup>> returnData = orderFeignClient.completedGroupList(orderGroup);
        List<OrderGroup> orderGroups = new ArrayList<>();
        OrderGroup og = new OrderGroup();
        for(Object o : returnData.getList()){
            BeanUtils.copyProperties(o,og);
            orderGroups.add(og);
            og = null;
        }
        return orderGroups;
    }

    /**
     * 用户信息转换
     * @return
     */
    private LotteryListDto.Member getMember(BaseUser baseUser) {
        if (Objects.isNull(baseUser)) return null;
        return new LotteryListDto.Member(baseUser.getUserId(), baseUser.getImagesUrl(),
                baseUser.getUserFullName(), baseUser.getUnionId());
    }

    public  Integer getIsFollow(JSONObject json){
//        try{
//
//            SysOfficialAccountsDto focus = officialFocusInfoService.isFocus(json.toString());
//            if(focus != null && null !=focus.getAttention()){
//                if(focus.getAttention() == 1 || focus.getAttention() == 3 || focus.getAttention() == 4){
//                    return 1;//已关注
//                }else{
//                    return focus.getAttention();
//                }
//            }
//        }catch (Exception e){
//            logger.info("接力购抽奖查询是否关注公众号报错：{}",e);
//        }
        return 0;
    }

}
