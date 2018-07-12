package fit.pay2play.web.manager;

import xyz.cleangone.data.aws.dynamo.entity.organization.Organization;
import xyz.cleangone.data.aws.dynamo.entity.person.User;
import xyz.cleangone.data.aws.dynamo.entity.person.UserToken;
import xyz.cleangone.data.manager.EventManager;
import xyz.cleangone.data.manager.OrgManager;
import xyz.cleangone.data.manager.UserManager;

import java.util.*;

/**
 * A session conststs of:
 * - a userManager if user has logged in
 * - an orgManager that controls the current org, which was set by direct login or the user
 *   selecting one of their orgs
 * - a cartManager that contains the cart for the current org
 */
public class SessionManager
{
    private UserManager userMgr = new UserManager();
    private OrgManager orgMgr = new OrgManager();
    private EventManager eventMgr = new EventManager();

    private String url;
    private boolean isMobileBrowser;
    private String navToAfterLogin;
    private String msg;

    public void reset()
    {
        userMgr.logout();
    }

    public String getUrl(String paramName, UserToken token)
    {
        return getUrl(paramName, token.getId());
    }
    public String getUrl(String paramName, String paramValue)
    {
        return url + "?" + paramName + "=" + paramValue;
    }
    public String getUrl()
    {
        return url;
    }
    public void setUrl(String url)
    {
        this.url = url;
    }

    public OrgManager getOrgManager()
    {
        return orgMgr;
    }
    public boolean hasOrg()
    {
        return (orgMgr.getOrg() != null);
    }
    public Organization getOrg()
    {
        return orgMgr.getOrg();
    }
    public void setOrg(Organization org)
    {
        orgMgr.setOrg(org);
    }

    public void resetOrg()
    {
        orgMgr.setOrg(null);
    }


    //
    // Events
    //
    public EventManager getEventManager()
    {
        if (eventMgr.getOrg() == null) { resetEventManager(); }
        return eventMgr;
    }
    public void resetEventManager()
    {
        if (orgMgr.getOrg() != null) { eventMgr.setOrg(orgMgr.getOrg()); }
    }


    //
    // Users
    //
    public UserManager getUserManager()
    {
        return userMgr;
    }
    public UserManager getPopulatedUserManager()
    {
        if (!hasUser()) { throw new IllegalStateException("User not set"); }
        return userMgr;
    }

    public User getUser() { return (userMgr.getUser()); }
    public boolean hasUser()
    {
        return (getUser() != null);
    }
    public boolean hasSuperUser()
    {
        return (hasUser() && getUser().isSuperAdmin());
    }

    public String getNavToAfterLogin(String defaultPage)
    {
        String navTo = navToAfterLogin == null ? defaultPage : navToAfterLogin;
        navToAfterLogin = null;
        return navTo;
    }
    public void setNavToAfterLogin(String navToAfterLogin)
    {
        this.navToAfterLogin = navToAfterLogin;
    }


    public String getMsg()
    {
        return msg;
    }
    public void setMsg(String msg)
    {
        this.msg = msg;
    }
    public void resetMsg()
    {
        msg = null;
    }

    public boolean isMobileBrowser()
    {
        return isMobileBrowser;
    }
    public void setIsMobileBrowser(boolean mobileBrowser)
    {
        isMobileBrowser = mobileBrowser;
    }
}