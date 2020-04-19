package com.mmj.order.tools;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mmj.common.model.JwtUserDetails;
import com.mmj.common.model.ReturnData;
import com.mmj.common.utils.SecurityUserUtil;
import com.mmj.order.common.feign.GoodFeignClient;
import com.mmj.order.model.OrderInfo;
import com.mmj.order.model.OrderLogistics;
import com.mmj.order.model.dto.OrderGoodsDto;
import com.mmj.order.model.dto.OrderPackageDto;
import com.mmj.order.model.dto.ShopCartsAllDto;
import com.mmj.order.model.dto.ShopCartsDto;
import com.mmj.order.model.vo.GoodClass;
import com.mmj.order.common.model.dto.GoodClassEx;
import com.mmj.order.model.vo.OrderGoodVo;
import com.mmj.order.service.OrderInfoService;
import com.mmj.order.service.OrderLogisticsService;
import com.mmj.order.service.OrderPackageService;
import com.mmj.order.service.OrderPaymentService;
import com.mmj.order.utils.PriceConversion;
import com.mmj.order.utils.http.HttpTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 购物工具类
 */
@Component
public class GroceryLlistUtils {

    // 删除已购订单
    public final static String deleteOrderUrl = "https://api.weixin.qq.com/mall/deleteorder?access_token=";

    // 更新订单
    public final static String updateOrderUrl = "https://api.weixin.qq.com/mall/importorder?action=update-order&access_token=";

    // 导入订单
    public final static String addOrderUrl = "https://api.weixin.qq.com/mall/importorder?action=add-order&access_token=";

    // 删除收藏
    public final static String deleteShoppingListUrl = "https://api.weixin.qq.com/mall/deleteshoppinglist?access_token=";

    // 添加收藏
    public final static String addShoppingListUrl = "https://api.weixin.qq.com/mall/addshoppinglist?access_token=";

    private Logger logger = LoggerFactory.getLogger(GroceryLlistUtils.class);

    @Autowired
    private OrderInfoService orderInfoService;

    @Autowired
    private OrderPaymentService orderPaymentService;

    @Autowired
    private OrderPackageService orderPackageService;

    @Autowired
    private GoodFeignClient goodFeignClient;

    @Autowired
    private OrderLogisticsService orderLogisticsService;


    /**
     * 获取小程序toKen
     * //获取ToKen，只能取生产环境的
     *
     * @return
     */
    private Object getAccessToken() {
        // todo 获取小程序 token
        String accessToken = "";
        JSONObject jsonObject = JSONObject.parseObject(accessToken);
        return jsonObject.get("data");
    }

    /**
     * 删除已购订单
     *
     * @param json
     * @return
     */
    private JSONObject wxDeleteOrder(JSONObject json) {
        Object token = getAccessToken();
        return HttpTools.doPost(deleteOrderUrl + token, json);
    }

    /**
     * 更新已购订单
     *
     * @param json
     * @return
     */
    private JSONObject wxUpdateOrder(JSONObject json) {
        Object token = getAccessToken();
        return HttpTools.doPost(updateOrderUrl + token, json);
    }


    /**
     * 导入已购订单
     *
     * @param json
     * @return
     */
    private JSONObject wxImportOrder(JSONObject json) {
        Object token = getAccessToken();
        return HttpTools.doPost(addOrderUrl + token, json);
    }


    /**
     * 删除想买清单
     *
     * @param json
     * @return
     */
    public JSONObject wxDeleteshoppinglist(JSONObject json) {
        Object token = getAccessToken();
        return HttpTools.doPost(deleteShoppingListUrl + token, json);
    }


    /**
     * 导入想买清单
     *
     * @param json
     * @return
     */
    public JSONObject wxAddshoppinglist(JSONObject json) {
        Object token = getAccessToken();
        return HttpTools.doPost(addShoppingListUrl + token, json);
    }

