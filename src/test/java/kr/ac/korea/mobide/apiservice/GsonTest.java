package kr.ac.korea.mobide.apiservice;

import kr.ac.korea.mobide.apiservice.domain.user.Role;
import kr.ac.korea.mobide.apiservice.domain.user.User;
import org.junit.Test;

/**
 * Created by Koo Lee on 2014-08-30.
 */
public class GsonTest {

    @Test
    public void test() {
        User user = new User();
        user.addRole(Role.USER);
        System.out.println(user.getRoles());
        user.addRole(Role.ADMIN);
        System.out.println(user.getRoles());
        user.removeRole(Role.ADMIN);
        System.out.println(user.getRoles());
    }
}
