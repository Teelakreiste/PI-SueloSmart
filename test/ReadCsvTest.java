/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import Entity.ReadCSV;
import java.util.ArrayList;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author osmel
 */
public class ReadCsvTest {

    public ReadCsvTest() {
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
    public void testReadCsv() {
        // Crea una instancia de la clase ReadCSV
        ReadCSV readCSV = new ReadCSV();

        // Llama al método ReadCsv para obtener la lista de idiomas y códigos
        ArrayList<String> result = readCSV.ReadCsv();

        // Realiza las aserciones según lo esperado
        assertNotNull("La lista no debería ser nula", result);
        assertFalse("La lista no debería estar vacía", result.isEmpty());
        assertEquals("La lista debería contener un número par de elementos (código e idioma)", 0, result.size() % 2);

        // Puedes realizar más aserciones específicas según la estructura de tu archivo CSV y la lógica esperada
    }
}
