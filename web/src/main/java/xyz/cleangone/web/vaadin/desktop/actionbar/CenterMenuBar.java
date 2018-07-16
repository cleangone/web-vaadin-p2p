package xyz.cleangone.web.vaadin.desktop.actionbar;

import com.vaadin.icons.VaadinIcons;
import fit.pay2play.web.vaadin.desktop.org.profile.PayPage;
import fit.pay2play.web.vaadin.desktop.org.profile.PlayPage;
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
            changeManager.unchanged(org, EntityType.Entity, EntityType.Event))
        {
            return PageDisplayType.NoChange;
        }

        // todo - move cart to right of events (old calendar spot)


        changeManager.reset(org);
        removeItems();

        if (!sessionMgr.isMobileBrowser())
        {
            addIconOnlyItem("Home", VaadinIcons.HOME, getNavigateCmd(OrgPage.NAME));
        }

        addIconOnlyItem(PlayPage.NAME, VaadinIcons.SMILEY_O, getNavigateCmd(PlayPage.NAME));
        addIconOnlyItem(PayPage.NAME, VaadinIcons.FROWN_O, getNavigateCmd(PayPage.NAME));


        msgMenuItem = addItem(sessionMgr.getAndClearMsg(), null, null);

        return PageDisplayType.ObjectRetrieval;
    }

    public void displayMessage(String msg)
    {
        msgMenuItem.setText(msg);
    }

}
