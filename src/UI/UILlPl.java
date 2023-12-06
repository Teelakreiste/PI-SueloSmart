/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UI;

import Entity.AtterbergGraph;
import Entity.Constants;
import Entity.Fonts;
import Entity.Help;
import Entity.SaveRestoreData;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.text.DecimalFormat;
import javax.swing.DefaultCellEditor;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.border.LineBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import org.apache.commons.math3.stat.regression.SimpleRegression;

/**
 *
 * @author osmel
 */
public class UILlPl extends javax.swing.JFrame {

    private Fonts font;
    private int xMouse, yMouse;

    private Help help = new Help();

    SaveRestoreData saveRestoreData;

    /**
     * Creates new form UILlPl
     */
    public UILlPl() {
        initComponents();
        init();
    }

    private void init() {
        font = new Fonts();
        help.Help(jButtonHelp, jPanelBackground);
        saveRestoreData = new SaveRestoreData();
        setTitle(Constants.TITLE + " - Granulométria");
        setLocationRelativeTo(null);
//        setIconImage(new ImageIcon(getClass().getResource("/dispersas/Icon.png")).getImage());

        font();
        setSieved(jTableWater);
        getTableCellEditor(jTableWater, 1);
        customScrollBar(jScrollPaneWater);
        customTableHeader(jTableWater);

        setSieved(jTableLl);
        getTableCellEditor(jTableLl, 2);
        customScrollBar(jScrollPaneLl);
        customTableHeader(jTableLl);

        setSieved(jTablePl);
        getTableCellEditor(jTablePl, 4);
        customScrollBar(jScrollPanePl);
        customTableHeader(jTablePl);

        setSieved(jTableResult);
        getTableCellEditor(jTableResult, 3);
        customScrollBar(jScrollPaneResult);
        customTableHeader(jTableResult);
        jButtonSaveAs.setVisible(false);
    }

    private void font() {
        jLabelTitle.setFont(font.Font(font.ROBOTO_MEDIUM, 1, 18));
        jLabelTitleS1.setFont(font.Font(font.ROBOTO_MEDIUM, 1, 14));
        jLabelTitleS2.setFont(font.Font(font.ROBOTO_MEDIUM, 1, 14));
        jLabelTitleS3.setFont(font.Font(font.ROBOTO_MEDIUM, 1, 14));
        jLabelTitleS4.setFont(font.Font(font.ROBOTO_MEDIUM, 1, 14));

        jLabelFile.setFont(font.Font(font.ROBOTO_LIGHT, 0, 11));
        jLabelFileInfo.setFont(font.Font(font.ROBOTO_LIGHT, 0, 11));
    }

    public void setData(SaveRestoreData saveRestoreData) {
        this.saveRestoreData = saveRestoreData;
        assignData();
    }

    private void assignData() {
        try {
            saveRestoreData.assignData(jTableWater, 0);
            saveRestoreData.assignData(jTableLl, 1);
            saveRestoreData.assignData(jTableResult, 2);
            saveRestoreData.assignData(jTablePl, 3);

            jButtonSaveAs.setVisible(true);

            setTextByElement(jLabelFileInfo, "Datos cargados exitosamente.");
            jLabelTimer(jLabelFileInfo, 0);
            setTextByElement(jLabelFile, "Project: " + saveRestoreData.getPROJECT_FOLDER());
            jLabelTitle.setText("SUELOSMART - " + saveRestoreData.getPROJECT_FOLDER());
        } catch (IndexOutOfBoundsException e) {

        }
    }

    private void setSieved(JTable jTable) {
        try {
            DefaultTableModel model = (DefaultTableModel) jTable.getModel();

            DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
            renderer.setHorizontalAlignment(SwingConstants.CENTER);

            for (int i = 1; i < jTable.getColumnCount(); i++) {
                jTable.getColumnModel().getColumn(i).setCellRenderer(renderer);
            }
        } catch (Exception e) {

        }
    }

    private void getTableCellEditor(JTable jTable, int section) {

        setColumnEditor(jTable, 1, section);
        switch (section) {
            case 2:
            case 4:
                setColumnEditor(jTable, 2, section);
                setColumnEditor(jTable, 3, section);
                break;
        }

    }

