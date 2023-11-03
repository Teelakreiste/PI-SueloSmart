package Entity;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

public class GranulometryGraph extends javax.swing.JFrame {

    public GranulometryGraph(String[][] values) {
        createGranulometryChart(values);
    }

    private void createGranulometryChart(String[][] values) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        for (int i = 0; i < values.length; i++) {
            try {
                double x = Math.log10(Double.parseDouble(values[i][1].replace("mm", ""))); // Aplica el logaritmo base 10
                double y = Double.parseDouble(values[i][4]);
                dataset.addValue(y, "Porcentaje", Double.toString(x));
            } catch (NumberFormatException | NullPointerException e) {
                // Manejo de errores si los datos no son válidos
            }
        }

        JFreeChart chart = ChartFactory.createLineChart(
                "Distribución Granulométrica", // Título del gráfico
                "Tamaño de partícula (log)", // Título del eje X
                "Porcentaje (%)", // Título del eje Y
                dataset, // Conjunto de datos
                PlotOrientation.VERTICAL,
                true, // Mostrar leyenda
                true, // Usar tooltips
                false // Usar URLs
        );

        CategoryPlot plot = chart.getCategoryPlot();
        NumberAxis yAxis = new NumberAxis("Porcentaje (%)"); // Configura el eje Y

        // Establece el rango del eje Y
        yAxis.setRange(0, 100);
        yAxis.setTickUnit(new NumberTickUnit(10));

        plot.setRangeAxis(yAxis);

        // Configura las etiquetas del eje X para mostrar valores logarítmicos
        CategoryAxis xAxis = plot.getDomainAxis();
        xAxis.setLabel("Tamaño de partícula (log)");

        // Dibuja los puntos de intersección
        LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot.getRenderer();
        renderer.setBaseLinesVisible(true);
        renderer.setBaseShapesVisible(true);

        // Muestra el gráfico en una ventana
        ChartFrame frame = new ChartFrame("Distribución Granulométrica", chart);
        frame.pack();
        frame.setVisible(true);
    }

}
