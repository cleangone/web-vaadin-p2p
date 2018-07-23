package xyz.cleangone.web.vaadin.desktop.actionbar;

import com.vaadin.icons.VaadinIcons;
import fit.pay2play.web.vaadin.desktop.pay.PaysAdminPage;
import fit.pay2play.web.vaadin.desktop.play.PlaysAdminPage;
import xyz.cleangone.data.aws.dynamo.entity.lastTouched.EntityType;
import xyz.cleangone.data.aws.dynamo.entity.organization.Organization;
import xyz.cleangone.web.manager.SessionManager;
import xyz.cleangone.web.vaadin.desktop.org.OrgPage;
import xyz.cleangone.web.vaadin.ui.PageDisplayType;

public class CenterMenuBar extends BaseMenuBar
{
    private MenuItem msgMenuItem;

    public PageDisplayType set(SessionManager sessionMgr)
    {
        super.set(sessionMgr);
        return set();
    }

    private PageDisplayType set()
    {
        Organization org = sessionMgr.getOrg();
        if (org == null )
        {
            removeItems();
            return PageDisplayType.NotApplicable;
        }

        if (changeManager.unchanged(org) &&
            changeManager.unchanged(org, EntityType.ENTITY, EntityType.ENTITY))
        {
            return PageDisplayType.NoChange;
        }

        changeManager.reset(org);
        removeItems();

        addNavigateItem(PaysAdminPage.NAME);
        if (!sessionMgr.isMobileBrowser()) { addIconOnlyItem("Home", VaadinIcons.HOME, getNavigateCmd(OrgPage.NAME)); }
        addNavigateItem(PlaysAdminPage.NAME);

        msgMenuItem = addItem(sessionMgr.getAndClearMsg(), null, null);

        return PageDisplayType.ObjectRetrieval;
    }

    public void displayMessage(String msg)
    {
        msgMenuItem.setText(msg);
    }

}
