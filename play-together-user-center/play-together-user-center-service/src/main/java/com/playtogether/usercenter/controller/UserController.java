package com.playtogether.usercenter.controller;

import com.playtogether.common.exception.PTException;
import com.playtogether.common.vo.R;
import com.playtogether.usercenter.pojo.User;
import com.playtogether.usercenter.service.UserService;
import com.playtogether.usercenter.vo.RegisterBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author guanlibin
 * @version 1.0
 * @since 2020/11/2 15:52
 */
@RestController
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 昵称是否可用
     * @param nickname 用户昵称
     * @return R
     */
    @GetMapping("nickname/available")
    public R checkNicknameAvailable(@RequestParam("nickname") String nickname) {
        // TODO 1. 格式判断
        // 2. 检查昵称是否已经存在
        User user = new User();
        user.setNickname(nickname);
        int count = userService.selectCountByUser(user);
        // 3. 如果数量不为0,证明已经存在
        if (count > 0) {
            throw new PTException(400, "昵称已经存在");
        }
        return R.ok();
    }

    /**
     * 发送注册验证码
     * @param phone 手机号
     * @return R
     */
    @GetMapping("/registerVerifyCode/{phone}")
    public R sendVerifyCode(@PathVariable("phone") String phone) {
        // TODO 1. 格式判断
        // 2. 验证手机号是否已经被注册
        User user = new User();
        user.setPhone(phone);
        int count = userService.selectCountByUser(user);
        if (count > 0) {
            throw new PTException(400, "该手机号已经被注册");
        }
        // TODO 3. 发送频率限制
        // TODO 4. 调用sms发送验证码
        return R.ok().message("注册验证码发送成功");
    }

    /**
     * 注册
     * @param registerBody
     * @return
     */
    @PostMapping("register")
    public R register(@RequestBody RegisterBody registerBody) {
        // 1. 非空判断
        if (registerBody.haveEmptyField()) {
            throw new PTException(400, "表单缺少必要的参数");
        }
        // 2. 格式判断
        // TODO 3. 防止表单重复提交
        // 4. 用户新增
        int count = userService.save(registerBody);
        if (count != 1) {
            throw new PTException(500, "注册失败");
        }
        return R.ok().message("用户注册成功");
    }

    @GetMapping("query/phoneAndPassword")
    public R queryUserByPhoneAndPassword(
            @RequestParam("phone") String phone,
            @RequestParam("password") String password) {
        return R.ok().data(userService.queryUserByPhoneAndPassword(phone, password));
    }
}
