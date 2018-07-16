package xyz.cleangone.web.manager;

import com.vaadin.navigator.Navigator;
import xyz.cleangone.data.aws.dynamo.entity.action.Action;
import xyz.cleangone.data.aws.dynamo.entity.organization.Organization;
import xyz.cleangone.data.aws.dynamo.entity.person.User;
import xyz.cleangone.data.aws.dynamo.entity.person.UserToken;
import xyz.cleangone.data.manager.OrgManager;
import xyz.cleangone.data.manager.UserManager;

import java.util.*;

/**
 * A session conststs of:
 * - a userManager if user has logged in
 * - an orgManager that controls the current org, which was set by direct login or the user
 *   selecting one of their orgs
 */
public class SessionManager
{
    private UserManager userMgr = new UserManager();
    private OrgManager orgMgr = new OrgManager();

    private String url;
    private boolean isMobileBrowser;
    private String initialOrgTag;
    private String navToAfterLogin;
    private String msg;
    private Action currentAction;

    public void reset()
    {
        userMgr.logout();
        resetOrg();
    }

    public String getInitialOrgTag()
    {
        return initialOrgTag;
    }
    public void setInitialOrgTag(String initialOrgTag)
    {
        this.initialOrgTag = initialOrgTag;
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
    public List<Organization> getOrgs()
    {
        return orgMgr.getAll();
    }
    public boolean hasOrg()
    {
        return (orgMgr.getOrg() != null);
    }
    public Organization getOrg()
    {
        return orgMgr.getOrg();
    }
    public String getOrgId()
    {
        return orgMgr.getOrg() == null ? null : orgMgr.getOrg().getId();
    }
    public void setOrg(Organization org)
    {
        orgMgr.setOrg(org);
    }
    public String getOrgName()
    {
        return hasOrg() ? orgMgr.getOrg().getName() : null;
    }

    public void createOrg(String name)
    {
        orgMgr.save(new Organization(name));
    }
    public void resetOrg()
    {
        orgMgr.setOrg(null);
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

    public User getExpectedUser() { return (getPopulatedUserManager().getUser()); }
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


    // todo - cheap work around for now
    public Action getCurrentAction()
    {
        return currentAction;
    }
    public void setCurrentAction(Action currentAction)
    {
        this.currentAction = currentAction;
    }

    public String getAndClearMsg()
    {
        String returnMsg = msg == null ? "" : msg;
        msg = null;
        return returnMsg;
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