    /**
     * 删除想买清单
     *
     * @param userId
     * @param shopCartsDtoList
     */
    public void deleteshoppinglist(Long userId, List<ShopCartsDto> shopCartsDtoList) {
        logger.info("开始删除想买清单：" + userId);
        JSONObject json = new JSONObject();
        // todo   获取openid
        String openid = "";

        json.put("user_open_id", openid);
        JSONArray skuProductList = new JSONArray();
        for (ShopCartsDto shopCartsDto : shopCartsDtoList) {
            JSONObject skuProductData = new JSONObject();
            skuProductData.put("item_code", shopCartsDto.getGoodsId());
            skuProductData.put("sku_id", shopCartsDto.getGoodsSkuId());
            skuProductList.add(skuProductData);
        }
        json.put("sku_product_list", skuProductList);
        logger.info("删除想买清单JSON:" + json.toString());
        JSONObject tsresult = wxDeleteshoppinglist(json);
        logger.info("openid:" + openid + "删除想买清单返回结果：" + tsresult.toJSONString());
    }

    /**
     * 导入想买的购物清单
     *
     * @param shopCartsAllDto
     */
    public void addShoppingList(ShopCartsAllDto shopCartsDto) {

        JwtUserDetails jwtUser = SecurityUserUtil.getUserDetails();
        Long userId = jwtUser.getUserId();

        // todo  openid 待实现
        String openid = "";

        logger.info("开始导入想买清单：" + userId);

        JSONObject json = new JSONObject();
        json.put("user_open_id", openid);

        JSONArray skuProductList = new JSONArray();
        JSONObject skuProductData = new JSONObject();
        skuProductData.put("item_code", shopCartsDto.getGoodsId());
        skuProductData.put("title", shopCartsDto.getGoodsTitle());


        //  获取商品分类
        GoodClassEx goodClassEx = new GoodClassEx();
        goodClassEx.setParentCode("0");
        List<GoodClass> goodClasses = new ArrayList<>();
        ReturnData<Map<String, Object>> returnData = goodFeignClient.queryLevel(goodClassEx);
        if (returnData != null) {
            Map<String, Object> map = returnData.getData();
            if (map != null && map.size() > 0) {
                goodClasses = (List<GoodClass>) map.get("goodClasses");
            }
        }


        skuProductData.put("category_list", goodClasses);//商品类目列表

        JSONArray imageList = new JSONArray();
        imageList.add(shopCartsDto.getGoodsImage());//图片地址

        skuProductData.put("image_list", imageList);//商品图片链接列表
        //  todo  小程序模板待验证
        skuProductData.put("src_wxapp_path", "pages/index/detail/main?ch=ON_MIN_HAOWUQUAN&goodsbaseid=" + shopCartsDto.getGoodsId() + "&activeId=&goodsbasetype=3" + "&from=");//商品来源小程序路径


        JSONObject skuInfo = new JSONObject();
        skuInfo.put("sku_id", shopCartsDto.getGoodsSkuId());//skuid
        skuInfo.put("price", PriceConversion.stringToInt(shopCartsDto.getUnitPrice()));//单价
        skuInfo.put("status", 1);//商品状态，1：在售，2：停售
        skuProductData.put("sku_info", skuInfo);//商品SKU信息，微信后台会合并多次导入的SKU

        skuProductList.add(skuProductData);

        json.put("sku_product_list", skuProductList);

        logger.info("导入JSON：" + json);
        JSONObject tsresult = wxAddshoppinglist(json);
        logger.info("上传想买清单返回结果：" + tsresult.toJSONString());

        json.clear();

    }


    /**
     * 删除购物单，该方法可删除shopOrder老订单和orders店铺订单的微信购物单
     *
     * @param openId
     * @param orderNo
     */
    public void deleteOrder(String openId, String orderNo) {
        JSONObject json = new JSONObject();
        json.put("user_open_id", openId);
        json.put("order_id", orderNo);

        JSONObject tsresult = wxDeleteOrder(json);
        logger.info("删除订单返回结果" + tsresult.toJSONString());
    }


