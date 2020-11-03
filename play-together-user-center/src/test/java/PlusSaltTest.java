import com.playtogether.usercenter.util.SafeUtils;
import org.junit.Test;

/**
 * @author guanlibin
 * @version 1.0
 * @since 2020/11/2 19:10
 */
public class PlusSaltTest {

    @Test
    public void test () {
        String s1 = SafeUtils.salt();
        System.out.println("s1 = " + s1);
        String password = SafeUtils.MD5WithSalt("123456", s1);

        System.out.println("password = " + password);

        String s2 = SafeUtils.getSaltFromHash(password);
        System.out.println("s2 = " + s2);

    }

}
