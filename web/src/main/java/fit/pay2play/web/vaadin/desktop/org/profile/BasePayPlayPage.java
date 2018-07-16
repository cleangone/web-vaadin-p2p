package fit.pay2play.web.vaadin.desktop.org.profile;

import com.vaadin.navigator.View;
import fit.pay2play.data.manager.Pay2PlayManager;
import xyz.cleangone.data.aws.dynamo.entity.person.User;
import xyz.cleangone.web.manager.SessionManager;
import xyz.cleangone.web.vaadin.desktop.org.BasePage;
import xyz.cleangone.web.vaadin.ui.PageDisplayType;

import static xyz.cleangone.web.vaadin.util.VaadinUtils.*;

public abstract class BasePayPlayPage extends BasePage implements View
{
    protected Pay2PlayManager p2pMgr;
    protected User user;

    public BasePayPlayPage()
    {
        super(BannerStyle.Single);

        setLayout(mainLayout, MARGIN_TRUE, SPACING_TRUE);
    }

    protected PageDisplayType set(SessionManager sessionManager)
    {
        super.set(sessionManager);
        p2pMgr = new Pay2PlayManager();
        user = sessionMgr.getPopulatedUserManager().getUser();

        resetHeader();

        set();

        return PageDisplayType.NotApplicable;
    }

    protected abstract void set();
}
