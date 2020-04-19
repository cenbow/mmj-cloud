package com.mmj.common.utils;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.xiaoleilu.hutool.collection.CollUtil;

public final class PermitAllUrl {

    /**
     * 监控中心和swagger需要访问的url
     */
    private static final String[] ENDPOINTS = {"/actuator/health", "/actuator/env", "/actuator/metrics/**", "/actuator/trace", "/actuator/dump", "/error",
            "/actuator/jolokia", "/actuator/info", "/actuator/logfile", "/actuator/refresh", "/actuator/flyway", "/actuator/liquibase",
            "/actuator/heapdump", "/actuator/loggers", "/actuator/auditevents", "/actuator/env/PID", "/actuator/jolokia/**",
            "/v2/api-docs/**", "/swagger-ui.html", "/swagger-resources/**", "/webjars/**", "/**/user/me", "/**/user/get/**", "/**/druid/**", "/**/oauth/token/**",
            "/**/login/**", "/**/wx/user/**", "/**/wx/config/**", "/**/wxpayOrder/notifyUrl", "/wxpayRedpack/sendRedpack", "/wxpayRefund/refund",
            "/wxpayTransfers/transfers", "/wxMedia/**", "/wxTag/doTag", "/wxImage/**", "/wxForm/del", "/**/jushuitan/**", "/**/recharge/**", "/**/statistics/**", "/**/async/**", "/**/goodFile/upload",
            "/**/es/**", "/es/**", "/**/wxmsg/**","/**/mobile","/**/channel/exportChannel/**","/**/threeSaleTenner/threeSaleFission/updateInvalid","/**/recommend/userShard/userShardSendMoney",
            "/**/wxDelayTask/repair","/**/wxpayRefund/notifyUrl","/**/homeManagement/webAlert/selectWebAlertByApp"};

    public static String[] permitAllUrl(List<String> list) {
        if (CollUtil.isEmpty(list)) {
            return ENDPOINTS;
        }
        Set<String> set = new HashSet<>();
        Collections.addAll(set, ENDPOINTS);
        list.stream().forEach(ignoreurl -> Collections.addAll(set, ignoreurl));
        return set.toArray(new String[set.size()]);
    }

}
