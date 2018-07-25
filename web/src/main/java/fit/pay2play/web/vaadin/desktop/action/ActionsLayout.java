package fit.pay2play.web.vaadin.desktop.action;

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
import xyz.cleangone.data.aws.dynamo.entity.person.User;
import xyz.cleangone.web.manager.SessionManager;
import xyz.cleangone.web.vaadin.ui.MessageDisplayer;

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
    private ActionsGrid actionsGrid;

    public ActionsLayout(SessionManager sessionMgr, MessageDisplayer messageDisplayer)
    {
        this.sessionMgr = sessionMgr;
        user = sessionMgr.getUser();

        actionAdmin = new ActionAdmin(p2pMgr, messageDisplayer, this);

        setLayout(this, MARGIN_TRUE, SPACING_TRUE, BACK_DK_BLUE);
        set();
    }

    public void set()
    {
        removeAllComponents();

//        ActionsCandlestickChart chart = new ActionsCandlestickChart(user, p2pMgr, this);

        Component chart = new ActionsChart(user, p2pMgr, this);
        if (user == null)
        {
            addComponents(chart);
            return;
        }

        Component addPay = getAddPayLayout();
        Component addPlay = getAddPlayLayout();
        if (sessionMgr.isMobileBrowser())
        {
            addComponents(addPay, addPlay);
        }
        else
        {
            HorizontalLayout topLayout = horizontal(MARGIN_FALSE, SPACING_TRUE, WIDTH_100_PCT, BACK_RED);
            topLayout.addComponents(addPay, addPlay);
            topLayout.setComponentAlignment(addPlay, new Alignment(AlignmentInfo.Bits.ALIGNMENT_RIGHT));
            addComponent(topLayout);
        }

        actionsGrid = new ActionsGrid(user, new Date(), p2pMgr, sessionMgr.isMobileBrowser(), this);
        addComponents(chart, actionsGrid);
        setExpandRatio(actionsGrid, 1.0f);
    }

    public void editAction(Action action)
    {
        actionAdmin.set(action);
        removeAllComponents();
        addComponents(actionAdmin);
    }

    public void setGrid(Date date)
    {
        removeComponent(actionsGrid);

        actionsGrid = new ActionsGrid(user, date, p2pMgr, sessionMgr.isMobileBrowser(), this);
        addComponent(actionsGrid);
        setExpandRatio(actionsGrid, 1.0f);
    }

    private Component getAddPayLayout()
    {
        HorizontalLayout layout = horizontal(MARGIN_FALSE, SPACING_TRUE, BACK_GREEN);

        List<Pay> pays = p2pMgr.getPays(user.getId());
        if (pays.size() > 0) { layout.addComponent(createButton(pays.get(0))); }
        if (pays.size() > 1) { layout.addComponent(createButton(pays.get(1))); }

        if (pays.size() > 2)
        {
            List<Pay> remainingPays = pays.stream()
                .filter(p -> p != pays.get(0) && p != pays.get(1))
                .collect(Collectors.toList());

            ComboBox<Pay> comboBox = new ComboBox<>();
            comboBox.addStyleName(ValoTheme.TEXTFIELD_TINY);
            comboBox.setItems(remainingPays);
            comboBox.setPlaceholder("More Pay");
            comboBox.setItemCaptionGenerator(Pay::getName);
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

        return layout;
    }

    private Component getAddPlayLayout()
    {
        HorizontalLayout layout = horizontal(MARGIN_FALSE, SPACING_TRUE, BACK_BLUE);

        List<Play> plays = p2pMgr.getPlays(user.getId());
        if (plays.size() > 0) { layout.addComponent(createButton(plays.get(0))); }
        if (plays.size() > 1) { layout.addComponent(createButton(plays.get(1))); }

        if (plays.size() > 2)
        {
            ComboBox<Play> comboBox = new ComboBox<>();

            List<Play> remainingPlays = plays.stream()
                .filter(p -> p != plays.get(0) && p != plays.get(1))
                .collect(Collectors.toList());

            comboBox.addStyleName(ValoTheme.TEXTFIELD_TINY);
            comboBox.setItems(remainingPlays);
            comboBox.setPlaceholder("More Play");
            comboBox.setItemCaptionGenerator(Play::getName);
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

        return layout;
    }

    private Button createButton(Pay pay)
    {
        return createButton(pay.getName(), event -> {
            p2pMgr.addAction(pay);
            set();
        });
    }

    private Button createButton(Play play)
    {
        return createButton(play.getName(), event -> {
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
