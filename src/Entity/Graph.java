/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Entity;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.axis.LogAxis; // Importante
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public final class Graph extends javax.swing.JFrame {

    double[] xValues;

    int[] yValues = {0, 10, 20, 30, 40, 50, 60, 70, 80, 90, 100};

    public Graph(String[][] values) {
        setParamerts(values);
    }

    private void getXValues(String[][] values) {
        double[] Xvalue = new double[values.length];

        for (int i = 0; i < values.length; i++) {
            Xvalue[i] = Double.parseDouble(String.valueOf(values[i][1]));
        }

        this.xValues = Xvalue;
    }

    public void setParamerts(String[][] values) {

        XYSeries series = new XYSeries("Porcentaje (%)");
        for (int i = 0; i < values.length; i++) {
            try {
                double x = Math.pow(10, i);
                double y = Double.parseDouble(String.valueOf(values[i][4]));
                series.add(x, y);
            } catch (NumberFormatException | NullPointerException e) {

            }
        }

//        for (int i = 1; i <= 10; i++) {
//            double x = Math.pow(10, i); // Valores X logarítmicos
//            double y = i * 10; // Valores Y
//            series.add(x, y);
//        }
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(series);

        JFreeChart chart = ChartFactory.createXYLineChart(
                "Distribución Granulométrica", // Título de la gráfica
                "Tamaño de partícula (log)", // Título del eje X
                "Porcentaje de retención (%)", // Título del eje Y
                dataset, // Conjunto de datos
                PlotOrientation.VERTICAL,
                true, // Mostrar leyenda
                true, // Usar tooltips
                false // Usar URLs
        );

        XYPlot plot = (XYPlot) chart.getPlot();
        LogAxis xAxis = new LogAxis("Tamaño de partícula (log)"); // Configura el eje X como logarítmico
        NumberAxis yAxis = new NumberAxis("Porcentaje de retención (%)"); // Configura el eje Y

        // Establece el rango del eje Y
        yAxis.setRange(0, 100);
        yAxis.setTickUnit(new NumberTickUnit(10));

        plot.setDomainAxis(xAxis);
        plot.setRangeAxis(yAxis);

        // Dibuja los puntos de intersección
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setBaseLinesVisible(true);
        renderer.setBaseShapesVisible(true);
        plot.setRenderer(renderer);

//        ChartPanel chartPanel = new ChartPanel(chart);
//        chartPanel.setPreferredSize(new java.awt.Dimension(800, 600));
        draw(chart);
    }

    private void draw(JFreeChart chart) {
        //Show of the graph in the desktop 
        ChartFrame frame = new ChartFrame("Distribución Granulométrica", chart);
        frame.pack();
        frame.setVisible(true);
    }
}
