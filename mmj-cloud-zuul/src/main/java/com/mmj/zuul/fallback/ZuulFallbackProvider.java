package com.mmj.zuul.fallback;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import lombok.extern.slf4j.Slf4j;

import org.springframework.cloud.netflix.zuul.filters.route.FallbackProvider;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.mmj.common.model.ReturnData;
import com.mmj.common.properties.SecurityConstants;

@Slf4j
@Component
public class ZuulFallbackProvider implements FallbackProvider {

    private static final String MSG_TRY_LATER = "Sorry, the server is updating, please try again later.";

	@Override
    public String getRoute() {
        return null;
    }

    @Override
    public ClientHttpResponse fallbackResponse(Throwable cause) {
    	log.error("-->Zuul请求发生异常：", cause);
        return getbackMassage();
    }

    @Override
    public ClientHttpResponse fallbackResponse() {
        return getbackMassage();
    }

    public ClientHttpResponse getbackMassage() {
        return new ClientHttpResponse() {
            @Override
            public HttpStatus getStatusCode() throws IOException {
                return HttpStatus.OK; //状态码
            }

            @Override
            public int getRawStatusCode() throws IOException {
                return 200; //状态码
            }

            @Override
            public String getStatusText() throws IOException {
                return "OK";  //状态
            }

            @Override
            public void close() {

            }

            @Override
            public InputStream getBody() throws IOException {
                ReturnData<String> rd = new ReturnData<String>(SecurityConstants.EXCEPTION_CODE, MSG_TRY_LATER);
                return new ByteArrayInputStream(JSON.toJSONString(rd).getBytes());
            }

            @Override
            public HttpHeaders getHeaders() {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                return headers;
            }
        };
    }
    
}
