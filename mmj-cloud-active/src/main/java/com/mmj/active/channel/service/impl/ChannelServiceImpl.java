package com.mmj.active.channel.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.mmj.active.channel.mapper.ChannelMapper;
import com.mmj.active.channel.model.Channel;
import com.mmj.active.channel.model.ChannelEx;
import com.mmj.active.channel.model.dto.ChannelDayDto;
import com.mmj.active.channel.model.vo.ChannelVo;
import com.mmj.active.channel.service.ChannelService;
import com.mmj.active.threeSaleTenner.service.impl.ThreeSaleFissionServiceImpl;
import jodd.util.StringUtil;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 分销渠道统计表 服务实现类
 * </p>
 *
 * @author dashu
 * @since 2019-08-05
 */
@Service
public class ChannelServiceImpl extends ServiceImpl<ChannelMapper, Channel> implements ChannelService {
    Logger logger = LoggerFactory.getLogger(ThreeSaleFissionServiceImpl.class);
    @Autowired
    private ChannelMapper channelMapper;

    @Override
    public Page<ChannelVo> query(ChannelEx channelEx) {

            String endTime = channelEx.getStartTime();
            String startTime = channelEx.getEndTime();
            SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
            Calendar calendar = Calendar.getInstance();
            //说明没有根据活动时间筛选查询
            if(endTime == null){
                Date date = new Date();//获取当前时间  
                calendar.setTime(date);
                calendar.add(Calendar.DATE, -14);
                //设置开始时间 
                endTime = sd.format(calendar.getTime());
            }
            if(startTime == null ){
                //设置当前时间为结束时间
                startTime =sd.format(new Date());
            }


        long start = 0;
        Date end = null;
        try {
            start = sd.parse(endTime).getTime();
            end  =  sd.parse(startTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        int arrSize =  ((int) (end.getTime() - start) /1000/60/60/24)+1;

        String [] days = new String[arrSize];

        days[days.length-1] = endTime ;

        //将字符串endTime转换为日期类型
        calendar.setTime(end);
        //将endtime往前推一天
        calendar.add(Calendar.DATE, -1);
        for (int i = days.length-2; i > 0; i--) {
             days[days.length-2-i+1] =  sd.format(calendar.getTime()) ;
             calendar.add(Calendar.DATE, -1);
        }
        days[0] = startTime ;

        //查询分销渠道
        EntityWrapper<Channel> entityWrapper = new EntityWrapper<>();
        if(StringUtil.isNotEmpty(channelEx.getChannelName())){
            entityWrapper.like("CHANNEL_NAME",channelEx.getChannelName());
        }
        entityWrapper.groupBy("CHANNEL_NAME");
        entityWrapper.orderBy("CREATE_TIME DESC");
        Page<ChannelVo> page = new Page<>(channelEx.getCurrentPage(),channelEx.getPageSize());
        List<Channel> channels = channelMapper.selectPage(page, entityWrapper);
        List<ChannelVo> list = new ArrayList<>();
        if(!CollectionUtils.isEmpty(channels)){
            for (Channel channel : channels) {
                ChannelVo channelVo = JSON.parseObject(JSON.toJSONString(channel), ChannelVo.class);
                String channelName = channel.getChannelName();

                //查询每天的分销人数和分销次数
                List<ChannelDayDto> channelDayDtoList = channelMapper.selectChannelBy15(channelName,startTime,endTime);
                List<ChannelDayDto> resultList = new ArrayList<>();

                //查询分销次数
                Integer scanSumCount = channelMapper.selectScanSumCount(channelName);

                //查询分销总人数
                Integer personSumCount =  channelMapper.selectpersonSumCount(channelName);
                channelVo.setScanSumCount(scanSumCount);
                channelVo.setPersonSumCount(personSumCount);

                //查询固定时间段总次数
                Integer scanDaySumCount = channelMapper.selectScanDaySumCount(channelName,startTime,endTime);
                //查询固定时间段总人数
                Integer personDaySumCount =  channelMapper.selectPersonDaySumCount(channelName,startTime,endTime);
                channelVo.setPersonDaySumCount(personDaySumCount);
                channelVo.setScanDaySumCount(scanDaySumCount);

                if(CollectionUtils.isEmpty(channelDayDtoList)){
                    for (String day : days) {
                        ChannelDayDto channelDayDto = new ChannelDayDto();
                        channelDayDto.setScanDayCount(0);
                        channelDayDto.setPersonDayCount(0);
                        channelDayDto.setDate(day);
                        resultList.add(channelDayDto);

                    }
                }else if(channelDayDtoList.size() != days.length){
                    boolean flag = false;
                    for (int i = 0; i < days.length; i++) {
                        for (ChannelDayDto channelDayDto : channelDayDtoList) {
                            if(days[i].equals(channelDayDto.getDate())){
                                flag = true;
                            }
                        }
                        if(!flag){
                            ChannelDayDto channelDayDto = new ChannelDayDto();
                            channelDayDto.setScanDayCount(0);
                            channelDayDto.setPersonDayCount(0);
                            channelDayDto.setDate(days[i]);
                            resultList.add(i,channelDayDto);

                        }else{
                            resultList.add(i, channelDayDtoList.remove(0));
                        }
                        flag = false;
                    }
                }else{
                    resultList =channelDayDtoList ;
                }
                channelVo.setDayList(resultList);
                list.add(channelVo);
            }
        }
        page.setRecords(list);
        return page;
    }

    @Override
    public Object exportChannel(ChannelEx channelEx, HttpServletRequest request, HttpServletResponse response) {
        List<ChannelVo> list = selectExportChannel(channelEx);
        String fileName = "分销渠道统计表.xls";
        String sheetName = "分销渠道统计表";
        List<String> titles = new ArrayList<>();
        titles.add("渠道名");
        titles.add("渠道链接");
        titles.add("效果统计");
        titles.add("总计");
        titles.add("合计");
        //获取日期
        if(list != null && list.size() > 0){
            List<ChannelDayDto> dayList = list.get(0).getDayList();
            for (ChannelDayDto channelDay : dayList) {
                titles.add(channelDay.getDate());
            }
        }
        //第一步:创建excel的文档对象
        HSSFWorkbook wb = new HSSFWorkbook();

        // 第二步，在workbook中添加一个sheet,对应Excel文件中的sheet
        HSSFSheet sheet = wb.createSheet(sheetName);

        // 第三步，在sheet中添加表头第0行,注意老版本poi对Excel的行数列数有限制
        HSSFRow row = sheet.createRow(0);

        // 第四步，创建单元格，并设置值表头 设置表头居中
        HSSFCellStyle style = wb.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER); // 创建一个居中格式

        HSSFCellStyle cellStyle = wb.createCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.CENTER); // 创建一个居中格式
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);//垂直居中
        //声明列对象
        HSSFCell cell = null;

