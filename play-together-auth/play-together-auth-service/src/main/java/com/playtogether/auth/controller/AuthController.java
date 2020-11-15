package com.playtogether.auth.controller;

import com.playtogether.auth.config.JwtProperties;
import com.playtogether.auth.service.AuthService;
import com.playtogether.auth.util.CookieUtils;
import com.playtogether.auth.vo.LoginBody;
import com.playtogether.common.vo.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

    @Autowired
    private JwtProperties jwtProp;

    /**
     * 登录cookie的过期时间
     */
    private static final int LOGIN_TOKEN_EXPIRE = -1;

    /**
     * 登录cookie的name
     */
    private static final String LOGIN_TOKEN_NAME = "Authorization";

    /**
     * 登录
     * @return
     */
    @PostMapping("login")
    public R login (@RequestBody LoginBody loginBody,
                    HttpServletRequest req, HttpServletResponse resp) {

        String token = authService.login(loginBody);

        addCookie(token, loginBody.isRememberMe(), req, resp);

        return R.ok().message("登录成功");
    }

    private void addCookie(String token, boolean rememberMe, HttpServletRequest request, HttpServletResponse response) {
        CookieUtils.newBuilder(response)
                .request(request)
                .httpOnly()
                .maxAge((rememberMe ? jwtProp.getPermanentExpire() : jwtProp.getTemporaryExpire()) * 60)
                .build(LOGIN_TOKEN_NAME, token);
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
    @GetMapping("qqLogin")
    public R qqLogin () {
        return R.ok().data(authService.qqLogin());
    }

    /**
     * 授权回调
     */
    @GetMapping("qqLoginCallback")
    public R qqLoginCallback (@RequestParam("code") String code,
                         @RequestParam("state") String state) throws Exception {
        return authService.qqLoginCallback(code, state);
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
                       @RequestParam("rememberMe") Boolean rememberMe,
                       HttpServletResponse resp) throws Exception {
        String token = authService.qqBinding(accessToken, openId, account, password);

        return R.ok().data(token);
    }

}
