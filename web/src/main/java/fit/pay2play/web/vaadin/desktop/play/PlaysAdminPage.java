package fit.pay2play.web.vaadin.desktop.play;

import com.vaadin.navigator.View;
import com.vaadin.ui.*;
import com.vaadin.ui.components.grid.HeaderRow;
import com.vaadin.ui.themes.ValoTheme;
import fit.pay2play.data.aws.dynamo.entity.Play;
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

import static fit.pay2play.data.aws.dynamo.entity.Play.*;
import static xyz.cleangone.web.vaadin.util.VaadinUtils.*;

public class PlaysAdminPage extends BaseAdminPage implements View
{
    public static final String NAME = "Play";

    private List<Play> plays = new ArrayList<>();
    private Grid<Play> playGrid;
    private PlayAdmin playAdmin;

    protected PageDisplayType set(SessionManager sessionManager)
    {
        super.set(sessionManager);
        playAdmin = new PlayAdmin(p2pMgr, actionBar, this);

        set();
        return PageDisplayType.NotApplicable;
    }

    public void set()
    {
        resetHeader();

        plays.clear();
        plays.addAll(p2pMgr.getPlays(user.getId()));
        playGrid = new PlayGrid();
        playGrid.setHeightByRows(plays.size() > 5 ? plays.size() + 1 : 5);

        // would like this in set(sessionMgr) but updates are not being reflected in grid
        // caching?
        mainLayout.removeAllComponents();
        mainLayout.addComponents(getAddPlayLayout(), playGrid);
        mainLayout.setExpandRatio(playGrid, 1.0f);
    }

    private Component getAddPlayLayout()
    {
        HorizontalLayout layout = horizontal(VaadinUtils.SIZE_UNDEFINED);

        TextField nameField = VaadinUtils.createGridTextField("Name");

        Button button = new Button("Add Play");
        button.addStyleName(ValoTheme.TEXTFIELD_TINY);
        button.addClickListener(event -> {
            String name = nameField.getValue();

            if (name.length() == 0) { VaadinUtils.showError("Name required"); }
            else
            {
                p2pMgr.save(new Play(name, user.getId()));
                actionBar.displayMessage("Play added");
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

            Grid.Column<Play, LinkButton> nameCol = addLinkButtonColumn(NAME_FIELD, this::buildNameLinkButton);
            nameCol.setComparator((link1, link2) -> link1.getName().compareTo(link2.getName()));

            addBigDecimalColumn(VALUE_FIELD, Play::getValue);
            addDeleteColumn();

            CountingDataProvider<Play> dataProvider = new CountingDataProvider<>(plays, countLabel);
            setDataProvider(dataProvider);

            HeaderRow filterHeader = appendHeaderRow();
            setColumnFiltering(filterHeader, dataProvider);
            appendCountFooterRow(NAME_FIELD);
        }

        private LinkButton buildNameLinkButton(Play play)
        {
            return new LinkButton(play.getName(), e -> {
                playAdmin.setPlay(play);
                setMainLayout(playAdmin);
            });
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