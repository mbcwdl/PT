package com.playtogether.authcenter.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

/**
 * @author guanlibin
 * @version 1.0
 * @since 2020/11/13 17:58
 */
public class CookieUtils {

    public static class CookieBuilder {
        private String domain;
        private int maxAge;
        private String path;
        private boolean httpOnly;

        public CookieBuilder() {
        }

        public CookieBuilder domain(String domain) {
            this.domain = domain;
            return this;
        }

        public CookieBuilder maxAge(int maxAge) {
            this.maxAge = maxAge;
            return this;
        }
        public CookieBuilder path(String path) {
            this.path = path;
            return this;
        }

        public CookieBuilder httpOnly(boolean httpOnly) {
            this.httpOnly = httpOnly;
            return this;
        }

        public Cookie build(String name, String value) {
            Cookie cookie = new Cookie(name, value);
            if (domain != null) {
                cookie.setDomain(domain);
            }
            if (path != null) {
                cookie.setPath(path);
            }
            cookie.setMaxAge(maxAge);
            cookie.setHttpOnly(httpOnly);

            return cookie;
        }


    }

    public static CookieBuilder builder() {
        return new CookieBuilder();
    }



}
