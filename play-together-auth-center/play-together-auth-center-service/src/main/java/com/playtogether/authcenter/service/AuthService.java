package com.playtogether.authcenter.service;

import cn.hutool.json.JSONObject;
import com.playtogether.authcenter.client.UserClient;
import com.playtogether.authcenter.config.JwtProperties;
import com.playtogether.authcenter.config.QqProperties;
import com.playtogether.authcenter.exception.PTAuthException;
import com.playtogether.authcenter.payload.UserInfo;
import com.playtogether.authcenter.util.JwtUtils;
import com.playtogether.authcenter.util.QqHttpClient;
import com.playtogether.common.enums.PTCommonEnums;
import com.playtogether.common.exception.PTException;
import com.playtogether.common.util.NumberUtils;
import com.playtogether.common.vo.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static com.playtogether.authcenter.enums.PTEnums.*;
import static com.playtogether.authcenter.constant.LoginActionConstants.*;
import static com.playtogether.usercenter.constant.UserPattern.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/**
 * @author guanlibin
 * @version 1.0
 * @since 2020/11/3 10:25
 */
@Slf4j
@Service
@EnableConfigurationProperties({JwtProperties.class, QqProperties.class})
public class AuthService {

    private static final String KEY_PREFIX = "user:login:verify:phone:";

    private static final String SEND_INTERVAL_CONTROL_PREFIX = "user:verify:code:interval:";

    private static final String SEND_TIMES_PER_DAY_CONTROL_PREFIX = "user:verify:code:times:";

    private static final int SEND_MAX_TIMES = 10;

    @Autowired
    private UserClient userClient;

    @Autowired
    private JwtProperties jwtProperties;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Autowired
    private QqProperties qqProperties;

    /**
     * 登录成功后返回token
     * @param action
     * @param account
     * @param password
     * @param verifyCode
     * @return 以jwt为载体的token
     */
    public String login(String action, String account, String password, String verifyCode) {
        String token;
        switch (action) {
            case PASSWORD:
                R result;
                // 0.对account进行正则判断，判断是手机还是邮箱
                if (Pattern.matches(PATTERN_PHONE, account)) {
                    // 1. 调用用户中心微服务获取用户信息
                    String phone = account;
                    result = userClient.queryUserByAccountAndPassword(phone, password);
                    if (result.getCode() != HttpStatus.OK.value()) {
                        throw new PTAuthException(ACCOUNT_OR_PASSWORD_ERROR);
                    }
                } else if (Pattern.matches(PATTERN_EMAIL, account)) {
                    // TODO 邮箱登录
                    String email = account;
                    result = userClient.queryUserByAccountAndPassword(email, password);
                    if (result.getCode() != HttpStatus.OK.value()) {
                        throw new PTAuthException(ACCOUNT_OR_PASSWORD_ERROR);
                    }
                } else {
                    throw new PTException(PTCommonEnums.BAD_REQUEST);
                }
                // 2. 生成token
                @SuppressWarnings("unchecked")
                LinkedHashMap<String, Object> map = (LinkedHashMap<String, Object>) result.getData();
                // 2.1 拿到id和nickname
                Integer id = (Integer) map.get("id");
                String nickname = map.get("nickname").toString();
                // 2.2 调用jwt工具类生成token
                UserInfo userInfo = new UserInfo();
                userInfo.setId(id);
                userInfo.setNickname(nickname);
                try {
                    token = JwtUtils.generateToken(
                            userInfo, jwtProperties.getPrivateKey(), jwtProperties.getExpire());
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new PTException(PTCommonEnums.SERVER_ERROR);
                }
                break;
            case VERIFYCODE:
                // 1. 有没有传验证码和手机号,以及校验格式
                String phone = account;
                if (StringUtils.isEmpty(phone)) {
                    throw new PTAuthException(PHONE_CANNOT_BE_NULL);
                }
                if (!phone.matches(PATTERN_PHONE)) {
                    throw new PTAuthException(PHONE_PATTERN_ILLEGAL);
                }
                if (StringUtils.isEmpty(verifyCode)) {
                    throw new PTAuthException(VERIFY_CODE_CANNOT_BE_NULL);
                }
                // 2. 从redis中取出来验证码，和用户传来的验证码进行比对
                String code = stringRedisTemplate.opsForValue().get(KEY_PREFIX + phone);
                if (code == null) {
                    throw new PTAuthException(VERIFY_CODE_CANNOT_BE_NULL);
                }
                if (!verifyCode.equals(code)) {
                    throw new PTAuthException(PHONE_OR_VERIFY_CODE_ERROR);
                }
                // TODO 3. 调用用户微服务查询用户信息
                token = "手机号验证码登录成功";
                break;
            default:
                throw new PTException(PTCommonEnums.BAD_REQUEST);
        }
        return token;
    }

