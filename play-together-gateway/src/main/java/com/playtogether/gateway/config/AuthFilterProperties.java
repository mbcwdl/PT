package com.playtogether.gateway.config;

import com.playtogether.common.util.JwtUtils;
import com.playtogether.common.util.RsaUtils;
import lombok.Getter;
import lombok.Setter;
import org.bouncycastle.jcajce.provider.asymmetric.rsa.RSAUtil;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.PostConstruct;
import java.io.File;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Arrays;
import java.util.List;

/**
 * @author guanlibin
 * @version 1.0
 * @since 2020/11/14 9:21
 */
@Getter
@Setter
@ConfigurationProperties("pt.filter.auth")
public class AuthFilterProperties {

    /**
     * 接口白名单
     */
    private List<String> allowPathList;

    /**
     * 认证中心公钥
     */
    private String authCenterPublicKeyPath;
    private PublicKey authCenterPublicKey;

    /**
     * 认证中心私钥
     */
    private String authCenterPrivateKeyPath;
    private PrivateKey authCenterPrivateKey;

    /**
     * 微服务之间调用使用的公钥
     */
    private String microServicePublicKeyPath;
    private PublicKey microServicePublicKey;

    /**
     * 微服务之间调用使用的私钥
     */
    private String microServicePrivateKeyPath;
    private PrivateKey microServicePrivateKey;

    public void setAllowPath(List<String> allowPathList) {
        this.allowPathList = allowPathList;
    }

    @PostConstruct
    public void init () throws Exception {
        // 首先看服务器本地有没有密钥，没有即抛出异常
        File mcPubKeyFile = new File(microServicePublicKeyPath);
        File mcPriKeyFile = new File(microServicePrivateKeyPath);
        File acPubKeyFile = new File(authCenterPublicKeyPath);
        File acPriKeyFile = new File(authCenterPrivateKeyPath);

        if (!mcPriKeyFile.exists()
         || !mcPubKeyFile.exists()
         || !acPriKeyFile.exists()
         || !acPubKeyFile.exists()) {
            throw new Exception("密钥不存在");
        }
        // 读取密钥
        microServicePublicKey = RsaUtils.getPublicKey(microServicePublicKeyPath);
        microServicePrivateKey = RsaUtils.getPrivateKey(microServicePrivateKeyPath);
        authCenterPublicKey = RsaUtils.getPublicKey(authCenterPublicKeyPath);
        authCenterPrivateKey = RsaUtils.getPrivateKey(authCenterPrivateKeyPath);
    }
}
