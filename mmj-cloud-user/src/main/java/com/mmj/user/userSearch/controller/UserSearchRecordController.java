package com.mmj.user.userSearch.controller;


import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.mmj.common.context.BaseContextHandler;
import com.mmj.common.controller.BaseController;
import com.mmj.common.model.ReturnData;
import com.mmj.common.properties.SecurityConstants;
import com.mmj.user.userSearch.model.UserSearchRecord;
import com.mmj.user.userSearch.service.UserSearchRecordService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author shenfuding
 * @since 2019-08-06
 */
@RestController
@RequestMapping("/userSearchRecord")
public class UserSearchRecordController extends BaseController {

    @Autowired
    private UserSearchRecordService userSearchRecordService;

    @ApiOperation(value = "获取历史搜索记录(返回10条数据)")
    @RequestMapping(value = "/queryList/{userId}", method = RequestMethod.POST)
    public ReturnData<List<UserSearchRecord>> queryByGoodId(@PathVariable("userId") Long userId) {
        EntityWrapper<UserSearchRecord> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("STATUS", 1);
        entityWrapper.eq("CREATER_ID", userId);
        entityWrapper.orderBy("CREATER_TIME", false);
        Page<UserSearchRecord> page = new Page<>(1,10);
        BaseContextHandler.set(SecurityConstants.SHARDING_KEY, userId);
        Page<UserSearchRecord> userSearchRecordPage = userSearchRecordService.selectPage(page, entityWrapper);
        if (userSearchRecordPage != null && userSearchRecordPage.getRecords() != null) {
            return initSuccessObjectResult(userSearchRecordPage.getRecords());
        }
        BaseContextHandler.set(SecurityConstants.SHARDING_KEY, null);
        return initSuccessResult();
    }

    @ApiOperation(value = "清除历史搜索记录（逻辑删除）")
    @RequestMapping(value = "/clean/{userId}", method = RequestMethod.POST)
    public ReturnData clean(@PathVariable("userId") Long userId) {
        UserSearchRecord userSearchRecord = new UserSearchRecord();
        userSearchRecord.setStatus(0);
        EntityWrapper<UserSearchRecord> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("CREATER_ID", userId);
        BaseContextHandler.set(SecurityConstants.SHARDING_KEY, userId);
        userSearchRecordService.update(userSearchRecord, entityWrapper);
        BaseContextHandler.set(SecurityConstants.SHARDING_KEY, null);
        return initSuccessResult();
    }

    @ApiOperation(value = "保存搜索记录")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public ReturnData save(@RequestBody UserSearchRecord userSearchRecord) {
        EntityWrapper<UserSearchRecord> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("STATUS", 1);
        entityWrapper.eq("CREATER_ID", userSearchRecord.getCreaterId());
        entityWrapper.eq("CONTENT", userSearchRecord.getContent());
        BaseContextHandler.set(SecurityConstants.SHARDING_KEY, userSearchRecord.getCreaterId());
        List<UserSearchRecord> userSearchRecords = userSearchRecordService.selectList(entityWrapper);
        if (userSearchRecords != null && userSearchRecords.size() > 0) {
            userSearchRecord.setCreaterTime(new Date());
            BaseContextHandler.set(SecurityConstants.SHARDING_KEY, userSearchRecord.getCreaterId());
            userSearchRecordService.update(userSearchRecord, entityWrapper);
        } else {
            UserSearchRecord searchRecord = new UserSearchRecord();
            searchRecord.setStatus(1);
            searchRecord.setContent(userSearchRecord.getContent());
            searchRecord.setCreaterId(userSearchRecord.getCreaterId());
            BaseContextHandler.set(SecurityConstants.SHARDING_KEY, userSearchRecord.getCreaterId());
            userSearchRecordService.insert(searchRecord);
        }
        BaseContextHandler.set(SecurityConstants.SHARDING_KEY, null);
        return initSuccessResult();
    }
}

