package com.playtogether.authcenter.service;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.playtogether.authcenter.client.UserClient;
import com.playtogether.authcenter.config.JwtProperties;
import com.playtogether.authcenter.payload.UserInfo;
import com.playtogether.authcenter.util.JwtUtils;
import com.playtogether.common.exception.PTException;
import com.playtogether.common.vo.R;
import com.playtogether.usercenter.pojo.User;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;

/**
 * @author guanlibin
 * @version 1.0
 * @since 2020/11/3 10:25
 */
@Service
@EnableConfigurationProperties(JwtProperties.class)
public class AuthService {

    @Autowired
    private UserClient userClient;

    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 登录成功后返回token
     * @param action
     * @param phone
     * @param password
     * @param verifyCode
     * @return 以jwt为载体的token
     */
    public String login(String action, String phone, String password, String verifyCode) {
        String token;
        switch (action) {
            case "PhoneAndPassword":
                // 1. 调用用户中心微服务获取用户信息
                R result = userClient.queryUserByPhoneAndPassword(phone, password);
                if (result.getCode() != 200) {
                    throw new PTException(result.getCode(), result.getMessage());
                }
                // 2. 生成token
                @SuppressWarnings("unchecked")
                LinkedHashMap<String, Object> map = (LinkedHashMap<String, Object>) result.getData();
                // 2.1 拿到id和nickname
                Integer id = (Integer) map.get("id");
                String nickname = map.get("nickname").toString();
                // 2.2 调用jwt工具类
                UserInfo userInfo = new UserInfo();
                userInfo.setId(id);
                userInfo.setNickname(nickname);
                try {
                    token = JwtUtils.generateToken(userInfo, jwtProperties.getPrivateKey(), jwtProperties.getExpire());
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new PTException(500, "服务器开了点小差哦");
                }
                break;
            case "PhoneAndVerifyCode":
                token = null;
                break;
            default:
                token = null;
        }
        return token;
    }
}