package com.mmj.third.kuaidi100.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mmj.third.kuaidi100.feign.BestFeignClient;
import com.mmj.third.kuaidi100.model.*;
import com.mmj.third.kuaidi100.service.BestService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.security.MessageDigest;
import java.util.List;
import java.util.Map;

/**
 * @description: 百世
 * @auther: KK
 * @date: 2019/6/4
 */
@Service
public class BestServiceImpl implements BestService {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private BestFeignClient bestFeignClient;

    @Override
    public PollQueryResponse query(String orderNo, String lId) {
        try {
            BestRequest bestRequest = new BestRequest();
            List<String> mailNo = Lists.newArrayList();
            mailNo.add(lId);
            bestRequest.setMailNos(new BestRequest.MailNos(mailNo));

            String url = "http://edi-q9.ns.800best.com/kd/api/process?serviceType={serviceType}&partnerID={partnerID}&bizData={bizData}&sign={sign}";
            String serviceType = "KD_TRACE_QUERY";
            String partnerID = "65292";
            String partnerKey = "rilnwedj2osj";
            String bizData = JSONObject.toJSONString(bestRequest);
            String sign = doSign(bizData, "UTF-8", partnerKey);

            Map<String, Object> params = Maps.newHashMap();
            params.put("serviceType", serviceType);
            params.put("partnerID", partnerID);
            params.put("bizData", bizData);
            params.put("sign", sign);
//            ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, bestRequest, String.class, params);
//            System.out.println(responseEntity);
//            if (responseEntity.getStatusCode() == HttpStatus.OK) {
//                BestResponse bestResponse = JSONObject.parseObject(responseEntity.getBody(), BestResponse.class);
            String content = bestFeignClient.query(serviceType, partnerID, bizData, sign, bestRequest);
            if (StringUtils.isBlank(content))
                return null;
            BestResponse bestResponse = JSONObject.parseObject(content, BestResponse.class);
            PollQueryResponse pollQueryResponse = new PollQueryResponse();
            pollQueryResponse.setResult(bestResponse.isResult());
            int n = bestResponse.getTraceLogs().size();
            if (n > 0) {
                BestTraceLogsResponse bestTraceLogsResponse = bestResponse.getTraceLogs().get(0);
                if (bestTraceLogsResponse.getTraces().getTrace().size() > 0) {
                    List<PollQueryResponse.Data> dataList = Lists.newArrayListWithCapacity(bestTraceLogsResponse.getTraces().getTrace().size());
                    List<BestTracesResponse.Trace> traces = bestTraceLogsResponse.getTraces().getTrace();
                    int size = traces.size();
                    for (int i = (size - 1); i >= 0; i--) {
                        BestTracesResponse.Trace trace = traces.get(i);
                        PollQueryResponse.Data data = new PollQueryResponse.Data();
                        data.setContext(trace.getRemark());
                        data.setTime(trace.getAcceptTime());
                        data.setFtime(trace.getAcceptTime());
                        dataList.add(data);
                    }
                    pollQueryResponse.setData(dataList);
                    return pollQueryResponse;
                }
            }
//            }
        } catch (Exception e) {
            log.error(" => 查询百世快递错误 error:{}", e.getMessage());
        }
        return null;
    }

    public static String doSign(String bizData, String charset, String keys) {
        String sign;
        bizData = bizData + keys;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(bizData.getBytes(charset));
            byte[] b = md.digest();
            StringBuilder output = new StringBuilder(32);
            for (int i = 0; i < b.length; i++) {
                String temp = Integer.toHexString(b[i] & 0xff);
                if (temp.length() < 2) {
                    output.append("0");
                }
                output.append(temp);
            }
            sign = output.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return sign;

    }
}
