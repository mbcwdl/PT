package com.playtogether.usercenter.controller;

import com.playtogether.common.vo.R;
import com.playtogether.usercenter.pojo.User;
import com.playtogether.usercenter.pojo.UserFriend;
import com.playtogether.usercenter.service.UserFriendService;
import com.playtogether.usercenter.service.UserService;
import com.playtogether.usercenter.vo.RegisterBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author guanlibin
 * @version 1.0
 * @since 2020/11/2 15:52
 */
@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserFriendService userFriendService;

    /**
     * 昵称是否可用
     * @param nickname 用户昵称
     * @return R
     * @login no
     */
    @GetMapping("nickname/available")
    public R checkNicknameAvailable(@RequestParam("nickname") String nickname) {
        userService.checkNicknameAvailable(nickname);
        return R.ok();
    }

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
     * 发送注册验证码
     * @param phone 手机号
     * @return R
     */
    @GetMapping("/registerVerifyCode")
    public R sendVerifyCode(@RequestParam("phone") String phone) {
        userService.sendVerifyCode(phone);
        return R.ok().message("注册验证码发送成功");
    }

    /**
     * 注册
     * @param registerBody
     * @return
     * @login no
     */
    @PostMapping("register")
    public R register(@RequestBody RegisterBody registerBody) {
        userService.register(registerBody);

        return R.ok().message("用户注册成功");
    }

    /**
     * [查询用户信息]查询条件：手机号+密码 或者 邮箱+密码
     * @param account 账号
     * @param password 密码
     * @return
     * @login no
     */
    @GetMapping("query/accountAndPassword")
    public R queryUserByAccountAndPassword(
            @RequestParam(value = "account", required = false) String account,
            @RequestParam(value = "password") String password) {

        return R.ok().data(userService.queryUserByAccountAndPassword(account, password));
    }

    /**
     * [新增好友]
     * @param userId
     * @param friendId
     * @return
     */
    @PostMapping("friend")
    public R addFriend(
            @RequestParam("userId") Integer userId,
            @RequestParam("friendId") Integer friendId) {

        userFriendService.insert(userId, friendId);

        return R.ok().message(String.format("[添加好友成功]用户id:%s,好友id:%s", userId, friendId));
    }

    /**
     * [获取好友]
     * @param userId
     * @return
     */
    @GetMapping("{userId}/friend")
    public R getFriends(@PathVariable("userId") Integer userId) {
        List<User> friends = userFriendService.getFriends(userId);

        return R.ok().message("[成功获取好友列表]用户id:" + userId).data(friends);
    }

    /**
     * [删除好友]
     * @param userId
     * @param friendId
     * @return
     */
    @DeleteMapping("{userId}/friend/{friendId}")
    public R delFriend(
            @PathVariable("userId") Integer userId,
            @PathVariable("friendId") Integer friendId) {

        userFriendService.delFriend(userId, friendId);

        return R.ok().message(String.format("[成功删除好友]用户id:%s,好友id:%s", userId, friendId));

    }

    /**
     * 根据qq的openId查询用户信息
     * @param qqOpenId
     * @return
     */
    @GetMapping("query/qqOpenId")
    public R queryUserByQqOpenId(@RequestParam("qqOpenId") String qqOpenId) {
        return R.ok().data(userService.queryUserByQqOpenId(qqOpenId));
    }

    @PutMapping("update")
    public R updateById(@RequestBody User user) {
        userService.updateById(user);
        return R.ok();
    }
}
