package com.mmj.order.controller;


import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.mmj.common.context.BaseContextHandler;
import com.mmj.common.controller.BaseController;
import com.mmj.common.model.ReturnData;
import com.mmj.common.model.order.UserLotteryDto;
import com.mmj.common.properties.SecurityConstants;
import com.mmj.order.model.OrderGroup;
import com.mmj.order.model.OrderGroupJoin;
import com.mmj.order.service.OrderGroupJoinService;
import com.mmj.order.service.OrderGroupService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 订单团信息表 前端控制器
 * </p>
 *
 * @author lyf
 * @since 2019-06-10
 */
@RestController
@RequestMapping("/order/orderGroup")
@Slf4j
public class OrderGroupController extends BaseController {

    @Autowired
    private OrderGroupService orderGroupService;

    @Autowired
    private OrderGroupJoinService groupJoinService;


    @RequestMapping(value = "/completedGroupCount", method = RequestMethod.POST)
    public ReturnData<Integer> completedGroupCount(@RequestBody OrderGroup orderGroup) {

        if (null == orderGroup) {
            return initErrorObjectResult("orderGroup 不能为空");
        }

        Integer cnt = orderGroupService.getCompletedGroupCount(orderGroup);
        return initSuccessObjectResult(cnt);
    }

    @RequestMapping(value = "/completedGroupList", method = RequestMethod.POST)
    public ReturnData<List<OrderGroup>> getCompletedGroup(@RequestBody OrderGroup orderGroup) {

        if (null == orderGroup) {
            return initErrorObjectResult("orderGroup 不能为空");
        }

        List<OrderGroup> list = orderGroupService.getCompletedGroupList(orderGroup);
        return initSuccessObjectResult(list);
    }

    @ApiOperation("查询已经获得免费送红包用户列表")
    @RequestMapping(value = "/redPackList", method = RequestMethod.POST)
    public List<Map<String, Object>> getRedPackList() {
        return orderGroupService.getRedPackList();
    }


    @ApiOperation("查询团信息")
    @RequestMapping(value = "/groupInfo", method = RequestMethod.POST)
    public OrderGroup getGroupInfo(@RequestBody OrderGroup group) {
        log.info("免费送活动查询团信息入参:{}", group);
        BaseContextHandler.set(SecurityConstants.SHARDING_GROUP_KEY, null == group.getGroupNo() ? "MFS" :
                group.getGroupNo());
        EntityWrapper<OrderGroup> wrapper = new EntityWrapper<>(group);
        OrderGroup orderGroup = orderGroupService.selectOne(wrapper);
        log.info("查询结果:{}", orderGroup);
        BaseContextHandler.set(SecurityConstants.SHARDING_GROUP_KEY, null);
        return orderGroup;
    }

    @ApiOperation("查询团员关联信息")
    @RequestMapping(value = "/groupJoin", method = RequestMethod.POST)
    public List<OrderGroupJoin> getGroupJoin(@RequestBody OrderGroupJoin group) {
        log.info("查询团员关联信息##:{}", group);
        EntityWrapper<OrderGroupJoin> wrapper = new EntityWrapper<>(group);
        BaseContextHandler.set(SecurityConstants.SHARDING_GROUP_KEY, group.getGroupNo());
        List<OrderGroupJoin> gList = groupJoinService.selectList(wrapper);
        log.info("查询团员结果##:{}", gList);
        return gList;
    }


    @ApiOperation("查询参与活动的用户订单信息")
    @RequestMapping(value = "/getJoinUser/{lotteryId}", method = RequestMethod.POST)
    public List<UserLotteryDto> getJoinUser(@PathVariable("lotteryId") Integer lotteryId) {
        return groupJoinService.getJoinUser(lotteryId);
    }

}

