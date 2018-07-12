package fit.pay2play.web.vaadin.desktop.user;

import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.VerticalLayout;
import fit.pay2play.web.manager.SessionManager;
import fit.pay2play.web.manager.VaadinSessionManager;
import fit.pay2play.web.vaadin.desktop.admin.BaseAdminPage;
import fit.pay2play.web.vaadin.desktop.org.profile.ProfileAdmin;

import static xyz.cleangone.web.vaadin.util.VaadinUtils.*;

public class ProfilePage extends BaseAdminPage
{
    public static final String NAME = "Profile";
    public static final String DISPLAY_NAME = "User Profile";

    protected VerticalLayout mainLayout = vertical(MARGIN_FALSE, SPACING_TRUE, BACK_ORANGE);

    public ProfilePage()
    {
        pageLayout.setSpacing(false);
        pageLayout.addComponent(mainLayout);
        pageLayout.setExpandRatio(mainLayout, 1.0f);
    }

    public void enter(ViewChangeListener.ViewChangeEvent event)
    {
        set(VaadinSessionManager.getExpectedSessionManager());
    }

    protected void set(SessionManager sessionMgr)
    {
        super.set(sessionMgr);

        sessionMgr.resetOrg();
        actionBar.set(sessionMgr);
        set();
    }

    protected void set()
    {
        mainLayout.removeAllComponents();

        ProfileAdmin profileAdmin = new ProfileAdmin(actionBar);
        profileAdmin.set(sessionMgr);
        mainLayout.addComponent(profileAdmin);
    }
}
