package fit.pay2play.web.vaadin.desktop.action.components;

import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.data.provider.Query;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.components.grid.HeaderRow;
import fit.pay2play.data.aws.dynamo.entity.Action;
import fit.pay2play.data.manager.Pay2PlayManager;
import fit.pay2play.web.vaadin.desktop.action.ActionsLayout;
import xyz.cleangone.data.aws.dynamo.entity.base.EntityField;
import xyz.cleangone.data.aws.dynamo.entity.person.User;
import xyz.cleangone.web.vaadin.ui.EntityGrid;
import xyz.cleangone.web.vaadin.ui.LinkButton;
import xyz.cleangone.web.vaadin.util.CountingDataProvider;
import xyz.cleangone.web.vaadin.util.MultiFieldFilter;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static fit.pay2play.data.aws.dynamo.entity.Action.DESC_FIELD;
import static fit.pay2play.data.aws.dynamo.entity.Action.TOTAL_VALUE_FIELD;

public class ActionsGrid extends EntityGrid<Action>
{
    private static final EntityField ACTION_TYPE_FIELD = new EntityField("pay.actionType", " ");
    private static final EntityField DATE_FIELD = new EntityField("action.createdDate", "Date");
    private static SimpleDateFormat SDF_MMDD = new SimpleDateFormat("MM/dd");

    private final Pay2PlayManager p2pMgr;
    private final ActionsLayout actionsLayout;

    public ActionsGrid(User user, Date date, Pay2PlayManager p2pMgr, ActionsLayout actionsLayout)
    {
        this.p2pMgr = p2pMgr;
        this.actionsLayout = actionsLayout;
        setSizeFull();

        addDateColumn(DATE_FIELD, Action::getCreatedDate, SDF_MMDD, SortDirection.DESCENDING);
        addColumn(ACTION_TYPE_FIELD, Action::getPayPlayDisplay);
        addLinkButtonColumn(DESC_FIELD, this::buildLinkButton, 3);
        addBigDecimalColumn(TOTAL_VALUE_FIELD, Action::getTotalValue);
        addDeleteColumn();

        List<Action> actions = p2pMgr.getActions(user.getId(), date);
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
            actionsLayout.editAction(action);
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
        actionsLayout.set();
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