    private void setColumnEditor(JTable jTable, int columnNumber, int section) {
        TableColumn column = jTable.getColumnModel().getColumn(columnNumber);

        column.setCellEditor(new DefaultCellEditor(new JTextField()) {
            @Override
            public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
                JTextField editor = (JTextField) super.getTableCellEditorComponent(table, value, isSelected, row, column);

                editor.setBorder(new LineBorder(Color.decode("#388E3C"), 2));
                editor.setBackground(Color.decode("#388E3C"));
                editor.setForeground(Color.WHITE);
                editor.setHorizontalAlignment(SwingConstants.CENTER);
                editor.setSelectionColor(Color.decode("#333333"));

                boolean flag = editorSections(editor, row, section);
                if (!flag) {
                    editor.setBackground(Color.decode("#333333"));
                }

                editor.addFocusListener(new FocusAdapter() {
                    @Override
                    public void focusLost(FocusEvent e) {
                        if (flag) { // Actualizar valores solo para las filas 0 y 1
                            updateValues();
                        }

                        if (section == 3) {
                            updateValues();
                        }
                    }

                    private void updateValues() {
                        switch (section) {
                            case 1:
                                calcWaterContent(jTable);
                                break;
                            case 2:
                                calcLiquidLimit(jTable, columnNumber);
                                break;
                            case 3:
                                calcResults(jTable);
                                break;
                            case 4:
                                calcPlasticLimit(jTable, columnNumber);
                                break;
                        }
                    }
                });

                return editor;
            }
        });
    }

    private boolean editorSections(JTextField editor, int row, int section) {
        boolean flag = false;
        switch (section) {
            case 1:
                flag = row < 2;
                break;
            case 2:
                flag = row < 4 || row == 5;
                break;
            case 3:
                flag = false;
                break;
            case 4:
                flag = row < 3 || row == 4;
                break;
        }
        setEditorEditable(editor, flag);
        return flag;
    }

    private void setEditorEditable(JTextField editor, boolean flag) {
        if (flag) { // Permitir la edición solo en las filas 0 y 1
            editor.setEditable(true);
        } else {
            editor.setEditable(false); // Deshabilitar la edición en otras filas
        }
    }

    private void calcWaterContent(JTable jTable) {
        DefaultTableModel model = (DefaultTableModel) jTable.getModel();
        try {
            double wetWeight = Double.valueOf((String) model.getValueAt(0, 1));
            double dryWeight = Double.valueOf((String) model.getValueAt(1, 1));
            double waterContent = wetWeight - dryWeight;
            model.setValueAt(fixFormatDecimal(waterContent), 2, 1);
            model.setValueAt(fixFormatDecimal((waterContent / dryWeight) * 100), 3, 1);
        } catch (NullPointerException | NumberFormatException e) {

        }
    }

    private void calcLiquidLimit(JTable jTable, int column) {
        DefaultTableModel model = (DefaultTableModel) jTable.getModel();
        try {
            int hitsNo = Integer.parseInt((String) model.getValueAt(1, column)); // No. GOLPES	
            double pContainerWetSoil = Double.valueOf((String) model.getValueAt(2, column)); // P. RECIPIENTE + SUELO HUMEDO (g)	
            double pContainerDrySoil = Double.valueOf((String) model.getValueAt(3, column)); // P. RECIPIENTE + SUELO SECO.(g)	
            double containerWeight = Double.valueOf((String) model.getValueAt(5, column)); // PESO DEL RECIPIENTE. (g)	

            double waterWeight = pContainerWetSoil - pContainerDrySoil; // PESO DEL AGUA (g)	
            double drySoilWeight = pContainerDrySoil - containerWeight; // PESO SUELO SECO(g)	
            double percentageMoisture = (waterWeight / drySoilWeight) * 100; // % DE HUMEDAD.	

            model.setValueAt(fixFormatDecimal(waterWeight), 4, column);
            model.setValueAt(fixFormatDecimal(drySoilWeight), 6, column);
            model.setValueAt(fixFormatDecimal(percentageMoisture), 7, column);

        } catch (NullPointerException | NumberFormatException e) {

        }
    }

    private void calcPlasticLimit(JTable jTable, int column) {
        DefaultTableModel model = (DefaultTableModel) jTable.getModel();
        try {
            double pContainerWetSoil = Double.valueOf((String) model.getValueAt(1, column)); // P. RECIPIENTE + SUELO HUMEDO (g)	
            double pContainerDrySoil = Double.valueOf((String) model.getValueAt(2, column)); // P. RECIPIENTE + SUELO SECO.(g)	
            double containerWeight = Double.valueOf((String) model.getValueAt(4, column)); // PESO DEL RECIPIENTE. (g)	

            double waterWeight = pContainerWetSoil - pContainerDrySoil; // PESO DEL AGUA (g)	
            double drySoilWeight = pContainerDrySoil - containerWeight; // PESO SUELO SECO(g)	
            double percentageMoisture = (waterWeight / drySoilWeight) * 100; // % DE HUMEDAD.	

            model.setValueAt(fixFormatDecimal(waterWeight), 3, column); // PESO DEL AGUA (g)
            model.setValueAt(fixFormatDecimal(drySoilWeight), 5, column);  // PESO SUELO SECO(g)	
            model.setValueAt(fixFormatDecimal(percentageMoisture), 6, column); // % DE HUMEDAD.	

        } catch (NullPointerException | NumberFormatException e) {

        }
    }

    private void calcResults(JTable jTable) {
        try {
            double Ll = fixFormatDecimal(percentajeLl(jTableLl));
            jTable.setValueAt(Ll, 0, 1);
            double Pl = fixFormatDecimal(percentagePl(jTablePl));
            jTable.setValueAt(Pl, 1, 1);
            jTable.setValueAt(fixFormatDecimal(plasticityIndex(jTable, Ll, Pl)), 2, 1);
        } catch (NullPointerException | NumberFormatException e) {

        }
    }

    private double percentajeLl(JTable jTable) {
        double result = 0;
        try {
            double[][] data = new double[jTable.getColumnCount() - 1][2];

            for (int j = 0; j < data[0].length; j++) {
                int row = (j == 0) ? 1 : 7;
                for (int i = 1; i <= data.length; i++) {
                    data[i - 1][j] = Double.valueOf(jTable.getValueAt(row, i).toString());
                }
            }

            SimpleRegression regression = new SimpleRegression();

            regression.addData(data);

            double intercept = regression.getIntercept();
            double slope = regression.getSlope();

            result = (intercept + slope * 25);
        } catch (NullPointerException | NumberFormatException e) {

        }
        return fixFormatDecimal(result);
    }

    private double percentagePl(JTable jTable) {
        int count = 0;
        double average = 0;
        try {
            for (int i = 1; i < jTable.getColumnCount(); i++) {
                String value = jTable.getValueAt(6, i).toString();
                if (!value.equals(null)) {
                    average += Double.valueOf(value);
                    count++;
                }
            }
        } catch (NullPointerException | NumberFormatException e) {

        }
        average /= count;
        return average;
    }

    private double plasticityIndex(JTable jTable, double Ll, double Pl) {
        String OLl = jTable.getValueAt(0, 1).toString();
        String OPl = jTable.getValueAt(1, 1).toString();

        double result;

        if (OLl.equals("NL") || OPl.equals("NP")) {
            if (OLl.equals("NL") && OPl.equals("NP")) {
                result = 0;
            } else {
                if (Ll > 0) {
                    result = OLl.equals("NL") ? 0 : Integer.parseInt(OLl);
                } else {
                    result = OPl.equals("NP") ? 0 : Integer.parseInt(OPl);
                }
            }
        } else {
            result = (Ll - Pl);
        }

        return result;
    }

    private void customScrollBar(JScrollPane jScrollPane) {
        jScrollPane.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                thumbColor = Color.decode("#282828");
                thumbDarkShadowColor = Color.decode("#282828");
                thumbHighlightColor = Color.decode("#282828");
            }

            @Override
            protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
                g.setColor(Color.decode("#333333"));
                g.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
            }
        });
    }

    private void customTableHeader(JTable jTable) {

        jTable.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                setBackground(Color.decode("#388E3C")); // Cambia el color del fondo según tus preferencias
                setForeground(Color.WHITE); // Cambia el color del texto según tus preferencias
                setHorizontalAlignment(SwingConstants.CENTER); // Centra el texto
                return this;
            }

        });
    }

    private double fixFormatDecimal(double value) {
        // Crear la parte decimal del patrón
        StringBuilder decimalPattern = new StringBuilder(".");
        int decimalPlaces = Integer.parseInt(jFormattedTextFieldFix.getText());
        for (int i = 0; i < decimalPlaces; i++) {
            decimalPattern.append("0");
        }

        String strTolerance = "1e-" + (decimalPlaces - 1);
        double tolerance = Double.parseDouble(strTolerance); // Puedes ajustar este valor según tu precisión requerida

        if (Math.abs(value) < tolerance) {
            return 0.0;
        } else {
            DecimalFormat df = new DecimalFormat("#" + decimalPattern.toString());
            return Double.parseDouble(df.format(value));
        }
    }

    private void clearTableValues(JTable jTable) {
        DefaultTableModel model = (DefaultTableModel) jTable.getModel();
        jLabelTitle.setText("SUELOSMART");

        for (int i = 0; i < jTable.getRowCount(); i++) {
            for (int j = 1; j < jTable.getColumnCount(); j++) {
                jTable.setValueAt("", i, j);
            }
        }
    }

    private void refresh() {
        calcWaterContent(jTableWater);
        calcResults(jTableResult);

        for (int i = 1; i < jTableLl.getColumnCount(); i++) {
            try {
                calcLiquidLimit(jTableLl, i);
                calcPlasticLimit(jTablePl, i);
            } catch (NumberFormatException e) {

            }
        }
    }

    public void jLabelTimer(JLabel jLabel, int timeVisible) {
        jLabel.setVisible(true);
        // Establece el tiempo en milisegundos que deseas que el JLabel sea visible
        timeVisible = (timeVisible == 0) ? 3000 : timeVisible;
        Timer timer = new Timer(timeVisible, (ActionEvent e) -> {
            jLabel.setVisible(false);
        });

        // Inicia el temporizador
        timer.setRepeats(false); // Para que el temporizador solo se ejecute una vez
        timer.start();
    }

    private void setTextByElement(JComponent component, String text) {
        if (component instanceof JLabel) {
            ((JLabel) component).setText(text);
        } else if (component instanceof JTextField) {
            ((JTextField) component).setText(text);
        }
    }

    private void setForegroundtByElement(JComponent component, String colorHex) {
        colorHex = (colorHex.contains("#")) ? colorHex : "#" + colorHex;
        component.setForeground(Color.decode(colorHex));
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanelBackground = new javax.swing.JPanel();
        jPanelHeader = new javax.swing.JPanel();
        jLabelLogo = new javax.swing.JLabel();
        jButtonMinimize = new javax.swing.JButton();
        jButtonExit = new javax.swing.JButton();
        jLabelTitle = new javax.swing.JLabel();
        jPanelOptionBar = new javax.swing.JPanel();
        jButtonGraph = new javax.swing.JButton();
        jButtonRefresh = new javax.swing.JButton();
        jButtonClear = new javax.swing.JButton();
        jButtonNew = new javax.swing.JButton();
        jButtonOpen = new javax.swing.JButton();
        jButtonSave = new javax.swing.JButton();
        jButtonSaveAs = new javax.swing.JButton();
        jButtonBack = new javax.swing.JButton();
        jPanelFix = new javax.swing.JPanel();
        jFormattedTextFieldFix = new javax.swing.JFormattedTextField();
        jLabelFix = new javax.swing.JLabel();
        jPanelFixPlus = new javax.swing.JPanel();
        jLabelFixPlus = new javax.swing.JLabel();
        jPanelFixMinus = new javax.swing.JPanel();
        jLabelFixMinus = new javax.swing.JLabel();
        jButtonTamized = new javax.swing.JButton();
        jButtonAbout = new javax.swing.JButton();
        jButtonHelp = new javax.swing.JButton();
        jLabelActionBg = new javax.swing.JLabel();
        jPanelFooter = new javax.swing.JPanel();
        jLabelFileInfo = new javax.swing.JLabel();
        jLabelFile = new javax.swing.JLabel();
        jPanelSection1 = new javax.swing.JPanel();
        jLabelTitleS1 = new javax.swing.JLabel();
        jScrollPaneWater = new javax.swing.JScrollPane();
        jTableWater = new javax.swing.JTable();
        jLabelBgS1 = new javax.swing.JLabel();
        jPanelSection2 = new javax.swing.JPanel();
        jScrollPaneLl = new javax.swing.JScrollPane();
        jTableLl = new javax.swing.JTable();
        jLabelTitleS2 = new javax.swing.JLabel();
        jLabelBgS2 = new javax.swing.JLabel();
        jPanelSection3 = new javax.swing.JPanel();
        jLabelTitleS3 = new javax.swing.JLabel();
        jScrollPaneResult = new javax.swing.JScrollPane();
        jTableResult = new javax.swing.JTable();
        jLabelBgS3 = new javax.swing.JLabel();
        jPanelSection4 = new javax.swing.JPanel();
        jLabelTitleS4 = new javax.swing.JLabel();
        jScrollPanePl = new javax.swing.JScrollPane();
        jTablePl = new javax.swing.JTable();
        jLabelBgS5 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);

        jPanelBackground.setBackground(new java.awt.Color(51, 51, 51));
        jPanelBackground.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanelHeader.setBackground(new java.awt.Color(56, 142, 60));
        jPanelHeader.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                jPanelHeaderMouseDragged(evt);
            }
        });
        jPanelHeader.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jPanelHeaderMousePressed(evt);
            }
        });
        jPanelHeader.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabelLogo.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabelLogo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/logos/SueloSmart_v2_70x35.png"))); // NOI18N
        jPanelHeader.add(jLabelLogo, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 8, 70, 35));

        jButtonMinimize.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/buttons/minimize.png"))); // NOI18N
        jButtonMinimize.setBorderPainted(false);
        jButtonMinimize.setContentAreaFilled(false);
        jButtonMinimize.setFocusPainted(false);
        jButtonMinimize.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/buttons/minimize.png"))); // NOI18N
        jButtonMinimize.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/buttons/minimize_hover.png"))); // NOI18N
        jButtonMinimize.setRolloverSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/buttons/minimize.png"))); // NOI18N
        jButtonMinimize.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/buttons/minimize_hover.png"))); // NOI18N
        jButtonMinimize.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jButtonMinimizeMousePressed(evt);
            }
        });
        jPanelHeader.add(jButtonMinimize, new org.netbeans.lib.awtextra.AbsoluteConstraints(1250, 18, 16, 16));

        jButtonExit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/buttons/close.png"))); // NOI18N
        jButtonExit.setBorderPainted(false);
        jButtonExit.setContentAreaFilled(false);
        jButtonExit.setFocusPainted(false);
        jButtonExit.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/buttons/close.png"))); // NOI18N
        jButtonExit.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/buttons/close_hover.png"))); // NOI18N
        jButtonExit.setRolloverSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/buttons/close.png"))); // NOI18N
        jButtonExit.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/buttons/close_hover.png"))); // NOI18N
        jButtonExit.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jButtonExitMousePressed(evt);
            }
        });
        jPanelHeader.add(jButtonExit, new org.netbeans.lib.awtextra.AbsoluteConstraints(1265, 18, 16, 16));

        jLabelTitle.setForeground(new java.awt.Color(224, 224, 224));
        jLabelTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelTitle.setText("SUELOSMART");
        jPanelHeader.add(jLabelTitle, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 10, 1300, 30));

        jPanelBackground.add(jPanelHeader, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1300, 50));

        jPanelOptionBar.setBackground(new java.awt.Color(51, 51, 51));
        jPanelOptionBar.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jButtonGraph.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/buttons/graph.png"))); // NOI18N
        jButtonGraph.setBorderPainted(false);
        jButtonGraph.setContentAreaFilled(false);
        jButtonGraph.setFocusPainted(false);
        jButtonGraph.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/buttons/graph.png"))); // NOI18N
        jButtonGraph.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/buttons/graph_hover.png"))); // NOI18N
        jButtonGraph.setRolloverSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/buttons/graph.png"))); // NOI18N
        jButtonGraph.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/buttons/graph_hover.png"))); // NOI18N
        jButtonGraph.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jButtonGraphMousePressed(evt);
            }
        });
        jPanelOptionBar.add(jButtonGraph, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 12, 16, 16));

        jButtonRefresh.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/buttons/update.png"))); // NOI18N
        jButtonRefresh.setBorderPainted(false);
        jButtonRefresh.setContentAreaFilled(false);
        jButtonRefresh.setFocusPainted(false);
        jButtonRefresh.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/buttons/update.png"))); // NOI18N
        jButtonRefresh.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/buttons/update_hover.png"))); // NOI18N
        jButtonRefresh.setRolloverSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/buttons/update.png"))); // NOI18N
        jButtonRefresh.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/buttons/update_hover.png"))); // NOI18N
        jButtonRefresh.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jButtonRefreshMousePressed(evt);
            }
        });
        jPanelOptionBar.add(jButtonRefresh, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 12, 16, 16));

        jButtonClear.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/buttons/clear.png"))); // NOI18N
        jButtonClear.setBorderPainted(false);
        jButtonClear.setContentAreaFilled(false);
        jButtonClear.setFocusPainted(false);
        jButtonClear.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/buttons/clear.png"))); // NOI18N
        jButtonClear.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/buttons/clear_hover.png"))); // NOI18N
        jButtonClear.setRolloverSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/buttons/clear.png"))); // NOI18N
        jButtonClear.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/buttons/clear_hover.png"))); // NOI18N
        jButtonClear.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jButtonClearMousePressed(evt);
            }
        });
        jPanelOptionBar.add(jButtonClear, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 12, 16, 16));

        jButtonNew.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/buttons/new.png"))); // NOI18N
        jButtonNew.setBorderPainted(false);
        jButtonNew.setContentAreaFilled(false);
        jButtonNew.setFocusPainted(false);
        jButtonNew.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/buttons/new.png"))); // NOI18N
        jButtonNew.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/buttons/new_hover.png"))); // NOI18N
        jButtonNew.setRolloverSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/buttons/new.png"))); // NOI18N
        jButtonNew.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/buttons/new_hover.png"))); // NOI18N
        jButtonNew.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jButtonNewMousePressed(evt);
            }
        });
        jPanelOptionBar.add(jButtonNew, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 12, 16, 16));

        jButtonOpen.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/buttons/open.png"))); // NOI18N
        jButtonOpen.setBorderPainted(false);
        jButtonOpen.setContentAreaFilled(false);
        jButtonOpen.setFocusPainted(false);
        jButtonOpen.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/buttons/open.png"))); // NOI18N
        jButtonOpen.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/buttons/open_hover.png"))); // NOI18N
        jButtonOpen.setRolloverSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/buttons/open.png"))); // NOI18N
        jButtonOpen.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/buttons/open_hover.png"))); // NOI18N
        jButtonOpen.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jButtonOpenMousePressed(evt);
            }
        });
        jPanelOptionBar.add(jButtonOpen, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 12, 16, 16));

        jButtonSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/buttons/save.png"))); // NOI18N
        jButtonSave.setBorderPainted(false);
        jButtonSave.setContentAreaFilled(false);
        jButtonSave.setFocusPainted(false);
        jButtonSave.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/buttons/save.png"))); // NOI18N
        jButtonSave.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/buttons/save_hover.png"))); // NOI18N
        jButtonSave.setRolloverSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/buttons/save.png"))); // NOI18N
        jButtonSave.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/buttons/save_hover.png"))); // NOI18N
        jButtonSave.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jButtonSaveMousePressed(evt);
            }
        });
        jPanelOptionBar.add(jButtonSave, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 12, 16, 16));

        jButtonSaveAs.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/buttons/save_as.png"))); // NOI18N
        jButtonSaveAs.setBorderPainted(false);
        jButtonSaveAs.setContentAreaFilled(false);
        jButtonSaveAs.setFocusPainted(false);
        jButtonSaveAs.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/buttons/save_as.png"))); // NOI18N
        jButtonSaveAs.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/buttons/save_as_hover.png"))); // NOI18N
        jButtonSaveAs.setRolloverSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/buttons/save_as.png"))); // NOI18N
        jButtonSaveAs.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/buttons/save_as_hover.png"))); // NOI18N
        jButtonSaveAs.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jButtonSaveAsMousePressed(evt);
            }
        });
        jPanelOptionBar.add(jButtonSaveAs, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 12, 16, 16));

        jButtonBack.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/buttons/back_16x16.png"))); // NOI18N
        jButtonBack.setBorderPainted(false);
        jButtonBack.setContentAreaFilled(false);
        jButtonBack.setFocusPainted(false);
        jButtonBack.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/buttons/back_16x16.png"))); // NOI18N
        jButtonBack.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/buttons/back_hover_16x16.png"))); // NOI18N
        jButtonBack.setRolloverSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/buttons/back_16x16.png"))); // NOI18N
        jButtonBack.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/buttons/back_hover_16x16.png"))); // NOI18N
        jButtonBack.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jButtonBackMousePressed(evt);
            }
        });
        jPanelOptionBar.add(jButtonBack, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 12, 16, 16));

        jPanelFix.setBackground(new java.awt.Color(51, 51, 51));
        jPanelFix.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jFormattedTextFieldFix.setBackground(new java.awt.Color(51, 51, 51));
        jFormattedTextFieldFix.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 0, 0, new java.awt.Color(40, 40, 40)), javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        jFormattedTextFieldFix.setForeground(new java.awt.Color(224, 224, 224));
        jFormattedTextFieldFix.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getIntegerInstance())));
        jFormattedTextFieldFix.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jFormattedTextFieldFix.setText("3");
        jFormattedTextFieldFix.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jFormattedTextFieldFix.setSelectionColor(new java.awt.Color(76, 175, 80));
        jFormattedTextFieldFix.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jFormattedTextFieldFixFocusLost(evt);
            }
        });
        jPanelFix.add(jFormattedTextFieldFix, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 0, 30, 30));

        jLabelFix.setBackground(new java.awt.Color(51, 51, 51));
        jLabelFix.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabelFix.setForeground(new java.awt.Color(153, 153, 153));
        jLabelFix.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelFix.setText("FIX");
        jPanelFix.add(jLabelFix, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 30, 30));

        jPanelFixPlus.setBackground(new java.awt.Color(51, 51, 51));
        jPanelFixPlus.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabelFixPlus.setFont(new java.awt.Font("Tahoma", 0, 7)); // NOI18N
        jLabelFixPlus.setForeground(new java.awt.Color(153, 153, 153));
        jLabelFixPlus.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelFixPlus.setText("▲");
        jLabelFixPlus.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabelFixPlusMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jLabelFixPlusMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jLabelFixPlusMouseExited(evt);
            }
        });
        jPanelFixPlus.add(jLabelFixPlus, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 15, 15));

        jPanelFix.add(jPanelFixPlus, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 0, 15, 15));

        jPanelFixMinus.setBackground(new java.awt.Color(51, 51, 51));
        jPanelFixMinus.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabelFixMinus.setFont(new java.awt.Font("Tahoma", 0, 7)); // NOI18N
        jLabelFixMinus.setForeground(new java.awt.Color(153, 153, 153));
        jLabelFixMinus.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelFixMinus.setText("▼");
        jLabelFixMinus.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabelFixMinusMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jLabelFixMinusMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jLabelFixMinusMouseExited(evt);
            }
        });
        jPanelFixMinus.add(jLabelFixMinus, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 15, 15));

        jPanelFix.add(jPanelFixMinus, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 15, 15, 15));

        jPanelOptionBar.add(jPanelFix, new org.netbeans.lib.awtextra.AbsoluteConstraints(398, 5, 75, 30));

        jButtonTamized.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jButtonTamized.setForeground(new java.awt.Color(255, 255, 255));
        jButtonTamized.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/buttons/btn_120x30.png"))); // NOI18N
        jButtonTamized.setText("TAMIZADO");
        jButtonTamized.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonTamized.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/buttons/btn_120x30.png"))); // NOI18N
        jButtonTamized.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/buttons/btn_120x30_hover.png"))); // NOI18N
        jButtonTamized.setRolloverSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/buttons/btn_120x30.png"))); // NOI18N
        jButtonTamized.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/buttons/btn_120x30_hover.png"))); // NOI18N
        jButtonTamized.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonTamizedActionPerformed(evt);
            }
        });
        jPanelOptionBar.add(jButtonTamized, new org.netbeans.lib.awtextra.AbsoluteConstraints(231, 5, 120, 30));

        jButtonAbout.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/buttons/about.png"))); // NOI18N
        jButtonAbout.setBorderPainted(false);
        jButtonAbout.setContentAreaFilled(false);
        jButtonAbout.setFocusPainted(false);
        jButtonAbout.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/buttons/about.png"))); // NOI18N
        jButtonAbout.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/buttons/about_hover.png"))); // NOI18N
        jButtonAbout.setRolloverSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/buttons/about.png"))); // NOI18N
        jButtonAbout.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/buttons/about_hover.png"))); // NOI18N
        jButtonAbout.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jButtonAboutMousePressed(evt);
            }
        });
        jPanelOptionBar.add(jButtonAbout, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 12, 16, 16));

        jButtonHelp.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/buttons/help.png"))); // NOI18N
        jButtonHelp.setBorderPainted(false);
        jButtonHelp.setContentAreaFilled(false);
        jButtonHelp.setFocusPainted(false);
        jButtonHelp.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/buttons/help.png"))); // NOI18N
        jButtonHelp.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/buttons/help_hover.png"))); // NOI18N
        jButtonHelp.setRolloverSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/buttons/help.png"))); // NOI18N
        jButtonHelp.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/buttons/help_hover.png"))); // NOI18N
        jPanelOptionBar.add(jButtonHelp, new org.netbeans.lib.awtextra.AbsoluteConstraints(548, 12, 16, 16));

        jLabelActionBg.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/buttons/action_bg_600X40.png"))); // NOI18N
        jPanelOptionBar.add(jLabelActionBg, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 600, 40));

        jPanelBackground.add(jPanelOptionBar, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 70, 600, 40));

        jPanelFooter.setBackground(new java.awt.Color(56, 142, 60));
        jPanelFooter.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabelFileInfo.setForeground(new java.awt.Color(255, 255, 51));
        jLabelFileInfo.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jPanelFooter.add(jLabelFileInfo, new org.netbeans.lib.awtextra.AbsoluteConstraints(710, 0, 570, 20));

        jLabelFile.setForeground(new java.awt.Color(255, 255, 255));
        jLabelFile.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jPanelFooter.add(jLabelFile, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 0, 640, 20));

        jPanelBackground.add(jPanelFooter, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 740, 1300, 20));

        jPanelSection1.setBackground(new java.awt.Color(51, 51, 51));
        jPanelSection1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabelTitleS1.setForeground(new java.awt.Color(224, 224, 224));
        jLabelTitleS1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelTitleS1.setText("CONTENIDO DE AGUA");
        jPanelSection1.add(jLabelTitleS1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 380, 30));

        jScrollPaneWater.setBackground(new java.awt.Color(51, 51, 51));
        jScrollPaneWater.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(40, 40, 40)));
        jScrollPaneWater.setFocusable(false);
        jScrollPaneWater.setRequestFocusEnabled(false);

        jTableWater.setBackground(new java.awt.Color(51, 51, 51));
        jTableWater.setForeground(new java.awt.Color(224, 224, 224));
        jTableWater.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"Peso humedo (g)", null},
                {"Peso seco (g)", null},
                {"Contenido de agua (g)", null},
                {"% Humedad", null}
            },
            new String [] {
                "Datos", "Valores"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTableWater.setFillsViewportHeight(true);
        jTableWater.setGridColor(new java.awt.Color(76, 175, 80));
        jTableWater.setRowHeight(32);
        jTableWater.setSelectionBackground(new java.awt.Color(76, 175, 80));
        jTableWater.setShowHorizontalLines(false);
        jTableWater.setShowVerticalLines(false);
        jScrollPaneWater.setViewportView(jTableWater);

        jPanelSection1.add(jScrollPaneWater, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 40, 340, 230));

        jLabelBgS1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/logos/bg_380x290.png"))); // NOI18N
        jPanelSection1.add(jLabelBgS1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 380, 290));

        jPanelBackground.add(jPanelSection1, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 130, 380, 290));

        jPanelSection2.setBackground(new java.awt.Color(51, 51, 51));
        jPanelSection2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jScrollPaneLl.setBackground(new java.awt.Color(51, 51, 51));
        jScrollPaneLl.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(40, 40, 40)));
        jScrollPaneLl.setFocusable(false);
        jScrollPaneLl.setRequestFocusEnabled(false);

        jTableLl.setBackground(new java.awt.Color(51, 51, 51));
        jTableLl.setForeground(new java.awt.Color(224, 224, 224));
        jTableLl.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"RECIPIENTE No.", null, null, null},
                {"No. GOLPES", null, null, null},
                {"P. RECIPIENTE + SUELO HUMEDO (g)", null, null, null},
                {"P. RECIPIENTE + SUELO SECO (g)", null, null, null},
                {"PESO DEL AGUA (g)", null, null, null},
                {"PESO DEL RECIPIENTE (g)", null, null, null},
                {"PESO SUELO SECO (g)", null, null, null},
                {"% DE HUMEDAD", null, null, null}
            },
            new String [] {
                "DETERMINACION No.", "1", "2", "3"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, true, true, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTableLl.setFillsViewportHeight(true);
        jTableLl.setGridColor(new java.awt.Color(76, 175, 80));
        jTableLl.setRowHeight(32);
        jTableLl.setSelectionBackground(new java.awt.Color(76, 175, 80));
        jTableLl.setShowHorizontalLines(false);
        jTableLl.setShowVerticalLines(false);
        jScrollPaneLl.setViewportView(jTableLl);

        jPanelSection2.add(jScrollPaneLl, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 40, 810, 230));

        jLabelTitleS2.setForeground(new java.awt.Color(224, 224, 224));
        jLabelTitleS2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelTitleS2.setText("LIMITE LIQUIDO");
        jPanelSection2.add(jLabelTitleS2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 850, 30));

        jLabelBgS2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/logos/bg_850x290.png"))); // NOI18N
        jPanelSection2.add(jLabelBgS2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 850, 290));

        jPanelBackground.add(jPanelSection2, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 130, 850, 290));

        jPanelSection3.setBackground(new java.awt.Color(51, 51, 51));
        jPanelSection3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabelTitleS3.setForeground(new java.awt.Color(224, 224, 224));
        jLabelTitleS3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelTitleS3.setText("RESULTADOS");
        jPanelSection3.add(jLabelTitleS3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 380, 30));

        jScrollPaneResult.setBackground(new java.awt.Color(51, 51, 51));
        jScrollPaneResult.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(40, 40, 40)));
        jScrollPaneResult.setFocusable(false);
        jScrollPaneResult.setRequestFocusEnabled(false);

        jTableResult.setBackground(new java.awt.Color(51, 51, 51));
        jTableResult.setForeground(new java.awt.Color(224, 224, 224));
        jTableResult.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"Limite Liquido (%)", null},
                {"Limite Plastico (%)", null},
                {"Indice de Plasticidad", null}
            },
            new String [] {
                "Datos", "Valores"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTableResult.setFillsViewportHeight(true);
        jTableResult.setGridColor(new java.awt.Color(76, 175, 80));
        jTableResult.setRowHeight(32);
        jTableResult.setSelectionBackground(new java.awt.Color(76, 175, 80));
        jTableResult.setShowHorizontalLines(false);
        jTableResult.setShowVerticalLines(false);
        jScrollPaneResult.setViewportView(jTableResult);

        jPanelSection3.add(jScrollPaneResult, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 40, 340, 230));

        jLabelBgS3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/logos/bg_380x290.png"))); // NOI18N
        jPanelSection3.add(jLabelBgS3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 380, 290));

        jPanelBackground.add(jPanelSection3, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 430, 380, 290));

        jPanelSection4.setBackground(new java.awt.Color(51, 51, 51));
        jPanelSection4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabelTitleS4.setForeground(new java.awt.Color(224, 224, 224));
        jLabelTitleS4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelTitleS4.setText("LIMITE PLASTICO");
        jPanelSection4.add(jLabelTitleS4, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 850, 30));

        jScrollPanePl.setBackground(new java.awt.Color(51, 51, 51));
        jScrollPanePl.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(40, 40, 40)));
        jScrollPanePl.setFocusable(false);
        jScrollPanePl.setRequestFocusEnabled(false);

        jTablePl.setBackground(new java.awt.Color(51, 51, 51));
        jTablePl.setForeground(new java.awt.Color(224, 224, 224));
        jTablePl.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"RECIPIENTE No.", null, null, null},
                {"P. RECIPIENTE + SUELO HUMEDO (g)", null, null, null},
                {"P. RECIPIENTE + SUELO SECO (g)", null, null, null},
                {"PESO DEL AGUA (g)", null, null, null},
                {"PESO DEL RECIPIENTE (g)", null, null, null},
                {"PESO SUELO SECO (g)", null, null, null},
                {"% DE HUMEDAD", null, null, null}
            },
            new String [] {
                "DETERMINACION No.", "1", "2", "3"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, true, true, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTablePl.setFillsViewportHeight(true);
        jTablePl.setGridColor(new java.awt.Color(76, 175, 80));
        jTablePl.setRowHeight(32);
        jTablePl.setSelectionBackground(new java.awt.Color(76, 175, 80));
        jTablePl.setShowHorizontalLines(false);
        jTablePl.setShowVerticalLines(false);
        jScrollPanePl.setViewportView(jTablePl);

        jPanelSection4.add(jScrollPanePl, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 40, 810, 230));

        jLabelBgS5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/logos/bg_850x290.png"))); // NOI18N
        jPanelSection4.add(jLabelBgS5, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 850, 290));

        jPanelBackground.add(jPanelSection4, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 430, 850, 290));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanelBackground, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanelBackground, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonMinimizeMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonMinimizeMousePressed
        setState(ICONIFIED);
    }//GEN-LAST:event_jButtonMinimizeMousePressed

    private void jButtonExitMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonExitMousePressed
        System.exit(0);
    }//GEN-LAST:event_jButtonExitMousePressed

    private void jPanelHeaderMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanelHeaderMouseDragged
        int x = evt.getXOnScreen();
        int y = evt.getYOnScreen();
        this.setLocation(x - xMouse, y - yMouse);
    }//GEN-LAST:event_jPanelHeaderMouseDragged

    private void jPanelHeaderMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanelHeaderMousePressed
        xMouse = evt.getX();
        yMouse = evt.getY();
    }//GEN-LAST:event_jPanelHeaderMousePressed

    private void jButtonGraphMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonGraphMousePressed
        double[][] values = new double[2][3];
        try {
            for (int i = 0; i < values.length; i++) {
                for (int j = 0; j < values[0].length; j++) {
                    String value = jTableLl.getValueAt((i == 0) ? 1 : 7, j + 1).toString();
                    if (!value.equals(null)) {
                        values[i][j] = Double.valueOf(value);
                    }
                }
            }
        } catch (NullPointerException | NumberFormatException e) {

        }

        AtterbergGraph atterbergGraph = new AtterbergGraph(values);
    }//GEN-LAST:event_jButtonGraphMousePressed

    private void jButtonRefreshMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonRefreshMousePressed
        refresh();
    }//GEN-LAST:event_jButtonRefreshMousePressed

    private void jButtonClearMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonClearMousePressed
        if (JOptionPane.showConfirmDialog(
                null,
                "Advertencia: Todos los datos se borrarán. ",
                "Confirmación",
                JOptionPane.OK_CANCEL_OPTION) == 0) {
            clearTableValues(jTableWater);
            clearTableValues(jTableLl);
            clearTableValues(jTablePl);
            clearTableValues(jTableResult);
        }
    }//GEN-LAST:event_jButtonClearMousePressed

    private void jButtonNewMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonNewMousePressed
        if (JOptionPane.showConfirmDialog(null,
                "Advertencia: Los datos se perderán. \nGuarde si desea conservarlos.",
                "Confirmación", JOptionPane.OK_CANCEL_OPTION) == 0) {

            saveRestoreData = new SaveRestoreData();
            clearTableValues(jTableWater);
            clearTableValues(jTableLl);
            clearTableValues(jTablePl);
            clearTableValues(jTableResult);
            jButtonSaveAs.setVisible(false);
        }
    }//GEN-LAST:event_jButtonNewMousePressed

    private void jButtonOpenMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonOpenMousePressed
        try {
            String response = saveRestoreData.openProject();
            assignData();
//
            setTextByElement(jLabelFileInfo, response);
            jLabelTimer(jLabelFileInfo, 0);
            setTextByElement(jLabelFile, "Project: " + saveRestoreData.getPROJECT_FOLDER());
            String project = saveRestoreData.getPROJECT_FOLDER();
            jLabelTitle.setText("SUELOSMART" + (project != null && !project.equals("") ? " - " + project : ""));
            jButtonSaveAs.setVisible(true);
        } catch (Exception e) {
        }
    }//GEN-LAST:event_jButtonOpenMousePressed

    private void jButtonSaveMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonSaveMousePressed
        String project = saveRestoreData.getPROJECT_FOLDER();
        try {
            if (project.equals("")) {
                project = saveRestoreData.getProjectName();
                saveRestoreData.addTable(new JTable(1, 1), "water");
                saveRestoreData.addTable(new JTable(1, 1), "liquid-limit");
                saveRestoreData.addTable(new JTable(1, 1), "result");
                saveRestoreData.addTable(new JTable(1, 1), "plastic-limit");
                saveRestoreData.addTable(new JTable(1, 1), "sieves");
                saveRestoreData.addTable(new JTable(1, 1), "weight_sample");
            }
            if (project != null && !project.equals("")) {
                saveRestoreData.addTable(jTableWater, "water", 0);
                saveRestoreData.addTable(jTableLl, "liquid-limit", 1);
                saveRestoreData.addTable(jTableResult, "result", 2);
                saveRestoreData.addTable(jTablePl, "plastic-limit", 3);

                String response = saveRestoreData.saveProject();
                jButtonSaveAs.setVisible(true);

                setTextByElement(jLabelFileInfo, response);
                jLabelTimer(jLabelFileInfo, 0);
                setTextByElement(jLabelFile, "Project: " + saveRestoreData.getPROJECT_FOLDER());
                jLabelTitle.setText("SUELOSMART - " + saveRestoreData.getPROJECT_FOLDER());
            }
        } catch (NumberFormatException | NullPointerException e) {
        }
    }//GEN-LAST:event_jButtonSaveMousePressed

    private void jButtonSaveAsMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonSaveAsMousePressed
        String project;
        try {
            project = saveRestoreData.getProjectName();

            if (project != null && !project.equals("")) {
                saveRestoreData.clearTables();
                saveRestoreData.addTable(jTableWater, "water");
                saveRestoreData.addTable(jTableLl, "liquid-limit");
                saveRestoreData.addTable(jTableResult, "result");
                saveRestoreData.addTable(jTablePl, "plastic-limit");

                String response = saveRestoreData.saveProject();
                jButtonSaveAs.setVisible(true);

                setTextByElement(jLabelFileInfo, response);
                jLabelTimer(jLabelFileInfo, 0);
                setTextByElement(jLabelFile, "Project: " + saveRestoreData.getPROJECT_FOLDER());
                jLabelTitle.setText("SUELOSMART - " + saveRestoreData.getPROJECT_FOLDER());
            }
        } catch (NumberFormatException | NullPointerException e) {
        }
    }//GEN-LAST:event_jButtonSaveAsMousePressed

    private void jButtonBackMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonBackMousePressed
        this.dispose();
        new UILogin().setVisible(true);
    }//GEN-LAST:event_jButtonBackMousePressed

    private void jFormattedTextFieldFixFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jFormattedTextFieldFixFocusLost
        if (jFormattedTextFieldFix.getText().trim().matches("^[0-9]+$")) {
            if (Integer.parseInt(jFormattedTextFieldFix.getText()) > 15) {
                jFormattedTextFieldFix.setText("15");
            }

            if (Integer.parseInt(jFormattedTextFieldFix.getText()) <= 0) {
                jFormattedTextFieldFix.setText("1");
            }
        } else {
            jFormattedTextFieldFix.setText("3");
        }
    }//GEN-LAST:event_jFormattedTextFieldFixFocusLost

    private void jLabelFixPlusMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabelFixPlusMouseClicked
        if (Integer.parseInt(jFormattedTextFieldFix.getText()) < 15) {
            jFormattedTextFieldFix.setText(String.valueOf(Integer.parseInt(jFormattedTextFieldFix.getText()) + 1));
        }
    }//GEN-LAST:event_jLabelFixPlusMouseClicked

    private void jLabelFixPlusMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabelFixPlusMouseEntered
        jPanelFixPlus.setBackground(Color.decode("#E0E0E0"));
    }//GEN-LAST:event_jLabelFixPlusMouseEntered

    private void jLabelFixPlusMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabelFixPlusMouseExited
        jPanelFixPlus.setBackground(Color.decode("#333333"));
    }//GEN-LAST:event_jLabelFixPlusMouseExited

    private void jLabelFixMinusMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabelFixMinusMouseClicked
        if (Integer.parseInt(jFormattedTextFieldFix.getText()) > 1) {
            jFormattedTextFieldFix.setText(String.valueOf(Integer.parseInt(jFormattedTextFieldFix.getText()) - 1));
        }
    }//GEN-LAST:event_jLabelFixMinusMouseClicked

    private void jLabelFixMinusMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabelFixMinusMouseEntered
        jPanelFixMinus.setBackground(Color.decode("#E0E0E0"));
    }//GEN-LAST:event_jLabelFixMinusMouseEntered

    private void jLabelFixMinusMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabelFixMinusMouseExited
        jPanelFixMinus.setBackground(Color.decode("#333333"));
    }//GEN-LAST:event_jLabelFixMinusMouseExited

    private void jButtonTamizedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonTamizedActionPerformed
        this.dispose();
        UITamized uITamized = new UITamized();
        uITamized.setData(saveRestoreData);
        uITamized.setVisible(true);
    }//GEN-LAST:event_jButtonTamizedActionPerformed

    private void jButtonAboutMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonAboutMousePressed
        new UIAbout().setVisible(true);
    }//GEN-LAST:event_jButtonAboutMousePressed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonAbout;
    private javax.swing.JButton jButtonBack;
    private javax.swing.JButton jButtonClear;
    private javax.swing.JButton jButtonExit;
    private javax.swing.JButton jButtonGraph;
    private javax.swing.JButton jButtonHelp;
    private javax.swing.JButton jButtonMinimize;
    private javax.swing.JButton jButtonNew;
    private javax.swing.JButton jButtonOpen;
    private javax.swing.JButton jButtonRefresh;
    private javax.swing.JButton jButtonSave;
    private javax.swing.JButton jButtonSaveAs;
    private javax.swing.JButton jButtonTamized;
    private javax.swing.JFormattedTextField jFormattedTextFieldFix;
    private javax.swing.JLabel jLabelActionBg;
    private javax.swing.JLabel jLabelBgS1;
    private javax.swing.JLabel jLabelBgS2;
    private javax.swing.JLabel jLabelBgS3;
    private javax.swing.JLabel jLabelBgS5;
    private javax.swing.JLabel jLabelFile;
    private javax.swing.JLabel jLabelFileInfo;
    private javax.swing.JLabel jLabelFix;
    private javax.swing.JLabel jLabelFixMinus;
    private javax.swing.JLabel jLabelFixPlus;
    private javax.swing.JLabel jLabelLogo;
    private javax.swing.JLabel jLabelTitle;
    private javax.swing.JLabel jLabelTitleS1;
    private javax.swing.JLabel jLabelTitleS2;
    private javax.swing.JLabel jLabelTitleS3;
    private javax.swing.JLabel jLabelTitleS4;
    private javax.swing.JPanel jPanelBackground;
    private javax.swing.JPanel jPanelFix;
    private javax.swing.JPanel jPanelFixMinus;
    private javax.swing.JPanel jPanelFixPlus;
    private javax.swing.JPanel jPanelFooter;
    private javax.swing.JPanel jPanelHeader;
    private javax.swing.JPanel jPanelOptionBar;
    private javax.swing.JPanel jPanelSection1;
    private javax.swing.JPanel jPanelSection2;
    private javax.swing.JPanel jPanelSection3;
    private javax.swing.JPanel jPanelSection4;
    private javax.swing.JScrollPane jScrollPaneLl;
    private javax.swing.JScrollPane jScrollPanePl;
    private javax.swing.JScrollPane jScrollPaneResult;
    private javax.swing.JScrollPane jScrollPaneWater;
    private javax.swing.JTable jTableLl;
    private javax.swing.JTable jTablePl;
    private javax.swing.JTable jTableResult;
    private javax.swing.JTable jTableWater;
    // End of variables declaration//GEN-END:variables
}
