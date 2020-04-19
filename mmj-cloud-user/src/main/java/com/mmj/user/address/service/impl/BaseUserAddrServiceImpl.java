package com.mmj.user.address.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.mmj.common.context.BaseContextHandler;
import com.mmj.common.model.JwtUserDetails;
import com.mmj.common.model.ReturnData;
import com.mmj.common.model.UserMerge;
import com.mmj.common.properties.SecurityConstants;
import com.mmj.common.utils.SecurityUserUtil;
import com.mmj.user.address.mapper.BaseUserAddrMapper;
import com.mmj.user.address.model.BaseUserAddr;
import com.mmj.user.address.service.BaseUserAddrService;
import com.xiaoleilu.hutool.collection.CollectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 用户收货地址 服务实现类
 * </p>
 *
 * @author dashu
 * @since 2019-07-01
 */
@Service
@Slf4j
public class BaseUserAddrServiceImpl extends ServiceImpl<BaseUserAddrMapper, BaseUserAddr> implements BaseUserAddrService {
    @Autowired
    private BaseUserAddrMapper baseUserAddrMapper;

    @Override
    public ReturnData<Object> save(BaseUserAddr baseUserAddr) {
        ReturnData<Object> rd = new ReturnData<>();
        JwtUserDetails userDetails = SecurityUserUtil.getUserDetails();
        baseUserAddr.setUserId(userDetails.getUserId());
        if(null == baseUserAddr.getAddrProvince() || null == baseUserAddr.getAddrCity() || null == baseUserAddr.getAddrArea()){
            rd.setCode(SecurityConstants.FAIL_CODE);
            rd.setDesc("省市区不能为空");
            return rd;
        }
        if(baseUserAddr.getAddrId() == null){   //保存
            EntityWrapper<BaseUserAddr> entityWrapper = new EntityWrapper<>();
            entityWrapper.eq("USER_ID",userDetails.getUserId());
            entityWrapper.eq("ADDR_PROVINCE",baseUserAddr.getAddrProvince());
            entityWrapper.eq("ADDR_CITY",baseUserAddr.getAddrCity());
            entityWrapper.eq("ADDR_AREA",baseUserAddr.getAddrArea());
            entityWrapper.eq("ADDR_DETAIL",baseUserAddr.getAddrDetail());
            entityWrapper.eq("USER_MOBILE",baseUserAddr.getUserMobile());
            entityWrapper.eq("CHECK_NAME",baseUserAddr.getCheckName());
            BaseContextHandler.set(SecurityConstants.SHARDING_KEY, userDetails.getUserId());
            List<BaseUserAddr> baseUserAddrs = baseUserAddrMapper.selectList(entityWrapper);
            if(CollectionUtil.isEmpty(baseUserAddrs)){
                entityWrapper = new EntityWrapper<>();
                entityWrapper.eq("USER_ID",userDetails.getUserId());
                BaseContextHandler.set(SecurityConstants.SHARDING_KEY, userDetails.getUserId());
                List<BaseUserAddr> address = baseUserAddrMapper.selectList(entityWrapper);
                if(CollectionUtil.isEmpty(address)){
                    //设置地址为默认地址
                    baseUserAddr.setDefaultFlag(1);
                }else{
                    baseUserAddr.setDefaultFlag(0);
                }
                baseUserAddr.setCreaterTime(new Date());
                BaseContextHandler.set(SecurityConstants.SHARDING_KEY, userDetails.getUserId());
                baseUserAddrMapper.insert(baseUserAddr);
            }else{
                rd.setCode(SecurityConstants.FAIL_CODE);
                rd.setDesc("不能重复添加地址哦");
                return rd;
            }
        }else{  //修改
            baseUserAddr.setModifyTime(new Date());
            BaseContextHandler.set(SecurityConstants.SHARDING_KEY, userDetails.getUserId());
            baseUserAddrMapper.updateById(baseUserAddr);
        }
        rd.setCode(SecurityConstants.SUCCESS_CODE);
        rd.setDesc("保存成功");
        rd.setData(baseUserAddr.getAddrId());
        return rd;
    }


    @Override
    public Object deleteByAddrId(Integer addrId, Long userid) {
        JSONObject map = new JSONObject();
        //查询用户地址数据
        EntityWrapper<BaseUserAddr> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("USER_ID",userid);
        entityWrapper.notIn("ADDR_ID",addrId);
        BaseContextHandler.set(SecurityConstants.SHARDING_KEY, userid);
        List<BaseUserAddr> baseUserAddrs = baseUserAddrMapper.selectList(entityWrapper);
        int size = baseUserAddrs.size();
        switch (size){
            case 0:
                map.put("message","只剩下一个地址, 不能删除哦");
                break;

            default:
                BaseContextHandler.set(SecurityConstants.SHARDING_KEY,userid);
                BaseUserAddr userAddr = baseUserAddrMapper.selectById(addrId);
                baseUserAddrMapper.deleteById(addrId);
                if(userAddr.getDefaultFlag() == 1) {
                    entityWrapper = new EntityWrapper<>();
                    entityWrapper.eq("USER_ID",userid);
                    entityWrapper.orderBy("CREATER_TIME desc");
                    BaseContextHandler.set(SecurityConstants.SHARDING_KEY,userid);
                    BaseUserAddr addr = baseUserAddrMapper.selectList(entityWrapper).get(0);
                    addr.setDefaultFlag(1);
                    BaseContextHandler.set(SecurityConstants.SHARDING_KEY,userid);
                    baseUserAddrMapper.updateById(addr);
                }
                break;
        }
        return map;
    }

