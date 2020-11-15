package com.playtogether.user.controller;

import com.playtogether.common.inteceptor.PtMicroServiceAuthInterceptor;
import com.playtogether.common.payload.JwtPayload;
import com.playtogether.common.vo.R;
import com.playtogether.user.service.UserService;
import com.playtogether.user.vo.RegisterBody;
import com.playtogether.user.vo.UserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author guanlibin
 * @version 1.0
 * @since 2020/11/2 15:52
 */
@RestController
public class UserController {

    private Logger log = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    /**
     * 注册
     *
     * @param registerBody
     * @return
     * @login no
     */
    @PostMapping("register")
    public R register(@RequestBody RegisterBody registerBody) {
        userService.register(registerBody);

        return R.ok();
    }

    /**
     * 发送注册验证码
     *
     * @param phone 手机号
     * @return R
     */
    @GetMapping("/registerVerifyCode")
    public R sendVerifyCode(@RequestParam("phone") String phone) {
        userService.sendVerifyCode(phone);
        return R.ok();
    }

    /**
     * 昵称是否可用
     *
     * @param nickname 用户昵称
     * @return R
     */
    @GetMapping("nickname/available")
    public R checkNicknameAvailable(@RequestParam("nickname") String nickname) {
        userService.checkNicknameAvailable(nickname);
        return R.ok();

    }
    /**
     * 获取用户信息
     * @return
     */
    @GetMapping("userInfo")
    public R getUserInfo() {

        UserInfo userInfo = userService.getUserInfo();

        return R.ok().data(userInfo);
    }
}