    /**
     * 10 元点订单添加购物订单
     *
     * @param orderNo
     * @param status
     */
    public void addOrder(String orderNo, Integer status) {
        logger.info("开始上传购物单：" + orderNo);

        JwtUserDetails jwtUser = SecurityUserUtil.getUserDetails();
        Long userId = jwtUser.getUserId();

        JSONObject json = new JSONObject();
        JSONArray orderList = new JSONArray();
        JSONObject orderData = new JSONObject();

        // todo  openid 待实现
        String openid = "";


        // todo  通过订单号获取微信支付信息
      /*  OrderPayment orderPayment = orderInfoService.getOrderPayment(orderNo, userId);
        if (orderPayment == null) {
            return;
        }*/
        OrderInfo orderInfo = orderInfoService.selectByOrderNo(orderNo, userId);


        orderData.put("order_id", orderInfo.getOrderNo());//订单id，需要保证唯一性
        orderData.put("create_time", orderInfo.getCreaterTime().getTime() / 1000);//订单创建时间，
        // todo  微信支付待实现
//        orderData.put("pay_finish_time", orderPayment.getCreaterTime().getTime() / 1000);//支付完成时间

        orderData.put("fee", orderInfo.getOrderAmount());//订单金额，单位：分
        //  todo  微信支付待实现
//        orderData.put("trans_id", wxorder.getTransactionId());//微信支付订单id，对于使用微信支付的订单，该字段必填
        orderData.put("status", 3);//订单状态，3：支付完成 4：已发货 5：已退款 100: 已完成


        JSONObject extInfo = new JSONObject();//订单扩展信息
        JSONObject productInfoDate = new JSONObject();//商品相关信息

        JSONArray itemList = new JSONArray();

        List<OrderPackageDto> list = orderInfoService.getOrderPackages(orderNo, String.valueOf(userId));
        if (list.size() <= 0 || list == null) {
            return;
        }


        List<OrderLogistics> orderLogisticsList = orderLogisticsService.getOrderLogistics(orderNo, userId);

        //  获取商品分类
        GoodClassEx goodClassEx = new GoodClassEx();
        goodClassEx.setParentCode("0");
//        Map<String, Object> map = goodFeignClient.queryLevel(goodClassEx);

        List<GoodClass> goodClasses = new ArrayList<>();
        ReturnData<Map<String, Object>> returnData = goodFeignClient.queryLevel(goodClassEx);
        if (returnData != null) {
            Map<String, Object> map = returnData.getData();
            if (map != null && map.size() > 0) {
                goodClasses = (List<GoodClass>) map.get("goodClasses");
            }
        }


        for (int i = 0; i < list.size(); i++) {
            List<OrderGoodsDto> goods = list.get(i).getGood();
            for (int j = 0; j < goods.size(); j++) {

                JSONObject itemDate = new JSONObject();
                itemDate.put("item_code", goods.get(j).getGoodId());//商品id
                itemDate.put("sku_id", goods.get(j).getSaleId());//sku_id

                itemDate.put("amount", goods.get(j).getGoodNum());//商品数量
                Integer totalFee = PriceConversion.stringToInt(goods.get(j).getGoodAmount()) * goods.get(j).getGoodNum() * 100;
                itemDate.put("total_fee", totalFee);//商品总价，单位：分
                itemDate.put("thumb_url", goods.get(j).getGoodImage());//商品缩略图url
                itemDate.put("title", goods.get(j).getGoodTitle());//商品名称
                itemDate.put("unit_price", PriceConversion.stringToInt(goods.get(j).getGoodAmount()) * 100);//商品单价（实际售价），单位：分
                //  todo  表缺少商品原价字段
//                itemDate.put("original_price", orderGoods.getOriginalPrice());//商品原价，单位：分

                JSONArray categoryList = new JSONArray();
                for (GoodClass goodClass : goodClasses) {
                    categoryList.add(goodClass.getClassName());
                }
                itemDate.put("category_list", categoryList);//商品类目列表
                JSONObject itemDetailPage = new JSONObject();

                // todo  小程序模板 待实现
                /*itemDetailPage.put("path","pages/index/detail/main?ch=ON_MIN_HAOWUQUAN&goodsbaseid="+goodsbase.getGoodsbaseid()+"&activeId=&goodsbasetype=3"+"&from=");//
                itemDate.put("item_detail_page",itemDetailPage);//商品详情页（小程序页面）*/
                itemList.add(itemDate);
            }
        }

        productInfoDate.put("item_list", itemList);
        extInfo.put("product_info", productInfoDate);

        JSONObject expressInfo = new JSONObject();
        if (orderLogisticsList != null && orderLogisticsList.size() > 0) {
            OrderLogistics orderLogistics = orderLogisticsList.get(0);

            expressInfo.put("name", orderLogistics.getConsumerName());//收件人姓名
            expressInfo.put("phone", orderLogistics.getConsumerMobile());//收件人联系电话
            String address = "";
            if (orderLogistics.getProvince().equals(orderLogistics.getCity())) {
                address = orderLogistics.getCity() + orderLogistics.getArea() + orderLogistics.getConsumerAddr();
            } else {
                address = orderLogistics.getProvince() + orderLogistics.getCity() + orderLogistics.getArea() + orderLogistics.getArea();
            }
            expressInfo.put("address", address);//收件人地址
//            expressInfo.put("price", orderLogistics.getLogisticsAmount());//运费，单位：分
            expressInfo.put("country", "中国");//国家
            extInfo.put("express_info", expressInfo);

            JSONObject promotionInfo = new JSONObject();
            //  todo  价格待统一
            promotionInfo.put("discount_fee", orderInfo.getCouponAmount());//	优惠金额
            extInfo.put("promotion_info", promotionInfo);


            JSONObject brandInfo = new JSONObject();
            brandInfo.put("phone", "400-8760618");//联系电话，必须提供真实有效的联系电话，缺少联系电话或联系电话不正确将影响商品曝光

            JSONObject contactDetailPage = new JSONObject();
            contactDetailPage.put("path", "/libs/xxxxx/portal/contact_detail/xxxx");//联系商家页跳转链接（小程序页面）
            brandInfo.put("contact_detail_page", contactDetailPage);

            extInfo.put("brand_info", brandInfo);

            extInfo.put("payment_method", 1);//支付方式
            extInfo.put("user_open_id", openid);//
            JSONObject orderDetailPage = new JSONObject();
            orderDetailPage.put("path", "/pkgOrder/orderDetail/main?orderNo=" + orderNo + "&ed=1");//订单详情页跳转链接（小程序页面）
            extInfo.put("order_detail_page", orderDetailPage);//

            orderData.put("ext_info", extInfo);//

            orderList.add(orderData);
            json.put("order_list", orderList);

            logger.info("导入" + orderNo + "购物单JSON： " + json);
            //导入订单
            JSONObject tsresult = wxImportOrder(json);
            logger.info("导入" + orderNo + "购物单返回结果：" + tsresult.toJSONString());

        }

    }


