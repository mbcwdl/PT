package com.playtogether.authcenter.controller;

import com.playtogether.authcenter.service.AuthService;
import com.playtogether.common.vo.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author guanlibin
 * @version 1.0
 * @since 2020/11/3 9:54
 */
@RestController
@RequestMapping("auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     *
     * @param action 登录方式  手机号+验证码 或 手机号+密码
     * @param phone
     * @param password
     * @param verifyCode
     * @return
     */
    @PostMapping("login")
    public R login (
            @RequestParam("action") String action,
            @RequestParam("phone") String phone,
            @RequestParam("password") String password,
            @RequestParam("verifyCode") String verifyCode) {

        return R.ok().data(authService.login(action, phone, password, verifyCode));
    }
}
