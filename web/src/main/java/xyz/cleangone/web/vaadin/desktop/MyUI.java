package xyz.cleangone.web.vaadin.desktop;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;

import com.vaadin.annotations.*;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.WebBrowser;
import com.vaadin.ui.*;
import fit.pay2play.web.vaadin.desktop.pay.PaysAdminPage;
import fit.pay2play.web.vaadin.desktop.play.PlaysAdminPage;
import xyz.cleangone.data.aws.dynamo.entity.item.CatalogItem;
import xyz.cleangone.data.aws.dynamo.entity.organization.OrgTag;
import xyz.cleangone.data.aws.dynamo.entity.organization.Organization;
import xyz.cleangone.data.aws.dynamo.entity.person.User;
import xyz.cleangone.data.aws.dynamo.entity.person.UserToken;
import xyz.cleangone.data.manager.OrgManager;
import xyz.cleangone.data.manager.TagManager;
import xyz.cleangone.data.manager.UserManager;
import xyz.cleangone.util.env.EnvManager;
import xyz.cleangone.util.env.P2pEnv;
import xyz.cleangone.web.manager.SessionManager;
import xyz.cleangone.web.manager.VaadinSessionManager;
import xyz.cleangone.web.vaadin.desktop.actionbar.ActionBar;
import xyz.cleangone.web.vaadin.desktop.admin.OrgAdminPage;
import xyz.cleangone.web.vaadin.desktop.admin.superadmin.SuperAdminPage;
import xyz.cleangone.web.vaadin.desktop.admin.superadmin.SuperAdminProfilePage;
import xyz.cleangone.web.vaadin.desktop.org.*;
import xyz.cleangone.web.vaadin.desktop.org.profile.ProfilePage;
import xyz.cleangone.web.vaadin.desktop.user.LoginPage;
import xyz.cleangone.web.vaadin.util.VaadinUtils;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Logger;

import static xyz.cleangone.web.manager.CookieManager.*;

@Push
@Theme("mytheme")
@Viewport("user-scalable=yes,initial-scale=1.0")
public class MyUI extends UI
{
    public static final String RESET_PASSWORD_URL_PARAM = "reset";
    public static final String VERIFY_EMAIL_URL_PARAM = "verify";
    public static final String ITEM_URL_PARAM = "item";
    private static SimpleDateFormat LOG_SDF = new SimpleDateFormat("MM/dd HH:mm:ss");



