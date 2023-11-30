package Entity;

import java.awt.HeadlessException;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class SaveRestoreData {

    private String PROJECT_FOLDER = "";
    private String FOLDER = "";

    private final List<DataTable> tables = new ArrayList<>();
    private final List<String> tableNames = new ArrayList<>();

    public SaveRestoreData() {
        File directory = new File(System.getProperty("user.dir") + "\\saves");
        if (!directory.exists()) {
            directory.mkdir();
        }
        FOLDER = directory.toString();
    }

    public String getPROJECT_FOLDER() {
        return PROJECT_FOLDER;
    }

    public String getProjectName() {
        String projectName = JOptionPane.showInputDialog("Enter the project name:");

        // Handle the case where the user clicks Cancel or provides an empty name
        while (projectName != null && projectName.trim().isEmpty()) {
            projectName = JOptionPane.showInputDialog("Project name cannot be empty. Enter a valid project name:");
        }

        PROJECT_FOLDER = projectName;

        return projectName;
    }

    public void clearTables() {
        tables.clear();
        tableNames.clear();
    }

    public void addTable(JTable jTable, String name, int index) {
        if (getTable(index)) {
            setTable(jTable, name, index);
        } else {
            addTable(jTable, name);
        }
    }

    public void addTable(JTable jTable, String name) {
        DataTable dataTable = getDataTable(jTable);
        tables.add(dataTable);
        tableNames.add(name);
    }

    public void setTable(JTable jTable, String name, int index) {
        DataTable dataTable = getDataTable(jTable);
        tables.set(index, dataTable);
        tableNames.set(index, name);
    }

    public void removeTable(int index) {
        tables.remove(index);
        tableNames.remove(index);
    }

    public boolean getTable(int index) {
        if (!tables.isEmpty()) {
            return tables.size() >= index;
        }

        return false;
    }

    public String saveProject() {
        File projectFolder = new File(FOLDER + "\\" + PROJECT_FOLDER);
        System.out.println(projectFolder);
        if (!projectFolder.exists()) {
            projectFolder.mkdir();
        }

        for (int i = 0; i < tables.size(); i++) {
            DataTable dataTable = tables.get(i);
            String fileName = (i + 1) + "_" + tableNames.get(i) + ".sm";
            saveData(dataTable, FOLDER + "\\" + PROJECT_FOLDER, fileName);
        }

        return "Project saved successfully in " + PROJECT_FOLDER;
    }

    private String openFolder() {
        String folderName = null;
        try {
            JFileChooser jf = new JFileChooser();

            // Establece el directorio de inicio del JFileChooser
            File folder = new File(FOLDER);
            if (folder.exists() && folder.isDirectory()) {
                jf.setCurrentDirectory(folder);
            }

            jf.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int result = jf.showOpenDialog(null);

            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFolder = jf.getSelectedFile();
                folderName = selectedFolder.getName();
            }
        } catch (HeadlessException e) {
        }

        folderName = (folderName.equals(null)) ? PROJECT_FOLDER : folderName;

        return folderName;
    }

    public String openProject() {
        String response;
        PROJECT_FOLDER = openFolder();
        File projectFolder = new File(FOLDER + "\\" + PROJECT_FOLDER);
        if (projectFolder.exists() && projectFolder.isDirectory()) {
            File[] files = projectFolder.listFiles();

            if (files != null) {
                tables.clear(); // Clear existing tables

                for (File file : files) {
                    if (file.isFile() && file.getName().endsWith(".sm")) {
                        DataTable dataTable = restoreData(FOLDER + "\\" + PROJECT_FOLDER, file.getName());
                        if (dataTable != null) {
                            tables.add(dataTable);
                            tableNames.add(RemovePrefixYExtension(file.getName()));
                        }
                    }
                }

                response = "Project opened successfully from " + PROJECT_FOLDER;
            } else {
                response = "The project folder is empty.";
            }
        } else {
            response = "Project folder not found.";
        }
        return response;
    }

    private static String RemovePrefixYExtension(String name) {
        // Usar expresión regular para eliminar el "numero_" y ".sm"
        return name.replaceAll("^\\d+_|\\.sm$", "");
    }

    private DataTable getDataTable(JTable jTable) {
        // Get the data model of the JTable
        DefaultTableModel model = (DefaultTableModel) jTable.getModel();

        // Get data from the table
        Object[][] data = new Object[model.getRowCount()][model.getColumnCount()];
        for (int row = 0; row < model.getRowCount(); row++) {
            for (int col = 0; col < model.getColumnCount(); col++) {
                data[row][col] = model.getValueAt(row, col);
            }
        }

        // Get column names
        Object[] columnNames = new Object[model.getColumnCount()];
        for (int col = 0; col < model.getColumnCount(); col++) {
            columnNames[col] = model.getColumnName(col);
        }

        return new DataTable(data, columnNames);
    }

    private void saveData(DataTable dataTable, String folder, String fileName) {
        String response = "";
        try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(folder + File.separator + fileName))) {
            outputStream.writeObject(dataTable);
            System.out.println("Data saved successfully in " + folder + File.separator + fileName);
        } catch (IOException e) {
        }
    }

    private DataTable restoreData(String folder, String fileName) {
        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(folder + File.separator + fileName))) {
            DataTable dataTable = (DataTable) inputStream.readObject();
            System.out.println("Data loaded successfully from " + folder + File.separator + fileName);
            return dataTable;
        } catch (IOException | ClassNotFoundException e) {
            return null;
        }
    }

    public void assignData(JTable jTable, int pos) {
        // Assign data to JTables (adapt this step according to your logic)
        DataTable dataTable = tables.get(pos);
        iterateTable(dataTable.getData(), jTable);
    }

    private void iterateTable(Object[][] data, JTable jTable) {
        for (int i = 0; i < data.length; i++) {
            for (int j = 1; j < data[0].length; j++) {
                jTable.setValueAt(data[i][j], i, j);
            }
        }
    }
}
