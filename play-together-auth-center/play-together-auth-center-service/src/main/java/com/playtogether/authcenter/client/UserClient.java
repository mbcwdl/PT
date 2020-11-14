package com.playtogether.authcenter.client;

import com.playtogether.authcenter.config.FeignConfiguration;
import com.playtogether.usercenter.api.UserApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author guanlibin
 * @version 1.0
 * @since 2020/11/3 10:50
 */
@FeignClient(name = "play-together-user-center", configuration = FeignConfiguration.class)
public interface UserClient extends UserApi {
}
