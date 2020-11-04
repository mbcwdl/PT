import org.junit.Test;

/**
 * @author guanlibin
 * @version 1.0
 * @since 2020/11/4 15:03
 */
public class GUANGUAN {

    @Test
    public void t() {
        String phone = "25252062654";
        boolean matches = phone.matches("^1[3456789]\\d{9}$");
        System.out.println("matches = " + matches);
    }
}