        //创建标题
        for(int i=0;i< titles.size();i++){
            cell = row.createCell(i);
            cell.setCellValue(titles.get(i));
            cell.setCellStyle(style);
        }

        //创建内容
        for(int i=0;i<list.size();i++){
            row = sheet.createRow(i+1+i);

            HSSFCell cell1 = row.createCell(0);
            cell1.setCellValue(list.get(i).getChannelName());

            HSSFCell cell2 = row.createCell(1);
            cell2.setCellValue(list.get(i).getChannelLink());



            row.createCell(2).setCellValue("扫码人次");
            if(list.get(i).getScanSumCount() != null){
                row.createCell(3).setCellValue(list.get(i).getScanSumCount());
            }else{
                row.createCell(3).setCellValue(0);
            }

            if(list.get(i).getScanDaySumCount() != null){
                row.createCell(4).setCellValue(list.get(i).getScanDaySumCount());
            }else{
                row.createCell(4).setCellValue(0);
            }


            List<ChannelDayDto> dayList = list.get(i).getDayList();
            int count1 = 5;


            HSSFRow	row2 = sheet.createRow(i +i+ 2);

            row2.createCell(0);

            row2.createCell(1);

            row2.createCell(2).setCellValue("扫码生成分销码人数");


            if(list.get(i).getPersonSumCount() != null ){
                row2.createCell(3).setCellValue(list.get(i).getPersonSumCount());
            }else{
                row2.createCell(3).setCellValue(0);
            }

            if(list.get(i).getPersonDaySumCount() != null ){
                row2.createCell(4).setCellValue(list.get(i).getPersonDaySumCount());
            }else{
                row2.createCell(4).setCellValue(0);
            }



            for (ChannelDayDto channelDay : dayList) {
                row2.createCell(count1).setCellValue(channelDay.getPersonDayCount());
                row.createCell(count1++).setCellValue(channelDay.getScanDayCount());

            }

            sheet.addMergedRegion(new CellRangeAddress(i+i+1, i+i+2, 0, 0));// 下标从0开始 起始行号，终止行号， 起始列号，终止列号
            sheet.addMergedRegion(new CellRangeAddress(i+i+1, i+i+2, 1, 1));// 下标从0开始 起始行号，终止行号， 起始列号，终止列号

            cell1.setCellStyle(cellStyle);
            cell2.setCellStyle(cellStyle);
        }

