package com.sky.task;


import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.zip.DataFormatException;

@Component
@Slf4j
public class OrderTask {

    @Autowired
    private OrderMapper orderMapper;
    //  处理超时订单
    @Scheduled(cron="0 * * * * ? ")
    public void processTimeoutOrder(){
        Integer status = Orders.PENDING_PAYMENT;
        log.info("定时处理超时订单{}", LocalDateTime.now());
        List<Orders> ordersList = orderMapper.getByStatusAndOrderTimeLt(status, LocalDateTime.now().minusMinutes(15));
        //select * from orders where status =? and order_time < [现在时间-15】
        if(ordersList != null && ordersList.size() != 0){
            ordersList.forEach(
                    order ->{
                        order.setStatus(Orders.CANCELLED);
                        order.setCancelReason("订单超时，自动取消");
                        order.setCancelTime(LocalDateTime.now());
                        orderMapper.update(order);
                    }
            );
        }
    }

    //处理一直处于派送中订单

    @Scheduled(cron = "0 0 1 * * ? ") //每天凌晨一点定时清理
    public void processDeliveryOrder(){
        log.info("定时处理一直处于配送中订单{}",new Date());
        List<Orders> ordersList = orderMapper.getByStatusAndOrderTimeLt(Orders.DELIVERY_IN_PROGRESS, LocalDateTime.now().minusHours(1));//清理前一个工作日订单
        if(ordersList != null && ordersList.size() != 0){
            ordersList.forEach(
                    order ->{
                        order.setStatus(Orders.COMPLETED);
                        orderMapper.update(order);
                    }
            );
        }

    }
}