    /**
     * 店铺订单更新购物单
     */
    public void updateOrder(String orderNo, Integer status) {
        logger.info("开始更新购物单：" + orderNo);

        JwtUserDetails jwtUser = SecurityUserUtil.getUserDetails();
        Long userId = jwtUser.getUserId();

        JSONObject json = new JSONObject();
        JSONArray orderList = new JSONArray();
        JSONObject orderData = new JSONObject();

        // todo  openid 待实现
        String openid = "";


        // todo  通过订单号获取微信支付信息
      /*  OrderPayment orderPayment = orderInfoService.getOrderPayment(orderNo, userId);
        if (orderPayment == null) {
            return;
        }*/
        OrderInfo orderInfo = orderInfoService.selectByOrderNo(orderNo, userId);
        List<OrderLogistics> orderLogisticsList = orderLogisticsService.getOrderLogistics(orderNo, userId);

        OrderGoodVo orderGoodVo = new OrderGoodVo();
        orderGoodVo.setOrderNo(orderNo);
        orderGoodVo.setUserId(String.valueOf(userId));
        List<OrderGoodsDto> orderGoodsDtoList = orderInfoService.getOrderGoodList(orderGoodVo);


        orderData.put("order_id", orderNo);
        //  todo 微信支付信息待实现
//        orderData.put("trans_id",wxorder.getTransactionId());
        orderData.put("status", status);//订单状态，3：支付完成 4：已发货 5：已退款 100: 已完成
        JSONObject extInfo = new JSONObject();


        if (orderLogisticsList != null && orderLogisticsList.size() > 0) {
            OrderLogistics orderLogistics = orderLogisticsList.get(0);

            JSONObject expressInfo = new JSONObject();
            expressInfo.put("name", orderLogistics.getConsumerName());//收货人
            expressInfo.put("phone", orderLogistics.getConsumerMobile());//电话
            String address = "";
            if (orderLogistics.getProvince().equals(orderLogistics.getCity())) {
                address = orderLogistics.getCity() + orderLogistics.getArea() + orderLogistics.getConsumerAddr();
            } else {
                address = orderLogistics.getProvince() + orderLogistics.getCity() + orderLogistics.getArea() + orderLogistics.getConsumerAddr();
            }
            expressInfo.put("address", address);
            // todo 价格待统一
//            expressInfo.put("price", orderLogistics.getLogisticsAmount());//运费，单位：分

            JSONArray expressPackageInfoList = new JSONArray();
            JSONObject expressPackageInfoData = new JSONObject();
            expressPackageInfoData.put("express_company_id", "2008");//快递公司编号 todo 暂时默认为百世汇通
            //因为更新购物单接口快递公司等字段为必填，所以当待发货订单发生取消退款时默认设置固定快递公司地址和时间
            if (orderLogisticsList.isEmpty()) {
                expressPackageInfoData.put("express_company_name", "百世汇通");//快递公司
                expressPackageInfoData.put("express_code", "88888888");//快递单号
                expressPackageInfoData.put("ship_time", new Date().getTime() / 1000);//发货时间
            } else {
                expressPackageInfoData.put("express_company_name", orderLogisticsList.get(0).getCompanyName());//快递公司
                expressPackageInfoData.put("express_code", orderLogisticsList.get(0).getLogisticsNo());//快递单号
                expressPackageInfoData.put("ship_time", orderLogisticsList.get(0).getSendTime().getTime() / 1000);//发货时间
            }


            JSONObject expressPage = new JSONObject();
            expressPage.put("path", "/pkgOrder/orderDetail/main?orderNo=" + orderNo + "&ed=1");//快递信息跳转页 先跳转到订单详情页
            expressPackageInfoData.put("express_page", expressPage);

            JSONArray expressGoodsInfoList = new JSONArray();
            JSONObject expressGoodsInfoData = new JSONObject();

            for (OrderGoodsDto orderGoodsDto : orderGoodsDtoList) {
                expressGoodsInfoData.put("item_code", orderGoodsDto.getGoodId());//商品id
                expressGoodsInfoData.put("sku_id", orderGoodsDto.getSaleId());//sku
            }

            expressGoodsInfoList.add(expressGoodsInfoData);

            expressPackageInfoData.put("express_goods_info_list", expressGoodsInfoList);

            expressPackageInfoList.add(expressPackageInfoData);


            expressInfo.put("express_package_info_list", expressPackageInfoList);
            extInfo.put("express_info", expressInfo);
            extInfo.put("user_open_id", openid);//openid

            orderData.put("ext_info", extInfo);
            orderList.add(orderData);
            json.put("order_list", orderList);
            logger.info("购物单JSON：" + json);
            JSONObject tsresult = wxUpdateOrder(json);
            logger.info("更新购物单返回结果" + tsresult.toJSONString());


        }

    }

