package fit.pay2play.web.vaadin.desktop.base;

import com.vaadin.navigator.View;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.themes.ValoTheme;
import fit.pay2play.data.manager.Pay2PlayManager;
import xyz.cleangone.data.aws.dynamo.entity.person.User;
import xyz.cleangone.web.manager.SessionManager;
import xyz.cleangone.web.vaadin.desktop.org.BasePage;
import xyz.cleangone.web.vaadin.ui.PageDisplayType;

import static xyz.cleangone.web.vaadin.util.VaadinUtils.*;

public abstract class BaseAdminPage extends BasePage implements View, Settable
{
    protected Pay2PlayManager p2pMgr;
    protected User user;

    public BaseAdminPage()
    {
        super(BannerStyle.Single);

        setLayout(mainLayout, MARGIN_TRUE, SPACING_TRUE);
    }

    protected PageDisplayType set(SessionManager sessionManager)
    {
        super.set(sessionManager);
        p2pMgr = new Pay2PlayManager();
        user = sessionMgr.getPopulatedUserManager().getUser();

        return PageDisplayType.NotApplicable;
    }

    public abstract void set();

    protected void setMainLayout(Component component)
    {
        mainLayout.removeAllComponents();
        mainLayout.addComponent(component);
    }
}
