package com.mmj.notice.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mmj.common.exception.WxException;
import com.mmj.common.model.JwtUserDetails;
import com.mmj.common.model.ReturnData;
import com.mmj.common.model.WxConfig;
import com.mmj.common.utils.HttpTools;
import com.mmj.common.utils.MD5Util;
import com.mmj.common.utils.SecurityUserUtil;
import com.mmj.common.utils.StringUtils;
import com.mmj.notice.common.constants.CosBucketApi;
import com.mmj.notice.common.utils.WxTokenUtils;
import com.mmj.notice.feigin.WxMessageFeignClient;
import com.mmj.notice.model.WxConstants;
import com.mmj.notice.model.WxFile;
import com.mmj.notice.service.WxFileService;
import com.mmj.notice.service.WxImageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.font.TextAttribute;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.text.AttributedString;
import java.util.concurrent.TimeUnit;

/**
 * 图片合成处理
 */
@Service
@Slf4j
public class WxImageServiceImpl implements WxImageService {

    @Autowired
    WxFileService wxFileService;

    @Autowired
    HttpTools httpTools;

    @Autowired
    WxMessageFeignClient wxMessageFeignClient;

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    private Font dynamicFont = null; //字体

    @Autowired
    WxTokenUtils wxTokenUtils;

    /**
     * 绘图
     *
     * @param params
     * @return
     */
    @Override
    public String createImage(JSONObject params) {
        String businessId = MD5Util.MD5Encode(params.toJSONString(), "utf-8");
        WxFile wxFile = wxFileService.queryByBusinessId(businessId);
        if (null != wxFile) { //如果之前存在 那么就直接返回
            return wxFile.getPath();
        }
        int width = params.getInteger("width");
        int height = params.getInteger("height");
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_BGR); //创建画布
        Graphics2D g = bufferedImage.createGraphics(); //创建画笔
        JSONArray views = params.getJSONArray("views"); //得到要绘制的对象
        views.forEach(view -> { //开始绘制各个组件
            JSONObject viewjson = (JSONObject) view;
            String type = viewjson.getString("type"); //组件类型
            switch (type) {
                case "rect": //画长方形
                    gRect(g, viewjson);
                    break;
                case "image": //画图片
                    gImage(g, viewjson);
                    break;
                case "text": //画文字
                    gText(g, viewjson, bufferedImage);
                    break;
            }
        });
        g.dispose();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        String path = "";
        try {
            ImageIO.write(bufferedImage, "jpg", out);
            byte[] bytes = out.toByteArray();
            ByteArrayInputStream inStream = new ByteArrayInputStream(bytes);
            String cloudPath = CosBucketApi.uploadFile("dxs-mmj-1257049906", "/wx/" + StringUtils.getUUid() + ".jpg", inStream, bytes.length);
            wxFile = new WxFile();
            wxFile.setBusinessId(businessId);
            path = "https://" + cloudPath;
            wxFile.setPath(path);
            wxFileService.create(wxFile);
        } catch (Exception e) {
            log.error("画图生成图片错误" , new Throwable(e));
        }
        return path;
    }

    /**
     * 画长方形
     *
     * @param g
     * @param viewjson
     */
    private void gRect(Graphics2D g, JSONObject viewjson) {
        Integer x = viewjson.getInteger("left");  //绘制的x坐标
        Integer y = viewjson.getInteger("top");   //绘制的y坐标
        String color0 = viewjson.getString("background").replaceAll("#", "");
        if (color0.length() == 3) { //如果前端传的颜色是三个字符 那么就补充
            color0 = color0 + color0;
        }
        g.setColor(new Color(Integer.parseInt(color0.substring(0, 2), 16), Integer.parseInt(color0.substring(2, 4), 16),
                Integer.parseInt(color0.substring(4, 6), 16))); //将16进制的补充成rgb格式
        g.fill3DRect(x, y, viewjson.getInteger("width"), viewjson.getInteger("height"), true);
    }

    /**
     * 画图片
     *
     * @param g
     * @param viewjson
     */
    private void gImage(Graphics2D g, JSONObject viewjson) {
        Integer x = viewjson.getInteger("left");  //绘制的x坐标
        Integer y = viewjson.getInteger("top");   //绘制的y坐标
        try {
            URL url = new URL(viewjson.getString("url"));
            InputStream inputStream = url.openStream();
            BufferedImage small = ImageIO.read(inputStream);
            g.drawImage(small, x, y, viewjson.getInteger("width"), viewjson.getInteger("height"), null);
            if (null != inputStream) {
                inputStream.close();
            }
        } catch (Exception e) {
            log.error("绘图图片" + viewjson.getString("url") + "错误");
            throw new WxException("绘图图片" + viewjson.getString("url") + "错误");
        }
    }

    /**
     * 画文字
     *
     * @param g
     * @param viewjson
     * @param bufferedImage
     */
    private void gText(Graphics2D g, JSONObject viewjson, BufferedImage bufferedImage) {
        Integer x = viewjson.getInteger("left");  //绘制的x坐标
        Integer y = viewjson.getInteger("top");   //绘制的y坐标
        String text = viewjson.getString("content");  //绘制的内容
        String textAlign = viewjson.getString("textAlign"); //对齐方式
        Boolean align = viewjson.getBoolean("align"); //是否是真正的居中对齐
        Boolean bolder = viewjson.getBoolean("bolder"); //是否粗体
        Integer fontSize = viewjson.getInteger("fontSize"); //字体大小
        String textDecoration = viewjson.getString("textDecoration");//中划线/下划线
        Font font = getFont(fontSize, bolder); //加载字体
        FontMetrics metrics = g.getFontMetrics(font); //设置字体 用户计算坐标

        String color = viewjson.getString("color").replaceAll("#", "");
        if (color.length() == 3) { //如果是三个字符 那么就补充成六个字符
            color = color + color;
        }
        int rc = Integer.parseInt(color.substring(0, 2), 16); //r颜色
        int gc = Integer.parseInt(color.substring(2, 4), 16); //g颜色
        int bc = Integer.parseInt(color.substring(4, 6), 16); //b颜色
        g.setColor(new Color(rc, gc, bc)); //设置画笔颜色
        g.setBackground(new Color(rc, gc, bc)); //设置画笔的背景颜色
        AttributedString as = null;
        if (!StringUtils.isEmpty(textDecoration)) { //字体是否有划线
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            as = new AttributedString(text);
            as.addAttribute(TextAttribute.FONT, font);
            as.addAttribute(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON, 0, text.length()); //中划线 目前位置全部是中划线
            metrics = g.getFontMetrics(font);
        }
        g.setFont(font); //设置画笔字体
        if ("center".equals(textAlign)) { //居中对齐
            //居中对齐有两种 一种是假的居中对齐 一种是真的居中对齐
            if(null != align && align){ //真的居中对齐
                x = (bufferedImage.getWidth() - metrics.stringWidth(text)) / 2;
                y = y + metrics.getHeight() / 2;
            }else { //假的居中对齐
                x = x  - metrics.stringWidth(text) / 2;
                y = y+ metrics.getHeight() / 2;
            }

        } else{ //左对齐 左对齐的考虑一行显示不了的处理
            boolean breakWord = Boolean.parseBoolean(viewjson.getString("breakWord"));
            if (breakWord) { //这种情况做了一行显示不了的处理 采用换行画的方式
                gLeftTextWithBreakWord(g, viewjson, metrics);
                return;
            } else { //否则就直接绘图左对齐
                x = viewjson.getInteger("left");
                y = y + metrics.getHeight();
            }
        }
        if(null == as){
            g.drawString(text, x, y);
        }else {
            g.drawString(as.getIterator(), x, y);
        }
    }


    /**
     * 换行画换行文字
     *
     * @param g        画笔
     * @param viewjson 画的对象
     * @param metrics  画笔的材质
     */
    private void gLeftTextWithBreakWord(Graphics2D g, JSONObject viewjson, FontMetrics metrics) {
        int fontSize = Integer.valueOf(viewjson.getString("fontSize"));//文本字体
        int srcImgWidth = Integer.valueOf(viewjson.getString("textWidth"));//文本宽度
        long maxLineNumber = Long.valueOf(viewjson.getString("maxLineNumber"));//最大行数
        int lineHeight = Integer.valueOf(viewjson.getString("lineHeight"));//行距
        String text = viewjson.getString("content");  //绘制的内容
        int tempCharLen = 0;//单字符长度
        int tempLineLen = 0;//单行字符总长度临时计算
        Integer top = viewjson.getInteger("top");
        int x = viewjson.getInteger("left");
        int y = top + metrics.getHeight() / 2;
        StringBuffer sb = new StringBuffer();
        int row = 0;
        for (int i = 0; i < text.length(); i++) {
            char tempChar = text.charAt(i);
            g.getFontMetrics(g.getFont()).charWidth(tempChar);
            tempCharLen = g.getFontMetrics(g.getFont()).charWidth(tempChar); //单个字符的长度
            if (tempLineLen >= srcImgWidth) {
                row++;
                //长度已经满一行,进行文字叠加
                if (row > 1) y += lineHeight;//绘图前加行高
                g.drawString(sb.toString(), x, y);
                sb.delete(0, sb.length());//清空内容,重新追加
                y += fontSize;
                tempLineLen = 0;
            } else {
                if (row == (maxLineNumber - 1) && tempLineLen >= srcImgWidth) {
                    sb.append(tempChar + "...");//追加字符
                    if (row > 1) y += lineHeight;//绘图前加行高
                    g.drawString(sb.toString(), x, y);
                    break;
                } else if (i == text.length() - 1) {//判断是否最后一行
                    if (row > 1) y += lineHeight;//绘图前加行高
                    sb.append(tempChar);
                    g.drawString(sb.toString(), x, y);
                }
            }
            tempLineLen += tempCharLen;
            sb.append(tempChar);//追加字符
        }
    }


    /**
     * 加载字体
     *
     * @param fontSize 字体大小
     * @param bolder   是否粗体
     * @return
     */
    private Font getFont(int fontSize, Boolean bolder) {
        String path = "PINGFANG_REGULAR.TTF";
        if (null == dynamicFont) {
            try {
                InputStream in = this.getClass().getClassLoader().getResourceAsStream(path);
                Font font = Font.createFont(Font.TRUETYPE_FONT, in);
                dynamicFont = font;
                in.close();
            } catch (Exception e) {
                log.error("加载字体文件出错了" , new Throwable(e));
            }
        }
        return null == bolder || bolder ? dynamicFont.deriveFont(Font.BOLD, fontSize) : dynamicFont.deriveFont(fontSize);
    }

    /**
     * 创建小程序码
     *
     * @param params
     * @return
     */
    @Override
    public String createQrcodeM(JSONObject params) {
        String appid = params.getString("appid"); //小程序appid
        String path = params.getString("path"); //小程序路径
        String width = params.getString("width"); //小程序码大小
        String token = redisTemplate.opsForValue().get("access_token_" + appid);
        params = new JSONObject();
        params.put("path", path);
        params.put("width", width);
        InputStream inputStream = httpTools.doPostInputStream(WxConstants.URL_QRCODE_MIN + "?access_token=" + token, params);
        try {
            byte[] data = new byte[1024];
            int len;
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            while ((len = inputStream.read(data)) > 0) {
                out.write(data, 0, len);
            }
            byte[] bytes = out.toByteArray();
            String result = new String(bytes);
            if(result.contains(WxConstants.CODE_INVALID_TOKEN)){ //token失效
                ReturnData<WxConfig> wxConfigReturnData = wxMessageFeignClient.queryByAppId(appid);
                if (null != wxConfigReturnData && null != wxConfigReturnData.getData()) {
                    WxConfig wxConfig = wxConfigReturnData.getData();
                    token = wxTokenUtils.reloadToken(wxConfig.getAppId(), wxConfig.getSecret());
                    inputStream = httpTools.doPostInputStream(WxConstants.URL_QRCODE_MIN + "?access_token=" + token, params);
                    data = new byte[1024];
                    out = new ByteArrayOutputStream();
                    while ((len = inputStream.read(data)) > 0) {
                        out.write(data, 0, len);
                    }
                    bytes = out.toByteArray();
                }
            }
            InputStream in = new ByteArrayInputStream(bytes);
            String cloudPath = CosBucketApi.uploadFile("dxs-mmj-1257049906", "/wx/" + StringUtils.getUUid() + ".jpg", in, bytes.length);
            inputStream.close();
            inputStream.close();
            out.close();
            return "https://" + cloudPath;
        } catch (Exception e) {
            log.error("生成二维码出错" , new Throwable(e));
        }
        return "";
    }

    /**
     * 会员返现图合成
     *
     * @return
     */
    @Override
    public String memberFx() {
        JwtUserDetails userDetails = SecurityUserUtil.getUserDetails();
        log.info("会员返现图合成当前用户信息:" + JSONObject.toJSONString(userDetails));
        String nickName = userDetails.getUserFullName(); //用户昵称
        Long userid = userDetails.getUserId(); //用户id
        String headImg = userDetails.getImagesUrl(); //头像地址
        String appid = userDetails.getAppId(); //当前环境的appid
        ReturnData<Object> wxConfigReturnData = wxMessageFeignClient.queryMemberConfig();
        JSONObject data = JSONObject.parseObject(JSONObject.toJSONString(wxConfigReturnData.getData()));
        String mmjUsersCountExceed = data.getString("mmjUsersCountExceed"); //获取买买家用户超过的数量
        String mmjMemberWorth = data.getString("mmjMemberWorth"); // 获取会员价值多少钱
        String memberActivityHowManyDaysToEnd = data.getString("memberActivityHowManyDaysToEnd"); //获取活动的持续天数
        JSONObject params = new JSONObject();
        params.put("width", 750);
        params.put("height", 1294);
        params.put("clear", true);
        JSONArray view = new JSONArray();
        JSONObject view1 = new JSONObject();
        view1.put("type", "image");
        view1.put("url", "https://dxs-mmj-1257049906.cos.ap-guangzhou.myqcloud.com/pre-product/fx.jpg");
        view1.put("top", 0);
        view1.put("left", 0);
        view1.put("width", 750);
        view1.put("height", 1294);
        view.add(view1);
        JSONObject view2 = new JSONObject();
        view2.put("type", "text");
        view2.put("fontSize", 30);
        view2.put("content", "买买家庆祝用户超" + mmjUsersCountExceed + "万会员");
        view2.put("color", "#000");
        view2.put("bolder", true);
        view2.put("textAlign", "center");
        view2.put("align", "true");
        view2.put("top", 427);
        view2.put("left", 425);
        view.add(view2);
        JSONObject view3 = new JSONObject();
        view3.put("type", "text");
        view3.put("fontSize", 30);
        view3.put("content", "累计消费免费获得价值" + mmjMemberWorth + "元会员");
        view3.put("textAlign", "center");
        view3.put("align", "true");
        view3.put("bolder", true);
        view3.put("color", "#000");
        view3.put("top", 468);
        view3.put("left", 460);
        view.add(view3);
        JSONObject view31 = new JSONObject();
        view31.put("type", "text");
        view31.put("fontSize", 30);
        view31.put("content", memberActivityHowManyDaysToEnd);
        view31.put("bolder", true);
        view31.put("color", "#000");
        view31.put("top", 547);
        view31.put("left", 365);
        view.add(view31);
        JSONObject view4 = new JSONObject();
        JSONObject qrcodeParams = new JSONObject();
        String path = "/pages/index/main?recommendUserId="+ userid;
        qrcodeParams.put("path",path);
        qrcodeParams.put("width","155");
        qrcodeParams.put("appid",appid);
        String img = createQrcodeM(qrcodeParams);
        log.info("创建二维码信息=======" + img);
        view4.put("url", img);
        view4.put("top", 1016);
        view4.put("type", "image");
        view4.put("left", 570);
        view4.put("width", 155);
        view4.put("height", 155);
        view.add(view4);
        JSONObject view41 = new JSONObject();
        view41.put("url", "https://dxs-mmj-1257049906.cos.ap-guangzhou.myqcloud.com/pre-product/fx_t.png");
        view41.put("top", 1016);
        view41.put("type", "image");
        view41.put("left", 570);
        view41.put("width", 155);
        view41.put("height", 155);
        view.add(view41);
        JSONObject view42 = new JSONObject();
        view42.put("url", headImg);
        view42.put("top", 1036);
        view42.put("type", "image");
        view42.put("left", 30);
        view42.put("width", 100);
        view42.put("height", 100);
        view.add(view42);
        JSONObject view43 = new JSONObject();
        view43.put("url", "https://dxs-mmj-1257049906.cos.ap-guangzhou.myqcloud.com/pre-product/fx_t.png");
        view43.put("top", 1036);
        view43.put("type", "image");
        view43.put("left", 30);
        view43.put("width", 100);
        view43.put("height", 100);
        view.add(view43);
        JSONObject view5 = new JSONObject();
        view5.put("type", "text");
        view5.put("fontSize", 20);
        String desc = nickName + "邀请你加入买买家会员,长按识别小程序码,立享买多少送多少,成功加入还送";
        view5.put("content", desc.substring(0,16));
        view5.put("bolder", true);
        view5.put("color", "#000");
        view5.put("top", 1032);
        view5.put("left", 174);
        view.add(view5);
        JSONObject view52 = new JSONObject();
        view52.put("type", "text");
        view52.put("fontSize", 20);
        view52.put("content", desc.substring(16, 32));
        view52.put("bolder", true);
        view52.put("color", "#000");
        view52.put("top", 1072);
        view52.put("left", 174);
        view.add(view52);
        JSONObject view53 = new JSONObject();
        view53.put("type", "text");
        view53.put("fontSize", 20);
        view53.put("content",desc.substring(32));
        view53.put("bolder", true);
        view53.put("color", "#000");
        view53.put("top", 1112);
        view53.put("left", 174);
        view.add(view53);
        JSONObject view6 = new JSONObject();
        view6.put("url", "https://dxs-mmj-1257049906.cos.ap-guangzhou.myqcloud.com/pre-product/fx5.png");
        view6.put("top", 1118);
        view6.put("type", "image");
        view6.put("left", desc.substring(32).length()+ 370);
        view6.put("width", 60);
        view6.put("height", 35);
        view.add(view6);
        params.put("views", view);
        return  createImage(params);
    }

    /**
     * 创建公众号码
     *
     * @param parseObject
     * @return
     */
    @Override
    public String createQrcode(JSONObject parseObject) {
        String businessId = MD5Util.MD5Encode(parseObject.toJSONString(), "utf-8");
        WxFile wxFile = wxFileService.queryByBusinessId(businessId);
        if(null != wxFile){
            return wxFile.getPath();
        }
        String appid = parseObject.getString("appid");      //appid
        String sceneStr = parseObject.getString("sceneStr"); //二维码上带的参数
        JSONObject params = new JSONObject();  //请求参数
        params.put("action_name", "QR_LIMIT_STR_SCENE");
        JSONObject actionInfo = new JSONObject(); //二维码详细信息
        JSONObject sceneStrJson = new JSONObject();
        sceneStrJson.put("scene_str", sceneStr);
        actionInfo.put("scene", sceneStrJson);
        params.put("action_info", actionInfo);
        String token = redisTemplate.opsForValue().get("access_token_" + appid);
        JSONObject result = httpTools.doPost(WxConstants.URL_QRCODE_MP + "?access_token=" + token, params);
        if(StringUtils.isEmpty(result.getString("ticket")) ||  WxConstants.CODE_INVALID_TOKEN.equals(result.getString("errcode"))){ //token失效 获取最新的token再次请求
            ReturnData<WxConfig> wxConfigReturnData = wxMessageFeignClient.queryByAppId(appid);
            WxConfig wxConfig = wxConfigReturnData.getData();
            token = wxTokenUtils.reloadToken(wxConfig.getAppId(), wxConfig.getSecret());
            result = httpTools.doPost(WxConstants.URL_QRCODE_MP + "?access_token=" + token, params);
        }
        String ticket = result.getString("ticket");
        InputStream imageStream = httpTools.getImageStream(WxConstants.URL_QRCODE_SHOW_MP + "?ticket=" + ticket);
        String cloudPath = null;
        try {
            cloudPath = CosBucketApi.uploadFile("dxs-mmj-1257049906","/wx/"+ StringUtils.getUUid()+".jpg",imageStream,0);
            imageStream.close();
        } catch (Exception e) {
            log.error("公众号二维码上传腾讯云错误" , new Throwable(e));
        }
        wxFile = new WxFile();
        wxFile.setBusinessId(businessId);
        wxFile.setPath(cloudPath);
        wxFile.setType("mp_qrcode");
        wxFileService.insert(wxFile);
        return cloudPath;
    }

    /**
     * 会员商品推荐图合成
     *
     * @param id
     * @return
     */
    @Override
    public String memberRecmond(String id) {
        log.info("开始调用推荐查询" + id);
        ReturnData<Object> returnData = wxMessageFeignClient.selectByRecommendId(id);
        log.info("结束调用推荐查询" + id);
        JSONObject data = JSON.parseObject(JSONObject.toJSONString(returnData.getData()));
        String headImg = data.getString("createrHead");
        String goodType = data.getString("goodType");
        String nickName = data.getString("createrName");
        String createTime = data.getString("createrTime");
        createTime = createTime.split(" ")[0];
        String content = data.getString("recommendContext");
        String goodsImage = data.getString("goodImage");
        String goodsName = data.getString("goodName");
        String price = data.getString("price");
        String goodsbaseid = data.getString("goodId");
        String createrId = data.getString("createrId");
        JSONObject params = new JSONObject();
        params.put("width", 750);
        params.put("height", 1335);
        params.put("clear", true);
        JSONArray view = new JSONArray();
        JSONObject view0 = new JSONObject();
        view0.put("type", "image");
        view0.put("url", "https://dxs-mmj-1257049906.cos.ap-guangzhou.myqcloud.com/pre-product/order_compose.jpg");
        view0.put("top", 0);
        view0.put("left", 0);
        view0.put("width", 750);
        view0.put("height", 1335);
        view.add(view0);
        JSONObject view1 = new JSONObject();
        view1.put("type", "image");
        view1.put("url", headImg);
        view1.put("top", 56);
        view1.put("left", 95);
        view1.put("width", 100);
        view1.put("height", 100);
        view.add(view1);
        JSONObject view01 = new JSONObject();
        view01.put("type", "image");
        view01.put("url", "https://dxs-mmj-1257049906.cos.ap-guangzhou.myqcloud.com/pre-product/head_circle.png");
        view01.put("top", 56);
        view01.put("left", 95);
        view01.put("width", 100);
        view01.put("height", 100);
        view.add(view01);
        JSONObject view2 = new JSONObject();
        view2.put("type", "text");
        view2.put("content", nickName);
        view2.put("fontSize", 21);
        view2.put("bolder", true);
        view2.put("top", 85);
        view2.put("color", "#ffffff");
        view2.put("left", 208);
        view.add(view2);
        JSONObject view3 = new JSONObject();
        view3.put("type", "text");
        view3.put("content", createTime);
        view3.put("fontSize", 15);
        view3.put("bolder", true);
        view3.put("top", 91);
        view3.put("left", 381);
        view3.put("color", "#ffffff");
        view.add(view3);
        int size = 42;
        int length = StringUtils.length(content);
        length = length % size ==0? (length / size ):((length / size) + 1);
        int offset = 0;
        for (int i = 0; i < length; i++) {
            try {
                JSONObject view4 = new JSONObject();
                view4.put("type", "text");
                int end = offset + size / 2;
                if(end > content.length()){
                    end = content.length();
                }else {
                    while (size > StringUtils.length(content.substring(offset, end))){ //说明这行有字母
                        end ++;
                        if(end > content.length()){
                            end = content.length();
                            break;
                        }
                    }
                }
                view4.put("content", content.substring(offset,end));
                offset = end;
                view4.put("fontSize", 24);
                view4.put("top", 168+(i*26));
                view4.put("left", 106);
                view4.put("color", "#ffffff");
                view4.put("bolder", true);
                view.add(view4);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        JSONObject view5 = new JSONObject();
        view5.put("type", "image");
        view5.put("url", goodsImage);
        view5.put("top", 486);
        view5.put("left", 100);
        view5.put("width", 550);
        view5.put("height", 470);
        view.add(view5);
        JSONObject view51 = new JSONObject();
        view51.put("type", "image");
        view51.put("url", "https://dxs-mmj-1257049906.cos.ap-guangzhou.myqcloud.com/pre-product/order_compose_t.jpg");
        view51.put("top", 487);
        view51.put("left", 553);
        view51.put("width", 99);
        view51.put("height", 69);
        view.add(view51);
        JSONObject view6 = new JSONObject();
        view6.put("type", "text");
        view6.put("content", Integer.parseInt(price) /100f+"元");
        view6.put("fontSize", 26);
        view6.put("top", 494);
        view6.put("color", "#000");
        view6.put("left", 555);
        view.add(view6);
        JSONObject view7 = new JSONObject();
        view7.put("type", "text");
        if(goodsName.length() > 18){
            view7.put("content", goodsName.substring(0,18));
            JSONObject view71 = new JSONObject();
            view71.put("type", "text");
            view71.put("fontSize", 26);
            view71.put("color", "#000");
            view71.put("content", goodsName.substring(18));
            view71.put("top", 1020);
            view71.put("bolder", true);
            view71.put("textAlign", "center");
            view71.put("align", "true");
            view.add(view71);
        }else {
            view7.put("content", goodsName);
        }
        view7.put("fontSize", 26);
        view7.put("color", "#000");
        view7.put("top", 990);
        view7.put("bolder", true);
        view7.put("textAlign", "center");
        view7.put("align", "true");
        view.add(view7);
        JSONObject view8 = new JSONObject();
        view8.put("type", "image");
        JSONObject qrcodeParams = new JSONObject();
        String path = "/pages/index/detail/main?goodsbaseid="+goodsbaseid+"&goodsbasetype="+goodType+"&recommendUserId="+createrId;
        qrcodeParams.put("path", path);
        qrcodeParams.put("width","198");
        JwtUserDetails userDetails = SecurityUserUtil.getUserDetails();
        String appId = userDetails.getAppId();
        qrcodeParams.put("appid", appId);
        String qrcodeM = createQrcodeM(qrcodeParams);
        view8.put("url", qrcodeM);
        view8.put("top", 1081);
        view8.put("left", 461);
        view8.put("width", 198);
        view8.put("height", 198);
        view.add(view8);
        params.put("views", view);
        String image = createImage(params);
        return image;
    }

    /**
     * 物流箱专属红包码生成
     *
     * @param openCode
     * @param nickName
     * @param headImg
     * @return
     */
    @Override
    public String createBoxOpenCode(String openCode, String nickName, String headImg) {
        JSONObject imgParams = new JSONObject();
        imgParams.put("width", 443);
        imgParams.put("height", 788);
        imgParams.put("clear", true);
        JSONArray views = new JSONArray();
        JSONObject view1 = new JSONObject();
        view1.put("top",0);
        view1.put("left",0);
        view1.put("width",443);
        view1.put("height",788);
        view1.put("type","image");
        view1.put("url","https://dxs-mmj-1257049906.cos.ap-guangzhou.myqcloud.com/pre-product/20190527-lbhb2.jpg");
        views.add(view1);
        JSONObject view2 = new JSONObject();
        view2.put("top",330);
        view2.put("left",443);
        view2.put("type","text");
        view2.put("textAlign","center");
        view2.put("fontSize","24");
        view2.put("color","#000");
        view2.put("align","true");
        view2.put("bolder","true");
        if(StringUtils.isNotEmpty(nickName) &&  nickName.length() > 6){
            nickName = nickName.substring(0, 5) + "**";
        }
        view2.put("content",nickName + "的专属兑换码");
        views.add(view2);
        JSONObject view3 = new JSONObject();
        view3.put("top",390);
        view3.put("left",443);
        view3.put("type","text");
        view3.put("textAlign","center");
        view3.put("fontSize","24");
        view3.put("color","#F00");
        view3.put("align","true");
        view3.put("bolder","true");
        view3.put("content",openCode);
        views.add(view3);
        JSONObject view4 = new JSONObject();
        view4.put("top", 110);
        view4.put("left", 150);
        view4.put("width", 50);
        view4.put("height", 50);
        view4.put("type", "image");
        view4.put("url", headImg);
        views.add(view4);
        JSONObject view5 = new JSONObject();
        view5.put("top", 110);
        view5.put("left", 150);
        view5.put("width", 50);
        view5.put("height", 50);
        view5.put("type", "image");
        view5.put("url", "https://dxs-mmj-1257049906.cos.ap-guangzhou.myqcloud.com/pre-product/20190530-hongri.png");
        views.add(view5);
        imgParams.put("views", views);
        return createImage(imgParams);
    }

    /**
     * 免费送图片合成
     *
     * @param url
     * @return
     */
    @Override
    public String freeGoodsCompose(String url) {
        JSONObject params = new JSONObject();
        params.put("width", 756);
        params.put("height", 609);
        params.put("clear", true);
        JSONArray view = new JSONArray();
        JSONObject view1 = new JSONObject();
        view1.put("type", "image");
        view1.put("url", "https://dxs-mmj-1257049906.cos.ap-guangzhou.myqcloud.com/pre-product/20190523001-spfxfd.png");
        view1.put("top", 0);
        view1.put("left", 0);
        view1.put("width", 756);
        view1.put("height", 609);
        view.add(view1);
        JSONObject view2 = new JSONObject();
        view2.put("type", "image");
        view2.put("url", url);
        view2.put("top", 48);
        view2.put("left", 41);
        view2.put("width", 358);
        view2.put("height", 358);
        view.add(view2);
        params.put("views", view);
        return createImage(params);
    }
}
