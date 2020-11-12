import com.playtogether.usercenter.UserCenterApplication;
import com.playtogether.usercenter.mapper.UserMapper;
import com.playtogether.usercenter.pojo.User;
import com.playtogether.usercenter.util.SafeUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

/**
 * @author guanlibin
 * @version 1.0
 * @since 2020/11/2 19:10
 */
@SpringBootTest(classes = UserCenterApplication.class)
@RunWith(SpringRunner.class)
public class PlusSaltTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    public void test () {
        User user = new User();
        user.setId(3);
        user.setQqOpenId("sdsjldslkjsldsjks");
        user.setGmtModified(new Date());

        int count = userMapper.updateUserById(user);

        System.out.println("count = " + count);


    }

}
