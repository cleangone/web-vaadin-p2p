package fit.pay2play.web.vaadin.desktop;

import com.vaadin.shared.ui.AlignmentInfo;
import com.vaadin.ui.*;
import com.vaadin.ui.components.grid.HeaderRow;
import com.vaadin.ui.themes.ValoTheme;
import fit.pay2play.data.aws.dynamo.entity.Action;
import fit.pay2play.data.aws.dynamo.entity.Pay;
import fit.pay2play.data.aws.dynamo.entity.Play;
import fit.pay2play.data.manager.Pay2PlayManager;
import xyz.cleangone.data.aws.dynamo.entity.base.EntityField;
import xyz.cleangone.data.aws.dynamo.entity.person.User;
import xyz.cleangone.web.vaadin.ui.EntityGrid;
import xyz.cleangone.web.vaadin.util.CountingDataProvider;
import xyz.cleangone.web.vaadin.util.MultiFieldFilter;

import java.text.SimpleDateFormat;
import java.util.List;

import static fit.pay2play.data.aws.dynamo.entity.Pay.*;
import static fit.pay2play.data.aws.dynamo.entity.Action.*;
import static xyz.cleangone.web.vaadin.util.VaadinUtils.*;

public class ActionsLayout extends VerticalLayout
{
    private static final EntityField ACTION_TYPE_FIELD = new EntityField("pay.actionType", " ");
    private static final EntityField DATE_FIELD = new EntityField("action.createdDate", "Date");
    private static SimpleDateFormat SDF_MMDD = new SimpleDateFormat("MM/dd");

    private Pay2PlayManager p2pMgr = new Pay2PlayManager();
    private User user;

    public ActionsLayout(User user)
    {
        this.user = user;

        setLayout(this, MARGIN_TRUE, SPACING_TRUE, BACK_DK_BLUE);
        set();
    }

    private void set()
    {
        removeAllComponents();

        HorizontalLayout topLayout = horizontal(MARGIN_FALSE, SPACING_TRUE, WIDTH_100_PCT, BACK_RED);
        Component addPlay = getAddPlayLayout();
        topLayout.addComponents(getAddPayLayout(), addPlay);
        topLayout.setComponentAlignment(addPlay, new Alignment(AlignmentInfo.Bits.ALIGNMENT_RIGHT));

        ActionGrid grid = new ActionGrid();
        addComponents(topLayout, grid);
        setExpandRatio(grid, 1.0f);
    }

    private Component getAddPayLayout()
    {
        MyIntegerField amountField = getAmountField();

        List<Pay> pays = p2pMgr.getPays(user.getId());
        ComboBox<Pay> comboBox = new ComboBox<>();
        comboBox.addStyleName(ValoTheme.TEXTFIELD_TINY);
        comboBox.setItems(pays);
        comboBox.setPlaceholder("Select Pay");
        comboBox.setItemCaptionGenerator(Pay::getName);

        Button button = new Button("Add Pay");
        button.addStyleName(ValoTheme.TEXTFIELD_TINY);
        button.addClickListener(event -> {
            Integer amount = amountField.getIntegerValue();
            Pay pay = comboBox.getValue();

            if (amount == null || amount == 0) { showError("Amount required"); }
            else if (pay == null) { showError("Pay required"); }
            else
            {
                p2pMgr.createPayAction(pay, amount);
                set();
            }
        });

        HorizontalLayout layout = horizontal(MARGIN_FALSE, SPACING_TRUE, BACK_GREEN);
        layout.addComponents(amountField, comboBox, button);
        return layout;
    }

    private Component getAddPlayLayout()
    {
        MyIntegerField amountField = getAmountField();

        List<Play> plays = p2pMgr.getPlays(user.getId());
        ComboBox<Play> comboBox = new ComboBox<>();
        comboBox.addStyleName(ValoTheme.TEXTFIELD_TINY);
        comboBox.setItems(plays);
        comboBox.setPlaceholder("Select Play");
        comboBox.setItemCaptionGenerator(Play::getName);

        Button button = new Button("Add Play");
        button.addStyleName(ValoTheme.TEXTFIELD_TINY);
        button.addClickListener(event -> {
            Integer amount = amountField.getIntegerValue();
            Play play = comboBox.getValue();

            if (amount == null || amount == 0) { showError("Amount required"); }
            else if (play == null) { showError("Play required"); }
            else
            {
                p2pMgr.createPlayAction(play, amount);
                set();
            }
        });

        HorizontalLayout layout = horizontal(MARGIN_FALSE, SPACING_TRUE, BACK_BLUE);
        layout.addComponents(amountField, comboBox, button);
        return layout;
    }

    private MyIntegerField getAmountField()
    {
        MyIntegerField amountField = new MyIntegerField();
        amountField.setWidth(6, Unit.EM);
        amountField.addStyleName(ValoTheme.TEXTFIELD_TINY);
        amountField.setPlaceholder("Amount");

        return amountField;
    }

        private class ActionGrid extends EntityGrid<Action>
    {
        ActionGrid()
        {
            setSizeFull();

            addDateColumn(DATE_FIELD, Action::getCreatedDate, SDF_MMDD);
            addColumn(ACTION_TYPE_FIELD, Action::getPayPlayDisplay);
            addColumn(DESC_FIELD, Action::getDescription);
            addBigDecimalColumn(TOTAL_VALUE_FIELD, Action::getTotalValue);

            List<Action> actions = p2pMgr.getActions(user.getId());
            CountingDataProvider<Action> dataProvider = new CountingDataProvider<>(actions, countLabel);
            setDataProvider(dataProvider);

            HeaderRow filterHeader = appendHeaderRow();
            setColumnFiltering(filterHeader, dataProvider);
            appendCountFooterRow(DESC_FIELD);
        }

        private void setColumnFiltering(HeaderRow filterHeader, CountingDataProvider<Action> dataProvider)
        {
            MultiFieldFilter<Action> filter = new MultiFieldFilter<>(dataProvider);
            addFilterField(ACTION_TYPE_FIELD, Action::getPayPlayDisplay, filter, filterHeader);
            addFilterField(DESC_FIELD, Action::getDescription, filter, filterHeader);
        }
    }

    private void showError(String msg) { Notification.show(msg, Notification.Type.ERROR_MESSAGE); }
}
