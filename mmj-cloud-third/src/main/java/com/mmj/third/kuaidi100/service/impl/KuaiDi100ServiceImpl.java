package com.mmj.third.kuaidi100.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.mmj.third.kuaidi100.feign.Kuaidi100AutoFeignClient;
import com.mmj.third.kuaidi100.feign.Kuaidi100PollFeignClient;
import com.mmj.third.kuaidi100.model.*;
import com.mmj.third.kuaidi100.service.BestService;
import com.mmj.third.kuaidi100.service.KuaiDi100Service;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.DigestUtils;

import java.nio.charset.Charset;
import java.util.*;

/**
 * @Description: 快递服务实现
 * @Auther: KK
 * @Date: 2018/10/13
 */
@Service
public class KuaiDi100ServiceImpl implements KuaiDi100Service {
    @Autowired
    private Kuaidi100PollFeignClient kuaidi100PollFeignClient;
    @Autowired
    private Kuaidi100AutoFeignClient kuaidi100AutoFeignClient;
    @Value("${kuaidi100.key:KGqjAGfh6626}")
    private String key;
    @Value("${kuaidi100.consumer:CF7E57491704E5DC1AC153046DDDBC06}")
    private String consumer;
    @Value("${kuaidi100.callBackUrl:http://ecommerce.52trip.vip}")
    private String callBackUrl;
    @Value("${kuaidi100.url.poll:https://poll.kuaidi100.com/poll}")
    private String pollUrl;
    @Value("${kuaidi100.url.pollQuery:https://poll.kuaidi100.com/poll/query.do}")
    private String pollQueryUrl;
    @Value("${kuaidi100.url.auto：http://www.kuaidi100.com/autonumber/auto?num={lId}&key={key}}")
    private String autoUrl;

    @Autowired
    private BestService bestService;

    @Override
    public PollQueryResponse query(String orderNo, String lId, String lcCode) {
        List<PollQueryRequest> pollQueryRequests = Lists.newArrayList();
        List<AutoResponse> autoResponses = auto(lId);
        if (Objects.isNull(autoResponses)) {
            PollQueryResponse response = new PollQueryResponse();
            response.setResult(true);
            response.setMessage("暂无物流信息");
            return response;
        }
        autoResponses.stream().forEach(autoResponse -> pollQueryRequests.add(new PollQueryRequest(autoResponse.getComCode(), lId)));
        PollQueryResponse response;
        for (PollQueryRequest pollQueryRequest : pollQueryRequests) {
            if ("huitongkuaidi".equalsIgnoreCase(pollQueryRequest.getCom())) {
                response = bestService.query(orderNo, lId);
                if (Objects.nonNull(response)) {
                    return response;
                }
            }
            String param = JSONObject.toJSONString(pollQueryRequest);

//            HttpHeaders httpHeaders = new HttpHeaders();
//            httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//
//            MultiValueMap params = new LinkedMultiValueMap();
//            params.add("param", param);
//            params.add("customer", consumer);
//            params.add("sign", DigestUtils.md5DigestAsHex((param + key + consumer).getBytes(Charset.forName("UTF-8"))).toUpperCase());

//            HttpEntity<Map> httpEntity = new HttpEntity(params, httpHeaders);

//            ResponseEntity<String> responseEntity = restTemplate.postForEntity(pollQueryUrl, httpEntity, String.class);
//            response = JSONObject.parseObject(responseEntity.getBody(), PollQueryResponse.class);
            String content = kuaidi100PollFeignClient.query(param, consumer, DigestUtils.md5DigestAsHex((param + key + consumer).getBytes(Charset.forName("UTF-8"))).toUpperCase());
            if (StringUtils.isBlank(content))
                continue;
            response = JSONObject.parseObject(content, PollQueryResponse.class);
            if (Objects.isNull(response) || (Objects.nonNull(response.getResult()) && !response.getResult())) {
                continue;
            }
            response.setResult(true);
            return response;
        }
        response = new PollQueryResponse();
        response.setResult(true);
        response.setMessage("暂无物流信息");
        return response;
    }

    @Override
    public List<AutoResponse> auto(String lId) {
        Assert.notNull(lId, "快递单号为空");
//        Map<String, String> params = Maps.newHashMap();
//        params.put("lId", lId);
//        params.put("key", key);
//        ResponseEntity<String> responseEntity = restTemplate.getForEntity(autoUrl, String.class, params);
//        return JSONObject.parseArray(responseEntity.getBody(), AutoResponse.class);
        String content = kuaidi100AutoFeignClient.auto(lId, key);
        if (StringUtils.isNotBlank(content))
            return JSONObject.parseArray(content, AutoResponse.class);
        return null;
    }
}
