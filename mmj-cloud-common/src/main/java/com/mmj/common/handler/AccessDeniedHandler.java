package com.mmj.common.handler;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.provider.error.OAuth2AccessDeniedHandler;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mmj.common.utils.ResultUtil;

@Component
public class AccessDeniedHandler extends OAuth2AccessDeniedHandler {
    
    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException authException) throws IOException, ServletException {
        PrintWriter printWriter = response.getWriter();
        printWriter.append(objectMapper.writeValueAsString(ResultUtil.error(0, "no Permission")));
    }

}
