package com.sky.controller.admin;

import com.aliyuncs.exceptions.ClientException;
import com.sky.constant.MessageConstant;
import com.sky.properties.AliOssProperties;
import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@Slf4j
@Api("文件上传接口")
@RequestMapping("/admin/common")
public class CommonController {
    @Autowired
    private AliOssUtil aliOssUtil;

    @PostMapping("/upload")
    @ApiOperation("文件上传")
    public Result<String> upload(MultipartFile file) {
        try {
            byte[] bytes = file.getBytes();
            String originalName = file.getOriginalFilename();
            String newName = UUID.randomUUID() + originalName.substring(originalName.lastIndexOf("."));
            return Result.success(aliOssUtil.upload(bytes, newName));
        }
        catch (IOException e){
           log.error("文件上传失败{}",e);
        }
        catch (ClientException e){
            log.error("初始化失败");
        }
    return Result.error(MessageConstant.UNKNOWN_ERROR);}
}
