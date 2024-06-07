package com.sky.config;

import com.sky.properties.AliOssProperties;
import com.sky.utils.AliOssUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class OssConfiguration {
    @Bean
    //启动时创建这个bean
    @ConditionalOnMissingClass
    public AliOssUtil aliOssUtil(AliOssProperties aliOssProperties){
        //配置类会自动传入bean对象aliossproperties
        log.info("开始创建阿里云上传工具");
       return new AliOssUtil(aliOssProperties.getEndpoint(), aliOssProperties.getBucketName());
    }
}
