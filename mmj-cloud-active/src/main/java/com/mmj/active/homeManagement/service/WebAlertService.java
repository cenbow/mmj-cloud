package com.mmj.active.homeManagement.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import com.mmj.active.homeManagement.model.WebAlert;
import com.mmj.active.homeManagement.model.WebAlertEX;
import com.mmj.common.model.BaseDict;
import com.mmj.common.model.ReturnData;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 弹窗管理 服务类
 * </p>
 *
 * @author dashu
 * @since 2019-06-05
 */
public interface WebAlertService extends IService<WebAlert> {

    Page<WebAlert> query(WebAlert webAlert);

    Map<String, Object> selectWebAlert(long userid,String source);

    ReturnData<Object> save(WebAlert webAlert);

    WebAlert selectByAlertId(Integer alertId);

    ReturnData<Object> clickWebAlert(Integer alertId,Long userid);

    BaseDict selectNewUsreTopic();

    BaseDict queryNewUserTopic(String dictCode);

    ReturnData<Object> clickWebAlertByApp(Integer alertId, Long userid);

    List<WebAlertEX> selectWebAlertByApp(HttpServletRequest request);
}
