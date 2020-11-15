package com.playtogether.auth.service;

import cn.hutool.json.JSONObject;

import com.playtogether.auth.client.UserClient;
import com.playtogether.auth.config.JwtProperties;
import com.playtogether.auth.config.QqProperties;
import com.playtogether.auth.exception.PtAuthException;
import com.playtogether.auth.util.QqHttpClient;
import com.playtogether.common.payload.JwtPayload;
import com.playtogether.common.util.JwtUtils;
import com.playtogether.common.util.RsaUtils;
import com.playtogether.auth.vo.LoginBody;
import com.playtogether.common.enums.PtCommonEnums;
import com.playtogether.common.exception.PtException;
import com.playtogether.common.util.NumberUtils;
import com.playtogether.common.vo.R;
import com.playtogether.user.pojo.User;
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

import static com.playtogether.auth.enums.PtEnums.*;
import static com.playtogether.auth.constant.LoginActionConstants.*;
import static com.playtogether.user.constant.UserPattern.*;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author guanlibin
 * @version 1.0
 * @since 2020/11/3 10:25
 */
@Slf4j
@Service
@EnableConfigurationProperties({JwtProperties.class, QqProperties.class})
public class AuthService {

    private static final String KEY_PREFIX = "pt:user:login:verify:phone:";

    private static final String SEND_INTERVAL_CONTROL_PREFIX = "pt:user:verify:code:interval:";

    private static final String SEND_TIMES_PER_DAY_CONTROL_PREFIX = "pt:user:verify:code:times:";

    private static final String DISTRIBUTE_SESSION_PREFIX = "pt:auth:token:";

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
     *
     * @return 以jwt为载体的token
     */
    public String login(LoginBody loginBody) {
        // 取出登录表单中的各字段
        String action = loginBody.getAction();
        String account = loginBody.getAccount();  // 账号可能为手机 或 邮箱
        String password = loginBody.getPassword();
        String verifyCode = loginBody.getVerifyCode();
        boolean rememberMe = loginBody.isRememberMe();
        // 登录token
        String token;
        switch (action) {
            case PASSWORD:
                R result;
                // 1. 调用用户中心微服务获取用户id
                result = userClient.queryUserByAccountAndPassword(account, password);
                if (result.getCode() != HttpStatus.OK.value()) {
                    throw new PtAuthException(ACCOUNT_OR_PASSWORD_ERROR);
                }
                // 2. 生成token
                @SuppressWarnings("unchecked")
                Map<String, Object> data = (LinkedHashMap<String, Object>)result.getData();
                int id = Integer.parseInt(data.get("id").toString());

                token = getJwtToken(id, rememberMe);
                break;
            case VERIFYCODE:
                // 1. 有没有传验证码和手机号,以及校验格式
                String phone = account;
                if (StringUtils.isEmpty(phone)) {
                    throw new PtAuthException(PHONE_CANNOT_BE_NULL);
                }
                if (!phone.matches(PATTERN_PHONE)) {
                    throw new PtAuthException(PHONE_PATTERN_ILLEGAL);
                }
                if (StringUtils.isEmpty(verifyCode)) {
                    throw new PtAuthException(VERIFY_CODE_CANNOT_BE_NULL);
                }
                // 2. 从redis中取出来验证码，和用户传来的验证码进行比对
                String code = stringRedisTemplate.opsForValue().get(KEY_PREFIX + phone);
                if (code == null) {
                    throw new PtAuthException(VERIFY_CODE_CANNOT_BE_NULL);
                }
                if (!verifyCode.equals(code)) {
                    throw new PtAuthException(PHONE_OR_VERIFY_CODE_ERROR);
                }
                // TODO 3. 调用用户微服务查询用户信息
                token = "";
                break;
            default:
                throw new PtException(PtCommonEnums.BAD_REQUEST);
        }

        return token;
    }

    /**
     * 发送登录验证码
     *
     * @param phone
     */
    public void sendLoginVerifyCode(String phone) {
        // 1. 格式判断
        if (StringUtils.isEmpty(phone)) {
            throw new PtAuthException(PHONE_CANNOT_BE_NULL);
        }
        if (!phone.matches(PATTERN_PHONE)) {
            throw new PtAuthException(PHONE_PATTERN_ILLEGAL);
        }
        // 2. 发送频率限制
        ValueOperations<String, String> ofv = stringRedisTemplate.opsForValue();
        // 2.1 单次发送时间要超过1分钟
        String sicK = SEND_INTERVAL_CONTROL_PREFIX + phone;
        String o = ofv.get(sicK);
        if (o != null) {
            throw new PtAuthException(SEND_INTERVAL_LESS_THAN_ONE_MINUTE);
        } else {
            ofv.set(sicK, "", 1, TimeUnit.MINUTES);
        }
        // 2.2 单日发送次数不超过10次
        String stpdcK = SEND_TIMES_PER_DAY_CONTROL_PREFIX + phone;
        String times = ofv.get(stpdcK);
        if (times != null && Integer.parseInt(times) >= SEND_MAX_TIMES) {
            throw new PtAuthException(SEND_MAX_TIME_MORE_THAN_CONTROL);
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
            ofv.set(stpdcK, Integer.parseInt(times) + 1 + "");
        } else {
            ofv.set(stpdcK, "1", 1, TimeUnit.DAYS);
        }
        // 3.4 存入redis
        // TODO lock
//        ofv.set(KEY_PREFIX + phone, code, 5, TimeUnit.MINUTES);
        ofv.set(KEY_PREFIX + phone, "123456", 5, TimeUnit.MINUTES);
    }

