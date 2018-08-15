package fit.pay2play.web.vaadin.desktop.actionCategory;

import com.vaadin.navigator.View;
import com.vaadin.ui.*;
import com.vaadin.ui.components.grid.HeaderRow;
import com.vaadin.ui.themes.ValoTheme;
import fit.pay2play.data.aws.dynamo.entity.ActionCategory;
import fit.pay2play.data.aws.dynamo.entity.ActionType;
import fit.pay2play.web.vaadin.desktop.base.BaseAdminPage;
import xyz.cleangone.web.manager.SessionManager;
import xyz.cleangone.web.vaadin.ui.EntityGrid;
import xyz.cleangone.web.vaadin.ui.LinkButton;
import xyz.cleangone.web.vaadin.ui.PageDisplayType;
import xyz.cleangone.web.vaadin.util.CountingDataProvider;
import xyz.cleangone.web.vaadin.util.MultiFieldFilter;
import xyz.cleangone.web.vaadin.util.VaadinUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static fit.pay2play.data.aws.dynamo.entity.ActionCategory.*;
import static xyz.cleangone.web.vaadin.util.VaadinUtils.horizontal;

public abstract class ActionCategoriesAdminPage extends BaseAdminPage implements View
{
    protected Set<String> usedActionCategoryIds = new HashSet<>();
    protected List<ActionCategory> actionCategories = new ArrayList<>();
    protected Grid<ActionCategory> grid;
    protected ActionCategoryAdmin adminLayout;

    protected PageDisplayType set(SessionManager sessionManager)
    {
        super.set(sessionManager);
        adminLayout = new ActionCategoryAdmin(p2pMgr, actionBar, this);

        set();
        return PageDisplayType.NotApplicable;
    }

    protected void set(ActionType actionType)
    {
        resetHeader();

        usedActionCategoryIds = p2pMgr.getActiveActionCategoryIds(user.getId());
        actionCategories.clear();
        actionCategories.addAll(p2pMgr.getActionCategories(user.getId(), actionType));

        grid = new ActionCategoryGrid();
        grid.setHeightByRows(actionCategories.size() > 5 ? actionCategories.size() + 1 : 5);

        // would like this in set(sessionMgr) but updates are not being reflected in grid
        // caching?
        mainLayout.removeAllComponents();
        mainLayout.addComponents(getAddActionTypeLayout(actionType), grid);
        mainLayout.setExpandRatio(grid, 1.0f);
    }

    protected Component getAddActionTypeLayout(ActionType actionType)
    {
        HorizontalLayout layout = horizontal(VaadinUtils.SIZE_UNDEFINED);

        TextField nameField = VaadinUtils.createGridTextField("Name");

        Button button = new Button("Add " + actionType);
        button.addStyleName(ValoTheme.TEXTFIELD_TINY);
        button.addClickListener(event -> {
            String name = nameField.getValue();

            if (name.length() == 0) { VaadinUtils.showError("Name required"); }
            else
            {
                p2pMgr.createActionCategory(name, user.getId(), actionType);
                actionBar.displayMessage(actionType + " added");
                set();
            }
        });

        layout.addComponents(nameField, button);
        return layout;
    }

    private class ActionCategoryGrid extends EntityGrid<ActionCategory>
    {
        ActionCategoryGrid()
        {
            setSizeFull();

            Grid.Column<ActionCategory, LinkButton> nameCol = addLinkButtonColumn(NAME_FIELD, this::buildNameLinkButton);
            nameCol.setComparator((link1, link2) -> link1.getName().compareTo(link2.getName()));

            addColumn(SHORT_NAME_FIELD, ActionCategory::getShortName);
            addBooleanColumn(ENABLED_FIELD, ActionCategory::getEnabled);
            addColumn(DISPLAY_ORDER_FIELD, ActionCategory::getDisplayOrder);
            addBigDecimalColumn(VALUE_FIELD, ActionCategory::getValue);
            addDeleteColumn();

            CountingDataProvider<ActionCategory> dataProvider = new CountingDataProvider<>(actionCategories, countLabel);
            setDataProvider(dataProvider);

            HeaderRow filterHeader = appendHeaderRow();
            setColumnFiltering(filterHeader, dataProvider);
            appendCountFooterRow(NAME_FIELD);
        }

        private void addDeleteColumn()
        {
            addIconButtonColumn(this::buildDeleteButton);
        }
        private Button buildDeleteButton(ActionCategory actionType)
        {
            Button button = buildDeleteButton(actionType, actionType.getName());
            if (usedActionCategoryIds.contains(actionType.getId())) { button.setEnabled(false); }
            return button;
        }

        private LinkButton buildNameLinkButton(ActionCategory actionCategory)
        {
            return new LinkButton(actionCategory.getName(), e -> {
                adminLayout.set(actionCategory);
                setMainLayout(adminLayout);
            });
        }

        @Override
        protected void delete(ActionCategory actionCategory)
        {
            p2pMgr.delete(actionCategory);
            set();
        }

        private void setColumnFiltering(HeaderRow filterHeader, CountingDataProvider<ActionCategory> dataProvider)
        {
            MultiFieldFilter<ActionCategory> filter = new MultiFieldFilter<>(dataProvider);
            addFilterField(NAME_FIELD, ActionCategory::getName, filter, filterHeader);
        }
    }

}