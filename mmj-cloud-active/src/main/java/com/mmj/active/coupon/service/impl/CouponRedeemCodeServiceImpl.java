package com.mmj.active.coupon.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.mmj.active.common.constants.WxMedia;
import com.mmj.active.common.feigin.CouponUserFeignClient;
import com.mmj.active.common.feigin.NoticeFeignClient;
import com.mmj.active.common.feigin.UserFeignClient;
import com.mmj.active.common.feigin.WxMessageFeignClient;
import com.mmj.active.common.model.vo.UserCouponVo;
import com.mmj.active.coupon.mapper.CouponRedeemCodeMapper;
import com.mmj.active.coupon.mapper.CouponRedeemCodeTemplateidMapper;
import com.mmj.active.coupon.model.CouponRedeemCode;
import com.mmj.active.coupon.model.CouponRedeemCodeTemplateid;
import com.mmj.active.coupon.model.dto.CouponInfoDto;
import com.mmj.active.coupon.model.vo.ExchangeCouponVo;
import com.mmj.active.coupon.model.vo.RedeemCodeVo;
import com.mmj.active.coupon.service.CouponRedeemCodeService;
import com.mmj.active.coupon.service.CouponRedeemCodeTemplateidService;
import com.mmj.active.coupon.utils.ExcelUtil;
import com.mmj.common.model.ReturnData;
import com.mmj.common.model.UserCouponDto;
import com.mmj.common.model.UserLogin;
import com.mmj.common.model.UserReceiveCouponDto;
import com.xiaoleilu.hutool.system.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.*;


/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author zhangyicao
 * @since 2019-09-03
 */
@Slf4j
@Service
public class CouponRedeemCodeServiceImpl extends ServiceImpl<CouponRedeemCodeMapper, CouponRedeemCode> implements CouponRedeemCodeService {

    @Autowired
    private CouponRedeemCodeTemplateidMapper couponRedeemCodeTemplateidMapper;
    @Autowired
    private CouponRedeemCodeMapper couponRedeemCodeMapper;
    @Autowired
    private UserFeignClient userFeignClient;
    @Autowired
    private CouponUserFeignClient couponUserFeignClient;
    @Autowired
    private WxMessageFeignClient wxMessageFeignClient;
    @Autowired
    private NoticeFeignClient noticeFeignClient;

    /**
     * 生成优惠券兑换码
     *
     * @param redeemCodeVo
     * @return
     */
    @Override
    public String addRedeemCode(RedeemCodeVo redeemCodeVo) {

        //获取批次编码
        Long batchCode = System.currentTimeMillis();

        //记录批次信息，关联优惠券id
        String[] templateids = redeemCodeVo.getTemplateIds().split(",");
        for (String templateid : templateids) {
            CouponRedeemCodeTemplateid codeTemplateid = new CouponRedeemCodeTemplateid();
            codeTemplateid.setBatchCode(String.valueOf(batchCode));
            codeTemplateid.setCouponTemplateid(Integer.valueOf(templateid));
            codeTemplateid.setCreateTime(new Date());
            couponRedeemCodeTemplateidMapper.insert(codeTemplateid);
        }

        //写入兑换码
        long n = redeemCodeVo.getCreateNum();
        //MCE（M:买买家，C:优惠券，E:兑换码）
        String defalt = "MCE-";
        List<CouponRedeemCode> relayLotteryCodes = Lists.newArrayList();
        for (int i = 0; i < n; i++) {
            CouponRedeemCode couponRedeemCode = new CouponRedeemCode();
            couponRedeemCode.setBatchCode(batchCode.toString());
            couponRedeemCode.setRedeemCode(defalt + getUUID());
            couponRedeemCode.setCreateTime(new Date());
            relayLotteryCodes.add(couponRedeemCode);
        }
        couponRedeemCodeMapper.batchInsert(relayLotteryCodes);

        return batchCode.toString();
    }


