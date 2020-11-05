package com.playtogether.authcenter.config;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author guanlibin
 * @version 1.0
 * @since 2020/11/5 20:06
 */
@Setter
@Getter
@ConfigurationProperties(prefix = "qq.oauth")
public class QqProperties {
    private String appId;
    private String appKey;
    private String cbUrl;
    private String authorizationCodeUrl;
    private String accessTokenUrl;
    private String openIdUrl;
    private String userInfoUrl;
}
