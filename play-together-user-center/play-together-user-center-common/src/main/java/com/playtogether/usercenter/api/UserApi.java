package com.playtogether.usercenter.api;

import com.playtogether.common.vo.R;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

/** 维护供其他微服务调用的接口
 * @author guanlibin
 * @version 1.0
 * @since 2020/11/3 10:45
 */
public interface UserApi {

    @GetMapping("query/accountAndPassword")
    R queryUserByAccountAndPassword(
            @RequestParam("phone") String phone,
            @RequestParam("password") String password);
}
