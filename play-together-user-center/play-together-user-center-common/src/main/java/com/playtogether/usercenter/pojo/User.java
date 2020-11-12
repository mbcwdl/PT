package com.playtogether.usercenter.pojo;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @author guanlibin
 * @version 1.0
 * @since 2020/11/2 16:23
 */
@Data
public class User {
    private Integer id;
    private String avatar;
    private String nickname;
    private String password;
    private String phone;
    private String email;
    private String qqOpenId;
    private Boolean deleted;
    private Date gmtCreated;
    private Date gmtModified;

}
