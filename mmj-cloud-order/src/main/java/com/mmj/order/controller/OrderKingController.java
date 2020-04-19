package com.mmj.order.controller;


import com.mmj.order.model.OrderKing;
import com.mmj.order.service.OrderKingService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/orderKing")
@Api(value = "订单和买买金关联控制器")
public class OrderKingController {

    private final OrderKingService orderKingService;

    public OrderKingController(OrderKingService orderKingService) {
        this.orderKingService = orderKingService;
    }

    @RequestMapping(value = "/frozenKingNum/{userId}", method = RequestMethod.POST)
    public int frozenKingNum(@PathVariable("userId")Long userId){
        return orderKingService.frozenKingNum(userId);
    }

    @RequestMapping(value = "/updateById",method = RequestMethod.POST)
    public boolean updateById(@RequestBody OrderKing ok){
        try {
            return orderKingService.updateById(ok);
        }catch (Exception e){
            log.error(e.getMessage(),e);
            return false;
        }
    }

    @RequestMapping(value = "/getGiveBy/{userId}" ,method = RequestMethod.POST)
    public String getGiveBy(@PathVariable("userId") Long userId){
        try {
            return orderKingService.getGiveBy(userId);
        }catch (Exception e){
            log.error(e.getMessage(),e);
            return null;
        }
    }
}
