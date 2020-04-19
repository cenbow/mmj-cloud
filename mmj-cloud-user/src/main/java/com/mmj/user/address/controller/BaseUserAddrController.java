package com.mmj.user.address.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mmj.common.controller.BaseController;
import com.mmj.common.model.ReturnData;
import com.mmj.common.model.UserMerge;
import com.mmj.common.utils.SecurityUserUtil;
import com.mmj.user.address.model.BaseUserAddr;
import com.mmj.user.address.service.BaseUserAddrService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 用户收货地址 前端控制器
 * </p>
 *
 * @author dashu
 * @since 2019-07-01
 */
@RestController
@RequestMapping("/address")
@Slf4j
public class BaseUserAddrController extends BaseController {
    @Autowired
    private BaseUserAddrService baseUserAddrService;

    @ApiOperation("保存/修改")
    @PostMapping("save")
    public ReturnData<Object> save(@RequestBody BaseUserAddr baseUserAddr){
        log.info("-->/address/save-->收货地址管理保存，用户id:{},参数：{}",SecurityUserUtil.getUserDetails().getUserId(), JSON.toJSONString(baseUserAddr));
        return baseUserAddrService.save(baseUserAddr);
    }

    @ApiOperation("删除")
    @PostMapping("delete/{addrId}")
    public ReturnData<Object> delete(@PathVariable("addrId") Integer addrId){
        Long userid = SecurityUserUtil.getUserDetails().getUserId();
        log.info("-->/address/delete-->收货地址管理删除，用户id参数：{}", userid);
        return initSuccessObjectResult(baseUserAddrService.deleteByAddrId(addrId,userid));
    }

    @ApiOperation("列表查询")
    @PostMapping("selectAddressList")
    public ReturnData<Object> selectList(){
        Long userid = SecurityUserUtil.getUserDetails().getUserId();
        log.info("-->/address/selectAddressList-->收货地址管理列表查询，用户id参数：{}", userid);
        return initSuccessObjectResult(baseUserAddrService.selectAddressList(userid));
    }

    /**
     * addrId: 不是必须的, addrId:有值,根据id获取详情, addrId没值查询默认地址
     * @param
     * @return
     */
    @ApiOperation("获取单个地址")
    @PostMapping("selectByAddrId")
    public ReturnData<Object> selectDefaultAddress(@RequestBody String params){
        JSONObject jsonObject = JSONObject.parseObject(params);
        Integer addrId = jsonObject.getInteger("addrId");
        Long userid = SecurityUserUtil.getUserDetails().getUserId();
        log.info("-->/address/selectByAddrId-->收货地址管理获取单个地址，用户id参数：{},收货地址id:{}", userid,addrId);
        return initSuccessObjectResult(baseUserAddrService.selectByAddrId(userid,addrId));
    }

    @ApiOperation("修改默认地址")
    @PostMapping("updateDefaultAddress/{addrId}")
    public ReturnData<Object> updateDefaultAddress(@PathVariable(value = "addrId") Integer addrId){
        Long userid = SecurityUserUtil.getUserDetails().getUserId();
        log.info("-->/address/updateDefaultAddress-->收货地址管理修改默认地址，用户id参数：{},收货地址id:{}", userid,addrId);
        return initSuccessObjectResult(baseUserAddrService.updateDefaultAddress(userid,addrId));
    }


    @ApiOperation("定位当前省市区")
    @PostMapping(value = "Wxlocation")
    public ReturnData<Object> Wxlocation(@RequestBody String params){
        JSONObject json = JSONObject.parseObject(params);
        String lng = json.getString("lng");
        String lat = json.getString("lat");

        Map<String, Object> resultMap = new HashMap<>();

        // 参数解释：lng：经度，lat：纬度。KEY：腾讯地图key，get_poi：返回状态。1返回，0不返回
        String key = "3AFBZ-23P3U-W2YVB-4XJSL-FSJ7T-R2BUX";
        String urlString = "http://apis.map.qq.com/ws/geocoder/v1/?location="+lat+","+lng+"&key="+key+"&get_poi=1";

        StringBuilder result  = new StringBuilder();

        try {
            URL url = new URL(urlString);
            //打开和url之间的连接
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");

            conn.setDoOutput(true);
            conn.setDoInput(true);

            conn.setRequestMethod("GET");//GET和POST必须全大写
            conn.connect();

            //构造一个字符流缓存
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

            String line;
            // 获取地址解析结果
            while ((line = in.readLine()) != null) {
                result.append(line) ;
            }
            in.close();

            conn.disconnect();
        } catch (Exception e) {
            log.info("-->/address/Wxlocation-->收货地址管理,定位当前位置失败，经度参数：{},纬度参数:{}", lng,lat);
            e.printStackTrace();
        }

        // 转JSON格式
        JSONObject jsonObject = JSONObject.parseObject(result.toString());

        // 获取地址（行政区划信息） 包含有国籍，省份，城市,区域
        JSONObject adInfo = jsonObject.getJSONObject("result").getJSONObject("ad_info");
        resultMap.put("nation", adInfo.get("nation"));  //国籍
        resultMap.put("receiverState", adInfo.get("province")); //省
        resultMap.put("receiverCity", adInfo.get("city"));  //市
        resultMap.put("receiverDistrict",adInfo.get("district"));   //区

        resultMap.put("provinceCode", adInfo.get("adcode"));
        resultMap.put("nationCode", adInfo.get("nation_code"));
        resultMap.put("cityCode", adInfo.get("city_code"));
        return (initSuccessObjectResult(resultMap));
    }

    /*@ApiOperation("数据合并,仅供测试")
    @PostMapping(value = "/updateUserId")
    public ReturnData<Object> updateUserId(@RequestBody UserMerge userMerge) {
        baseUserAddrService.updateUserId(userMerge);
        return  initSuccessObjectResult("success");
    }*/
}

