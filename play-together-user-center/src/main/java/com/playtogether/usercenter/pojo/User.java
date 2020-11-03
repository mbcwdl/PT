package com.playtogether.usercenter.pojo;

import lombok.Data;

import java.util.Date;

/**
 * @author guanlibin
 * @version 1.0
 * @since 2020/11/2 16:23
 */
@Data
public class User {
    private Integer id;
    private String nickname;
    private String password;
    private String phone;
    private String qq;
    private String wx;
    private Boolean deleted;
    private Date gmtCreated;
    private Date gmtModified;

}
