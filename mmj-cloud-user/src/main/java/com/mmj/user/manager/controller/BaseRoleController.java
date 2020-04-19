package com.mmj.user.manager.controller;


import java.util.Date;

import io.swagger.annotations.ApiOperation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.mmj.common.controller.BaseController;
import com.mmj.common.model.ReturnData;
import com.mmj.user.manager.model.BaseRole;
import com.mmj.user.manager.model.BaseUser;
import com.mmj.user.manager.service.BaseRoleService;

/**
 * <p>
 * 用户角色表 前端控制器
 * </p>
 *
 * @author ${author}
 * @since 2019-05-06
 */
@RestController
@RequestMapping("/user/baseRole")
public class BaseRoleController extends BaseController {
	
	@Autowired
	private BaseRoleService baseRoleService;
	
	@ApiOperation(value="新增角色接口")
    @PostMapping("save")
    public ReturnData<BaseUser> save(@RequestBody BaseRole entity){
        boolean flag = baseRoleService.insert(entity);
        if(flag) {
            return initSuccessResult();
        }
        return initErrorObjectResult("增加失败！");
    }
	
	@ApiOperation(value="删除角色接口")
    @PostMapping("delete")
    public ReturnData<String> delete(Integer roleId){
        boolean flag = baseRoleService.deleteByRoleId(roleId);
        if(flag) {
            return initSuccessResult();
        }
        return initErrorObjectResult("删除失败！");
    }
	
	@ApiOperation(value="分页查询角色接口")
    @PostMapping("query")
    public ReturnData<Page<BaseRole>> query(@RequestBody BaseRole entity){
        Page<BaseRole> page = new Page<BaseRole>(entity.getCurrentPage(), entity.getPageSize());
        EntityWrapper<BaseRole> wrapper = new EntityWrapper<>(entity);
        Page<BaseRole> resultList = baseRoleService.selectPage(page, wrapper);
        return initSuccessObjectResult(resultList);
    }

	@ApiOperation(value="修改角色接口")
    @PostMapping("update")
    public ReturnData<BaseRole> update(@RequestBody BaseRole entity){
		entity.setModifyTime(new Date());
        boolean flag = baseRoleService.updateById(entity);
        if(flag) {
            return initSuccessResult();
        }
        return initErrorObjectResult("修改失败！");
    }
}

