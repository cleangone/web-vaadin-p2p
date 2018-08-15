package fit.pay2play.web.vaadin.desktop.action;

import com.vaadin.shared.ui.AlignmentInfo;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import fit.pay2play.data.aws.dynamo.entity.Action;
import fit.pay2play.data.aws.dynamo.entity.ActionCategory;
import fit.pay2play.data.aws.dynamo.entity.ActionType;
import fit.pay2play.data.manager.Pay2PlayManager;
import fit.pay2play.web.vaadin.desktop.action.components.ActionsChart;
import fit.pay2play.web.vaadin.desktop.action.components.ActionsGrid;
import fit.pay2play.web.vaadin.desktop.base.Settable;
import org.apache.commons.lang3.StringUtils;
import xyz.cleangone.data.aws.dynamo.entity.person.User;
import xyz.cleangone.web.manager.SessionManager;
import xyz.cleangone.web.vaadin.desktop.actionbar.ActionBar;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static xyz.cleangone.web.vaadin.util.VaadinUtils.*;

public class ActionsLayout extends VerticalLayout implements Settable
{
    private final SessionManager sessionMgr;
    private final User user;
    private Pay2PlayManager p2pMgr = new Pay2PlayManager();
    private ActionAdmin actionAdmin;
    private Component actionsChart;
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
        if (user == null)
        {
            actionsChart = new ActionsChart(user, p2pMgr, sessionMgr.isMobileBrowser(), this);
            addComponents(actionsChart);
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

        reset();
    }

    private void reset()
    {
        if (actionsChart != null) { removeComponent(actionsChart); }
        removeComponent(actionsGridLayout);

        actionsChart = new ActionsChart(user, p2pMgr, sessionMgr.isMobileBrowser(), this);
        setActionsGrid(new Date());

        addComponents(actionsChart, actionsGridLayout);
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

//    private HorizontalLayout getAddPayLayout()
//    {
//        HorizontalLayout layout = getAddLayout("Pay");
//
//        List<ActionCategory> pays = p2pMgr.getEnabledActionCategories(user.getId(), ActionType.Pay);
//        List<ActionCategory> comboBoxPays = new ArrayList<>();
//        for (ActionCategory pay : pays)
//        {
//            if (!StringUtils.isBlank(pay.getDisplayOrder())) { layout.addComponent(createButton(pay)); }
//            else { comboBoxPays.add(pay); }
//        }
//
//        if (!comboBoxPays.isEmpty())
//        {
//            layout.addComponent(createComboBox(comboBoxPays));
//        }
//
//        return wrappedLayout(layout, "payLayout");
//    }
//
//    private HorizontalLayout getAddPlayLayout()
//    {
//        HorizontalLayout layout = getAddLayout("Play");
//
//        List<ActionCategory> plays = p2pMgr.getEnabledActionCategories(user.getId(), ActionType.Play);
//        List<ActionCategory> comboBoxPlays = new ArrayList<>();
//        for (ActionCategory play : plays)
//        {
//            if (!StringUtils.isBlank(play.getDisplayOrder())) { layout.addComponent(createButton(play)); }
//            else { comboBoxPlays.add(play); }
//        }
//
//        if (!comboBoxPlays.isEmpty())
//        {
//            layout.addComponent(createComboBox(comboBoxPlays));
//        }
//
//        return wrappedLayout(layout, "playLayout");
//    }

    private HorizontalLayout getAddPayLayout()
    {
        return getAddActionLayout("Pay", ActionType.Pay, "payLayout");
    }
    private HorizontalLayout getAddPlayLayout()
    {
        return getAddActionLayout("Play", ActionType.Play, "playLayout");
    }

    private HorizontalLayout getAddActionLayout(String name, ActionType actionType, String style)
    {
        HorizontalLayout layout = getAddLayout(name);

        List<ActionCategory> actionCategories = p2pMgr.getEnabledActionCategories(user.getId(), actionType);
        List<ActionCategory> comboBoxActionCategories = new ArrayList<>();
        for (ActionCategory actionCategory : actionCategories)
        {
            if (!StringUtils.isBlank(actionCategory.getDisplayOrder())) { layout.addComponent(createButton(actionCategory)); }
            else { comboBoxActionCategories.add(actionCategory); }
        }

        if (!comboBoxActionCategories.isEmpty())
        {
            layout.addComponent(createComboBox(comboBoxActionCategories));
        }

        return wrappedLayout(layout, style);
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

    private ComboBox<ActionCategory> createComboBox(List<ActionCategory> items)
    {
        ComboBox<ActionCategory> comboBox = new ComboBox<>();

        comboBox.setPlaceholder("");
        comboBox.addStyleName(ValoTheme.TEXTFIELD_TINY);
        comboBox.setTextInputAllowed(false);
        comboBox.setItems(items);
        comboBox.setItemCaptionGenerator(ActionCategory::getDisplayShortName);
        comboBox.setWidth(3, Unit.EM);
        comboBox.setPopupWidth(null);

        comboBox.addSelectionListener(event -> {
            ActionCategory actionCategory = event.getSelectedItem().orElse(null);
            if (actionCategory != null)
            {
                p2pMgr.addAction(actionCategory);
                reset();
            }
        });

        return comboBox;
    }

//    private Button createButton(ActionType actionCategory)
//    {
//        return createButton(actionCategory.getDisplayShortName(), event -> {
//            p2pMgr.addAction(actionCategory);
//            set();
//        });
//    }

    private Button createButton(ActionCategory play)
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