    /**
     * 拼团更新
     *
     * @param orderNo
     * @param status
     */
    public void updateShopOrder(String orderNo, Integer status) {

        JSONObject json = new JSONObject();
        JSONArray orderList = new JSONArray();
        JSONObject orderData = new JSONObject();

        JwtUserDetails jwtUser = SecurityUserUtil.getUserDetails();
        Long userId = jwtUser.getUserId();

        String openid = "";
        OrderInfo orderInfo = orderInfoService.selectByOrderNo(orderNo, userId);

        List<OrderLogistics> orderLogisticsList = orderLogisticsService.getOrderLogistics(orderNo, userId);

         OrderGoodVo orderGoodVo = new OrderGoodVo();
         orderGoodVo.setOrderNo(orderNo);
         orderGoodVo.setUserId(String.valueOf(userId));
        List<OrderGoodsDto> orderGoodsDtoList = orderInfoService.getOrderGoodList(orderGoodVo);
        if (orderGoodsDtoList == null || orderGoodsDtoList.size() <= 0) {
            return;
        }

        //  todo  查询支付消息 ，待实现
      /*  WxOrder wo = new WxOrder();
        wo.setOutTradeNo(order.getOrderno());
        WxOrder wxorder = wxOrderService.queryByOutTradeNo(wo);
        if(wxorder ==null ){
            return;
        }*/

        // todo  支付待实现
     /*   orderData.put("order_id",order.getOrderno());
        orderData.put("trans_id",wxorder.getTransactionId());*/
        orderData.put("status", status);//订单状态，3：支付完成 4：已发货 5：已退款 100: 已完成
        JSONObject extInfo = new JSONObject();
        if (orderLogisticsList.size() <= 0 || orderGoodsDtoList == null) {
            OrderLogistics orderLogistics = orderLogisticsList.get(0);
            OrderGoodsDto orderGoodsDto = orderGoodsDtoList.get(0);

            JSONObject expressInfo = new JSONObject();
            expressInfo.put("name", orderLogistics.getConsumerName());//收货人
            expressInfo.put("phone", orderLogistics.getConsumerMobile());//电话
            String address = "";
            if (orderLogistics.getProvince().equals(orderLogistics.getCity())) {
                address = orderLogistics.getCity() + orderLogistics.getArea() + orderLogistics.getConsumerAddr();
            } else {
                address = orderLogistics.getProvince() + orderLogistics.getCity() + orderLogistics.getArea() + orderLogistics.getConsumerAddr();
            }
            expressInfo.put("address", address);
            //  todo  价格待统一
//            expressInfo.put("price", orderLogistics.getLogisticsAmount());//运费，单位：分

            JSONArray expressPackageInfoList = new JSONArray();
            JSONObject expressPackageInfoData = new JSONObject();
            expressPackageInfoData.put("express_company_id", "2008");//快递公司编号 todo 暂时默认为百世汇通
            expressPackageInfoData.put("express_company_name", orderLogistics.getCompanyName());//快递公司
            expressPackageInfoData.put("express_code", orderLogistics.getLogisticsNo());//快递单号
            expressPackageInfoData.put("ship_time", orderLogistics.getSendTime().getTime() / 1000);//发货时间


            JSONObject expressPage = new JSONObject();
            expressPage.put("path", "/pkgOrder/orderDetail/main?orderNo=" + orderNo + "&ed=1");//快递信息跳转页 先跳转到订单详情页
            expressPackageInfoData.put("express_page", expressPage);

            JSONArray expressGoodsInfoList = new JSONArray();
            JSONObject expressGoodsInfoData = new JSONObject();

            expressGoodsInfoData.put("item_code", orderGoodsDto.getGoodId());//商品id
            expressGoodsInfoData.put("sku_id", orderGoodsDto.getSaleId());//skuid

            expressGoodsInfoList.add(expressGoodsInfoData);


            expressPackageInfoData.put("express_goods_info_list", expressGoodsInfoList);
            expressPackageInfoList.add(expressPackageInfoData);
            expressInfo.put("express_package_info_list", expressPackageInfoList);
            expressInfo.put("user_open_id", openid);//openid
            extInfo.put("express_info", expressInfo);
            orderData.put("ext_info", extInfo);
            orderList.add(orderData);
            json.put("order_list", orderList);
            JSONObject tsresult = wxUpdateOrder(json);
            logger.info("更新购物单返回结果" + tsresult.toJSONString());
        }
    }


