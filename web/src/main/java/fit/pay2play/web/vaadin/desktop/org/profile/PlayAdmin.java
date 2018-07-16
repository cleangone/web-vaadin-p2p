package fit.pay2play.web.vaadin.desktop.org.profile;

import com.vaadin.ui.*;
import com.vaadin.ui.components.grid.HeaderRow;
import com.vaadin.ui.themes.ValoTheme;
import fit.pay2play.data.aws.dynamo.entity.Play;
import fit.pay2play.data.manager.Pay2PlayManager;
import xyz.cleangone.data.aws.dynamo.entity.person.User;
import xyz.cleangone.web.manager.EntityChangeManager;
import xyz.cleangone.web.manager.SessionManager;
import xyz.cleangone.web.vaadin.desktop.admin.tabs.org.BaseAdmin;
import xyz.cleangone.web.vaadin.ui.EntityGrid;
import xyz.cleangone.web.vaadin.ui.MessageDisplayer;
import xyz.cleangone.web.vaadin.util.CountingDataProvider;
import xyz.cleangone.web.vaadin.util.MultiFieldFilter;
import xyz.cleangone.web.vaadin.util.VaadinUtils;

import java.util.ArrayList;
import java.util.List;

import static fit.pay2play.data.aws.dynamo.entity.Play.VALUE_FIELD;
import static xyz.cleangone.data.aws.dynamo.entity.base.BaseNamedEntity.NAME_FIELD;
import static xyz.cleangone.web.vaadin.util.VaadinUtils.*;

public class PlayAdmin extends BaseAdmin
{
    private Pay2PlayManager p2pMgr;
    private User user;
    private EntityChangeManager changeManager = new EntityChangeManager();

    private List<Play> plays = new ArrayList<>();
    private Grid<Play> playGrid;

    public PlayAdmin(MessageDisplayer msgDisplayer)
    {
        super(msgDisplayer);
        setLayout(this, MARGIN_T, SPACING_TRUE, HEIGHT_100_PCT, BACK_RED);

        playGrid = new PlayGrid();
        addComponents(getAddPayLayout(), playGrid);
        setExpandRatio(playGrid, 1.0f);
    }

    public void set(SessionManager sessionMgr)
    {
        p2pMgr = new Pay2PlayManager();
        user = sessionMgr.getExpectedUser();

        set();
    }

    public void set()
    {
        // todo - need to check user EntityType.Pay, but that means replacing enum w/ extendable class
//        if (changeManager.unchanged(user) &&
//            changeManager.unchanged(user.getId(), EntityType.Action))
//        {
//            return;
//        }

        changeManager.reset(user);

        plays.clear();
        plays.addAll(p2pMgr.getPlays(user.getId()));
        playGrid.setHeightByRows(plays.size() > 5 ? plays.size() + 1 : 5);
    }

    private Component getAddPayLayout()
    {
        HorizontalLayout layout = horizontal(VaadinUtils.SIZE_UNDEFINED);

        TextField nameField = VaadinUtils.createGridTextField("Name");

        Button button = new Button("Add Play");
        button.addStyleName(ValoTheme.TEXTFIELD_TINY);
        button.addClickListener(event -> {
            String name = nameField.getValue();

            if (name.length() == 0) { showError("Name required"); }
            else
            {
                p2pMgr.save(new Play(name, user.getId()));
                set();
            }
        });

        layout.addComponents(nameField, button);
        return layout;
    }

    private class PlayGrid extends EntityGrid<Play>
    {
        PlayGrid()
        {
            setSizeFull();

            addSortColumn(NAME_FIELD, Play::getName, Play::setName);
            addBigDecimalColumn(VALUE_FIELD, Play::getValue);
            addDeleteColumn();

            getEditor().setEnabled(true);
            getEditor().addSaveListener(event -> {
                Play play = event.getBean();
                p2pMgr.save(play);
                msgDisplayer.displayMessage("Play updates saved");
                set();
            });

            CountingDataProvider<Play> dataProvider = new CountingDataProvider<>(plays, countLabel);
            setDataProvider(dataProvider);

            HeaderRow filterHeader = appendHeaderRow();
            setColumnFiltering(filterHeader, dataProvider);
            appendCountFooterRow(NAME_FIELD);
        }

        private void addDeleteColumn()
        {
            addIconButtonColumn(this::buildDeleteButton);
        }
        private Button buildDeleteButton(Play play)
        {
            return (buildDeleteButton(play, play.getName()));
        }

        @Override
        protected void delete(Play play)
        {
            p2pMgr.delete(play);
            set();
        }

        private void setColumnFiltering(HeaderRow filterHeader, CountingDataProvider<Play> dataProvider)
        {
            MultiFieldFilter<Play> filter = new MultiFieldFilter<>(dataProvider);
            addFilterField(NAME_FIELD, Play::getName, filter, filterHeader);
        }
    }
}