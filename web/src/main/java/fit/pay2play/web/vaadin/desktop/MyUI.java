package fit.pay2play.web.vaadin.desktop;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.*;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.WebBrowser;
import com.vaadin.ui.*;
import fit.pay2play.web.manager.SessionManager;
import fit.pay2play.web.manager.VaadinSessionManager;
import fit.pay2play.web.vaadin.desktop.actionbar.ActionBar;
import fit.pay2play.web.vaadin.desktop.user.ProfilePage;
import fit.pay2play.web.vaadin.desktop.user.LoginPage;
import xyz.cleangone.util.env.EnvManager;
import xyz.cleangone.util.env.P2pEnv;

import java.util.logging.Logger;

@Push
@Theme("mytheme")
@Viewport("user-scalable=yes,initial-scale=1.0")
public class MyUI extends UI
{
    public static final String RESET_PASSWORD_URL_PARAM = "reset";
    public static final String VERIFY_EMAIL_URL_PARAM = "verify";
    public static final String ITEM_URL_PARAM = "item";
    public static boolean COLORS = false;

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
        new Navigator(this, this);
        UI.getCurrent().setResizeLazy(true);
        ActionBar.addActionBarStyle();

        SessionManager sessionMgr = VaadinSessionManager.createSessionManager();

        WebBrowser webBrowser = getCurrent().getPage().getWebBrowser();
        sessionMgr.setIsMobileBrowser(webBrowser.isIOS() || webBrowser.isAndroid() || webBrowser.isWindowsPhone());

        // strip off # qualifier and/or ? params
        String uri = vaadinRequest.getParameter("v-loc");
        if (uri.contains("?")) { uri = uri.substring(0, uri.indexOf("?")); }
        if (uri.contains("#")) { uri = uri.substring(0, uri.indexOf("#")); }
        sessionMgr.setUrl(uri);

        View loginPage = new LoginPage();

        Navigator nav = getNavigator();
        nav.addView(LoginPage.NAME, loginPage);
        nav.addView(ProfilePage.NAME, new ProfilePage());

        nav.navigateTo(LoginPage.NAME);
    }
}
