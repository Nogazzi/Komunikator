package UnitTests;

import Serwer.Users.User;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by Nogaz on 20.01.2017.
 */
public class UserTest {

    @Test
    public void userInChannelTest(){
        User user = new User();
        String channel = "newChannel";
        Assert.assertEquals(false, user.inChannel(channel));
        user.joinToChannel(channel);
        Assert.assertEquals(true, user.inChannel(channel));
    }
}
