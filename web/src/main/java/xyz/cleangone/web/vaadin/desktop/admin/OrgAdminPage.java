package xyz.cleangone.web.vaadin.desktop.admin;

import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import xyz.cleangone.web.manager.SessionManager;
import xyz.cleangone.web.vaadin.desktop.admin.tabs.UsersAdmin;
import xyz.cleangone.web.vaadin.desktop.admin.tabs.org.OrgAdmin;

public class OrgAdminPage extends BaseAdminPage
{
    public static final String NAME = "OrgAdmin";
    public static final String DISPLAY_NAME = "Admin";

    private OrgAdmin orgAdmin = new OrgAdmin(actionBar);
    private UsersAdmin usersAdmin = new UsersAdmin(actionBar);

    private TabSheet tabsheet = new TabSheet();
    private TabSheet.Tab orgTab = tabsheet.addTab(createLayoutSizeFull(orgAdmin), "Organization");
    private TabSheet.Tab usersTab = tabsheet.addTab(usersAdmin, "Users");
    private TabSheet.Tab[] tabs = { orgTab, usersTab };

    public OrgAdminPage()
    {
        tabsheet.addStyleName(ValoTheme.TABSHEET_FRAMED);
        tabsheet.setHeight("100%");
        tabsheet.addSelectedTabChangeListener(e -> handleTabChangeEvent());

        pageLayout.addComponent(tabsheet);
        pageLayout.setExpandRatio(tabsheet, 1.0f);
    }

    protected void set(SessionManager sessionMgr)
    {
        super.set(sessionMgr);

        actionBar.set(sessionMgr);

        boolean isAdmin = sessionMgr.getUserManager().userIsOrgAdmin(orgMgr.getOrgId());
        for (TabSheet.Tab tab : tabs)
        {
            tab.setEnabled(isAdmin);
        }

        if (isAdmin)
        {
            // org is the initial tab
            orgAdmin.set(sessionMgr);
            orgTab.setCaption(sessionMgr.getOrgName());
        }
    }

    protected void handleTabChangeEvent()
    {
        TabSheet.Tab selectedTab = tabsheet.getTab(tabsheet.getSelectedTab());

        // todo - pageHeight not set yet
        int pageHeight = getUI().getPage().getBrowserWindowHeight();

        if (selectedTab == usersTab)  { usersAdmin.set(orgMgr, sessionMgr.getUserManager()); }

    }

    public static String getName() { return NAME; }
    public static String getDisplayName() { return DISPLAY_NAME; }
}
