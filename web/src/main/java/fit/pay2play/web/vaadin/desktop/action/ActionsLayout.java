package fit.pay2play.web.vaadin.desktop.action;

import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.data.provider.Query;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.shared.ui.AlignmentInfo;
import com.vaadin.ui.*;
import com.vaadin.ui.components.grid.HeaderRow;
import com.vaadin.ui.themes.ValoTheme;
import fit.pay2play.data.aws.dynamo.entity.Action;
import fit.pay2play.data.aws.dynamo.entity.Pay;
import fit.pay2play.data.aws.dynamo.entity.Play;
import fit.pay2play.data.manager.Pay2PlayManager;
import fit.pay2play.web.vaadin.desktop.base.MyIntegerField;
import fit.pay2play.web.vaadin.desktop.base.Settable;
import xyz.cleangone.data.aws.dynamo.entity.base.EntityField;
import xyz.cleangone.data.aws.dynamo.entity.person.User;
import xyz.cleangone.web.vaadin.ui.EntityGrid;
import xyz.cleangone.web.vaadin.ui.LinkButton;
import xyz.cleangone.web.vaadin.ui.MessageDisplayer;
import xyz.cleangone.web.vaadin.util.CountingDataProvider;
import xyz.cleangone.web.vaadin.util.MultiFieldFilter;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Collectors;

import static fit.pay2play.data.aws.dynamo.entity.Action.*;
import static xyz.cleangone.web.vaadin.util.VaadinUtils.*;

public class ActionsLayout extends VerticalLayout implements Settable
{
    private static final EntityField ACTION_TYPE_FIELD = new EntityField("pay.actionType", " ");
    private static final EntityField DATE_FIELD = new EntityField("action.createdDate", "Date");
    private static SimpleDateFormat SDF_MMDD = new SimpleDateFormat("MM/dd");

    private User user;
    private Pay2PlayManager p2pMgr = new Pay2PlayManager();
    private ActionAdmin actionAdmin;

    public ActionsLayout(User user, MessageDisplayer messageDisplayer)
    {
        this.user = user;

        actionAdmin = new ActionAdmin(p2pMgr, messageDisplayer, this);

        setLayout(this, MARGIN_TRUE, SPACING_TRUE, BACK_DK_BLUE);
        set();
    }

    public void set()
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
                    p2pMgr.createAction(pay);
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
                    p2pMgr.createAction(play);
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
            p2pMgr.createAction(pay);
            set();
        });
    }

    private Button createButton(Play play)
    {
        return createButton(play.getName(), event -> {
            p2pMgr.createAction(play);
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


    private MyIntegerField getAmountField()
    {
        MyIntegerField amountField = new MyIntegerField();
        amountField.setWidth(6, Unit.EM);
        amountField.addStyleName(ValoTheme.TEXTFIELD_TINY);
        amountField.setPlaceholder("Amount");

        return amountField;
    }


//    private class ActionTreeGrid extends EntityTreeGrid<Action>
//    {
//        ActionTreeGrid()
//        {
//            TreeDataProvider<Action> dataProvider = (TreeDataProvider<Action>)getDataProvider();
//
//            TreeData<Action> data = dataProvider.getData();
//
//            data.addItem(null, newProject);
//            data.addItems(newProject, newProject.getChildren());
//
//// after adding / removing data, data provider needs to be refreshed
//            dataProvider.refreshAll();
//        }
//    }



    private class ActionGrid extends EntityGrid<Action>
    {
        ActionGrid()
        {
            setSizeFull();

            addDateColumn(DATE_FIELD, Action::getCreatedDate, SDF_MMDD, SortDirection.DESCENDING);
            addColumn(ACTION_TYPE_FIELD, Action::getPayPlayDisplay);
            addLinkButtonColumn(DESC_FIELD, this::buildLinkButton, 3);
            addBigDecimalColumn(TOTAL_VALUE_FIELD, Action::getTotalValue);
            addDeleteColumn();

            List<Action> actions = p2pMgr.getActions(user.getId());
            CountingDataProvider<Action> dataProvider = new CountingDataProvider<>(actions, countLabel);
            setDataProvider(dataProvider);

            HeaderRow filterHeader = appendHeaderRow();
            setColumnFiltering(filterHeader, dataProvider);
            appendCountFooterRow(DESC_FIELD);

            filterHeader.getCell(TOTAL_VALUE_FIELD.getName()).setComponent(new Label(calculateTotal(dataProvider)));
        }

        private LinkButton buildLinkButton(Action action)
        {
            return new LinkButton(action.getDescription(), e -> {
                actionAdmin.set(action);
                removeAllComponents();
                addComponents(actionAdmin);
            });
        }

        private void addDeleteColumn()
        {
            addIconButtonColumn(this::buildDeleteButton);
        }
        private Button buildDeleteButton(Action action)
        {
            return (buildDeleteButton(action, action.getDescription()));
        }

        @Override
        protected void delete(Action action)
        {
            p2pMgr.delete(action);
            set();
        }

        private String calculateTotal(ListDataProvider<Action> provider)
        {
            return String.valueOf(provider.fetch(new Query<>())
                .map(Action::getTotalValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add));
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
