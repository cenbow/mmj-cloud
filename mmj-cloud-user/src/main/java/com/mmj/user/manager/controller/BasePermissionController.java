package com.mmj.user.manager.controller;


import java.util.Date;
import java.util.List;

import io.swagger.annotations.ApiOperation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.mmj.common.controller.BaseController;
import com.mmj.common.model.ReturnData;
import com.mmj.user.manager.model.BasePermission;
import com.mmj.user.manager.model.BaseResource;
import com.mmj.user.manager.model.BaseRole;
import com.mmj.user.manager.service.BasePermissionService;

/**
 * <p>
 * 用户权限表 前端控制器
 * </p>
 *
 * @author ${author}
 * @since 2019-05-06
 */
@RestController
@RequestMapping("/user/basePermission")
public class BasePermissionController extends BaseController {
	
	@Autowired
	private BasePermissionService basePermissionService;
	
	@ApiOperation(value="新增权限接口")
    @PostMapping("save")
    public ReturnData<BasePermission> save(@RequestBody BasePermission entity){
        boolean flag = basePermissionService.insert(entity);
        if(flag) {
            return initSuccessResult();
        }
        return initErrorObjectResult("增加失败！");
    } 
	
	@ApiOperation(value="删除权限接口")
    @PostMapping("delete")
    public ReturnData<String> delete(Integer perId){
        boolean flag = basePermissionService.deleteByPerId(perId);
        if(flag) {
            return initSuccessResult();
        }
        return initErrorObjectResult("删除失败！");
    }
	
	@SuppressWarnings("rawtypes")
	@ApiOperation(value="查询所有权限接口")
    @PostMapping("query")
    public ReturnData<List> queryAll(){
        List<BasePermission> resultList = basePermissionService.selectList(new EntityWrapper<>());
        return initSuccessObjectResult(resultList);
    }

	@ApiOperation(value="修改权限接口")
    @PostMapping("update")
    public ReturnData<BasePermission> update(@RequestBody BasePermission entity){
		entity.setModifyTime(new Date());
        boolean flag = basePermissionService.updateById(entity);
        if(flag) {
            return initSuccessResult();
        }
        return initErrorObjectResult("修改失败！");
    }
	
}