    @Override
    public String downloadRedeemCode(HttpServletRequest request, HttpServletResponse response, String batchCode) {
        log.info("进入兑换码下载，入参：batchCode=" + batchCode);
        if ("".equals(batchCode)) {
            log.info("兑换码不可以为空");
            return "兑换码不可以为空";
        }
        // 获取数据
        List<CouponRedeemCode> couponRedeemCodes = couponRedeemCodeMapper.selectRedeemCodes(batchCode);

        String[] title = {"兑换码"};

        String fileName = "兑换码" + System.currentTimeMillis() + ".xls";

        String sheetName = "销售流量报表";

        String[][] content = new String[couponRedeemCodes.size()][];

        for (int i = 0; i < couponRedeemCodes.size(); i++) {
            content[i] = new String[title.length];
            CouponRedeemCode couponRedeemCode = couponRedeemCodes.get(i);
            content[i][0] = couponRedeemCode.getRedeemCode();
        }

        HSSFWorkbook wb = ExcelUtil.getHSSFWorkbook(sheetName, title, content, null);

        // 响应到客户端
        try {
            this.setResponseHeader(response, fileName);
            OutputStream os = response.getOutputStream();
            wb.write(os);
            os.flush();
            os.close();
        } catch (Exception e) {
            log.error("-->exportGoods发生异常", e);
        }
        return "下载成功";
    }

    //    @Async
    @Override
    public void kafkaExchangeCoupon(JSONObject jsonObject) {
        if (jsonObject != null) {
            String appid = jsonObject.getJSONObject("ex").getString("appid");
            String openId = jsonObject.getJSONObject("ex").getString("openid");
            String keyword = jsonObject.getString("Content");
            log.info("-->公众号消息-->用户{}优惠券兑换码:{}，appid:{}", openId, keyword, appid);

            //判断是否优惠券兑换码
            if (keyword.startsWith("MCE")) {
                log.info("-用户{}优惠券兑换码开始兑换:{}", openId, keyword);
                ExchangeCouponVo exchangeCouponVo = new ExchangeCouponVo();
                exchangeCouponVo.setOpenId(openId);
                exchangeCouponVo.setRedeemCode(keyword);
                Map<String, Object> resultMap = exchangeCoupon(exchangeCouponVo);
                log.info("=> 优惠券兑换入参:{},返回:{}", JSON.toJSONString(exchangeCouponVo), resultMap);
                boolean status = (Boolean) resultMap.get("status");
                if (status) {
                    try {
                        //获取素材
                        WxMedia wxMedia = new WxMedia();
                        wxMedia.setAppid(appid);
                        wxMedia.setBusinessName("优惠券兑换码");
                        wxMedia.setMediaType("forever");
                        wxMedia.setMediaUrl("https://dxs-mmj-1257049906.cos.ap-guangzhou.myqcloud.com/pre-product/20190907-mdssds-kk.png");
                        ReturnData<WxMedia> wxMediaReturnData = noticeFeignClient.wxMediaUpload(wxMedia);
                        //发送小程序卡片
                        JSONObject msgJson = new JSONObject();
                        msgJson.put("touser", openId);
                        msgJson.put("msgtype", "miniprogrampage");
                        msgJson.put("appid", appid);
                        JSONObject miniJson = new JSONObject();
                        miniJson.put("title", "恭喜你,兑换成功,点击去使用");
                        miniJson.put("appid", "wx7a01aef90c714fe2");
                        miniJson.put("pagepath", "pages/index/main?redeemCode=" + keyword);
                        miniJson.put("thumb_media_id", wxMediaReturnData.getData().getMediaId());
                        msgJson.put("miniprogrampage", miniJson);
                        wxMessageFeignClient.sendCustom(JSONObject.toJSONString(msgJson));
                    } catch (Exception e) {
                        log.error("=>优惠券兑换码，发送卡片失败:{}", e.toString());
                    }
                }
            }
        }
    }

