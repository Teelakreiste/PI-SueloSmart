/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UI;

import Entity.Constants;
import Entity.Fonts;
import Entity.Graph;
import Entity.Help;
import Entity.SaveRestoreData;
import Entity.Tamices;
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

/**
 *
 * @author osmel
 */
public class UITamized extends javax.swing.JFrame {

    private Fonts font;
    private SaveRestoreData saveRestoreData;
    private int xMouse, yMouse;

    private Help help = new Help();

    public UITamized() {
        initComponents();
        init();
    }

    private void init() {
        help.Help(jButtonHelp, jPanelBackground);
        saveRestoreData = new SaveRestoreData();
        font = new Fonts();

        setTitle(Constants.TITLE + " - Granulométria");
        setLocationRelativeTo(null);
//        setIconImage(new ImageIcon(getClass().getResource("/dispersas/Icon.png")).getImage());

        font();

        jTableTamices.getTableHeader().setBackground(Color.decode("#388E3C"));
        setForegroundtByElement(jTableTamices.getTableHeader(), "FFF");

        setSieved();
        getTableCellEditor(jTableTamices);
        customScrollBar(jScrollPaneTamices);
        customTableHeader(jTableTamices);

        jButtonSaveAs.setVisible(false);
    }

    private void font() {
        jTableTamices.getTableHeader().setFont(font.Font(font.ROBOTO_BOLD, 0, 12));
        jTableTamices.setFont(font.Font(font.ROBOTO_REGULAR, 0, 12));
        jTextFieldWeightSample.setFont(font.Font(font.ROBOTO_REGULAR, 1, 14));
        jLabelTitle.setFont(font.Font(font.ROBOTO_MEDIUM, 1, 18));
        jLabelFile.setFont(font.Font(font.ROBOTO_LIGHT, 0, 11));
        jLabelFileInfo.setFont(font.Font(font.ROBOTO_LIGHT, 0, 11));
    }

    public void setData(SaveRestoreData saveRestoreData) {
        this.saveRestoreData = saveRestoreData;
        assignData();
    }

    private void assignData() {
        try {
            saveRestoreData.assignData(jTableTamices, 4);
            JTable sample = new JTable(1, 2);
            saveRestoreData.assignData(sample, 5);

            jTextFieldWeightSample.setText((String) sample.getValueAt(0, 1));

            jButtonSaveAs.setVisible(true);

            setTextByElement(jLabelFileInfo, "Datos cargados exitosamente.");
            jLabelTimer(jLabelFileInfo, 0);
            setTextByElement(jLabelFile, "Project: " + saveRestoreData.getPROJECT_FOLDER());
            jLabelTitle.setText("SUELOSMART - " + saveRestoreData.getPROJECT_FOLDER());
        } catch (IndexOutOfBoundsException e) {

        }
    }

    // Crea un temporizador con el tiempo especificado
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

    private void autoAssign(double weightReturned, int row, DefaultTableModel model) {
        // Realiza los cálculos, reemplaza con tus fórmulas
        double percentageRetained = fixFormatDecimal(calculatePercentageRetained(weightReturned));
        model.setValueAt(percentageRetained, row, 3);
        double currentPercentage = 100.0;
        int previousRow = getLastPercentage(row, model);

        if (previousRow != -1) {
            currentPercentage = (double) model.getValueAt(previousRow, 4);
        }

        double percentagePass = fixFormatDecimal((currentPercentage - percentageRetained));
        model.setValueAt(fixFormatDecimal(percentagePass), row, 4);
    }

