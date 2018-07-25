package fit.pay2play.web.vaadin.desktop.pay;

import com.vaadin.navigator.View;
import com.vaadin.ui.*;
import com.vaadin.ui.components.grid.HeaderRow;
import com.vaadin.ui.themes.ValoTheme;
import fit.pay2play.data.aws.dynamo.entity.Pay;
import fit.pay2play.web.vaadin.desktop.base.BaseAdminPage;
import xyz.cleangone.web.manager.SessionManager;
import xyz.cleangone.web.vaadin.ui.EntityGrid;
import xyz.cleangone.web.vaadin.ui.LinkButton;
import xyz.cleangone.web.vaadin.ui.PageDisplayType;
import xyz.cleangone.web.vaadin.util.CountingDataProvider;
import xyz.cleangone.web.vaadin.util.MultiFieldFilter;
import xyz.cleangone.web.vaadin.util.VaadinUtils;

import java.util.ArrayList;
import java.util.List;

import static fit.pay2play.data.aws.dynamo.entity.Pay.*;
import static xyz.cleangone.web.vaadin.util.VaadinUtils.*;

public class PaysAdminPage extends BaseAdminPage implements View
{
    public static final String NAME = "Pay";

    private List<Pay> pays = new ArrayList<>();
    private Grid<Pay> payGrid;
    private PayAdmin payAdmin;

    protected PageDisplayType set(SessionManager sessionManager)
    {
        super.set(sessionManager);
        payAdmin = new PayAdmin(p2pMgr, actionBar, PaysAdminPage.this);

        set();
        return PageDisplayType.NotApplicable;
    }

    public void set()
    {
        // todo - need to check user EntityType.Pay, but that means replacing enum w/ extendable class
//        if (changeManager.unchanged(user) &&
//            changeManager.unchanged(user.getId(), EntityType.Action))
//        {
//            return;
//        }

        resetHeader();
        changeManager.reset(user);

        pays.clear();
        pays.addAll(p2pMgr.getPays(user.getId()));
        payGrid = new PayGrid();
        payGrid.setHeightByRows(pays.size() > 5 ? pays.size() + 1 : 5);

        // would like this in set(sessionMgr) but updates are not being reflected in grid
        // caching?
        mainLayout.removeAllComponents();
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

            Grid.Column<Pay, LinkButton> nameCol = addLinkButtonColumn(NAME_FIELD, this::buildNameLinkButton);
            nameCol.setComparator((link1, link2) -> link1.getName().compareTo(link2.getName()));

            addColumn(DISPLAY_ORDER_FIELD, Pay::getDisplayOrder);
            addBigDecimalColumn(VALUE_FIELD, Pay::getValue);
            addBooleanColumn(REQUIRED_FIELD, Pay::isRequired, Pay::setRequired);
            addDeleteColumn();

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

        private LinkButton buildNameLinkButton(Pay pay)
        {
            return new LinkButton(pay.getName(), e -> {
                payAdmin.setPay(pay);
                setMainLayout(payAdmin);
            });
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