    /**
     * 兑换优惠券
     *
     * @param exchangeCouponVo
     * @return
     */
    @Override
    public Map<String, Object> exchangeCoupon(ExchangeCouponVo exchangeCouponVo) {
        //查询兑换码
        Map<String, Object> map = new HashMap<>();
        log.info("=> 优惠券兑换，通过openId查询用户信息 openId:{}", exchangeCouponVo.getOpenId());
        UserLogin userLogin = userFeignClient.getUserLoginInfoByUserName(exchangeCouponVo.getOpenId()).getData();
        log.info("=> 优惠券兑换，通过openId查询用户信息 返回:{}", Objects.nonNull(userLogin) ? JSON.toJSONString(userLogin) : userLogin);
        if (userLogin == null) {
            map.put("status", false);
            map.put("msg", "用户信息为空");
            return map;
        }
        try {
            EntityWrapper<CouponRedeemCode> couponRedeemCodeEntityWrapper = new EntityWrapper<>();
            couponRedeemCodeEntityWrapper.eq("REDEEM_CODE", exchangeCouponVo.getRedeemCode());
            couponRedeemCodeEntityWrapper.eq("ACTIVE", true);
            List<CouponRedeemCode> couponRedeemCodes = couponRedeemCodeMapper.selectList(couponRedeemCodeEntityWrapper);
            log.info("=> 优惠券兑换，通过兑换码查询信息 redeemCode:{},result:{}", exchangeCouponVo.getRedeemCode(), JSON.toJSONString(couponRedeemCodes));
            if (couponRedeemCodes.isEmpty()) {
                map.put("status", false);
                map.put("msg", "兑换码已使用");
            } else {
                CouponRedeemCode couponRedeemCode = couponRedeemCodes.get(0);

                //根据批次id查优惠券
                EntityWrapper<CouponRedeemCodeTemplateid> couponRedeemCodeTemplateIdEntityWrapper = new EntityWrapper<>();
                couponRedeemCodeTemplateIdEntityWrapper.eq("BATCH_CODE", couponRedeemCode.getBatchCode());
                List<CouponRedeemCodeTemplateid> templateidList = couponRedeemCodeTemplateidMapper.selectList(couponRedeemCodeTemplateIdEntityWrapper);
                log.info("=> 优惠券兑换，通过兑换码查询绑定优惠券 redeemCode:{},batchCode:{},result:{}", exchangeCouponVo.getRedeemCode(), couponRedeemCode.getBatchCode(), JSON.toJSONString(templateidList));
                //发卷
                List<String> couponCodeList = Lists.newArrayListWithCapacity(templateidList.size());
                for (CouponRedeemCodeTemplateid codeTemplateid : templateidList) {
                    UserCouponVo userCouponVo = new UserCouponVo();
                    userCouponVo.setCouponSource("REDEEM_CODE");
                    userCouponVo.setCouponId(codeTemplateid.getCouponTemplateid());
                    userCouponVo.setUserId(userLogin.getUserId());
                    log.info("=> 优惠券兑换，发放优惠券请求参数:{}", JSON.toJSONString(userCouponVo));
                    UserReceiveCouponDto userReceiveCouponDto = couponUserFeignClient.receive(userCouponVo).getData();
                    log.info("=> 优惠券兑换，发放优惠券返回数据:{}", JSON.toJSONString(userReceiveCouponDto));
                    if (userReceiveCouponDto != null && null != userReceiveCouponDto.getUserCoupon().getCouponCode()) {
                        couponCodeList.add(userReceiveCouponDto.getUserCoupon().getCouponCode().toString());
                    }

                }
                map.put("status", true);
                map.put("msg", "兑换成功");

                //兑换码置为无效
                couponRedeemCode.setActive(false);
                couponRedeemCode.setUpdateTime(new Date());
                couponRedeemCode.setCouponCode(String.join(",", couponCodeList));
                EntityWrapper<CouponRedeemCode> couponRedeemCodeEntityWrapper1 = new EntityWrapper<>();
                couponRedeemCodeEntityWrapper1.eq("ID", couponRedeemCode.getId());
                couponRedeemCodeMapper.update(couponRedeemCode, couponRedeemCodeEntityWrapper1);
            }
        } catch (Exception e) {
            log.error("调用发券方法异常:{}", e.toString());
            map.put("status", false);
            map.put("msg", "兑换码已使用");
        }
        return map;
    }

