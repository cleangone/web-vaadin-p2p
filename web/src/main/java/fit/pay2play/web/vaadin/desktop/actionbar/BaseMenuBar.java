package fit.pay2play.web.vaadin.desktop.actionbar;

import com.vaadin.server.Resource;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.themes.ValoTheme;
import fit.pay2play.web.manager.SessionManager;
import xyz.cleangone.data.manager.UserManager;
import xyz.cleangone.web.vaadin.ui.PageDisplayType;

import static java.util.Objects.requireNonNull;

public class BaseMenuBar extends MenuBar
{
    protected SessionManager sessionMgr;
    protected UserManager userMgr;

    public BaseMenuBar()
    {
        addStyleName(ValoTheme.MENUBAR_BORDERLESS);
    }

    protected PageDisplayType set(SessionManager sessionMgr)
    {
        this.sessionMgr = requireNonNull(sessionMgr);
        userMgr = sessionMgr.getUserManager();
        return PageDisplayType.NotApplicable;
    }

    protected void addNavigateItem(String pageName, Resource icon, MenuBar menuBar)
    {
        MenuItem menuItem = menuBar.addItem("", null, getNavigateCmd(pageName));
        setMenuItem(menuItem, icon, pageName);
    }

    protected void setMenuItem(MenuItem menuItem, Resource icon, String description)
    {
        menuItem.setIcon(icon);
        menuItem.setStyleName("icon-only");
        menuItem.setDescription(description);
    }

    protected void addNavigateItem(String pageName, MenuBar menuBar)
    {
        addNavigateItem(pageName, pageName, menuBar);
    }

    protected void addNavigateItem(String pageName, String caption, MenuBar menuBar)
    {
        menuBar.addItem(caption, null, getNavigateCmd(pageName));
    }

    protected Command getNavigateCmd(String pageName)
    {
        return new Command() {
            public void menuSelected(MenuItem selectedItem) { navigateTo(pageName); }
        };
    }

    protected void addIconOnlyItem(String description, Resource icon, MenuBar.Command command)
    {
        MenuBar.MenuItem menuItem = addItem("",  icon, command);
        menuItem.setStyleName("icon-only");
        menuItem.setDescription(description);
    }

    protected void navigateTo(String pageName) { getUI().getNavigator().navigateTo(pageName); }

}
