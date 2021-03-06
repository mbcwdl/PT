package com.playtogether.user.service;

import cn.hutool.json.JSONUtil;
import com.playtogether.common.enums.PtCommonEnums;
import com.playtogether.common.exception.PtException;
import com.playtogether.common.inteceptor.PtMicroServiceAuthInterceptor;
import com.playtogether.common.payload.JwtPayload;
import com.playtogether.common.util.NumberUtils;
import com.playtogether.common.util.ValidationUtil;
import com.playtogether.user.exception.PtUserException;
import com.playtogether.user.mapper.UserMapper;
import com.playtogether.user.pojo.User;
import com.playtogether.user.util.SafeUtils;
import com.playtogether.user.vo.RegisterBody;
import com.playtogether.user.vo.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import static com.playtogether.user.constant.UserPattern.*;
import static com.playtogether.user.enums.PtEnums.*;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/**
 * @author guanlibin
 * @version 1.0
 * @since 2020/11/2 15:53
 */
@Slf4j
@Service
public class UserService {

    private static final String KEY_PREFIX = "pt:user:register:verify:phone:";

    private static final String SEND_INTERVAL_CONTROL_PREFIX = "pt:user:verify:code:interval:";

    private static final String SEND_TIMES_PER_DAY_CONTROL_PREFIX = "pt:user:verify:code:times:";

    private static final String USER_INFO_PREFIX = "pt:user:info:";

    private static final int SEND_MAX_TIMES = 10;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 新增用户
     * @param registerBody
     */
    public void register(RegisterBody registerBody) {
        // 1. 非空及格式校验
        ValidationUtil.ValidResult validResult = ValidationUtil.validateBean(registerBody);
        if (validResult.hasErrors()) {
            List<ValidationUtil.ErrorMessage> allErrors = validResult.getAllErrors();
            throw new PtException(400, allErrors.get(0).getMessage());
        }
        // 2. 确认验证码是否有效
        // 2.1 获取用户传的验证码
        String verifyCode = registerBody.getVerifyCode();
        // 2.2 获取redis中的验证码
        ValueOperations<String, String> ofv = stringRedisTemplate.opsForValue();
        String code = ofv.get(KEY_PREFIX + registerBody.getPhone());
        // 2.3 用户有没有点击发送验证码
        if (StringUtils.isEmpty(code)) {
            throw new PtUserException(SEND_VERIFY_CODE_FIRST);
        }
        // 2.4 比对
        if (!verifyCode.equals(code)) {
            throw new PtUserException(VERIFY_CODE_ERROR);
        }
        // 3. 封装User对象
        User user = new User();
        // 3.1 判断昵称是否唯一
        user.setNickname(registerBody.getNickname());
        int count = userMapper.selectCountByUser(user);
        if (count > 0) {
            throw new PtUserException(NICKNAME_ALREADY_EXIST);
        }
        // 3.2 判断手机号是否存在
        user.setNickname(null);
        user.setPhone(registerBody.getPhone());
        count = userMapper.selectCountByUser(user);
        if (count > 0) {
            throw new PtUserException(PHONE_ALREADY_EXIST);
        }

        user.setNickname(registerBody.getNickname());
        Date now = new Date();
        user.setGmtCreated(now);
        user.setGmtModified(now);

        // 4. 密码加盐
        user.setPassword(SafeUtils.MD5WithSalt(registerBody.getPassword(), SafeUtils.salt()));

        // 5. 执行新增
        count = userMapper.save(user);
        if (count != 1) {
            throw new PtException(PtCommonEnums.SERVER_ERROR);
        }
    }

    /**
     * 根据账号和密码查询用户id
     * @param account
     * @param password
     * @return
     */
    public User queryUserByAccountAndPassword(String account, String password) {
        User user = new User();
        // 1. 判断是手机号还是邮箱
        if (Pattern.matches(PATTERN_PHONE, account)) {
            user.setPhone(account);
        } else if (Pattern.matches(PATTERN_EMAIL, account)) {
            user.setEmail(account);
        } else {
            throw new PtException(PtCommonEnums.BAD_REQUEST);
        }
        // 2.根据手机号 或 email 查出用户信息
        user = userMapper.selectSingleByUser(user);
        if (user == null) {
            throw new PtUserException(ACCOUNT_OR_PASSWORD_ERROR);
        }
        // 3.取出盐
        String passwordStoreInDb = user.getPassword();
        String salt = SafeUtils.getSaltFromHash(passwordStoreInDb);
        // 4.使用盐和用户登录时输入的密码进行加密，判断加密后是否和数据库中存储的相同
        if (!passwordStoreInDb.equals(SafeUtils.MD5WithSalt(password, salt))) {
            throw new PtUserException(ACCOUNT_OR_PASSWORD_ERROR);
        }

        return user;
    }

