package fit.pay2play.web.vaadin.desktop.action.components;

import com.vaadin.addon.charts.*;
import com.vaadin.addon.charts.model.*;
import com.vaadin.addon.charts.model.style.Color;
import com.vaadin.addon.charts.model.style.SolidColor;
import com.vaadin.ui.Component;
import fit.pay2play.data.aws.dynamo.entity.DayAction;
import fit.pay2play.data.manager.Pay2PlayManager;
import fit.pay2play.web.vaadin.desktop.action.ActionsLayout;
import fit.pay2play.web.vaadin.desktop.action.components.chartExamples.AbstractVaadinChartExample;
import xyz.cleangone.data.aws.dynamo.entity.person.User;

import java.util.List;

public class ActionsChart extends AbstractVaadinChartExample implements PointClickListener
{
    private final User user;
    private final Pay2PlayManager p2pMgr;
    private final boolean isMobileBrowser;
    private final ActionsLayout actionsLayout;

    private DataSeries paySeries = createDataSeries("Pay", SolidColor.GREEN);;
    private DataSeries playSeries = createDataSeries("Play", SolidColor.RED);
    private DataSeries totalSeries = createDataSeries("Total", SolidColor.BLUE);;

    public ActionsChart(User user, Pay2PlayManager p2pMgr, boolean isMobileBrowser, ActionsLayout actionsLayout)
    {
        this.user = user;
        this.p2pMgr = p2pMgr;
        this.isMobileBrowser = isMobileBrowser;
        this.actionsLayout = actionsLayout;

        // chart timeline is utc by default
        Global global = new Global();
        global.setUseUTC(false);
        ChartOptions.get().setGlobal(global);
    }

    @Override
    protected Component getChart()
    {
        final Chart chart = new Chart();
        chart.setHeight(isMobileBrowser ? "300px" : "450px");
        chart.setWidth("100%");
        chart.setTimeline(true);

        Configuration configuration = chart.getConfiguration();

        YAxis yAxis = new YAxis();
        Labels yLabel = new Labels();
        yLabel.setFormatter("this.value");
        yAxis.setLabels(yLabel);

        PlotLine plotLine = new PlotLine();
        plotLine.setValue(0);
        plotLine.setWidth(3);
        plotLine.setColor(SolidColor.SILVER);
        yAxis.setPlotLines(plotLine);
        configuration.addyAxis(yAxis);

        Tooltip tooltip = new Tooltip();
        tooltip.getDateTimeLabelFormats().setMillisecond("%A, %b %e");
        tooltip.setHeaderFormat("{point.key}<br/>");
        tooltip.setPointFormat("<span style=\"color:{series.color}\">{series.name}</span>: <b>{point.y}</b> <br/>");
        tooltip.setValueDecimals(2);
        configuration.setTooltip(tooltip);

        populateDataSeries();

        PlotOptionsSeries plotOptionsSeries = new PlotOptionsSeries();

        configuration.setSeries(paySeries, playSeries, totalSeries);
        configuration.setPlotOptions(plotOptionsSeries);
        
        RangeSelector rangeSelector = new RangeSelector();
        rangeSelector.setButtons(
            new RangeSelectorButton(RangeSelectorTimespan.WEEK, 1, "1w"),
            new RangeSelectorButton(RangeSelectorTimespan.MONTH, 1, "1m"),
            new RangeSelectorButton(RangeSelectorTimespan.MONTH, 3, "3m"),
            new RangeSelectorButton(RangeSelectorTimespan.ALL, 3, "All")
        );
        rangeSelector.setSelected(0);
        configuration.setRangeSelector(rangeSelector);

        // todo - button moved to top left
        ResetZoomButton resetZoom = configuration.getChart().getResetZoomButton();
        Position position = resetZoom.getPosition();
        position.setHorizontalAlign(HorizontalAlign.LEFT);
        position.setVerticalAlign(VerticalAlign.LOW);

        if (isMobileBrowser) { configuration.getNavigator().setEnabled(false); }

        chart.addPointClickListener(this);
        chart.drawChart(configuration);
        return chart;
    }

    private DataSeries createDataSeries(String name, Color color)
    {
        DataSeries series = new DataSeries();
        series.setName(name);
        PlotOptionsLine plotOptions = new PlotOptionsLine();
        plotOptions.setColor(color);
        series.setPlotOptions(plotOptions);

        return series;
    }

    private void populateDataSeries()
    {
        if (user == null)
        {
            populateRandomDataSeries();
            return;
        }

        List<DayAction> dayActions = p2pMgr.getDayActions(user.getId());
        for (DayAction dayAction : dayActions)
        {
            paySeries.add(new DataSeriesItem(dayAction.getTime(), dayAction.getPay()));
            playSeries.add(new DataSeriesItem(dayAction.getTime(), dayAction.getPlay()));
            totalSeries.add(new DataSeriesItem(dayAction.getTime(), dayAction.getPay() + dayAction.getPlay()));
        }
    }

    private void populateRandomDataSeries()
    {
        long oneDay = 1000L * 60L * 60L * 24L;
        long now = (new java.util.Date()).getTime();
        long date = now - (oneDay * 35L);

        while (date <= now)
        {
            double pay = 5 + Math.random()*5;
            double play = -1 * (3 + Math.random()*7);

            paySeries.add(new DataSeriesItem(date, pay));
            playSeries.add(new DataSeriesItem(date, play));
            totalSeries.add(new DataSeriesItem(date, pay + play));

            date += oneDay;
        }
    }

    @Override
    public void onClick(PointClickEvent event)
    {
        DataSeries dataSeries = (DataSeries)event.getSeries();
        int pointIndex = event.getPointIndex();

        DataSeriesItem item = dataSeries.get(pointIndex);
        long time = item.getX().longValue();
        java.util.Date date = new java.util.Date();
        date.setTime(time);

        actionsLayout.setActionsGrid(date);
    }
}

