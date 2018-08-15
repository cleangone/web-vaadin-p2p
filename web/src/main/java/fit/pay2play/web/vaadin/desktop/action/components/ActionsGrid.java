package fit.pay2play.web.vaadin.desktop.action.components;

import com.vaadin.data.ValueProvider;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.data.provider.Query;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
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
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import static fit.pay2play.data.aws.dynamo.entity.Action.*;

public class ActionsGrid extends EntityGrid<Action>
{
    private static final EntityField ACTION_TYPE_FIELD = new EntityField("actionCategory.actionCategory", " ");
    private static final EntityField DATE_FIELD = new EntityField(UPDATED_DATE_FIELD, "Date");
    private static SimpleDateFormat SDF_MMDD = new SimpleDateFormat("MM/dd");

    private final ActionsLayout actionsLayout;

    public ActionsGrid(User user, Date date, Pay2PlayManager p2pMgr, boolean isMobileBrowser, ActionsLayout actionsLayout)
    {
        this.actionsLayout = actionsLayout;
        setSizeFull();

        if (!isMobileBrowser)
        {
            addDateColumn(DATE_FIELD, Action::getUpdatedDate, SDF_MMDD);
            addColumn(ACTION_TYPE_FIELD, Action::getActionTypeDisplay);
        }


        Column descCol = addLinkButtonOrLabelColumn(DESC_FIELD, this::buildLinkButton, 3);
        if (isMobileBrowser) { descCol.setCaption(SDF_MMDD.format(date)); }



        Column<Action, BigDecimal> totalCol = addBigDecimalColumn(TOTAL_VALUE_FIELD, Action::getTotalValue);

        List<Action> actions = p2pMgr.getActionsWithAdjustments(user.getId(), date);
        actions.sort(Comparator.comparing(Action::getUpdatedDate).reversed());
        if (isMobileBrowser) { totalCol.setCaption("Total: " + p2pMgr.sumTotalValue(actions)); }

        CountingDataProvider<Action> dataProvider = new CountingDataProvider<>(actions, countLabel);
        setDataProvider(dataProvider);
        setHeightByRows(actions.size() + 1);

        if (!isMobileBrowser)
        {
            HeaderRow filterHeader = appendHeaderRow();
            setColumnFiltering(filterHeader, dataProvider);
            filterHeader.getCell(TOTAL_VALUE_FIELD.getName()).setComponent(new Label(calculateTotal(dataProvider)));
            appendCountFooterRow(DESC_FIELD);
        }
    }

    // todo - yet another hack for action adjustments
    protected Column addLinkButtonOrLabelColumn(EntityField entityField, ValueProvider<Action, Component> valueProvider, int expandRatio)
    {
        return this.addComponentColumn(valueProvider).setId(entityField.getName()).setExpandRatio(expandRatio);
    }

    private Component buildLinkButton(Action action)
    {
        // todo - hack
        if (action.isAdjustment())
        {
            Label label = new Label(action.getDescription());
            label.addStyleName("marginLeft");
            return label;
        }
        else
            return new LinkButton(action.getDescription(), e -> actionsLayout.editAction(action));
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
        addFilterField(ACTION_TYPE_FIELD, Action::getActionTypeDisplay, filter, filterHeader);
        addFilterField(DESC_FIELD, Action::getDescription, filter, filterHeader);
    }
}
