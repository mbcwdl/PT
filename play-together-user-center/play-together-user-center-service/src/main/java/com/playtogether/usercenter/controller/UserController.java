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
     *
     * @param phone
     * @param password
     * @return
     * @login no
     */
    @GetMapping("query/accountAndPassword")
    public R queryUserByAccountAndPassword(
            @RequestParam(value = "phone", required = false) String phone,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "password") String password) {

        return R.ok().data(userService.queryUserByAccountAndPassword(phone, email, password));
    }

    @PostMapping("friend")
    public R addFriend(
            @RequestParam("userId") Integer userId,
            @RequestParam("friendId") Integer friendId) {

        userFriendService.insert(userId, friendId);

        return R.ok().message(String.format("[添加好友成功]用户id:%s,好友id:%s", userId, friendId));
    }

    @GetMapping("{userId}/friend")
    public R getFriends(@PathVariable("userId") Integer userId) {
        List<User> friends = userFriendService.getFriends(userId);

        return R.ok().message("[成功获取好友列表]用户id:" + userId).data(friends);
    }

    @DeleteMapping("{userId}/friend/{friendId}")
    public R delFriend(
            @PathVariable("userId") Integer userId,
            @PathVariable("friendId") Integer friendId) {

        userFriendService.delFriend(userId, friendId);

        return R.ok().message(String.format("[成功删除好友]用户id:%s,好友id:%s", userId, friendId));

    }
}