    public void addShopOrder(String orderNo) {

        JSONObject json = new JSONObject();
        JSONArray orderList = new JSONArray();
        JSONObject orderData = new JSONObject();

        JwtUserDetails jwtUser = SecurityUserUtil.getUserDetails();
        Long userId = jwtUser.getUserId();

        String openid = "";
        OrderInfo orderInfo = orderInfoService.selectByOrderNo(orderNo, userId);

        List<OrderLogistics> orderLogisticsList = orderLogisticsService.getOrderLogistics(orderNo, userId);


        OrderGoodVo orderGoodVo = new OrderGoodVo();
        orderGoodVo.setOrderNo(orderNo);
        orderGoodVo.setUserId(String.valueOf(userId));
        List<OrderGoodsDto> orderGoodsDtoList = orderInfoService.getOrderGoodList(orderGoodVo);
        if (orderGoodsDtoList == null || orderGoodsDtoList.size() <= 0) {
            return;
        }

        if (orderLogisticsList == null || orderLogisticsList.size() <= 0) {
            return;
        }


        //  todo  查询支付消息
       /* WxOrder wo = new WxOrder();
        wo.setOutTradeNo(shopOrder.getOrderno());
        WxOrder wxorder = wxOrderService.queryByOutTradeNo(wo);
        if(wxorder ==null ){
            return;
        }*/


        orderData.put("order_id", orderInfo.getOrderNo());//订单id，需要保证唯一性
        orderData.put("create_time", orderInfo.getCreaterTime().getTime() / 1000);//订单创建时间，

        // todo  微信支付待实现
//        orderData.put("pay_finish_time",wxorder.getCreateTime().getTime()/1000);//支付完成时间

        orderData.put("fee", orderInfo.getOrderAmount());//订单金额，单位：分
        //  todo  微信支付待实现
//        orderData.put("trans_id",wxorder.getTransactionId());//微信支付订单id，对于使用微信支付的订单，该字段必填
        orderData.put("status", 3);//订单状态，3：支付完成 4：已发货 5：已退款 100: 已完成

        JSONObject extInfo = new JSONObject();//订单扩展信息
        JSONObject productInfoDate = new JSONObject();//商品相关信息

        JSONArray itemList = new JSONArray();
        JSONObject itemDate = new JSONObject();

        OrderGoodsDto orderGoodsDto = orderGoodsDtoList.get(0);


        itemDate.put("item_code", orderGoodsDto.getGoodId());//商品id
        itemDate.put("sku_id", orderGoodsDto.getSaleId());//sku_id
        itemDate.put("amount", orderGoodsDto.getGoodNum());//商品数量
        itemDate.put("total_fee", orderInfo.getOrderAmount());//商品总价，单位：分
        itemDate.put("thumb_url", orderGoodsDto.getGoodImage());//商品缩略图url
        itemDate.put("title", orderGoodsDto.getGoodTitle());//商品名称
        // todo 价格 待修改
//        itemDate.put("unit_price",PriceConversion.bigDecimalToInt(orderGoodsDto.getGoodsAmount()));//商品单价（实际售价），单位：分
//        itemDate.put("original_price",PriceConversion.bigDecimalToInt(shopOrderDetail.getBasePrice()));//商品原价，单位：分


        GoodClassEx goodClassEx = new GoodClassEx();
        goodClassEx.setParentCode("0");
//        Map<String, Object> map = goodFeignClient.queryLevel(goodClassEx);

        ReturnData<Map<String, Object>> returnData = goodFeignClient.queryLevel(goodClassEx);
        if (returnData != null) {
            Map<String, Object> map = returnData.getData();
            List<GoodClass> goodClasses = new ArrayList<>();
            if (map != null && map.size() > 0) {
                goodClasses = (List<GoodClass>) map.get("goodClasses");
            }
            JSONArray categoryList = new JSONArray();
            for (GoodClass goodClass : goodClasses) {
                categoryList.add(goodClass.getClassName());
            }
            itemDate.put("category_list", categoryList);//商品类目列表
        }


        JSONObject itemDetailPage = new JSONObject();
        //  todo 小城待修改
//        itemDetailPage.put("path","pages/index/detail/main?ch=ON_MIN_HAOWUQUAN&goodsbaseid="+goodsbase.getGoodsbaseid()+"&activeId=&goodsbasetype=3"+"&from=");//
        itemDate.put("item_detail_page", itemDetailPage);//商品详情页（小程序页面）

        itemList.add(itemDate);
        productInfoDate.put("item_list", itemList);
        extInfo.put("product_info", productInfoDate);


        OrderLogistics orderLogistics = orderLogisticsList.get(0);
        JSONObject expressInfo = new JSONObject();
        expressInfo.put("name", orderLogistics.getConsumerName());//收件人姓名
        expressInfo.put("phone", orderLogistics.getConsumerMobile());//收件人联系电话
        String address = "";
        if (orderLogistics.getProvince().equals(orderLogistics.getCity())) {
            address = orderLogistics.getCity() + orderLogistics.getArea() + orderLogistics.getConsumerAddr();
        } else {
            address = orderLogistics.getProvince() + orderLogistics.getCity() + orderLogistics.getArea() + orderLogistics.getConsumerAddr();
        }

        expressInfo.put("address", address);//收件人地址
        expressInfo.put("price", 0);//运费，单位：分
        expressInfo.put("country", "中国");//国家
        expressInfo.put("province", orderLogistics.getProvince());//省份
        expressInfo.put("city", orderLogistics.getCity());//城市
        expressInfo.put("district", orderLogistics.getArea());//区
        extInfo.put("express_info", expressInfo);


        orderInfo.getGoodAmount();


        JSONObject promotionInfo = new JSONObject();
        //  todo  价格待统一
        promotionInfo.put("discount_fee", orderInfo.getGoodAmount());//	优惠金额
        extInfo.put("promotion_info", promotionInfo);

        JSONObject brandInfo = new JSONObject();
        brandInfo.put("phone", "400-8760618");//联系电话，必须提供真实有效的联系电话，缺少联系电话或联系电话不正确将影响商品曝光

        JSONObject contactDetailPage = new JSONObject();
        contactDetailPage.put("path", "/libs/xxxxx/portal/contact_detail/xxxx");//联系商家页跳转链接（小程序页面）
        brandInfo.put("contact_detail_page", contactDetailPage);

        extInfo.put("brand_info", brandInfo);

        extInfo.put("payment_method", 1);//支付方式
        extInfo.put("user_open_id", openid);//
        JSONObject orderDetailPage = new JSONObject();
        // todo  小程序模板待统一实现
        orderDetailPage.put("path", "/pkgOrder/orderDetail/main?orderNo=" + orderInfo.getOrderNo() + "&ed=1");//订单详情页跳转链接（小程序页面）
        extInfo.put("order_detail_page", orderDetailPage);//

        orderData.put("ext_info", extInfo);//

        orderList.add(orderData);
        json.put("order_list", orderList);

        //导入订单
        JSONObject tsresult = wxImportOrder(json);

        logger.info("导入购物单返回结果：" + tsresult.toJSONString());


    }


}
