package fit.pay2play.web.vaadin.desktop.actionbar;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.MenuBar;
import fit.pay2play.web.manager.SessionManager;
import fit.pay2play.web.vaadin.desktop.user.ProfilePage;
import fit.pay2play.web.manager.VaadinSessionManager;
import fit.pay2play.web.vaadin.desktop.user.LoginPage;
import xyz.cleangone.web.vaadin.ui.PageDisplayType;

public class RightMenuBar extends BaseMenuBar
{
    public PageDisplayType set(SessionManager sessionMgr)
    {
        super.set(sessionMgr);
        return set();
    }

    private PageDisplayType set()
    {
        removeItems();

        if (userMgr.hasUser())
        {
            MenuBar.MenuItem profileItem = addItem((sessionMgr.isMobileBrowser() ? "" : " " + userMgr.getFirstName()), null, null);
            profileItem.setIcon(VaadinIcons.USER_CHECK);
            profileItem.setDescription(ProfilePage.DISPLAY_NAME);

            profileItem.addItem(ProfilePage.DISPLAY_NAME, null, getNavigateCmd(ProfilePage.NAME));

            MenuBar.Command logoutCmd = new MenuBar.Command() {
                public void menuSelected(MenuBar.MenuItem selectedItem) {
                    userMgr.logout();
                    VaadinSessionManager.clearUserCookie();
                    navigateTo(LoginPage.NAME);
                }
            };

            profileItem.addItem("Logout", null, logoutCmd);
        }
        else
        {
            MenuBar.MenuItem signinItem = addItem((sessionMgr.isMobileBrowser() ? "" : LoginPage.NAME), null, null);
            signinItem.setIcon(VaadinIcons.USER);
            signinItem.addItem("Login", null, getNavigateCmd(LoginPage.NAME));
//            signinItem.addItem(CreateAccountPage.DISPLAY_NAME, null, getNavigateCmd(CreateAccountPage.NAME));
        }

        return PageDisplayType.NoRetrieval;
    }

}