    /**
     * 发送登录验证码
     * @param phone
     */
    public void sendLoginVerifyCode(String phone) {
        // 1. 格式判断
        if (StringUtils.isEmpty(phone)) {
            throw new PTAuthException(PHONE_CANNOT_BE_NULL);
        }
        if (!phone.matches(PATTERN_PHONE)) {
            throw new PTAuthException(PHONE_PATTERN_ILLEGAL);
        }
        // 2. 发送频率限制
        ValueOperations<String, String> ofv = stringRedisTemplate.opsForValue();
        // 2.1 单次发送时间要超过1分钟
        String sicK = SEND_INTERVAL_CONTROL_PREFIX + phone;
        String o = ofv.get(sicK);
        if (o != null) {
            throw new PTAuthException(SEND_INTERVAL_LESS_THAN_ONE_MINUTE);
        } else {
            ofv.set(sicK, "", 1, TimeUnit.MINUTES);
        }
        // 2.2 单日发送次数不超过10次
        String stpdcpK = SEND_TIMES_PER_DAY_CONTROL_PREFIX + phone;
        String times = ofv.get(stpdcpK);
        if (times !=null && Integer.parseInt(times) >= SEND_MAX_TIMES) {
            throw new PTAuthException(SEND_MAX_TIME_MORE_THAN_CONTROL);
        }
        // 3.调用sms发送验证码
        // 3.1 生成验证码
        String code = NumberUtils.generateCode(6);

        // 3.2 发送验证码
        Map<String, String> map = new HashMap<>(16);
        map.put("code", code);
        map.put("phone", phone);
        try {
            // TODO lock
//            amqpTemplate.convertAndSend("pt.sms.exchange", "sms.verify.code", map);
        } catch (AmqpException e) {
            log.error(e.getMessage());
        }
        // 3.3 redis中记录发送间隔和每天的发送次数
        if (times != null) {
            ofv.set(stpdcpK, Integer.parseInt(times) + 1 + "");
        } else {
            ofv.set(stpdcpK, "1", 1, TimeUnit.DAYS);
        }
        // 3.4 存入redis
        // TODO lock
//        ofv.set(KEY_PREFIX + phone, code, 5, TimeUnit.MINUTES);
        ofv.set(KEY_PREFIX + phone, "123456", 5, TimeUnit.MINUTES);
    }

    /**
     * 请求授权地址
     * @param session
     * @return
     */
    public String qqAuth(HttpSession session) {
        // 用于第三方应用防止CSRF攻击
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");

        // Step1：获取Authorization Code
        return String.format(
                qqProperties.getAuthorizationCodeUrl(),
                qqProperties.getAppId(),
                URLEncoder.encode(qqProperties.getCbUrl()), uuid);
    }

    /**
     * 授权回调
     * @param req
     * @return
     * @throws IOException
     */
    public String qqCallback(HttpServletRequest req) throws IOException {
        // 验证state
        String stateFromQq = req.getParameter("state");
        // todo pending 验证state (注意登录页面的域名和回调的域名要设置相同，否则拿到的session和qqauth中的session不一致）
        /*String stateInSession = req.getSession().getAttribute("state").toString();
        if (stateInSession != null) {
            if (!stateInSession.equals(stateFromQq)) {
                return "需要重定向到登录页面";
            }
        }*/
        // Step2：通过Authorization Code获取Access Token
        // 取出Authorization Code
        String code = req.getParameter("code");

        String url = String.format(
                qqProperties.getAccessTokenUrl(),
                qqProperties.getAppId(),
                qqProperties.getAppKey(), code,
                qqProperties.getCbUrl());
        String accessToken = QqHttpClient.getAccessToken(url);

        // Step3: 获取回调后的openID
        url = String.format(qqProperties.getOpenIdUrl(), accessToken);
        String openID = QqHttpClient.getOpenID(url);

        // Step4：获取QQ用户信息
        url = String.format(qqProperties.getUserInfoUrl(), accessToken, qqProperties.getAppId(), openID);
        JSONObject userInfo = QqHttpClient.getUserInfo(url);

        return userInfo.toString();
    }

}
