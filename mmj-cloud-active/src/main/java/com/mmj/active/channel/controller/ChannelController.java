package com.mmj.active.channel.controller;


import com.baomidou.mybatisplus.plugins.Page;
import com.mmj.active.channel.model.ChannelEx;
import com.mmj.active.channel.model.vo.ChannelVo;
import com.mmj.active.channel.service.ChannelService;
import com.mmj.common.controller.BaseController;
import com.mmj.common.model.ReturnData;
import com.mmj.common.properties.SecurityConstants;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 * 分销渠道统计表 前端控制器
 * </p>
 *
 * @author dashu
 * @since 2019-08-05
 */
@RestController
@RequestMapping("/channel")
@Slf4j
public class ChannelController extends BaseController {
    @Autowired
    private ChannelService channelService;

    @ApiOperation(value ="列表查询 - boss后台")
    @PostMapping("query")
    public ReturnData<Page<ChannelVo>> query(@RequestBody ChannelEx channelEx){
        return initSuccessObjectResult(channelService.query(channelEx));
    }

    @ApiOperation(value = "分销渠道统计导出报表 - boss后台")
    @GetMapping("/exportChannel/{channelName}/{startTime}/{endTime}")
    public ReturnData<Object> exportChannel(@PathVariable("channelName") String channelName,
                                            @PathVariable("startTime") String startTime,
                                            @PathVariable("endTime") String endTime,
                                            HttpServletRequest request,
                                            HttpServletResponse response){
        log.info("-->/channel/exportChannel-->文件导出,渠道名:{},开始时间:{},结束时间:{}",channelName,startTime,endTime);
        ReturnData<Object> rd = new ReturnData<>();
        try {
            ChannelEx channelEx = new ChannelEx();
            if(!"a".equals(channelName)){
                channelEx.setEndTime(channelName);
            }else{
                channelEx.setEndTime(null);
            }
            if(!"a".equals(startTime)){
                channelEx.setStartTime(startTime);
            }else{
                channelEx.setEndTime(null);
            }
            if(!"a".equals(endTime)){
                channelEx.setChannelName(endTime);
            }else{
                channelEx.setEndTime(null);
            }
            channelService.exportChannel(channelEx,request,response);
            log.info("-->/channel/exportChannel-->文件导出成功");
            rd.setCode(SecurityConstants.SUCCESS_CODE);
            rd.setDesc("导出报表成功");
        } catch (Exception e) {
            rd.setCode(SecurityConstants.EXCEPTION_CODE);
            rd.setDesc("导出报表失败");
        }
        return initSuccessObjectResult(rd);
    }
}

