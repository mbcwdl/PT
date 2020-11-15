package com.playtogether.common.inteceptor;

import com.playtogether.common.payload.JwtPayload;
import com.playtogether.common.util.JwtUtils;
import com.playtogether.common.util.RsaUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.PublicKey;

/** 对微服务间调用的token进行校验
 * @author guanlibin
 * @version 1.0
 * @since 2020/11/14 16:37
 */
public class PtMicroServiceAuthInterceptor implements HandlerInterceptor {
    /**
     * 一次请求多个类共享payload
     */
    public static final ThreadLocal<JwtPayload> tl = new ThreadLocal<>();

    private PublicKey publicKey;

    public PtMicroServiceAuthInterceptor(PublicKey publicKey) {
        this.publicKey = publicKey;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String token = request.getHeader("MicroServiceAuthToken");
        try {
            JwtPayload payload = JwtUtils.getInfoFromToken(token, publicKey);
            tl.set(payload);
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        tl.remove();
    }
}
