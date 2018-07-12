package fit.pay2play.web.vaadin.desktop.admin.tabs.org;

import com.vaadin.ui.*;
import fit.pay2play.web.manager.SessionManager;
import xyz.cleangone.web.vaadin.ui.MessageDisplayer;

// todo - rename to BaseLayout
public abstract class BaseAdmin extends VerticalLayout
{
    protected final MessageDisplayer msgDisplayer;
    protected UI ui;

    public BaseAdmin(MessageDisplayer msgDisplayer)
    {
        this.msgDisplayer = msgDisplayer;
    }

    public abstract void set(SessionManager sessionMgr);

    public void set(SessionManager sessionMgr, UI ui)
    {
        this.ui = ui;
        set(sessionMgr);
    }

    public abstract void set();
}