    @WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = MyUI.class, productionMode = false)
    public static class MyUIServlet extends VaadinServlet { }

    private static final Logger LOG = Logger.getLogger(MyUI.class.getName());

    static
    {
        EnvManager.setEnv(new P2pEnv());
    }

    @Override
    protected void init(VaadinRequest vaadinRequest)
    {
        log("UI init");

//        VaadinUtils.SHOW_BACKBROUND_COLORS = true;

        new Navigator(this, this);
        UI.getCurrent().setResizeLazy(true);
        ActionBar.addActionBarStyle();

        SessionManager sessionMgr = VaadinSessionManager.createSessionManager();

        WebBrowser webBrowser = getCurrent().getPage().getWebBrowser();
        sessionMgr.setIsMobileBrowser(webBrowser.isIOS() || webBrowser.isAndroid() || webBrowser.isWindowsPhone());

        ReconnectDialogConfiguration reconnect = getReconnectDialogConfiguration();
        reconnect.setDialogText(""); // quieter than the universally hated "Server connection lost, trying to reconnect..."
        reconnect.setDialogGracePeriod(10000); // 10 secs, instead or 400ms default

        addDetachListener(new DetachListener() {
            @Override
            public void detach(final DetachEvent detachEvent) {
                log("UI detached");
            }
        });

        // strip off # qualifier and/or ? params
        String uri = vaadinRequest.getParameter("v-loc");
        if (uri.contains("?")) { uri = uri.substring(0, uri.indexOf("?")); }
        if (uri.contains("#")) { uri = uri.substring(0, uri.indexOf("#")); }
        sessionMgr.setUrl(uri);

        View loginPage = new LoginPage();
        OrgPage orgPage = new OrgPage(sessionMgr.isMobileBrowser());

        Navigator nav = getNavigator();
        nav.addView(LoginPage.NAME, loginPage);
        nav.addView(OrgPage.NAME, orgPage);
        nav.addView(PaysAdminPage.NAME, new PaysAdminPage());
        nav.addView(PlaysAdminPage.NAME, new PlaysAdminPage());

        nav.addView(SuperAdminPage.NAME, new SuperAdminPage());
        nav.addView(SuperAdminProfilePage.NAME, new SuperAdminProfilePage());
        nav.addView(OrgAdminPage.NAME, new OrgAdminPage());
        nav.addView(SigninPage.NAME, new SigninPage());
        nav.addView(PasswordRequestPage.NAME, new PasswordRequestPage());
        nav.addView(PasswordResetPage.NAME, new PasswordResetPage());
        nav.addView(ProfilePage.NAME, new ProfilePage());
        nav.addView(CreateAccountPage.NAME, new CreateAccountPage());

        UserManager userMgr = sessionMgr.getUserManager();
        String resetPasswordToken = vaadinRequest.getParameter(RESET_PASSWORD_URL_PARAM);
        if (resetPasswordToken != null)
        {
            User user = userMgr.loginByToken(resetPasswordToken);
            if (user != null)
            {
                // get org from path - pathTags should always be set - request password always from an org
                // todo - user could nav to another org before requesting reset
                List<String> pathTags = getPathTags(vaadinRequest);
                if (!pathTags.isEmpty())
                {
                    getOrgFromPath(pathTags, sessionMgr.getOrgManager()); // does setOrg
                }

                nav.setErrorView(orgPage);
                nav.navigateTo(PasswordResetPage.NAME);
                return;
            }
        }

        loginByCookie(userMgr);
        verifyEmail(vaadinRequest, userMgr);

        // first check if org specified in the url - url may end with  /<orgTag>/<eventTag>
        String initialPage = getInitialPage(vaadinRequest, sessionMgr);


        // next check if default org config'd in env
        OrgManager orgMgr = sessionMgr.getOrgManager();
        if (initialPage == null)
        {
            String configuredDefaultOrgTag = EnvManager.getEnv().getDefaultOrg();
            if (configuredDefaultOrgTag != null)
            {
                Organization org = orgMgr.findOrg(configuredDefaultOrgTag);
                if (org != null && org.getEnabled())
                {
                    orgMgr.setOrg(org);
                    initialPage = OrgPage.NAME;
                }
            }
        }

        // next check if an org is set as default in db
        if (initialPage == null)
        {
            for (Organization org : orgMgr.getAll())
            {
                if (org.getEnabled() && org.getIsDefault())
                {
                    orgMgr.setOrg(org);
                    initialPage = OrgPage.NAME;
                }
            }
        }

        // default for superadmin
        if (initialPage == null && userMgr.userIsSuperAdmin())
        {
            initialPage = SuperAdminPage.NAME;
        }

        nav.setErrorView(initialPage == null ? loginPage : orgPage);
        nav.navigateTo(initialPage == null ? LoginPage.NAME : initialPage);
    }

    private boolean loginByCookie(UserManager userMgr)
    {
        // check for token
        Cookie userCookie = getUserCookie();
        if (userCookie != null && userCookie.getValue() != null && userCookie.getValue().length() > 0)
        {
            User user = userMgr.loginByToken(userCookie.getValue());
            if (user == null)
            {
                // cookie is old
                clearUserCookie();
            }
            else
            {
                UserToken newToken = userMgr.cycleToken();
                setUserCookie(newToken.getId());
                return true;
            }
        }

        return false;
    }

    private void verifyEmail(VaadinRequest vaadinRequest, UserManager userMgr)
    {
        String verifyEmailToken = vaadinRequest.getParameter(VERIFY_EMAIL_URL_PARAM);

        if (verifyEmailToken == null) { return; }

        userMgr.verifyEmail(verifyEmailToken);
    }

    // url path may contain /<orgTag>/<eventTag>
    private String getInitialPage(VaadinRequest vaadinRequest, SessionManager sessionMgr)
    {
        List<String> pathTags = getPathTags(vaadinRequest);
        if (pathTags.isEmpty()) { return null; }

        // first tag is org
        String returnPage = null;
        Organization org = getOrgFromPath(pathTags, sessionMgr.getOrgManager());
        if (org != null) { returnPage = OrgPage.NAME; }

        return returnPage;
    }


    // url path may contain /<orgTag>/<eventTag>
    private List<String> getPathTags(VaadinRequest vaadinRequest)
    {
        String path = vaadinRequest.getPathInfo();
        if (path == null || !path.startsWith("/") || path.equals("/"))
        {
            return Collections.emptyList();
        }

        return Arrays.asList(path.substring(1).split("\\s*/\\s*"));  // can be empty
    }

    private Organization getOrgFromPath(List<String> pathTags, OrgManager orgMgr)
    {
        // first tag is org
        Organization org = orgMgr.findOrg(pathTags.get(0));
        if (org != null)
        {
            orgMgr.setOrg(org);
        }

        return org;
    }

    private OrgTag getCategory(CatalogItem item, TagManager tagMgr)
    {
        Map<String, OrgTag> categoryIdToCategory = new HashMap<>();
        for (OrgTag category : tagMgr.getCategories()) { categoryIdToCategory.put(category.getId(), category); }

        for (String itemCategoryId : item.getCategoryIds())
        {
            if (categoryIdToCategory.containsKey(itemCategoryId))
            {
                return categoryIdToCategory.get(itemCategoryId);
            }
        }

        return null;
    }

    private void verifyUser(UserManager userMgr, Organization org)
    {
//        User user = userMgr.getUser();
//        if (user != null && user.getOrgId() != null && !user.getOrgId().equals(org.getId()))
//        {
//            LOG.info("Logging out User " + user.getId() + " because logged in by token but org " + user.getOrgId() +
//                " does not match path Org " + org.getId());
//            userMgr.logout();
//        }
    }

    @Override
    protected void refresh(VaadinRequest request) {
        super.refresh(request);
        log("UI refreshed");
    }

    private void log(String msg)
    {
        System.out.println(LOG_SDF.format(new Date()) + " UI[id=" + getUIId() + "] " + msg);
    }

}
