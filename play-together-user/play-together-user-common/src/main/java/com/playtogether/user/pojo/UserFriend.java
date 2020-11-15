package com.playtogether.user.pojo;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/** 用户好友表
 * @author guanlibin
 * @version 1.0
 * @since 2020/11/6 16:00
 */
@Getter
@Setter
public class UserFriend {
    private Integer id;
    private Integer userId;
    private Integer friendId;
    private Boolean deleted;
    private Date gmtCreated;
    private Date gmtModified;
}
