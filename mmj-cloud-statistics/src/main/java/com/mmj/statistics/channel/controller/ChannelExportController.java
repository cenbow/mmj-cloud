package com.mmj.statistics.channel.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.Valid;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.mmj.common.controller.BaseController;
import com.mmj.common.model.ChannelUserParam;
import com.mmj.common.model.ChannelUserVO;
import com.mmj.common.model.UserOrderStatistics;
import com.mmj.common.model.UserOrderStatisticsParam;
import com.mmj.common.utils.AESUtil;
import com.mmj.common.utils.DateUtils;
import com.mmj.common.utils.PriceConversion;
import com.mmj.statistics.channel.constant.ConfigTypeConstant;
import com.mmj.statistics.channel.dto.ChannelParam;
import com.mmj.statistics.channel.model.ChannelStatisticsConfig;
import com.mmj.statistics.channel.service.ChannelStatisticsConfigService;
import com.mmj.statistics.feigin.OrderFeignClient;
import com.mmj.statistics.feigin.UserFeignClient;


@Slf4j
@Controller
@RequestMapping("/channel")
public class ChannelExportController extends BaseController {

	@Autowired
	private UserFeignClient userFeignClient;
	
	@Autowired
	private OrderFeignClient orderFeignClient;
	
	@Autowired
	private ChannelStatisticsConfigService channelStatisticsConfigService;
	
	/**
	 * 根据mmj通过AES加密而来，因url中不能带+号，会被作为空格处理，另外去掉了==
	 */
	private static final String ACCESS = "zhQkbb32gW79Ob4VmXyA";

	@RequestMapping("/init")
	public String init(Model model) {
		log.info("-->init-->渠道访问数据统计");
		Date now = new Date();
		String startDate = DateUtils.SDF10.format(now);
		model.addAttribute("startDate", startDate);
		model.addAttribute("endDate", startDate);
		return "channel/data_export";
	}

