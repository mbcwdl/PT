package com.playtogether.user.service;

import com.playtogether.common.enums.PtCommonEnums;
import com.playtogether.common.exception.PtException;
import com.playtogether.user.mapper.UserFriendMapper;
import com.playtogether.user.mapper.UserMapper;
import com.playtogether.user.pojo.User;
import com.playtogether.user.pojo.UserFriend;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author guanlibin
 * @version 1.0
 * @since 2020/11/6 16:16
 */
@Slf4j
@Service
public class UserFriendService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserFriendMapper baseMapper;

    @Transactional(rollbackFor = Exception.class)
    public void insert(Integer userId, Integer friendId) {
        // 1. 检查有没有这两个用户
        User user = new User();
        user.setId(userId);
        int count = userMapper.selectCountByUser(user);
        if (count != 1) {
            throw new PtException(400, "[用户不存在]用户id:" + userId);
        }
        user.setId(friendId);
        count = userMapper.selectCountByUser(user);
        if (count != 1) {
            throw new PtException(400, "[好友不存在]好友id:" + friendId);
        }
        // 2. 判断是不是已经互为好友了
        int rows = baseMapper.selectCountByUidAndFid(userId, friendId);
        if (rows > 0) {
            throw new PtException(400, "[好友已存在]用户id:" + userId);
        }
        //  尝试修改deleted字段来完成好友添加（之前加过好友，然后删了（逻辑删除），所以数据库还存在记录，我们直接将deleted置为false即可）
        count = baseMapper.updateDeletedByUidAndFid(userId, friendId, false);
        UserFriend userFriend = null;
        if (count != 1) {
            // 如果数据库中没记录(即之前没加过这个人），那么就进行记录新增
            userFriend = new UserFriend();
            userFriend.setUserId(userId);
            userFriend.setFriendId(friendId);
            count = baseMapper.insert(userFriend);
            if (count != 1) {
                throw new PtException(PtCommonEnums.SERVER_ERROR);
            }
        }
        // 互加好友
        count = baseMapper.updateDeletedByUidAndFid(friendId, userId, false);
        if (count != 1) {
            if (userFriend == null) {
                userFriend = new UserFriend();
            };
            userFriend.setUserId(friendId);
            userFriend.setFriendId(userId);
            count = baseMapper.insert(userFriend);
            if (count != 1) {
                throw new PtException(PtCommonEnums.SERVER_ERROR);
            }
        }
    }

    public List<User> getFriends(Integer userId) {
        return baseMapper.selectFriendList(userId);
    }

    public void delFriend(Integer userId, Integer friendId) {
        baseMapper.delByUidAndFid(userId, friendId);
        baseMapper.delByUidAndFid(friendId, userId);
    }
}