    private int getLastPercentage(int row, DefaultTableModel model) {
        for (int i = row - 1; i >= 0; i--) {
            if (model.getValueAt(i, 4) != null) {
                return i;
            }
        }
        return -1;
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

    private double calculatePercentageRetained(double weightReturned) {
        double weightSample;
        try {
            weightSample = Double.parseDouble(jTextFieldWeightSample.getText().trim().replace("g", ""));
            return (weightReturned / weightSample) * 100;
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    private void refreshPreviousValues(JTable jTable) {
        DefaultTableModel model = (DefaultTableModel) jTable.getModel();
        for (int row = 0; row < jTable.getRowCount(); row++) {
            double value;
            try {
                value = Double.parseDouble(String.valueOf(jTable.getValueAt(row, 2))); // Verifica la columna que contiene los datos (columna 2 en este caso)
                autoAssign(value, row, model);
            } catch (NumberFormatException | ClassCastException e) {
            }
        }

    }

    private void clearTableValues(JTable jTable) {
        DefaultTableModel model = (DefaultTableModel) jTable.getModel();
        model.setRowCount(0); // Establece el número de filas en 0
        setSieved();
    }

    private void setTableTamices(String[][] matriz, JTable jTable) {
        try {
            DefaultTableModel model = (DefaultTableModel) jTable.getModel();
            model.setRowCount(matriz.length);

            for (int i = 0; i < matriz.length; i++) {
                for (int j = 0; j < matriz[0].length; j++) {
                    String data = matriz[i][j];
                    if (!data.equals("null")) {
                        model.setValueAt(data, i, j);
                    }
                }
            }
        } catch (Exception e) {

        }
    }

    private void setSieved() {
        try {
            Tamices objTamices = new Tamices();
            DefaultTableModel model = (DefaultTableModel) jTableTamices.getModel();
            model.setRowCount(objTamices.getApertureSizeMm().size());

            DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
            renderer.setHorizontalAlignment(SwingConstants.CENTER);

            for (int i = 0; i < jTableTamices.getColumnCount(); i++) {
                jTableTamices.getColumnModel().getColumn(i).setCellRenderer(renderer);
            }

            for (int i = 0; i < objTamices.getApertureSizeMm().size(); i++) {
                model.isCellEditable(i, 0);
                jTableTamices.setValueAt(objTamices.getNumberTamices().get(i), i, 0);
                jTableTamices.setValueAt(objTamices.getApertureSizeMm().get(i), i, 1);
            }
        } catch (Exception e) {

        }
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

    private void getTableCellEditor(JTable jTable) {
        DefaultTableModel model = (DefaultTableModel) jTable.getModel();
        TableColumn column2 = jTable.getColumnModel().getColumn(2);

        column2.setCellEditor(new DefaultCellEditor(new JTextField()) {
            @Override
            public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
                JTextField editor = (JTextField) super.getTableCellEditorComponent(table, value, isSelected, row, column);

                editor.setBorder(new LineBorder(Color.decode("#388E3C"), 2));
                editor.setBackground(Color.decode("#388E3C"));
                editor.setForeground(Color.WHITE);
                editor.setHorizontalAlignment(0);
                editor.setSelectionColor(Color.decode("#333333"));

                editor.addFocusListener(new FocusAdapter() {
                    @Override
                    public void focusLost(FocusEvent e) {
                        updateValues(row);
                    }

                    private void updateValues(int row) {
                        // Realiza los cálculos en tiempo real y actualiza las columnas 3 y 4
                        double weightReturned = 0.0;
                        try {
                            weightReturned = Double.parseDouble((String) model.getValueAt(row, 2));
                        } catch (NumberFormatException | ClassCastException | NullPointerException e) {
                            model.setValueAt(weightReturned, row, 2);
                        }
                        autoAssign(weightReturned, row, model);
                    }
                });

                return editor;
            }
        }
        );
    }

    private String[][] getMatriz(JTable jTable) {
        DefaultTableModel model = (DefaultTableModel) jTable.getModel();

        // Obtén la cantidad de filas y columnas
        int numRows = model.getRowCount();
        int numCols = model.getColumnCount();

        // Crea una matriz bidimensional para almacenar los datos
        String[][] data = new String[numRows][numCols];

        // Llena la matriz con los datos de la tabla
        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                data[row][col] = String.valueOf(model.getValueAt(row, col));
            }
        }

        return data;
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
        jLabel2 = new javax.swing.JLabel();
        jButtonMinimize = new javax.swing.JButton();
        jButtonExit = new javax.swing.JButton();
        jLabelTitle = new javax.swing.JLabel();
        jPanelFooter = new javax.swing.JPanel();
        jLabelFileInfo = new javax.swing.JLabel();
        jLabelFile = new javax.swing.JLabel();
        jPanelTamices = new javax.swing.JPanel();
        jScrollPaneTamices = new javax.swing.JScrollPane();
        jTableTamices = new javax.swing.JTable();
        jPanelWeightSample = new javax.swing.JPanel();
        jTextFieldWeightSample = new javax.swing.JTextField();
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
        jButtonHelp = new javax.swing.JButton();
        jLabelActionBg = new javax.swing.JLabel();

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

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/logos/SueloSmart_v2_70x35.png"))); // NOI18N
        jPanelHeader.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 8, 70, 35));

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
        jPanelHeader.add(jButtonMinimize, new org.netbeans.lib.awtextra.AbsoluteConstraints(605, 18, 16, 16));

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
        jPanelHeader.add(jButtonExit, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 18, 16, 16));

        jLabelTitle.setForeground(new java.awt.Color(224, 224, 224));
        jLabelTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelTitle.setText("DISTRIBUCIÓN GRANULOMÉTRICA");
        jPanelHeader.add(jLabelTitle, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 10, 653, 30));

        jPanelBackground.add(jPanelHeader, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 660, 50));

        jPanelFooter.setBackground(new java.awt.Color(56, 142, 60));
        jPanelFooter.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabelFileInfo.setForeground(new java.awt.Color(255, 255, 51));
        jLabelFileInfo.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jPanelFooter.add(jLabelFileInfo, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 0, 300, 20));

        jLabelFile.setForeground(new java.awt.Color(255, 255, 255));
        jLabelFile.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jPanelFooter.add(jLabelFile, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 0, 300, 20));

        jPanelBackground.add(jPanelFooter, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 410, 660, 20));

        jPanelTamices.setBackground(new java.awt.Color(51, 51, 51));
        jPanelTamices.setLayout(new java.awt.BorderLayout());

        jScrollPaneTamices.setBackground(new java.awt.Color(51, 51, 51));
        jScrollPaneTamices.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(40, 40, 40)));
        jScrollPaneTamices.setFocusable(false);
        jScrollPaneTamices.setRequestFocusEnabled(false);

        jTableTamices.setBackground(new java.awt.Color(51, 51, 51));
        jTableTamices.setForeground(new java.awt.Color(224, 224, 224));
        jTableTamices.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "TAMIZ", "ABERTURA (mm)", "PESO RETENIDO (g)", "% RETENIDO", "% PASA"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, true, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTableTamices.setFillsViewportHeight(true);
        jTableTamices.setGridColor(new java.awt.Color(76, 175, 80));
        jTableTamices.setSelectionBackground(new java.awt.Color(76, 175, 80));
        jTableTamices.setShowHorizontalLines(false);
        jTableTamices.setShowVerticalLines(false);
        jScrollPaneTamices.setViewportView(jTableTamices);

        jPanelTamices.add(jScrollPaneTamices, java.awt.BorderLayout.CENTER);

        jPanelBackground.add(jPanelTamices, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 140, 610, 250));

        jPanelWeightSample.setBackground(new java.awt.Color(51, 51, 51));
        jPanelWeightSample.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jTextFieldWeightSample.setBackground(new java.awt.Color(51, 51, 51));
        jTextFieldWeightSample.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTextFieldWeightSample.setForeground(new java.awt.Color(153, 153, 153));
        jTextFieldWeightSample.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextFieldWeightSample.setText("Peso muestra (g)");
        jTextFieldWeightSample.setBorder(javax.swing.BorderFactory.createCompoundBorder(null, javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        jTextFieldWeightSample.setSelectionColor(new java.awt.Color(76, 175, 80));
        jTextFieldWeightSample.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTextFieldWeightSampleFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextFieldWeightSampleFocusLost(evt);
            }
        });
        jTextFieldWeightSample.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTextFieldWeightSampleMousePressed(evt);
            }
        });
        jPanelWeightSample.add(jTextFieldWeightSample, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 190, 30));

        jPanelBackground.add(jPanelWeightSample, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 95, 190, 30));

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

        jPanelOptionBar.add(jPanelFix, new org.netbeans.lib.awtextra.AbsoluteConstraints(418, 5, 75, 30));

        jButtonHelp.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/buttons/help.png"))); // NOI18N
        jButtonHelp.setBorderPainted(false);
        jButtonHelp.setContentAreaFilled(false);
        jButtonHelp.setFocusPainted(false);
        jButtonHelp.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/buttons/help.png"))); // NOI18N
        jButtonHelp.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/buttons/help_hover.png"))); // NOI18N
        jButtonHelp.setRolloverSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/buttons/help.png"))); // NOI18N
        jButtonHelp.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/buttons/help_hover.png"))); // NOI18N
        jPanelOptionBar.add(jButtonHelp, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 12, 16, 16));

        jLabelActionBg.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/buttons/action_bg_600X40.png"))); // NOI18N
        jPanelOptionBar.add(jLabelActionBg, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 600, 40));

        jPanelBackground.add(jPanelOptionBar, new org.netbeans.lib.awtextra.AbsoluteConstraints(25, 90, 600, 40));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanelBackground, javax.swing.GroupLayout.PREFERRED_SIZE, 654, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanelBackground, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonExitMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonExitMousePressed
        System.exit(0);
    }//GEN-LAST:event_jButtonExitMousePressed

    private void jButtonMinimizeMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonMinimizeMousePressed
        setState(ICONIFIED);
    }//GEN-LAST:event_jButtonMinimizeMousePressed

    private void jButtonOpenMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonOpenMousePressed
        try {
            String response = saveRestoreData.openProject();
            assignData();
//
            setTextByElement(jLabelFileInfo, response);
            jLabelTimer(jLabelFileInfo, 0);
            setTextByElement(jLabelFile, "Project: " + saveRestoreData.getPROJECT_FOLDER());
            jLabelTitle.setText("SUELOSMART - " + saveRestoreData.getPROJECT_FOLDER());
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

                saveRestoreData.addTable(jTableTamices, "sieves", 4);

                JTable sample = new JTable(1, 2);
                sample.setValueAt(jTextFieldWeightSample.getText(), 0, 1);

                saveRestoreData.addTable(sample, "weight_sample", 5);

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
                saveRestoreData.removeTable(4);
                saveRestoreData.removeTable(5);
                saveRestoreData.addTable(jTableTamices, "sieves");

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

    private void jButtonRefreshMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonRefreshMousePressed
        refreshPreviousValues(jTableTamices);
    }//GEN-LAST:event_jButtonRefreshMousePressed

    private void jButtonClearMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonClearMousePressed
        if (JOptionPane.showConfirmDialog(
                null,
                "Advertencia: Todos los datos se borrarán. ",
                "Confirmación",
                JOptionPane.OK_CANCEL_OPTION) == 0) {
            clearTableValues(jTableTamices);
            setTextByElement(jTextFieldWeightSample, "Peso muestra (g)");
            setForegroundtByElement(jTextFieldWeightSample, "#999999");
        }
    }//GEN-LAST:event_jButtonClearMousePressed

    private void jButtonGraphMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonGraphMousePressed
//        GranulometryGraph granulometryGraph = new GranulometryGraph(getMatriz(jTableTamices));
        Graph graph = new Graph(getMatriz(jTableTamices));
    }//GEN-LAST:event_jButtonGraphMousePressed

    private void jTextFieldWeightSampleFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextFieldWeightSampleFocusLost
        String str;
        if (jTextFieldWeightSample.getText().trim().replace("g", "").matches("^[0-9]+(\\.[0-9]+)?$")) {
            str = jTextFieldWeightSample.getText().trim() + " g";
        } else {
            str = "Peso muestra (g)";
            setForegroundtByElement(jTextFieldWeightSample, "#999999");
        }
        setTextByElement(jTextFieldWeightSample, str);
    }//GEN-LAST:event_jTextFieldWeightSampleFocusLost

    private void jTextFieldWeightSampleFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextFieldWeightSampleFocusGained
        setTextByElement(jTextFieldWeightSample, jTextFieldWeightSample.getText().replace("g", "").trim());
        setForegroundtByElement(jTextFieldWeightSample, "#E0E0E0");
    }//GEN-LAST:event_jTextFieldWeightSampleFocusGained

    private void jPanelHeaderMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanelHeaderMousePressed
        xMouse = evt.getX();
        yMouse = evt.getY();
    }//GEN-LAST:event_jPanelHeaderMousePressed

    private void jPanelHeaderMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanelHeaderMouseDragged
        int x = evt.getXOnScreen();
        int y = evt.getYOnScreen();
        this.setLocation(x - xMouse, y - yMouse);
    }//GEN-LAST:event_jPanelHeaderMouseDragged

    private void jButtonBackMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonBackMousePressed
        this.dispose();
        UILlPl uILlPl = new UILlPl();
        uILlPl.setData(saveRestoreData);
        uILlPl.setVisible(true);
    }//GEN-LAST:event_jButtonBackMousePressed

    private void jTextFieldWeightSampleMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextFieldWeightSampleMousePressed
        if (jTextFieldWeightSample.getText().equals("Peso muestra (g)")) {
            setTextByElement(jTextFieldWeightSample, "");
            setForegroundtByElement(jTextFieldWeightSample, "#E0E0E0");
        }
    }//GEN-LAST:event_jTextFieldWeightSampleMousePressed

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

    private void jLabelFixMinusMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabelFixMinusMouseClicked
        if (Integer.parseInt(jFormattedTextFieldFix.getText()) > 1) {
            jFormattedTextFieldFix.setText(String.valueOf(Integer.parseInt(jFormattedTextFieldFix.getText()) - 1));
        }
    }//GEN-LAST:event_jLabelFixMinusMouseClicked

    private void jLabelFixPlusMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabelFixPlusMouseEntered
        jPanelFixPlus.setBackground(Color.decode("#E0E0E0"));
    }//GEN-LAST:event_jLabelFixPlusMouseEntered

    private void jLabelFixMinusMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabelFixMinusMouseEntered
        jPanelFixMinus.setBackground(Color.decode("#E0E0E0"));
    }//GEN-LAST:event_jLabelFixMinusMouseEntered

    private void jLabelFixPlusMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabelFixPlusMouseExited
        jPanelFixPlus.setBackground(Color.decode("#333333"));
    }//GEN-LAST:event_jLabelFixPlusMouseExited

    private void jLabelFixMinusMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabelFixMinusMouseExited
        jPanelFixMinus.setBackground(Color.decode("#333333"));
    }//GEN-LAST:event_jLabelFixMinusMouseExited

    private void jButtonNewMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonNewMousePressed
        if (JOptionPane.showConfirmDialog(null,
                "Advertencia: Los datos se perderán. \nGuarde si desea conservarlos.",
                "Confirmación", JOptionPane.OK_CANCEL_OPTION) == 0) {

            clearTableValues(jTableTamices);
            setTextByElement(jTextFieldWeightSample, "Peso muestra (g)");
            setForegroundtByElement(jTextFieldWeightSample, "#999999");

            setTextByElement(jLabelFileInfo, "Nuevo archivo");
            jLabelTimer(jLabelFileInfo, 0);
            setTextByElement(jLabelFile, "");
        }
    }//GEN-LAST:event_jButtonNewMousePressed

    /**
     * @param args the command line arguments
     */