    @Override
    public List<BaseUserAddr> selectAddressList(Long userid) {
        EntityWrapper<BaseUserAddr> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("USER_ID",userid);
        entityWrapper.orderBy("CREATER_TIME DESC");
        BaseContextHandler.set(SecurityConstants.SHARDING_KEY,userid);
        return baseUserAddrMapper.selectList(entityWrapper);
    }

    @Override
    public BaseUserAddr selectByAddrId(Long userid, Integer addrId) {
        if(null == addrId){  //查询默认地址
            EntityWrapper<BaseUserAddr> entityWrapper = new EntityWrapper<>();
            entityWrapper.eq("USER_ID",userid);
            entityWrapper.eq("DEFAULT_FLAG",1);
            BaseContextHandler.set(SecurityConstants.SHARDING_KEY,userid);
            List<BaseUserAddr> addrList = baseUserAddrMapper.selectList(entityWrapper);
            if(CollectionUtil.isNotEmpty(addrList)){
                return addrList.get(0);
            }
        }else{
            //根据id获取详情
            BaseContextHandler.set(SecurityConstants.SHARDING_KEY,userid);
            return baseUserAddrMapper.selectById(addrId);
        }
        return null;
    }

    @Override
    public Object updateDefaultAddress(Long userid, Integer addrId) {
        //清除这个用户原有的默认地址
        BaseUserAddr addr = new BaseUserAddr();
        addr.setDefaultFlag(0);
        EntityWrapper<BaseUserAddr> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("USER_ID",userid);
        entityWrapper.eq("DEFAULT_FLAG",1);
        BaseContextHandler.set(SecurityConstants.SHARDING_KEY,userid);
        baseUserAddrMapper.update(addr,entityWrapper);

        //修改默认地址
        BaseUserAddr baseUserAddr = new BaseUserAddr();
        baseUserAddr.setAddrId(addrId);
        baseUserAddr.setDefaultFlag(1);
        BaseContextHandler.set(SecurityConstants.SHARDING_KEY,userid);
        baseUserAddrMapper.updateById(baseUserAddr);
        return addrId;
    }

    @Override
    @Transactional(rollbackFor=Exception.class)
    public void updateUserId(UserMerge userMerge) {
        Long newUserId = userMerge.getNewUserId();
        Long oldUserId = userMerge.getOldUserId();
        log.info("-->收货地址管理表合并-->oldUserId:{}, newUserId:{}", oldUserId, newUserId);

        if(oldUserId == newUserId) {
            log.info("-->收货地址管理表合并-->新旧userId相等，不用合并");
            return;
        }

        List<BaseUserAddr> list = this.selectAddressList(oldUserId);
        if(CollectionUtils.isEmpty(list)){
            log.info("-->收货地址管理表合并-->根据oldUserId:{}未查到地址信息，不用合并", oldUserId);
            return;
        }

        //判断是否需要切换表
        int oldTableIndex = (int) (oldUserId % 10);
        int newTableIndex = (int) (newUserId % 10);
        log.info("-->收货地址管理表合并-->oldUserId:{}所在表t_base_user_addr_{}，newUserId:{}所在表t_base_user_addr_{}", oldUserId, oldTableIndex, newUserId, newTableIndex);
        if(oldTableIndex != newTableIndex){
            list.forEach(baseUserAddr -> {
               baseUserAddr.setUserId(userMerge.getNewUserId());
            });

            //插入新数据
            BaseContextHandler.set(SecurityConstants.SHARDING_KEY, newUserId);
            this.insertBatch(list);

            //删除老数据
            this.deleteAllAddr(oldUserId);
            log.info("-->收货地址管理表合并-->修改数据成功,newUserId:{}所在表t_base_user_addr_{}",newUserId, newTableIndex);
        }else{
            log.info("-->收货地址管理表合并-->新旧ID都在同一张表：t_base_user_addr_{}，直接修改用户ID：{}为{}", oldTableIndex, oldUserId, newUserId);
            BaseContextHandler.set(SecurityConstants.SHARDING_KEY,oldUserId);
            baseUserAddrMapper.updateUserId(oldUserId,newUserId);
            log.info("-->收货地址管理表合并-->修改数据成功,newUserId:{}所在表t_base_user_addr_{}",newUserId, newTableIndex);
        }
        BaseContextHandler.set(SecurityConstants.SHARDING_KEY, null);
    }

    private void deleteAllAddr(Long oldUserId) {
        EntityWrapper<BaseUserAddr> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("USER_ID",oldUserId);
        BaseContextHandler.set(SecurityConstants.SHARDING_KEY,oldUserId);
        baseUserAddrMapper.delete(entityWrapper);
    }
}
