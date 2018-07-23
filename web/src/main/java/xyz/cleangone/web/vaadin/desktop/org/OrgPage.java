package xyz.cleangone.web.vaadin.desktop.org;

import com.vaadin.navigator.View;
import com.vaadin.ui.*;
import fit.pay2play.web.vaadin.desktop.action.ActionsLayout;
import xyz.cleangone.data.aws.dynamo.entity.organization.Organization;
import xyz.cleangone.data.aws.dynamo.entity.person.User;
import xyz.cleangone.web.manager.SessionManager;
import xyz.cleangone.web.vaadin.ui.PageDisplayType;

import static xyz.cleangone.web.vaadin.util.VaadinUtils.*;

public class OrgPage extends BasePage implements View
{
    private static String STYLE_ARTICLE_BOTTOM = "link wordWrap backgroundWhite";
    private static String STYLE_ARTICLE = STYLE_ARTICLE_BOTTOM + " dividerBot";
    private static String STYLE_MARGIN_LEFT = "marginLeft";
    private static String STYLE_LINK = "link";

    private static int LEFT_WIDTH_DEFAULT = 550;
    private static int CENTER_RIGHT_WIDTH_DEFAULT = 250;

    public static final String NAME = "Org";

    private Organization org;
    private User user;

    public OrgPage(boolean isMobileBrowser)
    {
        super(isMobileBrowser ? BannerStyle.Single : BannerStyle.Carousel);
    }

    protected String getPageName()
    {
        return "Main Page";
    }

    protected PageDisplayType set(SessionManager sessionManager)
    {
        super.set(sessionManager);

        org = orgMgr.getOrg();
        user = sessionManager.getUser();
        resetHeader();

        return set();
    }

    protected PageDisplayType set()
    {
        mainLayout.removeAllComponents();

        String introHtml = org.getIntroHtml();
        if (introHtml != null)
        {
            VerticalLayout introLayout = vertical(getHtmlLabel(introHtml), MARGIN_LR);
            mainLayout.addComponent(introLayout);
        }

        if (user != null)
        {
            mainLayout.addComponent(new ActionsLayout(user, actionBar));
        }



        return PageDisplayType.ObjectRetrieval;
    }
}
