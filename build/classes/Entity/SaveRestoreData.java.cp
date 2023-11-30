/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Entity;

import java.io.*;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author osmel
 */
public class SaveRestoreData {

    private static final String NOMBRE_ARCHIVO = "datosTabla.ser";
    private Object[][] data;
    private Object[] columnNames;

    public SaveRestoreData(JTable jTable) {
        getDataAndColumnNames(jTable);
    }

    
    public void save() {
        // Crear objeto DatosTabla para la tabla 1
        DataTable dataTable = new DataTable(data, columnNames);

        // Guardar datos de la tabla 1
        saveData(dataTable, "datosTabla1.ser");

        
    }
    
    public void restore() {
//         Cargar datos de la tabla 1
        DataTable dataTable = restoreData("datosTabla1.ser");
        // Hacer lo mismo para las demás tablas...

        dataTable.show();
    }

    private void getDataAndColumnNames(JTable jTable) {

        // Obtener el modelo de datos del JTable
        DefaultTableModel model = (DefaultTableModel) jTable.getModel();

        // Obtener los datos de la tabla
        Object[][] data = new Object[model.getRowCount()][model.getColumnCount()];
        for (int row = 0; row < model.getRowCount(); row++) {
            for (int col = 0; col < model.getColumnCount(); col++) {
                data[row][col] = model.getValueAt(row, col);
            }
        }

        // Obtener los nombres de las columnas
        Object[] columnNames = new Object[model.getColumnCount()];
        for (int col = 0; col < model.getColumnCount(); col++) {
            columnNames[col] = model.getColumnName(col);
        }

        this.data = data;
        this.columnNames = columnNames;
    }

    private void saveData(DataTable datos, String nombreArchivo) {
        try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(nombreArchivo))) {
            outputStream.writeObject(datos);
            System.out.println("Datos guardados correctamente.");
        } catch (IOException e) {
        }
    }

    private DataTable restoreData(String nombreArchivo) {
        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(nombreArchivo))) {
            DataTable datos = (DataTable) inputStream.readObject();
            System.out.println("Datos cargados correctamente.");
            return datos;
        } catch (IOException | ClassNotFoundException e) {
            return null;
        }
    }
}
