package com.mmj.aftersale.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.mmj.aftersale.mapper.ReturnAddressMapper;
import com.mmj.aftersale.model.ReturnAddress;
import com.mmj.aftersale.model.dto.AfterSaleAddressDto;
import com.mmj.aftersale.service.ReturnAddressService;
import com.mmj.aftersale.utils.AreaDataUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;

/**
 * <p>
 * 退货地址 服务实现类
 * </p>
 *
 * @author zhangyicao
 * @since 2019-06-17
 */
@Service
public class ReturnAddressServiceImpl extends ServiceImpl<ReturnAddressMapper, ReturnAddress> implements ReturnAddressService {

    @Autowired
    private ReturnAddressMapper returnAddressMapper;

    @Autowired
    private AreaDataUtils areaDataUtils;

    @Override
    @Transactional(readOnly = true)
    public List<ReturnAddress> addressList() {
        EntityWrapper<ReturnAddress> returnAddressEntityWrapper = new EntityWrapper<>();
        return returnAddressMapper.selectList(returnAddressEntityWrapper);
    }

    @Override
    public AfterSaleAddressDto getAfterSaleAddressDto(Integer id) {
        Assert.notNull(id, "ID为空");
        ReturnAddress returnAddress = returnAddressMapper.selectById(id);
        Assert.notNull(returnAddress, "未查询到信息");
        String address = "";
        if (StringUtils.isNotBlank(returnAddress.getAreaCode())) {
            address = areaDataUtils.getArea(returnAddress.getAreaCode());
        }
        return new AfterSaleAddressDto(address + " " + returnAddress.getAddrDetail(), returnAddress.getCheckName(), returnAddress.getUserMobile());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int editAfterSaleAddress(ReturnAddress returnAddress) throws Exception{
        Assert.notNull(returnAddress, "参数为空");
        if (returnAddress.getAddressId() == null) {
            Assert.notNull(returnAddress.getAddressName(),"仓库名称不能为空");
            Assert.notNull(returnAddress.getAreaCode(),"区域编码不能为空");
            Assert.notNull(returnAddress.getAddrDetail(), "详细地址为空");
            Assert.notNull(returnAddress.getCheckName(), "收件人为空");
            Assert.notNull(returnAddress.getUserMobile(), "收件电话为空");
            if (1 == returnAddress.getDefaultFlag()) {
                updateAllDefaultIsFalse();
            }
            returnAddressMapper.insert(returnAddress);
            return 1;
        } else {
            ReturnAddress returnAddress1 = returnAddressMapper.selectById(returnAddress.getAddressId());
            if (1 == returnAddress.getDefaultFlag() && returnAddress1.getDefaultFlag() == 0) {
                updateAllDefaultIsFalse();
            }

            BeanUtils.copyProperties(returnAddress, returnAddress1);
            int updateNumber = returnAddressMapper.updateById(returnAddress1);
            if(updateNumber == 0){
                throw new Exception("更新失败");
            }
            return updateNumber;
        }
    }

    /**
     * 取消其他的地址的默认属性
     *
     * @return
     */
    @Transactional(propagation = Propagation.MANDATORY)
    public int updateAllDefaultIsFalse() {
        EntityWrapper<ReturnAddress> returnAddressEntityWrapper = new EntityWrapper<>();
        returnAddressEntityWrapper.eq("DEFAULT_FLAG",1);

        ReturnAddress returnAddress = new ReturnAddress();
        returnAddress.setDefaultFlag(0);
        return returnAddressMapper.update(returnAddress, returnAddressEntityWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int settingDefault(Integer id) throws Exception {
        Assert.notNull(id, "ID为空");
        ReturnAddress returnAddress = returnAddressMapper.selectById(id);
        Assert.notNull(returnAddress, "未查询到信息");
        if (1 == returnAddress.getDefaultFlag()) {
            return 1;
        }
        returnAddress.setDefaultFlag(1);

        updateAllDefaultIsFalse();
        int updateNumber = returnAddressMapper.updateById(returnAddress);
        if (updateNumber == 0)
            throw new Exception("更新不成功");
        return updateNumber;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeAfterSale(Integer id) {
        Assert.notNull(id, "ID为空");
        ReturnAddress returnAddress = returnAddressMapper.selectById(id);
        Assert.notNull(returnAddress, "未查询到信息");
        returnAddressMapper.deleteById(id);
    }
}
