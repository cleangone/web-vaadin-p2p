package fit.pay2play;

import fit.pay2play.data.manager.Pay2PlayManager;
import xyz.cleangone.data.aws.dynamo.entity.organization.Organization;
import xyz.cleangone.data.aws.dynamo.entity.person.AdminPrivledge;
import xyz.cleangone.data.aws.dynamo.entity.person.User;
import xyz.cleangone.data.manager.OrgManager;
import xyz.cleangone.data.manager.UserManager;
import xyz.cleangone.util.env.EnvManager;
import xyz.cleangone.util.env.P2pEnv;

import java.util.List;

public class Seed
{
    static { EnvManager.setEnv(new P2pEnv()); }

    OrgManager orgMgr = new OrgManager();
    UserManager userMgr = new UserManager();
    Pay2PlayManager p2pMgr = new Pay2PlayManager();

    public static void main(String[] args)
    {
        Seed seed = new Seed();

//        seed.seedOrg();
//        seed.seedUser();
    }

    private void seedOrg()
    {
        Organization org = new Organization("Pay2Play Default");
        org.setTag(EnvManager.getEnv().getDefaultOrg());

        orgMgr.save(org);
    }

    private void seedUser()
    {
        User user = new User();
        user.setEmail("blah.com");
        user.setPassword("blah");
        userMgr.save(user);
    }






}
