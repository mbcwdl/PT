package com.playtogether.authcenter.controller;

import com.playtogether.authcenter.service.AuthService;
import com.playtogether.authcenter.util.CookieUtils;
import com.playtogether.authcenter.vo.LoginBody;
import com.playtogether.common.vo.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

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
     * 登录
     * @return
     */
    @PostMapping("login")
    public R login (@RequestBody LoginBody loginBody,
                    HttpServletRequest req, HttpServletResponse resp) {

        String token = authService.login(loginBody);

        resp.addCookie(getCookie(token));


        return R.ok().data(token).message("登录成功");
    }

    private Cookie getCookie(String token) {
        return CookieUtils.builder()
                .domain("mbcwdl.space")
                .maxAge(-1)
                .path("/")
                .httpOnly(false)
                .build("Authorization", token);
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
    public R qqAuth (HttpSession session) {
        return R.ok().data(authService.qqAuth(session));
    }

    /**
     * 授权回调
     */
    @GetMapping("qqcb")
    public R qqCallback (@RequestParam("code") String code,
                         @RequestParam("state") String state) throws Exception {
        return authService.qqCallback(code, state);
    }

    /**
     * qq和账号绑定
     * @param accessToken
     * @param openId
     * @param account
     * @param password
     * @return
     * @throws Exception
     */
    @GetMapping("qqBinding")
    public R qqBinding(@RequestParam("accessToken") String accessToken,
                       @RequestParam("openId") String openId,
                       @RequestParam("account") String account,
                       @RequestParam("password") String password,
                       @RequestParam("rememberMe") String rememberMe,
                       HttpServletResponse resp) throws Exception {
        String token = authService.qqBinding(accessToken, openId, account, password);

        return R.ok().data(token);
    }

}
