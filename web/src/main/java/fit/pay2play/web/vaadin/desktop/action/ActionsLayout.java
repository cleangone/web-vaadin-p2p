package fit.pay2play.web.vaadin.desktop.action;

import com.vaadin.event.selection.SingleSelectionListener;
import com.vaadin.shared.ui.AlignmentInfo;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import fit.pay2play.data.aws.dynamo.entity.Action;
import fit.pay2play.data.aws.dynamo.entity.Pay;
import fit.pay2play.data.aws.dynamo.entity.Play;
import fit.pay2play.data.manager.Pay2PlayManager;
import fit.pay2play.web.vaadin.desktop.action.components.ActionsChart;
import fit.pay2play.web.vaadin.desktop.action.components.ActionsGrid;
import fit.pay2play.web.vaadin.desktop.base.Settable;
import org.apache.commons.lang3.StringUtils;
import xyz.cleangone.data.aws.dynamo.entity.base.BaseNamedEntity;
import xyz.cleangone.data.aws.dynamo.entity.person.User;
import xyz.cleangone.web.manager.SessionManager;
import xyz.cleangone.web.vaadin.desktop.actionbar.ActionBar;
import xyz.cleangone.web.vaadin.ui.MessageDisplayer;
import xyz.cleangone.web.vaadin.util.VaadinUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static xyz.cleangone.web.vaadin.util.VaadinUtils.*;

public class ActionsLayout extends VerticalLayout implements Settable
{
    private final SessionManager sessionMgr;
    private final User user;
    private Pay2PlayManager p2pMgr = new Pay2PlayManager();
    private ActionAdmin actionAdmin;
    private VerticalLayout actionsGridLayout = vertical(MARGIN_FALSE, SPACING_TRUE, BACK_YELLOW);

    public ActionsLayout(SessionManager sessionMgr, ActionBar actionBar)
    {
        this.sessionMgr = sessionMgr;
        user = sessionMgr.getUser();

        actionAdmin = new ActionAdmin(p2pMgr, actionBar, this);

        setLayout(this, MARGIN_TRUE, SPACING_TRUE, BACK_DK_BLUE);
        set();
    }

    public void set()
    {
        removeAllComponents();
        Component chart = new ActionsChart(user, p2pMgr, sessionMgr.isMobileBrowser(), this);
        if (user == null)
        {
            addComponents(chart);
            return;
        }

        HorizontalLayout addPay = getAddPayLayout();
        HorizontalLayout addPlay = getAddPlayLayout();

        if (sessionMgr.isMobileBrowser())
        {
            setLayout(addPay, WIDTH_100_PCT);
            setLayout(addPlay, WIDTH_100_PCT);
            addComponents(addPay, addPlay);
        }
        else
        {
            HorizontalLayout topLayout = horizontal(MARGIN_FALSE, SPACING_TRUE, WIDTH_100_PCT, BACK_RED);
            topLayout.addComponents(addPay, addPlay);
            topLayout.setComponentAlignment(addPlay, new Alignment(AlignmentInfo.Bits.ALIGNMENT_RIGHT));
            addComponent(topLayout);
        }

        setActionsGrid(new Date());
        addComponents(chart, actionsGridLayout);
        setExpandRatio(actionsGridLayout, 1.0f);
    }

    public void editAction(Action action)
    {
        actionAdmin.set(action);
        removeAllComponents();
        addComponents(actionAdmin);
    }

    public void setActionsGrid(Date date)
    {
        actionsGridLayout.removeAllComponents();

        ActionsGrid actionsGrid = new ActionsGrid(user, date, p2pMgr, sessionMgr.isMobileBrowser(), this);
        actionsGridLayout.addComponent(actionsGrid);
        actionsGridLayout.setExpandRatio(actionsGrid, 1.0f);
    }

    private HorizontalLayout getAddPayLayout()
    {
        HorizontalLayout layout = getAddLayout("Pay");

        List<Pay> pays = p2pMgr.getEnabledPays(user.getId());
        List<Pay> comboBoxPays = new ArrayList<>();
        for (Pay pay : pays)
        {
            if (!StringUtils.isBlank(pay.getDisplayOrder())) { layout.addComponent(createButton(pay)); }
            else { comboBoxPays.add(pay); }
        }

        if (!comboBoxPays.isEmpty())
        {
            ComboBox<Pay> comboBox = createComboBox(new ComboBox<Pay>(), "Pay", comboBoxPays);
            comboBox.addSelectionListener(event -> {
                Pay pay = event.getSelectedItem().orElse(null);
                if (pay != null)
                {
                    p2pMgr.addAction(pay);
                    set();
                }
            });

            layout.addComponent(comboBox);
        }

        return wrappedLayout(layout, "payLayout");
    }

    private HorizontalLayout getAddPlayLayout()
    {
        HorizontalLayout layout = getAddLayout("Play");

        List<Play> plays = p2pMgr.getEnabledPlays(user.getId());
        List<Play> comboBoxPlays = new ArrayList<>();
        for (Play play : plays)
        {
            if (!StringUtils.isBlank(play.getDisplayOrder())) { layout.addComponent(createButton(play)); }
            else { comboBoxPlays.add(play); }
        }

        if (!comboBoxPlays.isEmpty())
        {
            ComboBox<Play> comboBox = createComboBox(new ComboBox<Play>(), "Play", comboBoxPlays);
            comboBox.addSelectionListener(event -> {
                Play play = event.getSelectedItem().orElse(null);
                if (play != null)
                {
                    p2pMgr.addAction(play);
                    set();
                }
            });

            layout.addComponent(comboBox);
        }

        return wrappedLayout(layout, "playLayout");
    }

    private HorizontalLayout getAddLayout(String name)
    {
        Label label = new Label(name);
        label.addStyleName("payLabel");

        return horizontal(label, MARGIN_FALSE, SPACING_TRUE);
    }

    private HorizontalLayout wrappedLayout(HorizontalLayout layout, String wrapperStyle)
    {
        HorizontalLayout wrapper = horizontal(layout, MARGIN_TRUE);
        wrapper.addStyleName(wrapperStyle);
        return wrapper;
    }

    private <T extends Play> ComboBox<T> createComboBox(ComboBox<T> comboBox, String name, List<T> items)
    {
        comboBox.setPlaceholder("");
        comboBox.addStyleName(ValoTheme.TEXTFIELD_TINY);
        comboBox.setTextInputAllowed(false);
        comboBox.setItems(items);
        comboBox.setItemCaptionGenerator(T::getDisplayShortName);
        comboBox.setWidth(3, Unit.EM);
        comboBox.setPopupWidth(null);

        return comboBox;
    }

    private Button createButton(Pay pay)
    {
        return createButton(pay.getDisplayShortName(), event -> {
            p2pMgr.addAction(pay);
            set();
        });
    }

    private Button createButton(Play play)
    {
        return createButton(play.getDisplayShortName(), event -> {
            p2pMgr.addAction(play);
            set();
        });
    }

    private Button createButton(String caption, Button.ClickListener listener)
    {
        Button button = new Button(caption);
        button.addStyleName(ValoTheme.TEXTFIELD_TINY);
        button.addClickListener(listener);

        return button;
    }

    private void showError(String msg) { Notification.show(msg, Notification.Type.ERROR_MESSAGE); }
}
