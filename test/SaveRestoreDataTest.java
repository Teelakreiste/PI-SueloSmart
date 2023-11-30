/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import Entity.DataTable;
import Entity.SaveRestoreData;
import javax.swing.JTable;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author osmel
 */
public class SaveRestoreDataTest extends SaveRestoreData {

    private SaveRestoreData saveRestoreData;

    @Before
    public void setUp() {
        saveRestoreData = new SaveRestoreData();
    }

    @Test
    public void testOpenProject() {
        // Assuming you have a project folder with some .sm files for testing
        String response = saveRestoreData.openProject();

        assertEquals("Project opened successfully from " + saveRestoreData.getPROJECT_FOLDER(), response);
        assertTrue(saveRestoreData.getSize() > 0);
    }

    @Test
    public void testSaveProject() {
        saveRestoreData.getProjectName();
        // Agrega una tabla de ejemplo al SaveRestoreData
        JTable jTable = new JTable();
        saveRestoreData.addTable(jTable, "testTable");

        // Guarda el proyecto y obtén la respuesta
        String response = saveRestoreData.saveProject();

        // Verifica que la respuesta indique un guardado exitoso y el nombre del proyecto
        assertTrue(response.startsWith("Project saved successfully"));
        assertTrue(response.endsWith(saveRestoreData.getPROJECT_FOLDER()));

        // Aquí podrías realizar más verificaciones según tu lógica de guardado
        // Por ejemplo, podrías verificar si se crearon archivos .sm en la carpeta correcta
    }

    @Test
    public void testRemovePrefixAndExtension() {
        // Caso de prueba cuando se proporciona un nombre de archivo con prefijo y extensión
        String result1 = SaveRestoreData.RemovePrefixYExtension("1_testFile.sm");
        assertEquals("testFile", result1);

        // Caso de prueba cuando se proporciona un nombre de archivo sin prefijo pero con extensión
        String result2 = SaveRestoreData.RemovePrefixYExtension("exampleFile.sm");
        assertEquals("exampleFile", result2);

        // Caso de prueba cuando se proporciona un nombre de archivo sin prefijo ni extensión
        String result3 = SaveRestoreData.RemovePrefixYExtension("filename");
        assertEquals("filename", result3);

        // Caso de prueba cuando se proporciona un nombre de archivo sin extensión pero con prefijo
        String result4 = SaveRestoreData.RemovePrefixYExtension("2_anotherFile");
        assertEquals("anotherFile", result4);

        // Caso de prueba cuando se proporciona un nombre de archivo vacío
        String result5 = SaveRestoreData.RemovePrefixYExtension("");
        assertEquals("", result5);
    }

    @Test
    public void testRestoreData() {
        SaveRestoreData saveRestoreData = new SaveRestoreData();

        // Crear una DataTable de ejemplo
        Object[][] data = {{1, "Item 1", 10.0}, {2, "Item 2", 20.0}};
        String[] columnNames = {"ID", "Name", "Value"};
        DataTable expectedDataTable = new DataTable(data, columnNames);

        // Guardar la DataTable en un archivo temporal
        String tempFileName = "tempTestFile.sm";
        saveRestoreData.saveData(expectedDataTable, saveRestoreData.getFOLDER() + "\\" + saveRestoreData.getPROJECT_FOLDER(), tempFileName);

        // Restaurar la DataTable desde el archivo temporal
        DataTable actualDataTable = saveRestoreData.restoreData(saveRestoreData.getFOLDER() + "\\" + saveRestoreData.getPROJECT_FOLDER(), tempFileName);

        // Verificar que la DataTable restaurada sea igual a la DataTable original
        assertNotNull(actualDataTable);
        assertArrayEquals(expectedDataTable.getColumnNames(), actualDataTable.getColumnNames());
        assertArrayEquals(expectedDataTable.getData(), actualDataTable.getData());

        // Borrar el archivo temporal
        java.io.File tempFile = new java.io.File(saveRestoreData.getFOLDER() + "\\" + saveRestoreData.getPROJECT_FOLDER() + java.io.File.separator + tempFileName);
        tempFile.delete();
    }
}
