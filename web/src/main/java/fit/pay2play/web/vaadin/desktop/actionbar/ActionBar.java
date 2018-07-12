package fit.pay2play.web.vaadin.desktop.actionbar;

import com.vaadin.shared.ui.AlignmentInfo;
import com.vaadin.ui.*;
import fit.pay2play.web.manager.SessionManager;
import xyz.cleangone.web.vaadin.ui.MessageDisplayer;
import xyz.cleangone.web.vaadin.ui.PageDisplayType;

import static xyz.cleangone.web.vaadin.util.VaadinUtils.*;

public class ActionBar extends BaseActionBar implements MessageDisplayer
{
    private LeftMenuBar leftMenuBar = new LeftMenuBar();
    private CenterMenuBar centerMenuBar = new CenterMenuBar();
    private RightMenuBar rightMenuBar = new RightMenuBar();

    public ActionBar()
    {
        // todo - may need to adjust thes %'s on the fly to adapt to mobile
        HorizontalLayout leftLayout = getLayout(leftMenuBar, "10%");
        HorizontalLayout centerLayout = getLayout(centerMenuBar, "50%");
        HorizontalLayout rightLayout = getLayout(rightMenuBar, "40%");
        rightLayout.addComponent(getHtmlLabel(""));

        addComponents(leftLayout, centerLayout, rightLayout);
        setComponentAlignment(rightLayout, new Alignment(AlignmentInfo.Bits.ALIGNMENT_RIGHT));
    }

    public PageDisplayType set(SessionManager sessionMgr)
    {
        setStyle(sessionMgr);
        return rightMenuBar.set(sessionMgr);
    }

    public void displayMessage(String msg)
    {
    }
    public void setCartMenuItem()
    {
    }

}
