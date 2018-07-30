package xyz.cleangone.web.vaadin.desktop.actionbar;

import com.vaadin.icons.VaadinIcons;
import fit.pay2play.data.manager.Pay2PlayManager;
import fit.pay2play.web.vaadin.desktop.pay.PaysAdminPage;
import fit.pay2play.web.vaadin.desktop.play.PlaysAdminPage;
import xyz.cleangone.data.aws.dynamo.entity.organization.Organization;
import xyz.cleangone.web.manager.SessionManager;
import xyz.cleangone.web.vaadin.desktop.org.OrgPage;
import xyz.cleangone.web.vaadin.ui.PageDisplayType;

import static xyz.cleangone.web.vaadin.util.VaadinUtils.*;

public class CenterMenuBar extends BaseMenuBar
{
    private MenuItem msgMenuItem;
    private Pay2PlayManager p2pMgr = new Pay2PlayManager();

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

//        if (changeManager.unchanged(org) &&
//            changeManager.unchanged(org, EntityType.ENTITY, EntityType.ENTITY))
//        {
//            return PageDisplayType.NoChange;
//        }

        changeManager.reset(org);
        removeItems();

        if (sessionMgr.hasUser())
        {
            if (!sessionMgr.isMobileBrowser())
            {
                addNavigateItem(PaysAdminPage.NAME);
                addIconOnlyItem("Home", VaadinIcons.HOME, getNavigateCmd(OrgPage.NAME));
                addNavigateItem(PlaysAdminPage.NAME);
            }
        }

        if (!sessionMgr.isMobileBrowser()) { msgMenuItem = addItem(sessionMgr.getAndClearMsg(), null, null); }

        return PageDisplayType.ObjectRetrieval;
    }

    public void displayMessage(String msg)
    {
        if (msgMenuItem != null) { msgMenuItem.setText(msg); }
    }

}
