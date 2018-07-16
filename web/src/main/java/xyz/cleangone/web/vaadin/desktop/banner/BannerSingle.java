package xyz.cleangone.web.vaadin.desktop.banner;

import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.HorizontalLayout;
import xyz.cleangone.data.aws.dynamo.entity.organization.Organization;
import xyz.cleangone.data.manager.OrgManager;
import xyz.cleangone.web.manager.SessionManager;

import static xyz.cleangone.web.vaadin.desktop.banner.BannerUtil.*;

public class BannerSingle extends HorizontalLayout implements BannerComponent
{
    public BannerSingle()
    {
        setWidth(getBannerWidth());
        setHeight(getBannerHeight());
    }

    public void reset(SessionManager sessionMgr)
    {
        removeAllComponents();

        boolean isMobileBrowser = sessionMgr.isMobileBrowser();


        OrgManager orgMgr = sessionMgr.getOrgManager();
        Organization org = orgMgr.getOrg();
        AbsoluteLayout orgBanner = getBanner(org, isMobileBrowser);
        addComponent(orgBanner);
        addComponentToLayout(getHtml(org, isMobileBrowser, getUI()), orgBanner, isMobileBrowser); // a bit ugly
    }

}
