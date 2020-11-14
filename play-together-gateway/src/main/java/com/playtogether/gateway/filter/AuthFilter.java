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

        String uri = RequestContext.getCurrentContext().getRequest().getRequestURI();

        List<String> allowPathList = prop.getAllowPathList();

        // todo 优化
        for (String allowPath : allowPathList) {
            if (uri.startsWith(allowPath)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Object run() throws ZuulException {
        // 获取当前的请求上下文对象
        RequestContext ctx = RequestContext.getCurrentContext();
        // 获取当前请求
        HttpServletRequest req = ctx.getRequest();
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
            throw new PTZuulException(HttpStatus.UNAUTHORIZED.value(), "用户未登录");
        }
        // 验证token
        String token = c.getValue();
        try {
            JwtPayload payload = JwtUtils.getInfoFromToken(token, prop.getAuthCenterPublicKey());
            Integer id = payload.getId();
            String uuid = payload.getUuid();
            log.info("id:{},uuid:{}", id, uuid);
            // [redis] => id,uuid => compare
            String uuidInRedis = stringRedisTemplate.opsForValue().get(DISTRIBUTE_SESSION_PREFIX + id.toString());
            log.info("uuidInRedis:{}", uuidInRedis);
            if (!uuid.equals(uuidInRedis)) {
                throw new PTZuulException(HttpStatus.UNAUTHORIZED.value(), "无效的登录状态");
            }
            // todo 生成微服务调用token，放入请求头
        } catch (Exception e) {
            throw new PTZuulException(HttpStatus.UNAUTHORIZED.value(), "无效的登录状态");
        }

        return null;
    }
}