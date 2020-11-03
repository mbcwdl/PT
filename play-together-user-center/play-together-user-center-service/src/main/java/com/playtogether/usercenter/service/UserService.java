package com.playtogether.usercenter.service;

import com.playtogether.common.exception.PTException;
import com.playtogether.usercenter.mapper.UserMapper;
import com.playtogether.usercenter.pojo.User;
import com.playtogether.usercenter.util.SafeUtils;
import com.playtogether.usercenter.vo.RegisterBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Date;

/**
 * @author guanlibin
 * @version 1.0
 * @since 2020/11/2 15:53
 */
@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    /**
     * 根据 昵称 或 手机号 或 qq 或 wx 查记录数
     * @param user
     * @return 记录数
     */
    public int selectCountByUser(User user) {
        return userMapper.selectCountByUser(user);
    }

    /**
     * 新增用户
     * @param registerBody
     * @return 影响行数
     */
    @Transactional(rollbackFor = Exception.class)
    public int save(RegisterBody registerBody) {
        // TODO 1. 确认验证码是否有效
        String verifyCode = registerBody.getVerifyCode();

        // 2. 封装User对象
        User user = new User();
        // 2.1 判断昵称是否唯一
        user.setNickname(registerBody.getNickname());
        int count = this.selectCountByUser(user);
        if (count > 0) {
            throw new PTException(400, "注册失败,昵称已经存在");
        }
        // 2.2 判断手机号是否存在
        user.setNickname(null);
        user.setPhone(registerBody.getPhone());
        count = this.selectCountByUser(user);
        if (count > 0) {
            throw new PTException(400, "注册失败,手机号已经存在");
        }
        user.setNickname(registerBody.getNickname());
        Date now = new Date();
        user.setGmtCreated(now);
        user.setGmtModified(now);

        // 3. 密码加盐
        user.setPassword(SafeUtils.MD5WithSalt(registerBody.getPassword(), SafeUtils.salt()));

        // 4. 执行新增
        return userMapper.save(user);
    }

    /**
     * 根据手机号和密码查询用户信息
     * @param phone
     * @param password
     * @return
     */
    public User queryUserByPhoneAndPassword(String phone, String password) {
        // TODO 格式判断
        User user = new User();
        user.setPhone(phone);
        // 根据手机号查出用户信息
        user = userMapper.selectSingleByUser(user);
        if (user == null) {
            throw new PTException(400, "该手机号未注册");
        }
        // 取出盐
        String passwordStoreInDB = user.getPassword();
        String salt = SafeUtils.getSaltFromHash(passwordStoreInDB);
        // 使用盐和用户登录时输入的密码进行加密，判断加密后是否和数据库中存储的相同
        if (!passwordStoreInDB.equals(SafeUtils.MD5WithSalt(password, salt))) {
            throw new PTException(400, "手机号或密码错误");
        }
        return user;
    }
}