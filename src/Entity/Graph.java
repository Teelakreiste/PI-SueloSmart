/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Entity;

import java.awt.Font;
import java.text.DecimalFormat;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYTextAnnotation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.axis.LogAxis; // Importante
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public final class Graph extends javax.swing.JFrame {

    public Graph(String[][] values) {
        setParamerts(values);
    }

    public void setParamerts(String[][] values) {

        XYSeries series = new XYSeries("Porcentaje (%)");

        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(series);

        JFreeChart chart = ChartFactory.createXYLineChart(
                "Distribución Granulométrica", // Título de la gráfica
                "Tamaño del grano (mm)", // Título del eje X
                "Porcentaje que pasa (%)", // Título del eje Y
                dataset, // Conjunto de datos
                PlotOrientation.VERTICAL,
                true, // Mostrar leyenda
                true, // Usar tooltips
                false // Usar URLs
        );

        XYPlot plot = (XYPlot) chart.getPlot();

        for (String[] value : values) {
            try {
                double x = Double.parseDouble(String.valueOf(value[1]));
                double y = Double.parseDouble(String.valueOf(value[4]));
                series.add(x, y);
                // Agrega etiquetas al punto de intersección
                XYTextAnnotation annotation = new XYTextAnnotation(String.valueOf(value[0]), x, y);
                annotation.setFont(new Font("SansSerif", Font.PLAIN, 10)); // Puedes ajustar el tamaño y el estilo de fuente
                annotation.setRotationAngle(Math.PI / 8); // Puedes ajustar el ángulo de la etiqueta
                plot.addAnnotation(annotation); // Agrega la anotación al XYPlot
            } catch (NumberFormatException | NullPointerException e) {

            }
        }

        // Arena
        XYSeries seriesAux1 = new XYSeries("Arena");
        seriesAux1.add(4.75, 0);
        seriesAux1.add(4.75, 100);
        dataset.addSeries(seriesAux1);

        // Arcilla
        XYSeries seriesAux2 = new XYSeries("Arcilla");
        seriesAux2.add(0.074, 0);
        seriesAux2.add(0.074, 100);
        dataset.addSeries(seriesAux2);

        LogAxis xAxis = new LogAxis("Tamaño del grano (mm)"); // Configura el eje X como logarítmico
        NumberAxis yAxis = new NumberAxis("Porcentaje que pasa (%)"); // Configura el eje Y

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

        // Configura un generador de tooltips para mostrar información al poner el cursor sobre los puntos
        XYToolTipGenerator tooltipGenerator = new StandardXYToolTipGenerator("{0}: ({1}, {2})", new DecimalFormat("0.000"), new DecimalFormat("0.00"));
        renderer.setBaseToolTipGenerator(tooltipGenerator);

//        ChartPanel chartPanel = new ChartPanel(chart);
//        chartPanel.setPreferredSize(new java.awt.Dimension(800, 600));
        draw(chart);
    }

    public void draw(JFreeChart chart) {
        //Show of the graph in the desktop 
        ChartFrame frame = new ChartFrame("Distribución Granulométrica", chart);
        frame.pack();
        frame.setVisible(true);
    }
}
