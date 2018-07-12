package fit.pay2play.web.vaadin.desktop.admin.tabs.org.disclosure;

import com.vaadin.ui.Component;
import org.vaadin.viritin.components.DisclosurePanel;
import xyz.cleangone.web.vaadin.disclosure.BaseDisclosure;

public class MyDisclosurePanel extends DisclosurePanel
{
    private BaseDisclosure parent;

    public MyDisclosurePanel(Component content, BaseDisclosure parent)
    {
        super("", content);
        this.parent = parent;
    }

    public DisclosurePanel setOpen(boolean open)
    {
        parent.setOpen(open);
        return super.setOpen(open);
    }

}
