package com.mmj.oauth.code.config;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.mmj.common.properties.SecurityProperties;
import com.mmj.oauth.code.mobile.SmsValidateCodeHelp;
import com.xiaoleilu.hutool.collection.CollUtil;
import com.xiaoleilu.hutool.util.StrUtil;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

@Component("validateCodeFilter")
public class ValidateCodeFilter extends OncePerRequestFilter implements InitializingBean {

    @Autowired
    private SecurityProperties securityProperties;
    
    private List<String> codeurls = new ArrayList<>();

    private AntPathMatcher antPathMatcher = new AntPathMatcher();
    
    @Autowired
    SmsValidateCodeHelp smsValidateCodeHelp;
    
    @Override
    public void afterPropertiesSet() throws ServletException {
        super.afterPropertiesSet();
        List<String> urls = securityProperties.getCode().getCodeurls();
        if(CollUtil.isNotEmpty(urls)) {
            codeurls.addAll(urls);
        }
        codeurls.add("/login/mobile");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        AtomicBoolean checkCode = new AtomicBoolean(false);
        codeurls.stream().filter(url -> StrUtil.isNotEmpty(url)
                && antPathMatcher.match(url, request.getRequestURI()))
                .findFirst().ifPresent(url -> checkCode.set(true));
        if(checkCode.get()) {
        	try {
        		smsValidateCodeHelp.validate(request, response);
			} catch (Exception e) {
				responseMessage(e.getMessage(), response);
				return;
			}
            
        }
        chain.doFilter(request, response);

    }

    private void responseMessage(String message, HttpServletResponse response) {
		PrintWriter out;
		try {
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/json;charset=utf-8");
			out = response.getWriter();
			Map<String, Object> map = new HashMap<String, Object>();
			if (out != null) {
				map.put("code", -1);
				map.put("desc", message);
				out.write(JSONObject.toJSONString(map, SerializerFeature.WriteMapNullValue));
				out.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
