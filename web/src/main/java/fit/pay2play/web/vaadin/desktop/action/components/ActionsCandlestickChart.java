package fit.pay2play.web.vaadin.desktop.action.components;

import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.*;
import com.vaadin.ui.Component;
import fit.pay2play.data.manager.Pay2PlayManager;
import fit.pay2play.web.vaadin.desktop.action.ActionsLayout;
import fit.pay2play.web.vaadin.desktop.action.components.chartExamples.AbstractVaadinChartExample;
import fit.pay2play.web.vaadin.desktop.action.components.chartExamples.StockPrices;
import xyz.cleangone.data.aws.dynamo.entity.person.User;

public class ActionsCandlestickChart extends AbstractVaadinChartExample
{
    public ActionsCandlestickChart(User user, Pay2PlayManager p2pMgr, ActionsLayout actionsLayout)
    {
    }

    @Override
    protected Component getChart()
    {
        final Chart chart = new Chart(ChartType.CANDLESTICK);
        chart.setHeight("450px");
        chart.setWidth("100%");
        chart.setTimeline(true);

        Configuration configuration = chart.getConfiguration();
        configuration.getTitle().setText("Pay to Play");

        DataSeries dataSeries = new DataSeries();
        PlotOptionsCandlestick plotOptionsCandlestick = new PlotOptionsCandlestick();
        DataGrouping grouping = new DataGrouping();
        grouping.addUnit(new TimeUnitMultiples(TimeUnit.WEEK, 1));
        grouping.addUnit(new TimeUnitMultiples(TimeUnit.MONTH, 1, 2, 3, 4, 6));
        plotOptionsCandlestick.setDataGrouping(grouping);
        dataSeries.setPlotOptions(plotOptionsCandlestick);
        for (StockPrices.OhlcData data : StockPrices.fetchAaplOhlcPrice())
        {
            OhlcItem item = new OhlcItem();
            item.setX(data.getDate());
            item.setLow(data.getLow());
            item.setHigh(data.getHigh());
            item.setClose(data.getClose());
            item.setOpen(data.getOpen());
            dataSeries.add(item);
        }
        configuration.setSeries(dataSeries);

        RangeSelector rangeSelector = new RangeSelector();
        rangeSelector.setSelected(4);
        configuration.setRangeSelector(rangeSelector);

        chart.drawChart(configuration);

        return chart;
    }
}

