package com.playtogether.user.controller;

import com.playtogether.common.vo.R;
import com.playtogether.user.pojo.User;
import com.playtogether.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**提供其他微服务使用的接口
 * @author guanlibin
 * @version 1.0
 * @since 2020/11/14 19:08
 */
@RestController
public class FeignController {

    @Autowired
    private UserService userService;

    /**
     * 根据User中的非空字段组成where子句的查询条件，查询记录数
     * @param user
     * @return
     */
    @PostMapping("count")
    public R getCountByUser(@RequestBody User user) {
        return R.ok().data(userService.getCountByUser(user));
    }

    /**
     * [查询用户id]查询条件：手机号+密码 或者 邮箱+密码
     * @param account 账号
     * @param password 密码
     * @return
     */
    @GetMapping("query/accountAndPassword")
    public R queryUserByAccountAndPassword(
            @RequestParam(value = "account", required = false) String account,
            @RequestParam(value = "password") String password) {

        return R.ok().data(userService.queryUserByAccountAndPassword(account, password));
    }

    /**
     * 根据qq的openId查询用户信息
     * @param qqOpenId
     * @return
     */
    @GetMapping("uid/qqOpenId")
    public R queryUserIdByQqOpenId(@RequestParam("qqOpenId") String qqOpenId) {
        return R.ok().data(userService.queryUserIdByQqOpenId(qqOpenId));
    }

    /**
     * 更新用户信息
     * @param user
     * @return
     */
    @PutMapping("update")
    public R updateById(@RequestBody User user) {
        userService.updateById(user);
        return R.ok();
    }

}
