package com.playtogether.user.mapper;

import com.playtogether.user.pojo.User;
import com.playtogether.user.pojo.UserFriend;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author guanlibin
 * @version 1.0
 * @since 2020/11/6 16:04
 */
@Repository
public interface UserFriendMapper {

    int insert(UserFriend userFriend);

    List<User> selectFriendList(Integer userId);

    int updateDeletedByUidAndFid(@Param("uid") Integer uid, @Param("fid") Integer fid, @Param("deleted") Boolean deleted);

    int selectCountByUidAndFid(@Param("uid") Integer uid, @Param("fid") Integer fid);

    void delByUidAndFid(@Param("uid") Integer uid, @Param("fid") Integer fid);
}
