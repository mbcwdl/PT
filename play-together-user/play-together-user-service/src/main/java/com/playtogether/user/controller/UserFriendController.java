package com.playtogether.user.controller;

import com.playtogether.common.vo.R;
import com.playtogether.user.pojo.User;
import com.playtogether.user.service.UserFriendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author guanlibin
 * @version 1.0
 * @since 2020/11/14 19:16
 */
@RestController
public class UserFriendController {

    @Autowired
    private UserFriendService userFriendService;

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
}