	@ResponseBody
	@RequestMapping(value = "/export", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	public Object export(@RequestBody @Valid ChannelParam param) {

		log.info("-->export-->查询渠道商用户数据，参数：{}", param);
		if (StringUtils.isBlank(param.getChannel())) {
			return this.initErrorObjectResult("请填写渠道编码");
		}
		if (StringUtils.isBlank(param.getStartDate())) {
			return this.initErrorObjectResult("请填写起始时间");
		}
		if (StringUtils.isBlank(param.getEndDate())) {
			return this.initErrorObjectResult("请填写截止时间");
		}
		if (param.getStartDate().length() != 10 || param.getEndDate().length() != 10) {
			return this.initErrorObjectResult("请填写正确的日期格式");
		}
		param.setStartDate(param.getStartDate() + " 00:00:01");
		param.setEndDate(param.getEndDate() + " 23:59:59");
		try {
			String limitDate = "2019-03-19 00:00:01";
			Date endDate = DateUtils.SDF1.parse(param.getEndDate());// 格式化日期，以防出错
			Date startDate = DateUtils.SDF1.parse(param.getStartDate());// 格式化日期，以防出错
			if(!ACCESS.equalsIgnoreCase(param.getAccess()) && startDate.before(DateUtils.SDF1.parse(limitDate))) {
				return this.initErrorObjectResult("当前仅支持查询2019年3月19号及之后的数据");
			}
			if(startDate.after(endDate)) {
				return this.initErrorObjectResult("截止时间不能早于开始时间");
			}
			int days = (int) ((endDate.getTime() - startDate.getTime()) / (1000*3600*24));
			if(days > 31) {
				return this.initErrorObjectResult("时间跨度不能超过31天");
			}
		} catch (Exception e) {
			return this.initErrorObjectResult("请填写正确的时间格式");
		}

		String channel = param.getChannel();
		try {
			channel = AESUtil.decode(channel);
			log.info("-->export-->解密后的code:{}", channel);
			if(StringUtils.isBlank(channel)) {
				return this.initErrorObjectResult("请填写正确的渠道编码");
			}
		} catch (Exception e) {
			log.error("-->export-->解密发生错误：", e);
			return this.initErrorObjectResult("请填写正确的渠道编码");
		}

		param.setChannel(channel);
		try {
			Map<String, Object> result = new HashMap<String, Object>();
			
			ChannelUserParam paramVo = new ChannelUserParam();
			paramVo.setChannel(channel);
			paramVo.setStartTime(param.getStartDate());
			paramVo.setEndTime(param.getEndDate());
			
			List<ChannelUserVO> list = userFeignClient.getChannelUsers(paramVo).getData();
			Collections.sort(list);
			int total = list.size();
			log.info("-->查询用户结果：{}条", total);
			result.put("channelUserList", list);
			result.put("userTotalCount", total);
//			result.put("channelName", getChannelName(param.getChannel()));// 渠道名称，可以不用显示，反正渠道方知道自己的渠道编码
			result.put("channelCode", channel);
			boolean needLoadOrderData = true;
			// 部分渠道无法查看订单总数和交易金额，此处获取配置的渠道编码
			if(!ACCESS.equalsIgnoreCase(param.getAccess())) {
				Wrapper<ChannelStatisticsConfig> wrapper = new EntityWrapper<ChannelStatisticsConfig>();
				wrapper.eq("CONFIG_TYPE", ConfigTypeConstant.CONFIG_TYPE_ORDER);
				wrapper.eq("CHANNEL_CODE", channel);
				List<ChannelStatisticsConfig> cscList = channelStatisticsConfigService.selectList(wrapper);
				if(!cscList.isEmpty()) {
					log.info("-->该渠道{}无法查看订单数据", cscList.get(0).getChannelCode());
					needLoadOrderData = false;
				}
			}
			
			if (needLoadOrderData) {
				List<UserOrderStatistics> orderList = new ArrayList<UserOrderStatistics>();
				Set<Long> userIdSet = new HashSet<Long>();
				
				// 订单根据用户ID分了100张表，所以此处将userId进行规类后再去查询，即使统计某个时间段的用户量过大，订单查询数据库最多不会超过100次
				int queryCount = 0;
				for(int i=0;i<100;i++) {
					for(ChannelUserVO vo : list) {
						if(vo.getUserId() % 100 == i) {
							userIdSet.add(vo.getUserId());
						}
					}
					if(userIdSet.size() > 0) {
						orderList.addAll(this.getOrderList(userIdSet, param.getStartDate(), param.getEndDate()));
						queryCount++;
						userIdSet = new HashSet<Long>();
					}
				}
				int orderAmount = 0;
				int orderCount = 0;
				for(UserOrderStatistics entity : orderList) {
					orderAmount += entity.getOrderAmount();
					orderCount += entity.getOrderCount();
				}
				result.put("orderCount", orderCount);
				String orderAmountStr = PriceConversion.intToString(orderAmount);
				result.put("orderAmountStr", orderAmountStr);
				log.info("-->export-->orderCount: {}, orderAmount: {}元, {}分，查询订单库次数：{}", orderCount, orderAmountStr, orderAmount, queryCount);
			}
			return this.initSuccessObjectResult(result);
		} catch (Exception e) {
			log.error("-->export-->获取渠道数据发生错误：", e);
			return this.initErrorObjectResult("系统发生错误");
		}

	}
	
	private List<UserOrderStatistics> getOrderList(Set<Long> userIdSet, String startTime, String endTime) {
		UserOrderStatisticsParam orderParam = new UserOrderStatisticsParam();
		orderParam.setUserIdSet(userIdSet);
		orderParam.setStartTime(startTime);
		orderParam.setEndTime(endTime);
		return orderFeignClient.getUsersOrdersDataForChannel(orderParam).getData();
	}

//	private String getChannelName(String channel) {
//		String name = null;
//		// TODO 修改取数据的方式
//		String distributorsJson = HttpURLConnectionUtil.doGet("https://www.polynome.cn/spreads/channel/maps", null);
//		if (StringUtils.isNotEmpty(distributorsJson)) {
//			JSONObject jsonData = JSONObject.parseObject(distributorsJson);
//			JSONObject data = JSONObject.parseObject(jsonData.getString("data"));
//			JSONArray array = data.getJSONArray("chs");
//			for (int i = 0; i < array.size(); i++) {
//				JSONObject obj = array.getJSONObject(i);
//				if (channel.equalsIgnoreCase(obj.getString("chv"))) {
//					name = obj.getString("chk");
//					log.info("-->根据channel获取到的名称为：{}", name);
//					break;
//				}
//			}
//		}
//		return name;
//	}

	@ResponseBody
	@RequestMapping("/getEncryptCode")
	public String getEncryptCode(@RequestParam("code") String code) {
		return AESUtil.encode(code);
	}
	
}
