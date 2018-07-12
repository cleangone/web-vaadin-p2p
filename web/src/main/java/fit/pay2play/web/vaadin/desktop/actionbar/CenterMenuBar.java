package fit.pay2play.web.vaadin.desktop.actionbar;

import fit.pay2play.web.manager.SessionManager;
import xyz.cleangone.web.vaadin.ui.PageDisplayType;

public class CenterMenuBar extends BaseMenuBar
{
    public PageDisplayType set(SessionManager sessionMgr)
    {
        return PageDisplayType.ObjectRetrieval;
    }
}
