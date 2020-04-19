package com.mmj.user.manager.controller;


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
import com.mmj.user.manager.model.BaseRolePermission;
import com.mmj.user.manager.model.BaseUserRole;
import com.mmj.user.manager.service.BaseRolePermissionService;

/**
 * <p>
 * 角色权限映射表 前端控制器
 * </p>
 *
 * @author ${author}
 * @since 2019-05-06
 */
@RestController
@RequestMapping("/user/baseRolePermission")
public class BaseRolePermissionController extends BaseController {
	
	@Autowired
	private BaseRolePermissionService baseRolePermissionService;
	
	@ApiOperation(value="新增角色&权限关联关系接口")
    @PostMapping("save")
    public ReturnData<BaseRolePermission> save(@RequestBody BaseRolePermission entity){
        boolean flag = baseRolePermissionService.insert(entity);
        if(flag) {
            return initSuccessResult();
        }
        return initErrorObjectResult("增加失败！");
    }
	
	@ApiOperation(value="根据主键删除角色&权限关联关系接口")
    @PostMapping("delete")
    public ReturnData<String> delete(Integer mapperId){
        boolean flag = baseRolePermissionService.deleteById(mapperId);
        if(flag) {
            return initSuccessResult();
        }
        return initErrorObjectResult("删除失败！");
    }

	@SuppressWarnings("rawtypes")
	@ApiOperation(value="查询角色下包含的所有权限接口")
    @PostMapping("query")
    public ReturnData<List> query(Integer roleId){
       return initSuccessObjectResult(baseRolePermissionService.queryByRoleId(roleId));
    }
}

