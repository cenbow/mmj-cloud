<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>买买家 - 渠道数据查询</title>
<link rel="shortcut icon" href="${pageContext.request.contextPath }/mmj/statistics/static/images/mmj_icon.png"/>
<link href="${pageContext.request.contextPath }/mmj/statistics/static/css/channel.css" rel="stylesheet" type="text/css"></style>
<script src="${pageContext.request.contextPath }/mmj/statistics/static/js/jquery/jquery-3.3.1.min.js" type="text/javascript"></script>
<script type="text/javascript">
	var domain = "${pageContext.request.contextPath}";
</script>
<script src="${pageContext.request.contextPath }/mmj/statistics/static/js/channel/channel.js" type="text/javascript"></script>
</head>
<body>
<div class="mmj_head">
	<form id="distributorForm">
		<div class="mmj_search">
			<span>查询时间：</span>
			<input type="text" name="startDate" id="startDate" value="${startDate }"/>
			至
			<input type="text" name="endDate" id="endDate" value="${endDate }"/>
		</div>
		<div><span class="mmj_tip">日期格式：yyyy-mm-dd，开始和结束时间相等表示查询当天数据</span></div>
		<div class="mmj_search">
			<span>渠道编码：</span>
			<input type="text" name="channel" id="channel"/>&nbsp;&nbsp;<span type="button" class="mmj_button" id="mmj_btn_search">查询</span>
		</div>
		<input type="hidden" name="access" id="access" value="${param.access }"/>
	</form>
</div>
<div class="mmj_content">
	<table cellpadding="0" cellspacing="0" bordercolor="#ebeef5">
		<tr class="mmj_tr">
			<th>引流用户数</th>
			<th>授权总数</th>
			<th>订单总数</th>
			<th>交易金额</th>
		</tr>
		<tr>
			<td id="mmj_td_total">--</td>
			<td id="mmj_td_authorized">--</td>
			<td id="mmj_td_order">--</td>
			<td id="mmj_td_money">--元</td>
		</tr>
	</table>
</div>
<div class="mmj_content">
	<table cellpadding="0" cellspacing="0" bordercolor="#ebeef5">
		<tr class="mmj_tr" id="mmj_table_head">
			<th width="25%">日期</th>
			<th width="25%">推广渠道</th>
			<th width="25%">渠道引流用户ID</th>
			<th width="25%">是否授权</th>
		</tr>
		<tr name="mmj_tr" id="mmj_tr_loading">
			<td colspan="5" class="mmj_td_loading"><img class="mmj_image_loading" src="${pageContext.request.contextPath }/mmj/statistics/static/images/loading.gif"/></td>
		</tr>
	</table>
</div>
</body>
</html>