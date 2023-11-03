/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Entity;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author osmel
 */
public class FileManager {

    private File file;
    private String folder = null;
    private String route = null;

    private String value;

    public FileManager() {
        File directorio = new File(System.getProperty("user.dir") + "\\saves");
        if (!directorio.exists()) {
            directorio.mkdir();
        }
        folder = directorio.toString();
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getFolder() {
        return folder;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void openFolder() {
        try {
            File objetofile = new File(folder);
            Desktop.getDesktop().open(objetofile);

        } catch (IOException ex) {

            System.out.println(ex);
        }
    }

    public String[][] openFile() {
        String[][] data = null;
        try {
            JFileChooser jf = new JFileChooser(folder);
            FileNameExtensionFilter filter = new FileNameExtensionFilter("SueloSmart Files (*.sm)", "sm");
            jf.setFileFilter(filter);
            int l = jf.showOpenDialog(null);
            if (l == 0) {
                file = jf.getSelectedFile();
                route = file.getAbsolutePath();
                if (file != null) {
                    BufferedReader br = new BufferedReader(new FileReader(route));
                    List<String> list = new ArrayList<>();
                    String s;

                    while ((s = br.readLine()) != null) {
                        list.add(s);
                    }

                    this.value = list.get(0).trim().split(",")[0];
                    int row = Integer.parseInt(list.get(0).trim().split(",")[1]);
                    int column = Integer.parseInt(list.get(0).trim().split(",")[2]);
                    if (!list.isEmpty()) {
                        data = new String[row][column];

                        for (int i = 1; i < data.length; i++) {
                            String str = list.get(i);
                            String[] strings = str.trim().split(",");
                            for (int j = 0; j < data[0].length; j++) {
                                data[i - 1][j] = strings[j];
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
        }
        return data;
    }

    public String save(double x, String[][] y) {
        String result;
        try {
            if (route != null && !route.equals("")) {
                try (PrintWriter bw = new PrintWriter(new FileWriter(route))) {
                    bw.print(x + ",");
                    bw.print(y.length + ",");
                    bw.print(y[0].length + ",");
                    bw.println();
                    for (String[] y1 : y) {
                        for (int j = 0; j < y[0].length; j++) {
                            bw.print(y1[j] + ",");
                        }
                        bw.println();
                    }
                }
            } else {
                saveAs(x, y);
            }
            result = file.getName() + " se ha guardado satisfactoriamente.";
        } catch (IOException ex) {
            result = ex.getMessage();
        }

        return result;
    }

    public void saveAs(double x, String[][] y) {
        JFileChooser jf = new JFileChooser(folder);
        FileNameExtensionFilter filter = new FileNameExtensionFilter("SueloSmart Files (*.sm)", "sm");
        jf.setFileFilter(filter);
        jf.setDialogTitle("Guardar como...");
        if (file == null) {
            jf.setSelectedFile(new File("*.sm"));
        } else {
            jf.setSelectedFile(new File(file.getName()));
        }
        int l;
        do {
            l = jf.showSaveDialog(null);
            file = jf.getSelectedFile();
            if (file.exists() && l != 1) {
                if (JOptionPane.showConfirmDialog(null, "¿Desea sobreescribir el archivo?",
                        "Confirmación", JOptionPane.YES_NO_OPTION) == 0) {
                    break;
                }
            } else {
                break;
            }
        } while (l == 0);
        if (file != null && l != 1) {
            route = file.getAbsolutePath();
            save(x, y);
        }
    }

    public boolean validateExtension(String x) {
        Pattern pattern = Pattern
                .compile("^[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.sm)$");

        Matcher mather = pattern.matcher(x);
        return mather.find();
    }
}
