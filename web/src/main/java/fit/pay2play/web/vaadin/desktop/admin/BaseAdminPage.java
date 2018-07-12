package fit.pay2play.web.vaadin.desktop.admin;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import fit.pay2play.web.manager.SessionManager;
import fit.pay2play.web.manager.VaadinSessionManager;
import fit.pay2play.web.vaadin.desktop.actionbar.ActionBar;

import static xyz.cleangone.web.vaadin.util.VaadinUtils.*;

public abstract class BaseAdminPage extends Panel implements View
{
    protected VerticalLayout pageLayout = vertical(MARGIN_FALSE, SPACING_TRUE, SIZE_FULL, BACK_YELLOW);
    protected ActionBar actionBar = new ActionBar();

    protected SessionManager sessionMgr;

    public BaseAdminPage()
    {
        // panel fills the browser screen
        setSizeFull();

        // pageLayout sits in components, scrolls if doesn't fit
        // todo - making pagelayout full - other will scroll in it
        pageLayout.addComponent(actionBar);
        setContent(pageLayout);
    }

    public static String getName() { return ""; }
    public static String getDisplayName() { return ""; }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event)
    {
        SessionManager sessionManager = VaadinSessionManager.getExpectedSessionManager();
        if (sessionManager.hasOrg())
        {
            set(sessionManager);
        }
    }

    protected void set(SessionManager sessionMgr)
    {
        this.sessionMgr = sessionMgr;
    }

}
