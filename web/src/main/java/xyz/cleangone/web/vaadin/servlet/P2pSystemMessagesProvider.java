package xyz.cleangone.web.vaadin.servlet;

import com.vaadin.server.CustomizedSystemMessages;
import com.vaadin.server.SystemMessages;
import com.vaadin.server.SystemMessagesInfo;
import com.vaadin.server.SystemMessagesProvider;

public class P2pSystemMessagesProvider implements SystemMessagesProvider
{
    private CustomizedSystemMessages customizedMessages;

    public P2pSystemMessagesProvider()
    {
        customizedMessages = new CustomizedSystemMessages();

        // slim down ugly "Session Expired, Take note of any unsaved data and click here or press ESC key to continue" msg
        customizedMessages.setSessionExpiredCaption("Session expired");
        customizedMessages.setSessionExpiredMessage("");

        // disable notification - reload page instead
        customizedMessages.setSessionExpiredNotificationEnabled(false);
    }

    @Override
    public SystemMessages getSystemMessages(SystemMessagesInfo systemMessagesInfo)
    {
        return customizedMessages;
    }

}
