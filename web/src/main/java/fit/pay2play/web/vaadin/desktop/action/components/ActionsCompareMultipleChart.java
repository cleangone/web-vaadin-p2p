package fit.pay2play.web.vaadin.desktop.action.components;

import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.*;
import com.vaadin.addon.charts.model.style.Color;
import com.vaadin.addon.charts.model.style.SolidColor;
import com.vaadin.ui.Component;
import fit.pay2play.data.manager.Pay2PlayManager;
import fit.pay2play.web.vaadin.desktop.action.ActionsLayout;
import fit.pay2play.web.vaadin.desktop.action.components.chartExamples.AbstractVaadinChartExample;
import fit.pay2play.web.vaadin.desktop.action.components.chartExamples.StockPrices;
import xyz.cleangone.data.aws.dynamo.entity.person.User;

import java.util.List;

public class ActionsCompareMultipleChart extends AbstractVaadinChartExample
{
    public ActionsCompareMultipleChart(User user, Pay2PlayManager p2pMgr, ActionsLayout actionsLayout)
    {
    }

    @Override
    protected Component getChart()
    {
        final Chart chart = new Chart();
        chart.setHeight("450px");
        chart.setWidth("100%");
        chart.setTimeline(true);

        Configuration configuration = chart.getConfiguration();
        configuration.getTitle().setText("Pay to Play");

        YAxis yAxis = new YAxis();
        Labels label = new Labels();
        //label.setFormatter("(this.value > 0 ? ' + ' : '') + this.value + '%'");
        label.setFormatter("this.value");
        yAxis.setLabels(label);

        PlotLine plotLine = new PlotLine();
        plotLine.setValue(2);
        plotLine.setWidth(2);
        plotLine.setColor(SolidColor.SILVER);
        yAxis.setPlotLines(plotLine);
        configuration.addyAxis(yAxis);

        Tooltip tooltip = new Tooltip();
        tooltip.setPointFormat("<span style=\"color:{series.color}\">{series.name}</span>: <b>{point.y}</b> <br/>");
        tooltip.setValueDecimals(2);
        configuration.setTooltip(tooltip);

        DataSeries paySeries = getDataSeries("Pay", StockPrices.fetchAaplPrice(), SolidColor.RED);
        DataSeries playSeries = getDataSeries("Play", StockPrices.fetchGoogPrice(), SolidColor.GREEN);
        DataSeries totalSeries = getDataSeries("Total", StockPrices.fetchMsftPrice(), SolidColor.BLUE);

        PlotOptionsSeries plotOptionsSeries = new PlotOptionsSeries();
//        plotOptionsSeries.setCompare(Compare.PERCENT);
        plotOptionsSeries.setCompare(Compare.VALUE);

        configuration.setSeries(paySeries, playSeries, totalSeries);
        configuration.setPlotOptions(plotOptionsSeries);
        
        RangeSelector rangeSelector = new RangeSelector();
        rangeSelector.setSelected(4);
        configuration.setRangeSelector(rangeSelector);

        chart.drawChart(configuration);
        return chart;
    }

    private DataSeries getDataSeries(String name, List<StockPrices.PriceData> prices, Color color)
    {
        DataSeries series = new DataSeries();
        series.setName(name);
        PlotOptionsLine plotOptions = new PlotOptionsLine();
        plotOptions.setColor(color);
        series.setPlotOptions(plotOptions);

        for (StockPrices.PriceData data : prices)
        {
            DataSeriesItem item = new DataSeriesItem();
            item.setX(data.getDate());
            item.setY(data.getPrice());
            series.add(item);
        }

        return series;
    }
}

