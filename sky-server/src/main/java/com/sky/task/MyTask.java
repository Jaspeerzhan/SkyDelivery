package com.sky.task;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Select;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.zip.DataFormatException;

@Component //交给IOC管理，才能进行调度
@Slf4j
public class MyTask {

    //每五秒执行一次
//    @Scheduled(cron = "0/5 * * * * ?")
//    public void executeTask(){
//        log.info("定时任务执行: {}",new Date());
//    }
}
