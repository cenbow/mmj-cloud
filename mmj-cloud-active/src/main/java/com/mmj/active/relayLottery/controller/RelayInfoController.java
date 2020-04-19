package com.mmj.active.relayLottery.controller;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.plugins.Page;
import com.mmj.active.relayLottery.model.vo.RelayInfoVo;
import com.mmj.active.relayLottery.service.RelayInfoService;
import com.mmj.common.controller.BaseController;
import com.mmj.common.model.ReturnData;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 接力购抽奖表 前端控制器
 * </p>
 *
 * @author zhangyicao
 * @since 2019-06-04
 */
@RestController
@RequestMapping("/relayLottery")
public class RelayInfoController extends BaseController {
    private Logger logger = LoggerFactory.getLogger(RelayInfoController.class);

    @Autowired
    private RelayInfoService relayInfoService;

    /**
     * boss-保存接力购抽奖配置
     * @param relayInfoVo
     * @return
     */
    @RequestMapping(value = "/saveConfigure",method = RequestMethod.POST)
    @ApiOperation("保存接力购抽奖配置")
    public Object saveConfigure(@RequestBody RelayInfoVo relayInfoVo){
        return relayInfoService.saveConfigure(relayInfoVo);
    }

    /**
     * boss-接力购抽奖开启/关闭
     * @param parame
     * @return
     */
    @RequestMapping(value = "/updateStatus",method = RequestMethod.POST)
    @ApiOperation("接力购抽奖开启/关闭")
    public Object updateStatus(@RequestBody String parame){
        return relayInfoService.onOff(parame);
    }

    /**
     * boss-接力购抽奖删除
     * @param relayId
     * @return
     */
    @RequestMapping(value = "/delRelayLottery/{relayId}",method = RequestMethod.POST)
    @ApiOperation("接力购抽奖删除")
    public Object delRelayLottery(@PathVariable("relayId") Integer relayId){
        return relayInfoService.del(relayId);
    }


    @RequestMapping(value = "/queryLotteryRelay/{relayId}",method = RequestMethod.POST)
    @ApiOperation("接力购抽奖详情")
    public Object queryLotteryRelay(@PathVariable("relayId") Integer relayId){
        return initSuccessObjectResult(relayInfoService.lotteryReleyInfo(relayId));
    }

    /**
     * boss-接力购抽奖列表查询
     * @param relayInfoVo
     * @return
     */
    @RequestMapping(value = "/bossQueryList",method = RequestMethod.POST)
    @ApiOperation("boss-接力购抽奖列表查询")
    public ReturnData<Page<RelayInfoVo>> bossQueryList(@RequestBody RelayInfoVo relayInfoVo){
        return initSuccessObjectResult(relayInfoService.queryList(relayInfoVo));
    }

    /**
     * 小程序-接力购抽奖列表查询
     * @return
     */
    @RequestMapping(value = "/lotteryList",method = RequestMethod.POST)
    @ApiOperation("小程序-接力购抽奖列表查询")
    public Object lotteryList(){
        return relayInfoService.lotteryList(null);
    }

    /**
     * 小程序-我的抽奖列表
     * @param parame
     * @return
     */
    @RequestMapping(value = "/myLotteryList",method = RequestMethod.POST)
    @ApiOperation("小程序-我的抽奖列表")
    public Object myLotteryList(@RequestBody String parame){
        logger.info("小程序-我的抽奖列表:{}",parame);
        JSONObject jsonObject = JSONObject.parseObject(parame);
        return relayInfoService.myLotteryList(Long.valueOf(jsonObject.get("userid").toString()));
    }

    /**
     * 小程序-接力购抽奖活动开奖信息
     * @param parame
     * @return
     */
    @RequestMapping(value = "/relayLotteryInfo",method = RequestMethod.POST)
    @ApiOperation("小程序-接力购抽奖活动开奖信息")
    public Object relayLotteryInfo(@RequestBody String parame){
        return relayInfoService.queryLotteryResult(parame);
    }

    /**
     * 小程序-接力购抽奖团信息
     * @param parame
     * @return
     */
    @RequestMapping(value = "/group",method = RequestMethod.POST)
    @ApiOperation("小程序-接力购抽奖团信息")
    public Object group(@RequestBody String parame){
        return relayInfoService.queryGroup(parame);
    }

    /**
     * 小程序-接力购抽奖开奖页信息
     * @param parame
     * @return
     */
    @RequestMapping(value = "/queryOpenLotteryInfo",method = RequestMethod.POST)
    @ApiOperation("小程序-接力购抽奖开奖页信息")
    public Object queryOpenLotteryInfo(@RequestBody String parame){
        return relayInfoService.queryOpenLotteryInfo(parame);
    }

    /**
     * 小程序-关注发送模板消息
     * @return
     */
    @RequestMapping(value = "/mpSubMsg",method = RequestMethod.POST)
    @ApiOperation("小程序-接力购抽奖关注公众号发送模板消息")
    public Object mpSubMsg(@RequestBody String parame){
        return null;
    }


    /**
     * 小程序-接力购抽奖查询是否有下单记录
     * @return
     */
    @RequestMapping(value = "/getJieligouCount",method = RequestMethod.POST)
    @ApiOperation("小程序-接力购抽奖查询是否有下单记录")
    public Object getJieligouCount(){
        return relayInfoService.getJieligouCount();
    }

    /**
     * 小程序-接力购抽奖判断是否新用户
     * @return
     */
    @RequestMapping(value = "/checkIsNewUser",method = RequestMethod.POST)
    @ApiOperation("小程序-接力购抽奖判断是否新用户")
    public Object getJieligouCount(Long userid){
        return null;
    }

}

