package com.playtogether.user.mapper;

import com.playtogether.user.pojo.User;
import org.springframework.stereotype.Repository;

/**
 * @author guanlibin
 * @version 1.0
 * @since 2020/11/2 15:53
 */
@Repository
public interface UserMapper {

    int selectCountByUser(User user);

    int save(User user);

    User selectSingleByUser(User user);

    int updateUserById(User user);
}
