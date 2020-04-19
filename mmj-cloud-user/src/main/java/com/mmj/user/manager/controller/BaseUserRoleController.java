package com.mmj.user.manager.controller;


import io.swagger.annotations.ApiOperation;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.mmj.common.controller.BaseController;
import com.mmj.common.model.ReturnData;
import com.mmj.user.manager.model.BaseUser;
import com.mmj.user.manager.model.BaseUserRole;
import com.mmj.user.manager.service.BaseUserRoleService;

/**
 * <p>
 * 用户角色映射表 前端控制器
 * </p>
 *
 * @author ${author}
 * @since 2019-05-06
 */
@RestController
@RequestMapping("/user/baseUserRole")
public class BaseUserRoleController extends BaseController {
	
	@Autowired
	private BaseUserRoleService baseUserRoleService;
	
	@ApiOperation(value="新增用户&角色关联关系接口")
    @PostMapping("save")
    public ReturnData<BaseUser> save(@RequestBody BaseUserRole entity){
        boolean flag = baseUserRoleService.insert(entity);
        if(flag) {
            return initSuccessResult();
        }
        return initErrorObjectResult("增加失败！");
    } 
	
	@ApiOperation(value="删除用户&角色关联关系接口")
    @PostMapping("delete")
    public ReturnData<String> delete(Integer mapperId){
        boolean flag = baseUserRoleService.deleteById(mapperId);
        if(flag) {
            return initSuccessResult();
        }
        return initErrorObjectResult("删除失败！");
    }
	
	@SuppressWarnings("rawtypes")
	@ApiOperation(value="查询用户&角色关联关系")
    @PostMapping("query")
    public ReturnData<List> query(Integer userId){
        EntityWrapper<BaseUserRole> wrapper = new EntityWrapper<>();
        wrapper.eq("USER_ID", userId);
        List<BaseUserRole> resultList = baseUserRoleService.selectList(wrapper);
        return initSuccessObjectResult(resultList);
    }

}

