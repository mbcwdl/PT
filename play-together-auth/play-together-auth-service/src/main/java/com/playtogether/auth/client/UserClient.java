package com.playtogether.auth.client;

import com.playtogether.auth.config.FeignConfiguration;
import com.playtogether.user.api.UserApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author guanlibin
 * @version 1.0
 * @since 2020/11/3 10:50
 */
@FeignClient(name = "play-together-user-center", configuration = FeignConfiguration.class)
public interface UserClient extends UserApi {
}
