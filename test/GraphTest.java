/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import Entity.Graph;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYTextAnnotation;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeriesCollection;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

/**
 *
 * @author osmel
 */
public class GraphTest {

    public GraphTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testDraw() {
        // Mock del objeto JFreeChart para verificar métodos llamados
        JFreeChart mockChart = Mockito.mock(JFreeChart.class);

        // Crear instancia de Graph con el mockChart
        Graph graph = new Graph(new String[][]{});

        // Capturar el argumento pasado al método draw
        ArgumentCaptor<JFreeChart> chartCaptor = ArgumentCaptor.forClass(JFreeChart.class);

        // Llamar al método draw con el mockChart
        graph.draw(mockChart);

        // Llamar al método draw con el mockChart
        graph.draw(mockChart);

    }
}
