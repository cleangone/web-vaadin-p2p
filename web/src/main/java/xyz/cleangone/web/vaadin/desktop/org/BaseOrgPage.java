package xyz.cleangone.web.vaadin.desktop.org;

import com.vaadin.navigator.View;
import xyz.cleangone.web.manager.SessionManager;
import xyz.cleangone.web.vaadin.ui.PageDisplayType;
import xyz.cleangone.web.vaadin.util.PageUtils;

public abstract class BaseOrgPage extends BasePage implements View
{
    public BaseOrgPage()
    {
        super(BannerStyle.Single);
    }

    protected PageDisplayType set(SessionManager sessionMgr)
    {
        super.set(sessionMgr);

        PageDisplayType headerDisplayType = resetHeader();
        PageDisplayType pageDisplayType = set();

        return PageUtils.getPageDisplayType(headerDisplayType, pageDisplayType);
    }

    protected abstract PageDisplayType set();
}