    /**
     * 检查昵称是否已存在
     * @param nickname
     */
    public void checkNicknameAvailable(String nickname) {
        // 1. 格式判断
        if (StringUtils.isEmpty(nickname)) {
            throw new PtUserException(NICKNAME_CANNOT_BE_NULL);
        }
        int len = nickname.length();
        if (len < 1 || len > 12) {
            throw new PtUserException(NICKNAME_LENGTH_ILLEGAL);
        }
        // 2. 检查昵称是否已经存在
        User user = new User();
        user.setNickname(nickname);
        int count = userMapper.selectCountByUser(user);
        // 3. 如果数量不为0,证明已经存在
        if (count > 0) {
            throw new PtUserException(NICKNAME_ALREADY_EXIST);
        }
    }

    /**
     * 发送验证码
     * @param phone
     */
    public void sendVerifyCode(String phone) {
        // 1. 格式判断
        if (StringUtils.isEmpty(phone)) {
            throw new PtUserException(PHONE_CANNOT_BE_NULL);
        }
        if (!phone.matches(PATTERN_PHONE)) {
            throw new PtUserException(PHONE_PATTERN_ILLEGAL);
        }
        // 2. 验证手机号是否已经被注册
        User user = new User();
        user.setPhone(phone);
        int count = userMapper.selectCountByUser(user);
        if (count > 0) {
            throw new PtUserException(PHONE_ALREADY_EXIST);
        }
        // 3. 发送频率限制
        ValueOperations<String, String> ofv = stringRedisTemplate.opsForValue();
        // 3.1 单次发送时间要超过1分钟
        String sicK = SEND_INTERVAL_CONTROL_PREFIX + phone;
        String o = ofv.get(sicK);
        if (o != null) {
            throw new PtUserException(SEND_INTERVAL_LESS_THAN_ONE_MINUTE);
        } else {
            ofv.set(sicK, "", 1, TimeUnit.MINUTES);
        }
        // 3.2 单日发送次数不超过10次
        String stpdcpK = SEND_TIMES_PER_DAY_CONTROL_PREFIX + phone;
        String times = ofv.get(stpdcpK);
        if (times !=null && Integer.parseInt(times) >= SEND_MAX_TIMES) {
            throw new PtUserException(SEND_MAX_TIME_MORE_THAN_CONTROL);
        }
        // 4.调用sms发送验证码
        // 4.1 生成验证码
        String code = NumberUtils.generateCode(6);

        // 4.2 发送验证码
        Map<String, String> map = new HashMap<>(16);
        map.put("code", code);
        map.put("phone", phone);
        try {
            // TODO lock
            // amqpTemplate.convertAndSend("pt.sms.exchange", "sms.verify.code", map);
        } catch (AmqpException e) {
            log.error(e.getMessage());
        }
        // 4.3 redis中记录发送间隔和每天的发送次数
        if (times != null) {
            ofv.set(stpdcpK, Integer.parseInt(times) + 1 + "");
        } else {
            ofv.set(stpdcpK, "1", 1, TimeUnit.DAYS);
        }
        // 4.3 存入redis
        // TODO lock
        // ofv.set(KEY_PREFIX + phone, code, 5, TimeUnit.MINUTES);
        ofv.set(KEY_PREFIX + phone, "123456", 5, TimeUnit.MINUTES);
    }

    /**
     * 根据非空条件查询用户数
     * @param user
     * @return
     */
    public int getCountByUser(User user) {
        return userMapper.selectCountByUser(user);
    }

    /**
     * 根据qq的openId查询用户id
     * @param qqOpenId
     * @return
     */
    public int queryUserIdByQqOpenId(String qqOpenId) {

        User user = new User();
        user.setQqOpenId(qqOpenId);

        user = userMapper.selectSingleByUser(user);

        if (user == null) {
            throw new PtException(PtCommonEnums.BAD_REQUEST);
        }

        return user.getId();
    }

    /**
     * 根据id更新用户
     * @param user
     */
    public void updateById(User user) {
        user.setGmtModified(new Date());
        int count = userMapper.updateUserById(user);
        if (count != 1) {
            throw new PtUserException(UPDATE_USER_INFO_FAILURE);
        }
    }

    /**
     * 获取用户信息
     * @return
     */
    public UserInfo getUserInfo() {

        Integer id = PtMicroServiceAuthInterceptor.tl.get().getId();

        // 首先从redis中查，没有再从数据库中查，然后放入redis中

        ValueOperations<String, String> vp = stringRedisTemplate.opsForValue();

        String k = USER_INFO_PREFIX + id;
        String userInfoJsonStrInRedis = vp.get(k);

        UserInfo userInfo;
        if (StringUtils.isEmpty(userInfoJsonStrInRedis)) {
            // 从数据库中查
            User user = new User();
            user.setId(id);

            user = userMapper.selectSingleByUser(user);
            if (user == null) {
                throw new PtUserException(USER_NOT_FOUND);
            }

            userInfo = new UserInfo();
            userInfo.setId(id);
            userInfo.setAvatar(user.getAvatar());
            userInfo.setNickname(user.getNickname());

            // 设置到redis中
            vp.set(k, JSONUtil.toJsonStr(userInfo));
        } else {
            // 直接解析redis中的userInfo
            userInfo = JSONUtil.toBean(userInfoJsonStrInRedis, UserInfo.class);
        }

        return userInfo;
    }
}