//    public static void main(String args[]) {
//        /* Set the Nimbus look and feel */
//        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
//        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
//         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
//         */
//        try {
//            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
//                if ("Nimbus".equals(info.getName())) {
//                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
//                    break;
//
//                }
//            }
//        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
//            java.util.logging.Logger.getLogger(UITamized.class
//                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
//
//        }
//        //</editor-fold>
//        //</editor-fold>
//        //</editor-fold>
//        //</editor-fold>
//
//        //</editor-fold>
//        //</editor-fold>
//
//        /* Create and display the form */
//        java.awt.EventQueue.invokeLater(() -> {
//            new UITamized().setVisible(true);
//        });
//    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
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
    private javax.swing.JFormattedTextField jFormattedTextFieldFix;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabelActionBg;
    private javax.swing.JLabel jLabelFile;
    private javax.swing.JLabel jLabelFileInfo;
    private javax.swing.JLabel jLabelFix;
    private javax.swing.JLabel jLabelFixMinus;
    private javax.swing.JLabel jLabelFixPlus;
    private javax.swing.JLabel jLabelTitle;
    private javax.swing.JPanel jPanelBackground;
    private javax.swing.JPanel jPanelFix;
    private javax.swing.JPanel jPanelFixMinus;
    private javax.swing.JPanel jPanelFixPlus;
    private javax.swing.JPanel jPanelFooter;
    private javax.swing.JPanel jPanelHeader;
    private javax.swing.JPanel jPanelOptionBar;
    private javax.swing.JPanel jPanelTamices;
    private javax.swing.JPanel jPanelWeightSample;
    private javax.swing.JScrollPane jScrollPaneTamices;
    private javax.swing.JTable jTableTamices;
    private javax.swing.JTextField jTextFieldWeightSample;
    // End of variables declaration//GEN-END:variables

}
