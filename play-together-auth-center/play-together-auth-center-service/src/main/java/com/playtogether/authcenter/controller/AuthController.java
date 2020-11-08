package com.playtogether.authcenter.controller;

import cn.hutool.json.JSONObject;
import com.playtogether.authcenter.service.AuthService;
import com.playtogether.authcenter.util.QqHttpClient;
import com.playtogether.authcenter.vo.LoginBody;
import com.playtogether.common.vo.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author guanlibin
 * @version 1.0
 * @since 2020/11/3 9:54
 */
@Slf4j
@RestController
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     *
     * 登录
     * @return
     */
    @PostMapping("login")
    public R login (@RequestBody LoginBody loginBody,
                    HttpServletRequest req, HttpServletResponse resp) {

        String token = authService.login(
                loginBody.getAction(),
                loginBody.getAccount(),
                loginBody.getPassword(),
                loginBody.getVerifyCode());

        return R.ok().data(token).message("登录成功");
    }

    /**
     * 发送登录验证码
     * @param phone 手机号
     * @return R
     */
    @GetMapping("/loginVerifyCode/{phone}")
    public R sendLoginVerifyCode(@PathVariable("phone") String phone) {
        authService.sendLoginVerifyCode(phone);
        return R.ok().message("登录验证码发送成功");
    }

    /**
     * 请求授权地址
     */
    @GetMapping("qqlogin")
    public String  qqAuth (HttpSession session) {
        String url = authService.qqAuth(session);
        return url;
    }

    /**
     * 授权回调
     */
    @GetMapping("qqcb")
    public String qqCallback (HttpServletRequest req) throws IOException {
        String info = authService.qqCallback(req);
        return info;
    }
}
