package com.playtogether.gateway.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import com.playtogether.common.exception.PTException;
import com.playtogether.common.payload.JwtPayload;
import com.playtogether.common.util.JwtUtils;
import com.playtogether.gateway.config.AuthFilterProperties;
import com.playtogether.gateway.exception.PTZuulException;
import org.bouncycastle.jcajce.provider.asymmetric.rsa.RSAUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author guanlibin
 * @version 1.0
 * @since 2020/11/14 8:35
 */
@Component
@EnableConfigurationProperties({AuthFilterProperties.class})
public class AuthFilter extends ZuulFilter {

    private Logger log = LoggerFactory.getLogger(ZuulFilter.class);

    private static final String DISTRIBUTE_SESSION_PREFIX = "pt:auth:token:";

    @Autowired
    private AuthFilterProperties prop;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return FilterConstants.PRE_DECORATION_FILTER_ORDER - 1;
    }

    @Override
    public boolean shouldFilter() {
        RequestContext ctx = RequestContext.getCurrentContext();

        String uri = ctx.getRequest().getRequestURI();

        List<String> allowPathList = prop.getAllowPathList();

        // todo 优化
        for (String allowPath : allowPathList) {
            if (uri.startsWith(allowPath)) {
                // 将微服务之间调用的token添加到请求头中
                try {
                    JwtPayload payload = new JwtPayload();
                    ctx.addZuulRequestHeader("Authorization", JwtUtils.generateToken(payload, prop.getMicroServicePrivateKey(), 1));
                } catch (Exception e) {
                    log.info("网关异常", e);
                }
                return false;
            }
        }
        return true;
    }

    @Override
    public Object run() throws ZuulException {
        // 获取当前的请求上下文对象
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest req = ctx.getRequest();
        String uri = req.getRequestURI();
        // 禁止访问接口过滤
        for (String forbiddenPath : prop.getForbiddenPathList()) {
            if (uri.equals(forbiddenPath)) {
                throw new PTZuulException(HttpStatus.FORBIDDEN.value(), "您无权访问此接口");
            }
        }
        // 拿出存放token的Cookie
        Cookie[] cookies = req.getCookies();
        Cookie c = null;
        for (Cookie cookie : cookies == null ? new Cookie[0] : cookies) {
            if ("Authorization".equals(cookie.getName())) {
                c = cookie;
                break;
            }
        }
        if (c == null) {
            throw new PTZuulException(HttpStatus.UNAUTHORIZED.value(), "您还未登录");
        }
        // 验证token
        String token = c.getValue();
        JwtPayload payload;
        String id;
        try {
            payload = JwtUtils.getInfoFromToken(token, prop.getAuthCenterPublicKey());
            id = payload.getId().toString();
            String uuid = payload.getUuid();
            log.info("id:{},uuid:{}", id, uuid);
            // [redis] => id,uuid => compare
            String uuidInRedis = stringRedisTemplate.opsForValue().get(DISTRIBUTE_SESSION_PREFIX + id);
            log.info("uuidInRedis:{}", uuidInRedis);
            if (!uuid.equals(uuidInRedis)) {
                throw new PTZuulException(HttpStatus.UNAUTHORIZED.value(), "您的登录已过期，请重新登录");
            }
        } catch (Exception e) {
            throw new PTZuulException(HttpStatus.UNAUTHORIZED.value(), "您的登录已过期，请重新登录");
        }
        payload.setUuid(null);
        // 将微服务之间调用的token添加到请求头中(此token额外包含用户id)
        try {
            ctx.addZuulRequestHeader("Authorization", JwtUtils.generateToken(payload, prop.getMicroServicePrivateKey(), 1));
        } catch (Exception e) {
            throw new PTZuulException(500, "网关异常");
        }
        return null;
    }
}