        // 响应到客户端
        try {
            this.setResponseHeader(request,response, fileName);
            OutputStream os = response.getOutputStream();
            wb.write(os);
            os.flush();
            os.close();
        } catch (Exception e) {
            logger.error("-->exportAddedGoods发生异常", e);
        }
        return null;
    }

    // 发送响应流方法
    public void setResponseHeader(HttpServletRequest request, HttpServletResponse response, String fileName) {
        try {
            if (request.getHeader("user-agent").toLowerCase().indexOf("firefox") > -1) {
                //火狐浏览器自己会对URL进行一次URL转码所以区别处理
                response.setHeader("Content-Disposition", "attachment;filename=" + new String(fileName.getBytes("utf-8"), "ISO-8859-1"));
            }else{
               response.setHeader("Content-Disposition","attachment;filename=" + URLEncoder.encode(fileName,"utf-8"));
            }
            response.setContentType("application/octet-stream;charset=ISO8859-1");
            response.addHeader("Pargam", "no-cache");
            response.addHeader("Cache-Control", "no-cache");
        } catch (Exception e) {
            logger.error("-->setResponseHeader发生异常", e);
        }
    }


    public List<ChannelVo> selectExportChannel(ChannelEx channelEx) {
        String endTime = channelEx.getStartTime();
        String startTime = channelEx.getEndTime();
        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        //说明没有根据活动时间筛选查询
        if(endTime == null){
            Date date = new Date();//获取当前时间  
            calendar.setTime(date);
            calendar.add(Calendar.DATE, -14);
            //设置开始时间 
            endTime = sd.format(calendar.getTime());
        }
        if(startTime == null ){
            //设置当前时间为结束时间
            startTime =sd.format(new Date());
        }


        long start = 0;
        Date end = null;
        try {
            start = sd.parse(endTime).getTime();

            end = sd.parse(startTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        int arrSize =  ((int) (end.getTime() - start) /1000/60/60/24)+1;

        String [] days = new String[arrSize];

        days[days.length-1] = endTime ;

        //将字符串endTime转换为日期类型
        calendar.setTime(end);
        //将endtime往前推一天
        calendar.add(Calendar.DATE, -1);
        for (int i = days.length-2; i > 0; i--) {
            days[days.length-2-i+1] =  sd.format(calendar.getTime()) ;
            calendar.add(Calendar.DATE, -1);
        }
        days[0] = startTime ;

        //查询分销渠道
        EntityWrapper<Channel> entityWrapper = new EntityWrapper<>();
        if(StringUtil.isNotEmpty(channelEx.getChannelName())){
            entityWrapper.like("CHANNEL_NAME",channelEx.getChannelName());
        }
        entityWrapper.groupBy("CHANNEL_NAME");
        entityWrapper.orderBy("CREATE_TIME DESC");
        List<Channel> channels = channelMapper.selectList(entityWrapper);
        List<ChannelVo> list = new ArrayList<>();
        if(!CollectionUtils.isEmpty(channels)){
            for (Channel channel : channels) {
                ChannelVo channelVo = JSON.parseObject(JSON.toJSONString(channel), ChannelVo.class);
                String channelName = channel.getChannelName();

                //查询每天的分销人数和分销次数
                List<ChannelDayDto> channelDayDtoList = channelMapper.selectChannelBy15(channelName,startTime,endTime);
                List<ChannelDayDto> resultList = new ArrayList<>();

                //查询分销次数
                Integer scanSumCount = channelMapper.selectScanSumCount(channelName);

                //查询分销总人数
                Integer personSumCount =  channelMapper.selectpersonSumCount(channelName);
                channelVo.setScanSumCount(scanSumCount);
                channelVo.setPersonSumCount(personSumCount);

                //查询固定时间段总次数
                Integer scanDaySumCount = channelMapper.selectScanDaySumCount(channelName,startTime,endTime);
                //查询固定时间段总人数
                Integer personDaySumCount =  channelMapper.selectPersonDaySumCount(channelName,startTime,endTime);
                channelVo.setPersonDaySumCount(personDaySumCount);
                channelVo.setScanDaySumCount(scanDaySumCount);

                if(CollectionUtils.isEmpty(channelDayDtoList)){
                    for (String day : days) {
                        ChannelDayDto channelDayDto = new ChannelDayDto();
                        channelDayDto.setScanDayCount(0);
                        channelDayDto.setPersonDayCount(0);
                        channelDayDto.setDate(day);
                        resultList.add(channelDayDto);

                    }
                }else if(channelDayDtoList.size() != days.length){
                    boolean flag = false;
                    for (int i = 0; i < days.length; i++) {
                        for (ChannelDayDto channelDayDto : channelDayDtoList) {
                            if(days[i].equals(channelDayDto.getDate())){
                                flag = true;
                            }
                        }
                        if(!flag){
                            ChannelDayDto channelDayDto = new ChannelDayDto();
                            channelDayDto.setScanDayCount(0);
                            channelDayDto.setPersonDayCount(0);
                            channelDayDto.setDate(days[i]);
                            resultList.add(i,channelDayDto);

                        }else{
                            resultList.add(i, channelDayDtoList.remove(0));
                        }
                        flag = false;
                    }
                }else{
                    resultList =channelDayDtoList ;
                }
                channelVo.setDayList(resultList);
                list.add(channelVo);
            }
        }
        return list;
    }
}
