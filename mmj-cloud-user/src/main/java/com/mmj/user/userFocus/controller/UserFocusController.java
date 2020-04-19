package com.mmj.user.userFocus.controller;


import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.mmj.common.context.BaseContextHandler;
import com.mmj.common.controller.BaseController;
import com.mmj.common.model.JwtUserDetails;
import com.mmj.common.model.ReturnData;
import com.mmj.common.properties.SecurityConstants;
import com.mmj.common.utils.SecurityUserUtil;
import com.mmj.user.common.feigin.ActiveFeignClient;
import com.mmj.user.userFocus.constants.UserFocusConstants;
import com.mmj.user.userFocus.model.UserFocus;
import com.mmj.user.userFocus.service.UserFocusService;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 用户关注公众号记录 前端控制器
 * </p>
 *
 * @author shenfuding
 * @since 2019-07-16
 */
@RestController
@RequestMapping("/userFocus")
public class UserFocusController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(UserFocusController.class);

    @Autowired
    private UserFocusService userFocusService;

    @Autowired
    private ActiveFeignClient activeFeignClient;

    @Value("${weChatTmpId.officialAppid}")
    private String officialAppid;//公众号appid

    @ApiOperation(value = "查询关注信息")
    @RequestMapping(value = "/query", method = RequestMethod.POST)
    public ReturnData<UserFocus> query(@RequestBody UserFocus param) {
        if (null == param.getUserId()) {
            JwtUserDetails userDetails = SecurityUserUtil.getUserDetails();
            param.setUserId(userDetails.getUserId());
        }
        Integer form = param.getForm();
        Integer module = param.getModule();
        UserFocus userFocus = new UserFocus();
        userFocus.setAppId(officialAppid);
        //判断是否是查询抽奖的关注
        if (3 == module && (1 == form || 2 == form)) {
            int status = activeFeignClient.getRemind(param.getUserId(), form);
            userFocus.setStatus(status);
            userFocus.setReward(status);
            userFocus.setModule(module);
            userFocus.setForm(form);
            return initSuccessObjectResult(userFocus);
        }
        EntityWrapper<UserFocus> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("USER_ID", param.getUserId());
        entityWrapper.eq("FORM", form);
        if (UserFocusConstants.FocusForm.GROUP == form) {
            entityWrapper.eq("MODULE", param.getModule());
        } //else if (UserFocusConstants.FocusForm.OFFICIAL == form) {
        //entityWrapper.eq("MODULE", param.getModule());//多个公众号时扩展
        //}
        BaseContextHandler.set(SecurityConstants.SHARDING_KEY, param.getUserId());
        List<UserFocus> userFocuses = userFocusService.selectList(entityWrapper);
        if (userFocuses == null || userFocuses.isEmpty()) {
            userFocus.setStatus(UserFocusConstants.FocusStatus.NO_FOCUS);
        } else {
            userFocus = userFocuses.get(0);
        }
        BaseContextHandler.set(SecurityConstants.SHARDING_KEY, null);
        return initSuccessObjectResult(userFocus);
    }

    @RequestMapping(value = "/init", method = RequestMethod.POST)
    @ApiOperation(value = "初始化关注信息(公众号)")
    public ReturnData init(@RequestBody UserFocus userFocus) {
        logger.info("UserFocusController-init:初始化关注信息" + userFocus.getUserId());
        userFocus.setStatus(UserFocusConstants.FocusStatus.NO_FOCUS);
        userFocus.setReward(UserFocusConstants.FocusReward.NOT_GET);
        userFocus.setForm(UserFocusConstants.FocusForm.OFFICIAL);
        BaseContextHandler.set(SecurityConstants.SHARDING_KEY, userFocus.getUserId());
        userFocusService.insert(userFocus);
        BaseContextHandler.set(SecurityConstants.SHARDING_KEY, null);
        return initSuccessResult();
    }

    @RequestMapping(value = "/focusGroup", method = RequestMethod.POST)
    @ApiOperation(value = "初始化关注信息(群)")
    public ReturnData focusGroup(@RequestBody UserFocus userFocus) {
        logger.info("UserFocusController-focusGroup:初始化关注信息" + userFocus.getUserId());
        userFocus.setStatus(UserFocusConstants.FocusStatus.FOCUS);
        userFocus.setReward(UserFocusConstants.FocusReward.NOT_GET);
        userFocus.setForm(UserFocusConstants.FocusForm.GROUP);
        BaseContextHandler.set(SecurityConstants.SHARDING_KEY, userFocus.getUserId());
        userFocusService.insert(userFocus);
        BaseContextHandler.set(SecurityConstants.SHARDING_KEY, null);
        return initSuccessResult();
    }


    /*@RequestMapping(value="/getImageUrl",method = RequestMethod.POST)
    @ApiOperation(value = "获取二维码")
    public ReturnData<String> getImageUrl(@RequestBody String params){
        JSONObject jsonObject = JSONObject.parseObject(params);
        String appId = jsonObject.getString("appId");
        Integer module = jsonObject.getInteger("module");
        Integer type = jsonObject.getInteger("type");
        Integer form = jsonObject.getInteger("form");
        StringBuilder sb;
        if (UserFocusConstants.FocusForm.GROUP == form) {
            sb = new StringBuilder(UserFocusConstants.FOCUS_GROUP_IMAGE);
            sb.append(module);
        } else {
            sb = new StringBuilder(UserFocusConstants.FOCUS_OFFICIAL_IMAGE);
            sb.append(appId).append("_").append(module).append("_").append(type);
        }
        //读取缓存
        Object o = redisTemplate.opsForValue().get(sb.toString());
        if (o != null && !"".equals(o)) {
            return initSuccessObjectResult(o.toString());
        } else {
            WxMediaDto wxMediaDto = new WxMediaDto();
            wxMediaDto.setAppid(appId);
            wxMediaDto.setBusinessId(sb.toString());
            wxMediaDto.setBusinessName("流量池");
            wxMediaDto.setMediaType(UserFocusConstants.mediaType.FOREVER);
            ReturnData<WxMediaDto> returnData = wxFeignClient.query(wxMediaDto);
            if (returnData != null && returnData.getCode() == SecurityConstants.SUCCESS_CODE && returnData.getData() != null ) {
                WxMediaDto data = returnData.getData();
                redisTemplate.opsForValue().set(sb.toString(), data.getMediaId());
                return initSuccessObjectResult(data.getMediaId());
            } else {

            }


        }
    }*/


    //打标签

    //上传素材接口

}