    /**
     * 请求授权地址
     *
     * @return
     */
    public String qqLogin() {
        // 用于第三方应用防止CSRF攻击
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        // Step1：获取Authorization Code
        return String.format(
                qqProperties.getAuthorizationCodeUrl(),
                qqProperties.getAppId(),
                URLEncoder.encode(qqProperties.getCbUrl()), uuid);
    }

    /**
     * 处理qq登录后的回调
     * @param code
     * @param state
     * @return
     * @throws Exception
     */
    public R qqLoginCallback(String code, String state) throws Exception {
        // todo 验证state (注意登录页面的域名和回调的域名要设置相同，否则拿到的session和qqauth中的session不一致）
        // Step2：通过Authorization Code获取Access Token
        String url = String.format(
                qqProperties.getAccessTokenUrl(),
                qqProperties.getAppId(),
                qqProperties.getAppKey(), code,
                qqProperties.getCbUrl());
        String accessToken = QqHttpClient.getAccessToken(url);

        // Step3: 获取回调后的openId
        url = String.format(qqProperties.getOpenIdUrl(), accessToken);
        String openId = QqHttpClient.getOpenId(url);

        if (StringUtils.isEmpty(openId)) {
            throw new PtException(PtCommonEnums.INVALID_REQUEST);
        }

        // Step4: 判断此qq是否已经绑定用户
        User selectObj = new User();
        selectObj.setQqOpenId(openId);


        int count = Integer.parseInt(userClient.getCountByUser(selectObj).getData().toString());

        if (count == 1) {

            // 用户已经注册，查询用户信息，签发token
            int id = Integer.parseInt(userClient.queryUserIdByQqOpenId(openId).toString());
            return R.ok().data(getJwtToken(id, true));

        } else if (count == 0){

            // 用户未注册 指示前端 跳转 qq和用户绑定 页面
            JSONObject obj = new JSONObject();
            obj.putOnce("accessToken", RsaUtils.encrypt(accessToken, jwtProperties.getAuthCenterPublicKey()));
            obj.putOnce("openId", RsaUtils.encrypt(openId, jwtProperties.getAuthCenterPublicKey()));
            return R.fail().code(400).message("未绑定用户").data(obj);

        } else {
            return R.fail().code(500);
        }
    }

    /**
     * qq和账号绑定
     *
     * @param accessToken
     * @param openId
     * @param account
     * @param password
     */
    public String qqBinding(String accessToken, String openId, String account, String password) throws Exception {

        // Step1: 验证账户和密码的正确性 以及 此账号是否已经被绑定
        R r = userClient.queryUserByAccountAndPassword(account, password);
        // 账号密码错误
        if (r.getCode() != HttpStatus.OK.value()) {
            throw new PtException(r.getCode(), r.getMessage());
        }


        // Step2: 解密accessToken和openId
        String at = RsaUtils.decrypt(accessToken, jwtProperties.getAuthCenterPrivateKey());

        String oi = RsaUtils.decrypt(openId, jwtProperties.getAuthCenterPrivateKey());

        // 账号是否已经被绑定
        @SuppressWarnings("unchecked")
        Map<String, Object> data = (LinkedHashMap<String, Object>)r.getData();

        if (!StringUtils.isEmpty(data.get("qqOpenId"))) {
            throw new PtException(HttpStatus.BAD_REQUEST.value(), "此账号已经被绑定了");
        }

        // Step3: 根据accessToken和openId获取qq上的用户信息
        String url = String.format(qqProperties.getUserInfoUrl(), at, qqProperties.getAppId(), oi);
        JSONObject userInfo = QqHttpClient.getUserInfo(url);
        // Step4: 操作数据库

        User user = new User();
        int id = Integer.parseInt(data.get("id").toString());
        user.setId(id);
        user.setQqOpenId(oi);

        if (StringUtils.isEmpty(data.get("avatar"))) {
            String avatar = userInfo.getStr("figureurl_qq");
            user.setAvatar(avatar);
        }
        r = userClient.updateById(user);
        if (r.getCode() != 200) {
            throw new PtException(r.getCode(), r.getMessage());
        }
        // Step5: 签发token
        return getJwtToken(id, true);
    }


    private String getJwtToken(int id, boolean rememberMe) {
        return rememberMe ?
                generateJwtToken(id, jwtProperties.getPermanentExpire()):
                generateJwtToken(id, jwtProperties.getTemporaryExpire());
    }

    private String generateJwtToken(int id, int expire) {
        String token;
        // 调用jwt工具类生成token
        JwtPayload payload = new JwtPayload();
        payload.setId(id);
        String uuid = UUID.randomUUID().toString();
        payload.setUuid(uuid);
        try {
            token = JwtUtils.generateToken(
                    payload, jwtProperties.getAuthCenterPrivateKey(), expire);
        } catch (Exception e) {
            log.info("pares jwt error", e);
            throw new PtException(PtCommonEnums.SERVER_ERROR);
        }
        // id-uuid映射存入redis中
        ValueOperations<String, String> ssvo = stringRedisTemplate.opsForValue();
        ssvo.set(DISTRIBUTE_SESSION_PREFIX + id, uuid);
        return token;
    }
}