    /**
     * 根据兑换码获取优惠券
     *
     * @param exchangeCouponVo
     * @return
     */
    @Override
    public List<UserCouponDto> getRedeemCoupon(ExchangeCouponVo exchangeCouponVo) {

        EntityWrapper<CouponRedeemCode> couponRedeemCodeEntityWrapper = new EntityWrapper<>();
        couponRedeemCodeEntityWrapper.eq("REDEEM_CODE", exchangeCouponVo.getRedeemCode());
        List<CouponRedeemCode> couponRedeemCodes = couponRedeemCodeMapper.selectList(couponRedeemCodeEntityWrapper);
        CouponRedeemCode couponRedeemCode = new CouponRedeemCode();
        if (couponRedeemCodes.isEmpty()) {
            log.info("兑换码无效");
            return null;
        } else {
            couponRedeemCode = couponRedeemCodes.get(0);
        }

        EntityWrapper<CouponRedeemCodeTemplateid> couponRedeemCodeTemplateidEntityWrapper = new EntityWrapper<>();
        couponRedeemCodeTemplateidEntityWrapper.eq("BATCH_CODE", couponRedeemCode != null ? couponRedeemCode.getBatchCode() : null);
        List<CouponRedeemCodeTemplateid> templateids = couponRedeemCodeTemplateidMapper.selectList(couponRedeemCodeTemplateidEntityWrapper);


        List<UserCouponDto> couponDetailsInfos = new ArrayList<>();
        for (CouponRedeemCodeTemplateid templateid : templateids) {
            List<UserCouponDto> list = userFeignClient.myCouponInfoByCouponId(templateid.getCouponTemplateid()).getData();
            couponDetailsInfos.add(!list.isEmpty() ? list.get(0) : null);
        }
        return couponDetailsInfos;
    }

    public void setResponseHeader(HttpServletResponse response, String fileName) {
        try {
            try {
                fileName = new String(fileName.getBytes(), "ISO8859-1");
            } catch (UnsupportedEncodingException e) {
                log.error("-->setResponseHeader发生异常, fileName: " + fileName,
                        e);
            }
            response.setContentType("application/octet-stream;charset=ISO8859-1");
            response.setHeader("Content-Disposition", "attachment;filename="
                    + fileName);
            response.addHeader("Pargam", "no-cache");
            response.addHeader("Cache-Control", "no-cache");
        } catch (Exception e) {
            log.error("-->setResponseHeader发生异常", e);
        }
    }

    public static String[] chars = new String[]{"a", "b", "c", "d", "e", "f",
            "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s",
            "t", "u", "v", "w", "x", "y", "z", "0", "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "H", "I",
            "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
            "W", "X", "Y", "Z"};

    public static String getUUID() {
        //调用Java提供的生成随机字符串的对象：32位，十六进制，中间包含-
        String uuid = UUID.randomUUID().toString().replace("-", "");
        StringBuffer shortBuffer = new StringBuffer();
        for (int i = 0; i < 8; i++) { //分为8组
            String str = uuid.substring(i * 4, i * 4 + 4); //每组4位
            int x = Integer.parseInt(str, 16); //输出str在16进制下的表示
            shortBuffer.append(chars[x % 0x3E]); //用该16进制数取模62（十六进制表示为314（14即E）），结果作为索引取出字符
        }
        return shortBuffer.toString();//生成8位字符
    }
}
