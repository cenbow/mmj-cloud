package com.mmj.aftersale.service;

import com.baomidou.mybatisplus.service.IService;
import com.mmj.aftersale.model.ReturnAddress;
import com.mmj.aftersale.model.dto.AfterSaleAddressDto;

import java.util.List;

/**
 * <p>
 * 退货地址 服务类
 * </p>
 *
 * @author zhangyicao
 * @since 2019-06-17
 */
public interface ReturnAddressService extends IService<ReturnAddress> {
    List<ReturnAddress> addressList();

    AfterSaleAddressDto getAfterSaleAddressDto(Integer id);

    int editAfterSaleAddress(ReturnAddress returnAddress) throws Exception;

    int settingDefault(Integer id) throws Exception;

    void removeAfterSale(Integer id);

}
