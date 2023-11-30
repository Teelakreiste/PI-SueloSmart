
package Entity;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;

import java.text.DecimalFormat;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;

public final class AtterbergGraph extends JFrame {

    public AtterbergGraph(double[][] values) {
        setParamerts(values);
    }

    public void setParamerts(double[][] values) {

        XYSeries series = new XYSeries("Porcentaje (%)");

        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(series);

        JFreeChart chart = ChartFactory.createXYLineChart(
                "Límites de Atterberg", // Título de la gráfica
                "Número de Golpes", // Título del eje X
                "% de Humedad", // Título del eje Y
                dataset, // Conjunto de datos
                PlotOrientation.VERTICAL,
                true, // Mostrar leyenda
                true, // Usar tooltips
                false // Usar URLs
        );

        XYPlot plot = (XYPlot) chart.getPlot();

        for (int j = 0; j < values[0].length; j++) {
                double x = values[0][j];
                double y = values[1][j];
                series.add(x, y);
        }


        // Dibuja los puntos de intersección
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setBaseLinesVisible(true);
        renderer.setBaseShapesVisible(true);
        plot.setRenderer(renderer);

        // Configura un generador de tooltips para mostrar información al poner el cursor sobre los puntos
        XYToolTipGenerator tooltipGenerator = new StandardXYToolTipGenerator("{0}: ({1}, {2})", new DecimalFormat("0.000"), new DecimalFormat("0.00"));
        renderer.setBaseToolTipGenerator(tooltipGenerator);

//        ChartPanel chartPanel = new ChartPanel(chart);
//        chartPanel.setPreferredSize(new java.awt.Dimension(800, 600));
        draw(chart);
    }

    private void draw(JFreeChart chart) {
        //Show of the graph in the desktop 
        ChartFrame frame = new ChartFrame("Límites de Atterberg", chart);
        frame.pack();
        frame.setVisible(true);
    }
}
