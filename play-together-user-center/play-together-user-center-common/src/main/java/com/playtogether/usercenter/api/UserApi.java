package com.playtogether.usercenter.api;

import com.playtogether.common.vo.R;
import com.playtogether.usercenter.pojo.User;
import org.springframework.web.bind.annotation.*;

/** 维护供其他微服务调用的接口
 * @author guanlibin
 * @version 1.0
 * @since 2020/11/3 10:45
 */
public interface UserApi {

    @GetMapping("query/accountAndPassword")
    R queryUserByAccountAndPassword(
            @RequestParam(value = "account", required = false) String account,
            @RequestParam(value = "password") String password);

    @PostMapping("count")
    R getCountByUser(@RequestBody User user);

    @GetMapping("query/qqOpenId")
    R queryUserByQqOpenId(@RequestParam("qqOpenId") String qqOpenId);

    @PutMapping("update")
    R updateById(@RequestBody User user);
}
