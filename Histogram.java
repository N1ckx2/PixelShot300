import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.statistics.HistogramType;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Nicholas Vadivelu on 2017-01-11.
 */
public class Histogram extends JPanel{
    private HistogramDataset dataset;
    private int number;
    private ChartPanel CP;
    private JFreeChart chart;

    public Histogram(double[] values, int number){
        super();
        this.number = number;
        XYBarRenderer.setDefaultShadowsVisible(false);
        BarRenderer.setDefaultBarPainter(new StandardBarPainter());
        ChartFactory.setChartTheme(StandardChartTheme.createLegacyTheme());
        //chart = ChartFactory.createHistogram("Photo Histogram", "Luminance", "Frequency", dataset, PlotOrientation.VERTICAL, true, false, false);
        chart = ChartFactory.createHistogram("Histogram", "Normalized Luminance =>", "Frequency =>", dataset, PlotOrientation.VERTICAL, true, false, false);

        CP = new ChartPanel(chart){
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(300, 300);
            }
        };
        CP.setMouseWheelEnabled(true);
        CP.setMouseZoomable(true);
        add(CP);
    }

    public void update(double[] values) {
        dataset = new HistogramDataset();
        dataset.setType(HistogramType.RELATIVE_FREQUENCY);
        dataset.addSeries("Luminance", values, number, 0, 1);
        chart.fireChartChanged();
        ((XYPlot)chart.getPlot()).setDataset(dataset);
    }
}