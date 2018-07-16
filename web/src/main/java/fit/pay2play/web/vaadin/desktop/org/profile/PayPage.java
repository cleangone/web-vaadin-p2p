package fit.pay2play.web.vaadin.desktop.org.profile;

import com.vaadin.navigator.View;
import com.vaadin.ui.*;
import com.vaadin.ui.components.grid.HeaderRow;
import com.vaadin.ui.themes.ValoTheme;
import fit.pay2play.data.aws.dynamo.entity.Pay;
import fit.pay2play.data.manager.Pay2PlayManager;
import xyz.cleangone.data.aws.dynamo.entity.person.User;
import xyz.cleangone.web.manager.SessionManager;
import xyz.cleangone.web.vaadin.desktop.org.BasePage;
import xyz.cleangone.web.vaadin.ui.EntityGrid;
import xyz.cleangone.web.vaadin.ui.PageDisplayType;
import xyz.cleangone.web.vaadin.util.CountingDataProvider;
import xyz.cleangone.web.vaadin.util.MultiFieldFilter;
import xyz.cleangone.web.vaadin.util.VaadinUtils;

import java.util.ArrayList;
import java.util.List;

import static fit.pay2play.data.aws.dynamo.entity.Pay.*;
import static fit.pay2play.data.aws.dynamo.entity.Play.*;
import static xyz.cleangone.data.aws.dynamo.entity.base.BaseNamedEntity.*;
import static xyz.cleangone.web.vaadin.util.VaadinUtils.*;

public class PayPage extends BasePayPlayPage implements View
{
    public static final String NAME = "Pay";

    private List<Pay> pays = new ArrayList<>();
    private Grid<Pay> payGrid;

    protected void set()
    {
        // todo - need to check user EntityType.Pay, but that means replacing enum w/ extendable class
//        if (changeManager.unchanged(user) &&
//            changeManager.unchanged(user.getId(), EntityType.Action))
//        {
//            return;
//        }

        changeManager.reset(user);

        pays.clear();
        pays.addAll(p2pMgr.getPays(user.getId()));
        payGrid.setHeightByRows(pays.size() > 5 ? pays.size() + 1 : 5);

        // would like this in set(sessionMgr) but updates are not being reflected in grid
        // caching?
        mainLayout.removeAllComponents();

        payGrid = new PayGrid();
        mainLayout.addComponents(getAddPayLayout(), payGrid);
        mainLayout.setExpandRatio(payGrid, 1.0f);
    }

    private Component getAddPayLayout()
    {
        HorizontalLayout layout = horizontal(VaadinUtils.SIZE_UNDEFINED);

        TextField nameField = VaadinUtils.createGridTextField("Name");

        Button button = new Button("Add Pay");
        button.addStyleName(ValoTheme.TEXTFIELD_TINY);
        button.addClickListener(event -> {
            String name = nameField.getValue();

            if (name.length() == 0) { showError("Name required"); }
            else
            {
                p2pMgr.save(new Pay(name, user.getId()));
                set();
            }
        });

        layout.addComponents(nameField, button);
        return layout;
    }

    private class PayGrid extends EntityGrid<Pay>
    {
        PayGrid()
        {
            setSizeFull();

            addSortColumn(NAME_FIELD, Pay::getName, Pay::setName);
            addBigDecimalColumn(VALUE_FIELD, Pay::getValue);
            addBooleanColumn(REQUIRED_FIELD, Pay::isRequired, Pay::setRequired);

            addDeleteColumn();

            getEditor().setEnabled(true);
            getEditor().addSaveListener(event -> {
                Pay pay = event.getBean();
                p2pMgr.save(pay);
                actionBar.displayMessage("Pay updates saved");
                set();
            });

            CountingDataProvider<Pay> dataProvider = new CountingDataProvider<>(pays, countLabel);
            setDataProvider(dataProvider);

            HeaderRow filterHeader = appendHeaderRow();
            setColumnFiltering(filterHeader, dataProvider);
            appendCountFooterRow(NAME_FIELD);
        }

        private void addDeleteColumn()
        {
            addIconButtonColumn(this::buildDeleteButton);
        }
        private Button buildDeleteButton(Pay pay)
        {
            return (buildDeleteButton(pay, pay.getName()));
        }

        @Override
        protected void delete(Pay pay)
        {
            p2pMgr.delete(pay);
            set();
        }

        private void setColumnFiltering(HeaderRow filterHeader, CountingDataProvider<Pay> dataProvider)
        {
            MultiFieldFilter<Pay> filter = new MultiFieldFilter<>(dataProvider);
            addFilterField(NAME_FIELD, Pay::getName, filter, filterHeader);
        }
    }

}
