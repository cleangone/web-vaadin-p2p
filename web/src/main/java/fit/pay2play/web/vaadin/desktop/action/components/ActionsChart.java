package fit.pay2play.web.vaadin.desktop.action.components;

import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.ChartClickListener;
import com.vaadin.addon.charts.PointClickEvent;
import com.vaadin.addon.charts.PointClickListener;
import com.vaadin.addon.charts.model.*;
import com.vaadin.addon.charts.model.style.Color;
import com.vaadin.addon.charts.model.style.SolidColor;
import com.vaadin.ui.Component;
import fit.pay2play.data.aws.dynamo.entity.DayAction;
import fit.pay2play.data.manager.Pay2PlayManager;
import fit.pay2play.web.vaadin.desktop.action.ActionsLayout;
import fit.pay2play.web.vaadin.desktop.action.components.chartExamples.AbstractVaadinChartExample;
import xyz.cleangone.data.aws.dynamo.entity.person.User;

import java.util.Date;
import java.util.List;

public class ActionsChart extends AbstractVaadinChartExample implements PointClickListener
{
    private final User user;
    private final Pay2PlayManager p2pMgr;
    private final ActionsLayout actionsLayout;

    private DataSeries paySeries = createDataSeries("Pay", SolidColor.GREEN);;
    private DataSeries playSeries = createDataSeries("Play", SolidColor.RED);
    private DataSeries totalSeries = createDataSeries("Total", SolidColor.BLUE);;

    public ActionsChart(User user, Pay2PlayManager p2pMgr, ActionsLayout actionsLayout)
    {
        this.user = user;
        this.p2pMgr = p2pMgr;
        this.actionsLayout = actionsLayout;
    }

    @Override
    protected Component getChart()
    {
        final Chart chart = new Chart();
        chart.setHeight("450px");
        chart.setWidth("100%");
        chart.setTimeline(true);

        Configuration configuration = chart.getConfiguration();
//        configuration.getTitle().setText("Pay to Play");

        YAxis yAxis = new YAxis();
        Labels label = new Labels();
        label.setFormatter("this.value");
        yAxis.setLabels(label);

        PlotLine plotLine = new PlotLine();
        plotLine.setValue(0);
        plotLine.setWidth(3);
        plotLine.setColor(SolidColor.SILVER);
        yAxis.setPlotLines(plotLine);
        configuration.addyAxis(yAxis);

        Tooltip tooltip = new Tooltip();
        tooltip.setPointFormat("<span style=\"color:{series.color}\">{series.name}</span>: <b>{point.y}</b> <br/>");
        tooltip.setValueDecimals(2);
        configuration.setTooltip(tooltip);

        populateDataSeries();

        PlotOptionsSeries plotOptionsSeries = new PlotOptionsSeries();

        configuration.setSeries(paySeries, playSeries, totalSeries);
        configuration.setPlotOptions(plotOptionsSeries);
        
        RangeSelector rangeSelector = new RangeSelector();
        rangeSelector.setSelected(0); // 1m, 3m, 6m, ytd, 1y all
        configuration.setRangeSelector(rangeSelector);

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

        actionsLayout.setGrid(date);
    }
}

