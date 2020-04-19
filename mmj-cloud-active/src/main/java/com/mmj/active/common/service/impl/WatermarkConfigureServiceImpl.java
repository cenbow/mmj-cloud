package com.mmj.active.common.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.mmj.active.common.feigin.NoticeFeignClient;
import com.mmj.active.common.mapper.WatermarkConfigureMapper;
import com.mmj.active.common.model.ImgModel;
import com.mmj.active.common.model.WatermarkConfigure;
import com.mmj.active.common.service.WatermarkConfigureService;
import com.mmj.common.model.ReturnData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author shenfuding
 * @since 2019-07-23
 */
@Service
public class WatermarkConfigureServiceImpl extends ServiceImpl<WatermarkConfigureMapper, WatermarkConfigure> implements WatermarkConfigureService {

    @Autowired
    private WatermarkConfigureMapper watermarkConfigureMapper;

    @Autowired
    private NoticeFeignClient noticeFeignClient;

    /**
     * 绘图
     * @param params
     * @return
     */
    @Override
    public String createMark(String params){
        JSONObject jsonObject = JSONObject.parseObject(params.toString());
        String url = jsonObject.getString("url");//分享图url
        Long classify = Long.valueOf(jsonObject.getString("classify"));//水印类型：1、商品分享图，2、抽奖分享图
        String title = jsonObject.getString("title");//商品标题
        String drawprice = jsonObject.getString("drawprice");//抽奖价
        String originalprice = jsonObject.getString("originalprice");//原价

        ImgModel imgModel = new ImgModel();
        Calendar cal = Calendar.getInstance();
        Date date = cal.getTime();
        imgModel.setBusinessId(date.getTime()+"");
        List<ImgModel.View> viewList = new ArrayList<ImgModel.View>();

        if(classify==1){
            //底图
            ImgModel.View view = new ImgModel.View();
            view.setType("image");
            view.setUrl(url);
            view.setLeft(0);
            view.setTop(0);
            view.setHeight(600);
            view.setWidth(750);
            viewList.add(view);

            //水印图
            EntityWrapper<WatermarkConfigure> entityWrapper = new EntityWrapper<>();
            entityWrapper.eq("CLASSIFY",classify);
            List<WatermarkConfigure> list = watermarkConfigureMapper.selectList(entityWrapper);
            for(WatermarkConfigure swc : list){
                ImgModel.View viewTwo = new ImgModel.View();
                viewTwo.setType(swc.getType());
                viewTwo.setUrl(swc.getUrl());
                viewTwo.setLeft(swc.getLefts());
                viewTwo.setTop(swc.getTop());
                viewTwo.setHeight(swc.getHeight());
                viewTwo.setWidth(swc.getWidth());
                viewList.add(viewTwo);
            }
            imgModel.setViews(viewList);
        }

        if(classify == 2){
            ImgModel.View view = new ImgModel.View();
            EntityWrapper<WatermarkConfigure> entityWrapper = new EntityWrapper<>();
            entityWrapper.eq("CLASSIFY",classify);
            List<WatermarkConfigure> list = watermarkConfigureMapper.selectList(entityWrapper);
            for(WatermarkConfigure swc : list){
                if("rect".equals(swc.getType())){//底图
                    ImgModel.View viewRect = new ImgModel.View();
                    viewRect.setType(!"".equals(swc.getType())?swc.getType():null);
                    viewRect.setBackground(swc.getBackground());
                    viewRect.setTop(swc.getTop());
                    viewRect.setLeft(swc.getLefts());
                    viewRect.setWidth(swc.getWidth());
                    viewRect.setHeight(swc.getHeight());
                    viewList.add(viewRect);
                }
                if("text".equals(swc.getType())){//文本
                    ImgModel.View viewText = new ImgModel.View();
                    viewText.setType(swc.getType());
                    viewText.setTop(swc.getTop());
                    viewText.setLeft(swc.getLefts());
                    //拼接参数动态参数
                    if("title".equals(swc.getParameterType())){//标题
                        viewText.setContent(title);
                    }else if("drawprice".equals(swc.getParameterType())){//抽奖价
                        viewText.setContent(drawprice);
                    }else if("originalprice".equals(swc.getParameterType())){//原价
                        viewText.setContent("原价：¥"+originalprice);
                    }else{
                        viewText.setContent(swc.getContent());
                    }
                    viewText.setFontSize(swc.getFontSize());
                    viewText.setColor(swc.getColor());
                    viewText.setTextAlign(swc.getTextAlign());
                    viewText.setLineHeight(swc.getLineHeight());//行距

                    if(null!=swc.getBreakWord())viewText.setBreakWord(swc.getBreakWord());
                    if(null!=swc.getMaxLineNumber())viewText.setMaxLineNumber(swc.getMaxLineNumber());
                    if(null!=swc.getTextWidth())viewText.setTextWidth(swc.getTextWidth());
                    if(!"".equals(swc.getTextDecoration()))viewText.setTextDecoration(swc.getTextDecoration());
                    viewList.add(viewText);
                }
                if("image".equals(swc.getType())){//图片
                    ImgModel.View viewImage = new ImgModel.View();
                    viewImage.setType(swc.getType());
                    viewImage.setUrl(swc.getUrl());
                    viewImage.setTop(swc.getTop());
                    viewImage.setLeft(swc.getLefts());
                    viewImage.setWidth(swc.getWidth());
                    viewImage.setHeight(swc.getHeight());
                    viewList.add(viewImage);
                }

            }

            //绘制商品图
            ImgModel.View viewImageTwo = new ImgModel.View();
            viewImageTwo.setType("image");
            viewImageTwo.setUrl(url);
            viewImageTwo.setTop(39);
            viewImageTwo.setLeft(332);
            viewImageTwo.setWidth(380);
            viewImageTwo.setHeight(380);
            viewList.add(viewImageTwo);
            imgModel.setViews(viewList);
        }

        //调用绘图接口

        ReturnData<String> returnData = noticeFeignClient.createImage(params);

        if(returnData.getCode()== 1){
            return returnData.getData();
        } else {
            return "图片生成失败";
        }
    }
}
