package com.mmj.third.recharge.bm.service;

import com.alibaba.fastjson.JSONObject;
import com.mmj.common.constants.MQTopicConstant;
import com.mmj.common.model.third.recharge.NumberInfoDto;
import com.mmj.common.model.third.recharge.RechargeDto;
import com.mmj.common.model.third.recharge.RechargeVo;
import com.mmj.common.utils.CommonUtil;
import com.mmj.common.utils.EnvUtil;
import com.mmj.common.utils.StringUtils;
import com.mmj.third.jushuitan.MQProducer;
import com.mmj.third.recharge.service.RechargeService;
import com.qianmi.open.api.DefaultOpenClient;
import com.qianmi.open.api.OpenClient;
import com.qianmi.open.api.QianmiResponse;
import com.qianmi.open.api.domain.elife.OrderDetailInfo;
import com.qianmi.open.api.domain.elife.PhoneInfo;
import com.qianmi.open.api.request.BmOrderCustomGetRequest;
import com.qianmi.open.api.request.BmRechargeMobileGetItemInfoRequest;
import com.qianmi.open.api.request.BmRechargeMobileGetPhoneInfoRequest;
import com.qianmi.open.api.request.BmRechargeMobilePayBillRequest;
import com.qianmi.open.api.response.BmOrderCustomGetResponse;
import com.qianmi.open.api.response.BmRechargeMobileGetItemInfoResponse;
import com.qianmi.open.api.response.BmRechargeMobileGetPhoneInfoResponse;
import com.qianmi.open.api.response.BmRechargeMobilePayBillResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Map;
import java.util.Objects;

/**
 * @description: 话费充值平台
 * @auther: KK
 * @date: 2019/8/31
 */
@Slf4j
@Service("bm001")
public class Bm001RechargeServiceImpl implements RechargeService {
    private String url = "http://apilf.bm001.com/api";
    private String appKey = "10001990";
    private String appSecret = "Pp9syrGLr4YSImSyc1fRFhSsI4aaDxOM";
    private String accessToken = "3fd0090807f24108bfeb70f3657ff008";
    private String callbackUrl = "https://mmj.polynome.cn/mmj/third/recharge/bm001/callback";
    @Autowired
    private MQProducer mqProducer;
    @Value("${spring.cloud.config.profile}")
    private String profile;

    @Override
    public RechargeDto recharge(RechargeVo rechargeVo) {
        String amount;
        if (EnvUtil.isPro(profile)) { //正式环境
            amount = rechargeVo.getAmount();
        } else {  //测试环境
            amount = "1";
        }
        String mobile = rechargeVo.getMobile();
        String callback = StringUtils.isNotEmpty(rechargeVo.getCallBackUrl()) ? rechargeVo.getCallBackUrl() : this.callbackUrl;
        String outerTid = rechargeVo.getOuterId();
        String itemId = getItem(mobile, amount);
        Assert.isTrue(StringUtils.isNotEmpty(itemId), String.format("[%s][%s]获取话费充值商品失败", mobile, amount));
        log.info("=> 话费充值 outerTid:{},itemId:{},mobile:{},amount:{},callback:{}", outerTid, itemId, mobile, amount, callback);
        OpenClient client = new DefaultOpenClient(url, appSecret);
        BmRechargeMobilePayBillRequest request = new BmRechargeMobilePayBillRequest();
        request.setOuterTid(outerTid);//外部订单编号
        request.setMobileNo(mobile);
        request.setRechargeAmount(amount);
        request.setItemId(itemId);
        request.setCallback(callback);
        BmRechargeMobilePayBillResponse response;
        try {
            response = client.execute(request, accessToken);
        } catch (Exception e) {
            log.error("=> 话费充值错误 mobile:{},amount:{},error:{}", mobile, amount, e.toString());
            return new RechargeDto(false, outerTid, 2, "BM001", null, e.getMessage(), null);
        }
        String resultJson = JSONObject.toJSONString(response);
        log.info("=> 平台话费充值 response:{}", resultJson);
        OrderDetailInfo orderDetailInfo = response.getOrderDetailInfo();
        return returnResponse(outerTid, resultJson, orderDetailInfo, response);
    }

