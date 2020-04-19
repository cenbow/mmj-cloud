package com.mmj.active.advertisement.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.mmj.active.advertisement.mapper.AdvertisementManageMapper;
import com.mmj.active.advertisement.model.AdvertisementManage;
import com.mmj.active.advertisement.model.AdvertisementManageVo;
import com.mmj.active.advertisement.service.AdvertisementManageService;
import com.mmj.active.common.feigin.OrderFeignClient;
import com.mmj.active.common.feigin.UserFeignClient;
import com.mmj.active.common.model.UserMember;
import com.mmj.common.model.JwtUserDetails;
import com.mmj.common.utils.SecurityUserUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author zhangyicao
 * @since 2019-07-24
 */
@Service
public class AdvertisementManageServiceImpl extends ServiceImpl<AdvertisementManageMapper, AdvertisementManage> implements AdvertisementManageService {

    @Autowired
    private AdvertisementManageMapper advertisementManageMapper;

    @Autowired
    private OrderFeignClient orderFeignClient;
    @Autowired
    private UserFeignClient userFeignClient;

    private String news = "new";
    private String old = "old";
    private String member = "member";

    /**
     * 查询广告位列表
     * @return
     */
    @Override
    public List<AdvertisementManage> queryList(){
        return advertisementManageMapper.selectList(null);
    }

    /**
     * 查询广告信息
     * @param pageType
     * @return
     */
    @Override
    public AdvertisementManage queryAdvertisement(String pageType){

        JwtUserDetails userDetails = SecurityUserUtil.getUserDetails();
        Map<String,Object> map = new HashMap<>();
        map.put("userId",userDetails.getUserId());
        boolean checkUser = orderFeignClient.checkNewUser(map).getData();//false:否（新用户），true:是(老用户)

        boolean chenkMember = false;
        UserMember userMember = userFeignClient.queryUserMemberInfoByUserId(userDetails.getUserId()).getData();
        if(userMember!=null && userMember.getActive()){
            chenkMember = true;
        }

        AdvertisementManage advertisementManage = new AdvertisementManage();
        advertisementManage.setPageType(pageType);

        EntityWrapper<AdvertisementManage> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("PAGE_TYPE",pageType);
        List<AdvertisementManage> advertisementManages = advertisementManageMapper.selectList(entityWrapper);
        if(!advertisementManages.isEmpty()){
            advertisementManage = advertisementManages .get(0);
            if(!"".equals(advertisementManage.getIsNewUser()) && advertisementManage.getIsNewUser().indexOf(news)!=-1 && !checkUser){//新用户
                return advertisementManage;
            }
            if(!"".equals(advertisementManage.getIsNewUser())  && advertisementManage.getIsNewUser().indexOf(old)!=-1 && checkUser && !chenkMember){//老用户
                return advertisementManage;
            }
            if(!"".equals(advertisementManage.getIsNewUser())  && advertisementManage.getIsNewUser().indexOf(member)!=-1 && chenkMember){//会员
                return advertisementManage;
            }
        }
        return null;
    }


    /**
     * 保存广告位
     */
    @Override
    public String save(AdvertisementManageVo manageVo){
        try {
            advertisementManageMapper.deleteAll();//清除原有数据
            for (AdvertisementManage manage : manageVo.getList()) {
                AdvertisementManage advertisementManage = new AdvertisementManage();
                BeanUtils.copyProperties(manage,advertisementManage);
                advertisementManage.setCreateTime(new Date());
                advertisementManageMapper.insert(advertisementManage);
            }
            return "保存成功";
        }catch (Exception e){
            return "保存失败";
        }
    }

}
