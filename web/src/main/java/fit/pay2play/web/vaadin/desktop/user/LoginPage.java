package fit.pay2play.web.vaadin.desktop.user;

import com.vaadin.event.ShortcutAction;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.*;
import fit.pay2play.web.manager.SessionManager;
import fit.pay2play.web.manager.VaadinSessionManager;
import fit.pay2play.web.vaadin.desktop.actionbar.ActionBar;
import xyz.cleangone.data.aws.dynamo.entity.person.User;
import xyz.cleangone.data.aws.dynamo.entity.person.UserToken;
import xyz.cleangone.data.manager.UserManager;

import javax.servlet.http.Cookie;

import static xyz.cleangone.web.vaadin.util.VaadinUtils.*;

public class LoginPage extends Panel implements View
{
    public static final String NAME = "Login";

    private ActionBar actionBar;
    private Panel loginPanel = new Panel("Login");

    public LoginPage()
    {
        // components fills the browser screen
        setSizeFull();

        // pageLayout sits in components, scrolls if doesn't fit
        VerticalLayout pageLayout = vertical(MARGIN_FALSE, SPACING_TRUE, HEIGHT_100_PCT, WIDTH_100_PCT);
        setContent(pageLayout);

        actionBar = new ActionBar();

        loginPanel.setSizeUndefined();
        VerticalLayout mainLayout = vertical(loginPanel, MARGIN_FALSE, SPACING_FALSE);
        mainLayout.setComponentAlignment(loginPanel, Alignment.MIDDLE_CENTER);

        pageLayout.addComponents(actionBar, mainLayout);
        pageLayout.setExpandRatio(mainLayout, 1.0f);
    }

    @Override
    public void enter(ViewChangeEvent event)
    {
        set();
    }

    public void set()
    {
        // session could be new or an existing one from someone who is logging in after browsing
        SessionManager sessionMgr = VaadinSessionManager.createSessionManager();
        UserManager userMgr = sessionMgr.getUserManager();

        actionBar.set(sessionMgr);
        // check for token
        Cookie userCookie = VaadinSessionManager.getUserCookie();
        if (userCookie != null && userCookie.getValue() != null && userCookie.getValue().length() > 0)
        {
            User user = userMgr.loginByToken(userCookie.getValue());
            if (user == null)
            {
                // cookie is old
                VaadinSessionManager.clearUserCookie();
            }
            else
            {
                UserToken newToken = userMgr.cycleToken();
                VaadinSessionManager.setUserCookie(newToken.getId());
                return;
            }
        }

        setLogin(userMgr);
    }


    private void setLogin(UserManager userMgr)
    {
        FormLayout layout = new FormLayout();
        setLayout(layout, MARGIN_TRUE, SPACING_FALSE, SIZE_UNDEFINE);
        loginPanel.setContent(layout);

        TextField emailField = new TextField("Email");
        PasswordField passwordField = new PasswordField("Password");
        Button loginButton = new Button("Login");

        loginButton.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        loginButton.addClickListener(event -> {
            if (userMgr.login(emailField.getValue(), passwordField.getValue()) != null)
            {
                getUI().getNavigator().navigateTo(ProfilePage.NAME);
            }
            else
            {
                Notification.show("Invalid email/password", Notification.Type.ERROR_MESSAGE);
            }
        });

        layout.addComponents(emailField, passwordField, loginButton);
    }
}
