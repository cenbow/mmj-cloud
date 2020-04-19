package com.mmj.user.manager.controller;


import java.util.List;

import io.swagger.annotations.ApiOperation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mmj.common.controller.BaseController;
import com.mmj.common.model.ReturnData;
import com.mmj.user.manager.model.BaseRoleResource;
import com.mmj.user.manager.service.BaseRoleResourceService;

/**
 * <p>
 * 角色资源映射表 前端控制器
 * </p>
 *
 * @author ${author}
 * @since 2019-05-06
 */
@RestController
@RequestMapping("/user/baseRoleResource")
public class BaseRoleResourceController extends BaseController {
	
	@Autowired
	private BaseRoleResourceService baseRoleResourceService;
	
	@ApiOperation(value="新增角色&资源关联关系接口")
    @PostMapping("save")
    public ReturnData<BaseRoleResource> save(@RequestBody BaseRoleResource entity){
        boolean flag = baseRoleResourceService.insert(entity);
        if(flag) {
            return initSuccessResult();
        }
        return initErrorObjectResult("增加失败！");
    } 

	@ApiOperation(value="根据主键删除角色&资源关联关系接口")
    @PostMapping("delete")
    public ReturnData<String> delete(Integer mapperId){
        boolean flag = baseRoleResourceService.deleteById(mapperId);
        if(flag) {
            return initSuccessResult();
        }
        return initErrorObjectResult("删除失败！");
    }
	
	@SuppressWarnings("rawtypes")
	@ApiOperation(value="查询角色下包含的所有资源接口")
    @PostMapping("query")
    public ReturnData<List> query(Integer roleId){
       return initSuccessObjectResult(baseRoleResourceService.queryByRoleId(roleId));
    }
}

