package com.sky.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.sky.constant.MessageConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.exception.LoginFailedException;
import com.sky.mapper.UserMapper;
import com.sky.properties.WeChatProperties;
import com.sky.service.UserService;
import com.sky.utils.HttpClientUtil;
import com.sky.vo.UserLoginVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private WeChatProperties weChatProperties;
    private static final String WX_LOGIN = "https://api.weixin.qq.com/sns/jscode2session";
    @Autowired
    private UserMapper userMapper;
    //微信登陆
    @Override
    public User login(UserLoginDTO loginDTO) {
        String openid = getOpenId(loginDTO.getCode());
        //判断openId是否为空，如果为空表示登陆失败，抛出异常
        if(openid == null) throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
        //判断是否为新用户
        User user= userMapper.getByOpenId(openid);
        if(user == null){
            user = User.builder().openid(openid).createTime(LocalDateTime.now()).build();
            userMapper.insert(user);
        }
    return user;
    }


    private String getOpenId(String code){
        Map<String,String> requestMap = new HashMap<>();
        requestMap.put("appid", weChatProperties.getAppid());
        requestMap.put("secret", weChatProperties.getSecret());
        requestMap.put("js_code",code);
        requestMap.put("grant_type","authorization_code");
        String response = HttpClientUtil.doGet(WX_LOGIN, requestMap);
        //调用微信接口服务，获得当前用户的openId（唯一标识）
        //把response Json字符串转换成JsonObject
        JSONObject jsonObject = JSONObject.parseObject(response);
        String openid = jsonObject.getString("openid");
        return openid;
    }
}
