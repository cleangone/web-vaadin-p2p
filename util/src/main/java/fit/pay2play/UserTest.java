package fit.pay2play;

import xyz.cleangone.data.aws.dynamo.entity.person.User;
import xyz.cleangone.data.manager.UserManager;
import xyz.cleangone.util.env.EnvManager;
import xyz.cleangone.util.env.P2pEnv;

import java.util.List;

public class UserTest
{
    static
    {
        EnvManager.setEnv(new P2pEnv());
    }

    UserManager userMgr = new UserManager();

    public static void main(String[] args)
    {
        UserTest test = new UserTest();
        UserManager userMgr = test.userMgr;


        List<User> users = userMgr.getUsers();


//        User user = new User();
//        user.setEmail("andy_robbins@yahoo.com");
//        user.setPassword("andy");
//
//        userMgr.save(user);

        int i=1;
    }

}
