/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Entity;

import java.io.Serializable;

/**
 *
 * @author osmel
 */
public class DataTable implements Serializable {

    // Define las variables que deseas almacenar para cada tabla
    private Object[][] data;
    private Object[] columnNames;

    public DataTable(Object[][] data, Object[] columnNames) {
        this.data = data;
        this.columnNames = columnNames;
    }

    public Object[][] getData() {
        return data;
    }

    public Object[] getColumnNames() {
        return columnNames;
    }
}
