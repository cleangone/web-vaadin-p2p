package xyz.cleangone.web.vaadin.desktop.actionbar;

import com.vaadin.icons.VaadinIcons;
import xyz.cleangone.data.aws.dynamo.entity.lastTouched.EntityType;
import xyz.cleangone.data.aws.dynamo.entity.person.User;
import xyz.cleangone.web.manager.SessionManager;
import xyz.cleangone.web.vaadin.desktop.admin.superadmin.SuperAdminProfilePage;
import xyz.cleangone.web.vaadin.desktop.org.CreateAccountPage;
import xyz.cleangone.web.vaadin.desktop.org.PasswordRequestPage;
import xyz.cleangone.web.vaadin.desktop.org.SigninPage;
import xyz.cleangone.web.vaadin.desktop.org.profile.ProfilePage;
import xyz.cleangone.web.vaadin.ui.PageDisplayType;

import static xyz.cleangone.web.manager.CookieManager.*;

public class RightMenuBar extends BaseMenuBar
{
    public PageDisplayType set(SessionManager sessionMgr)
    {
        super.set(sessionMgr);
        return set();
    }

    private PageDisplayType set()
    {
        User user = userMgr.getUser();

        if (changeManager.unchanged(user) &&
            changeManager.unchanged(user, EntityType.Entity) &&
            (user == null  || changeManager.unchanged(user.getId(), EntityType.Entity)))
        {
            return PageDisplayType.NoChange;
        }

        changeManager.reset(user);
        removeItems();

        if (userMgr.hasUser())
        {
            MenuItem profileItem = addItem((sessionMgr.isMobileBrowser() ? "" : " " + userMgr.getFirstName()), null, null);
            profileItem.setIcon(VaadinIcons.USER_CHECK);
            profileItem.setDescription(ProfilePage.DISPLAY_NAME);

            if (sessionMgr.hasOrg())
            {
                profileItem.addItem(ProfilePage.NAME, null, getNavigateCmd(ProfilePage.NAME));
            }
            else
            {
                profileItem.addItem(SuperAdminProfilePage.DISPLAY_NAME, null, getNavigateCmd(SuperAdminProfilePage.NAME));
            }

            Command logoutCmd = new Command() {
                public void menuSelected(MenuItem selectedItem) {
                    userMgr.logout();
                    clearUserCookie();
                    navigateTo("");
                }
            };

            profileItem.addItem("Logout", null, logoutCmd);
        }
        else
        {
            MenuItem signinItem = addItem((sessionMgr.isMobileBrowser() ? "" : SigninPage.DISPLAY_NAME), null, null);
            signinItem.setIcon(VaadinIcons.USER);
            signinItem.addItem("Login", null, getNavigateCmd(SigninPage.NAME));
            signinItem.addItem(CreateAccountPage.DISPLAY_NAME, null, getNavigateCmd(CreateAccountPage.NAME));
            signinItem.addItem("Reset Password", null, getNavigateCmd(PasswordRequestPage.NAME));
        }

        return PageDisplayType.NoRetrieval;
    }

}
