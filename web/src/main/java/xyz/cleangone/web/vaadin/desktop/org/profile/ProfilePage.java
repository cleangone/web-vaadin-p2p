package xyz.cleangone.web.vaadin.desktop.org.profile;

import com.vaadin.ui.*;

public class ProfilePage extends BaseProfilePage
{
    public static final String NAME = "UserProfile";
    public static final String DISPLAY_NAME = "User Profile";

    public ProfilePage()
    {
        super(ProfilePageType.GENERAL);
        components.put(ProfilePageType.GENERAL, new ProfileAdmin(actionBar));
    }

    protected Component getLinksLayout()
    {
        Label label = new Label("User Profile");
        label.setStyleName(STYLE_FONT_BOLD);

        VerticalLayout layout = new VerticalLayout();
        layout.setMargin(false);
        layout.addComponents(label, getLinksLayout(ProfilePageType.GENERAL));

        return layout;
    }
}