    @Override
    public RechargeDto getRechargeInfo(String outerId) {
        OpenClient client = new DefaultOpenClient(url, appSecret);
        BmOrderCustomGetRequest request = new BmOrderCustomGetRequest();
        request.setOuterTid(outerId);
        BmOrderCustomGetResponse response;
        try {
            response = client.execute(request, accessToken);
        } catch (Exception e) {
            log.error("=> 话费订单详细查询错误 disOrderNo:{},error:{}", outerId, e.toString());
            return new RechargeDto(false, outerId, 2, "BM001", null, e.getMessage(), null);
        }
        String resultJson = JSONObject.toJSONString(response);
        log.info("=> 平台订单详情 response:{}", resultJson);
        OrderDetailInfo orderDetailInfo = response.getOrderDetailInfo();
        return returnResponse(outerId, resultJson, orderDetailInfo, response);
    }

    private RechargeDto returnResponse(String outerId, String resultJson, OrderDetailInfo orderDetailInfo, QianmiResponse response) {
        if (Objects.nonNull(orderDetailInfo)) {
            int resultCode;
            if ("1".equals(orderDetailInfo.getRechargeState())) {
                resultCode = 1;
            } else if ("9".equals(orderDetailInfo.getRechargeState())) {
                resultCode = 3;
            } else {
                resultCode = 0;
            }
            JSONObject jsonObject = JSONObject.parseObject(resultJson);
            jsonObject.remove("body");
            return new RechargeDto(true, orderDetailInfo.getOuterTid(), resultCode, "BM001", orderDetailInfo.getBillId(), orderDetailInfo.getItemName(), jsonObject.toJSONString());
        } else {
            JSONObject jsonObject = JSONObject.parseObject(resultJson);
            jsonObject.remove("body");
            return new RechargeDto(false, outerId, 3, "BM001", null, response.getSubMsg(), jsonObject.toJSONString());
        }
    }

    @Override
    public NumberInfoDto getNumberInfo(String mobile) {
        Assert.isTrue(CommonUtil.checkMobile(mobile), "错误的手机号格式");
        OpenClient client = new DefaultOpenClient(url, appSecret);
        BmRechargeMobileGetPhoneInfoRequest request = new BmRechargeMobileGetPhoneInfoRequest();
        request.setPhoneNo(mobile);
        request.setRespType("area");
        try {
            BmRechargeMobileGetPhoneInfoResponse response = client.execute(request, accessToken);
            log.info("=> 查询手机号归属信息 phoneNumber:{}, response:{}", mobile, response);
            PhoneInfo phoneInfo = response.getPhoneInfo();
            if (Objects.isNull(phoneInfo)) {
                return null;
            } else {
                return new NumberInfoDto(mobile, phoneInfo.getProvince(), phoneInfo.getCity(), phoneInfo.getOperator());
            }
        } catch (Exception e) {
            log.error("=> 查询手机号归属地错误 phoneNumber:{},error:{}", mobile, e.toString());
        }
        return null;
    }

    /**
     * 获取平台话费商品信息
     *
     * @param mobile
     * @param amount
     * @return
     */
    public String getItem(String mobile, String amount) {
        log.info("=> 获取话费商品 mobile:{},amount:{}", mobile, amount);
        OpenClient client = new DefaultOpenClient(url, appSecret);
        BmRechargeMobileGetItemInfoRequest request = new BmRechargeMobileGetItemInfoRequest();
        request.setMobileNo(mobile);
        request.setRechargeAmount(amount);
        try {
            BmRechargeMobileGetItemInfoResponse response = client.execute(request, accessToken);
            log.info("=> 获取平台话费商品 response:{}", JSONObject.toJSONString(response));
            return Objects.nonNull(response.getMobileItemInfo()) ? response.getMobileItemInfo().getItemId() : null;
        } catch (Exception e) {
            log.error("=> 获取话费商品错误 mobile:{},amount:{},error:{}", mobile, amount, e.toString());
        }
        return null;
    }

    @Override
    public void bm001Callback(Map<String, String> params) {
        String rechargeState = params.get("recharge_state");
        String outerTid = params.get("outer_tid");
        int resultCode = "1".equals(rechargeState) ? 1 : 3;
        RechargeDto rechargeDto = new RechargeDto(resultCode == 1, outerTid, resultCode, "BM001", null, "话费结果异步响应", params.toString());
        mqProducer.send(rechargeDto, MQTopicConstant.SYNC_RECHARGE_RESULT);
    }
}
