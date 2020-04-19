package com.mmj.user.recommend.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.plugins.Page;
import com.mmj.common.annotation.ApiWaitForCompletion;
import com.mmj.common.controller.BaseController;
import com.mmj.common.model.JwtUserDetails;
import com.mmj.common.model.ReturnData;
import com.mmj.common.utils.SecurityUserUtil;
import com.mmj.user.recommend.model.UserRecommend;
import com.mmj.user.recommend.model.UserRecommendEx;
import com.mmj.user.recommend.model.vo.UserRecommendOrder;
import com.mmj.user.recommend.model.vo.UserRecommendVo;
import com.mmj.user.recommend.service.UserRecommendService;

/**
 * <p>
 * 用户推荐表 前端控制器
 * </p>
 *
 * @author dashu
 * @since 2019-06-18
 */
@RestController
@RequestMapping("/recommend/userRecommend")
@Api("用户推荐")
@Slf4j
public class UserRecommendController extends BaseController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private UserRecommendService userRecommendService;

    @ApiWaitForCompletion
    @ApiOperation(value = "保存")
    @PostMapping("save")
    public ReturnData<Object> save(@RequestBody UserRecommendVo userRecommendVo) {
        log.info("-->/recommend/userRecommend/save-->推荐保存，用户id:{},参数：{}", SecurityUserUtil.getUserDetails().getUserId(), JSON.toJSONString(userRecommendVo));
        return initSuccessObjectResult(userRecommendService.save(userRecommendVo));
    }


    @ApiOperation(value = "修改")
    @PostMapping("update")
    public ReturnData<Object> update(@RequestBody UserRecommend userRecommend) {
        return initSuccessObjectResult(userRecommendService.updateUserRecommend(userRecommend));
    }

    @ApiOperation(value = "推荐列表查询 - boss后台")
    @PostMapping("queryList")
    public ReturnData<Page<UserRecommendEx>> query(@RequestBody UserRecommend userRecommend) {
        return initSuccessObjectResult(userRecommendService.queryList(userRecommend));
    }

    @ApiOperation(value = "批量审核")
    @PostMapping("batchRecommend")
    public ReturnData<Object> batchRecommend(@RequestBody List<UserRecommend> list) {
        return initSuccessObjectResult(userRecommendService.updateByRecommendId(list));
    }

    @ApiOperation(value = "推荐列表查询 - 小程序")
    @PostMapping("selectRecommendList")
    public ReturnData<Object> selectRecommendList(@RequestBody UserRecommendVo userRecommendVo) {
        JwtUserDetails userDetails = SecurityUserUtil.getUserDetails();
        userRecommendVo.setCreaterId(userDetails.getUserId());
        log.info("-->/recommend/userRecommend/selectRecommendList-->推荐列表查询，用户id:{},参数：{}", SecurityUserUtil.getUserDetails().getUserId(), JSON.toJSONString(userRecommendVo));
        return initSuccessObjectResult(userRecommendService.selectRecommendList(userRecommendVo));
    }


    @ApiOperation(value = "根据id查询推荐详情 - 小程序")
    @PostMapping("selectByRecommendId/{recommendId}")
    public ReturnData<Object> selectByRecommendId(@PathVariable("recommendId") Integer recommendId) {
        log.info("-->/recommend/userRecommend/selectByRecommendId-->根据id查询推荐详情，用户id:{},参数：{}", SecurityUserUtil.getUserDetails().getUserId(), recommendId);
        return initSuccessObjectResult(userRecommendService.selectByRecommendId(recommendId));
    }

    @ApiOperation(value = "给订单调用, 判断该订单是展示 去写推荐 or 分享得返现")
    @PostMapping("selectByOrderNo")
    public ReturnData<List<UserRecommendOrder>> selectByOrderNo(@RequestBody Map<String, Object> map) {
        log.info("-->/recommend/userRecommend/selectByOrderNo-->订单调用，入参{}", JSON.toJSONString(map));
        List<String> orderNoList = (List<String>) map.get("orderNoList");
        Long createrId = Long.parseLong(map.get("createrId").toString());
        log.info("-->/recommend/userRecommend/selectByOrderNo-->订单调用，用户id参数：{},订单集合:{}", createrId, JSON.toJSONString(orderNoList));
        return initSuccessObjectResult(userRecommendService.selectByOrderNo(orderNoList, createrId));
    }

    @ApiOperation(value = "给订单调用, 判断该商品是展示 去写推荐 or 分享得返现")
    @PostMapping("selectByGoodSku")
    public ReturnData<List<UserRecommendOrder>> selectByGoodSku(@RequestBody Map<String, Object> map) {
        List<String> goodSku = (List<String>) map.get("goodSku");
        String orderNo = null == map.get("orderNo") ? null : map.get("orderNo").toString();
        Long createrId = null == map.get("createrId") ? null : Long.parseLong(map.get("createrId").toString());
        log.info("-->/recommend/userRecommend/selectByGoodSku-->订单调用，用户id参数：{},订单号:{},商品skuId:{}",
                createrId, orderNo, goodSku);
        return initSuccessObjectResult(userRecommendService.selectByGoodSku(goodSku, orderNo, createrId));
    }

    @ApiOperation(value = "会员返现中心-查询未写推荐的订单数量")
    @PostMapping("selectNORecommendOrderCont")
    public ReturnData<Integer> selectNORecommendOrderCont() {
        Long userId = SecurityUserUtil.getUserDetails().getUserId();
        log.info("-->/recommend/userRecommend/selectNORecommendOrderCont-->查询未写推荐的订单数量，用户id参数：{}", userId);
        return initSuccessObjectResult(userRecommendService.selectNORecommendOrderCont(userId));
    }

    @ApiOperation(value = "推荐-查询用户是否填写过该商品的推荐(小程序-商品详情页)")
    @PostMapping("getRecommendByUserid/{goodId}")
    public ReturnData<Object> getRecommendByUserid(@PathVariable("goodId") Integer goodId) {
        Long userId = SecurityUserUtil.getUserDetails().getUserId();
        log.info("-->/recommend/userRecommend/getRecommendByUserid-->查询用户是否填写过该商品的推荐，用户id参数：{}", userId);
        return initSuccessObjectResult(userRecommendService.getRecommendByUserid(userId, goodId));
    }

    /*@ApiOperation("数据合并,仅供测试")
    @PostMapping(value = "/updateUserId")
    public ReturnData<Object> updateUserId(@RequestBody UserMerge userMerge) {
        userRecommendService.updateUserId(userMerge);
        return  initSuccessObjectResult("success");
    }*/
}

