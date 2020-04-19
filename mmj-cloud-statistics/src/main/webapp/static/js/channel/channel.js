$(document).ready(function(){
	init();
	$("#mmj_btn_search").click(function(){
		if($("#channel").val() == "") {
			alert("请填写渠道编码");
			return;
		}
		if($("#startDate").val() == "") {
			alert("请填写起始时间");
			return;
		}
		if($("#startDate").val() == "") {
			alert("请填写截止时间");
			return;
		}
		showLoading();
		loadData();
	});
	
	function init() {
		$("#mmj_td_total").html("--");
		$("#mmj_td_authorized").html("--");
		$("#mmj_td_order").html("--");
		$("#mmj_td_money").html("--元");
		$("#mmj_tr_loading").hide();
	}
	
	function showLoading() {
		$("tr[name='mmj_tr_data']").remove();
		$("#mmj_tr_loading").show();
		$("#mmj_td_total").html("<img class='mmj_image_loading' src='"+domain+"/mmj/statistics/static/images/loading.gif'/>");
		$("#mmj_td_authorized").html("<img class='mmj_image_loading' src='"+domain+"/mmj/statistics/static/images/loading.gif'/>");
		$("#mmj_td_order").html("<img class='mmj_image_loading' src='"+domain+"/mmj/statistics/static/images/loading.gif'/>");
		$("#mmj_td_money").html("<img class='mmj_image_loading' src='"+domain+"/mmj/statistics/static/images/loading.gif'/>");
	}
	
	function hideLoading() {
		$("#mmj_tr_loading").hide();
	}
	
	function loadData() {
		$.ajax({
            type: "POST",
			contentType:"application/json",
            dataType: "json",
            url: domain+"/mmj/statistics/channel/export" ,
            data: JSON.stringify($("#distributorForm").serializeObject()),
            success: function (result) {
                if (result.code == 1) {
                    showData(result.data);
                } else {
                	var errorMsg = result.desc;
                	init();
                	alert(errorMsg);
                }
            },
            error : function(e) {
            	init();
                alert("查询数据发生异常！");
            }
        });
	}
	
	function showData(data) {
		var authorizedCount = 0;
		for(var i=0;i<data.channelUserList.length;i++) {
			var html = "<tr name='mmj_tr_data'>";
			html = html + "<td>"+data.channelUserList[i].createTime+"</td><td>"+data.channelCode+"</td><td>"+data.channelUserList[i].openId+"</td><td>"+data.channelUserList[i].authorized+"</td>";
			html = html + "</tr>";
			$("#mmj_table_head").after(html);
			if(data.channelUserList[i].authorized=="是") {
				authorizedCount = authorizedCount + 1;
			}
		}
		$("#mmj_td_total").html("<span style='color:red;'>"+data.userTotalCount+"</span>");
		$("#mmj_td_authorized").html("<span style='color:red;'>"+authorizedCount+"</span>");
		var orderCount = data.orderCount;
		if(typeof(orderCount) == "undefined") {
			orderCount = "--"
		}
		var orderAmountStr = data.orderAmountStr;
		if(typeof(orderAmountStr) == "undefined") {
			orderAmountStr = "--";
		}
		$("#mmj_td_order").html("<span style='color:red;'>"+orderCount+"</span>");
		$("#mmj_td_money").html("<span style='color:red;'>"+orderAmountStr+"</span>元");
		hideLoading();
	}
	
    $.fn.serializeObject = function() {
        var o = {};
        var a = this.serializeArray();
        $.each(a, function() {
            if (o[this.name]) {
                if (!o[this.name].push) {
                    o[this.name] = [ o[this.name] ];
                }
                o[this.name].push(this.value || '');
            } else {
                o[this.name] = this.value || '';
            }
        });
        return o;
    };
});