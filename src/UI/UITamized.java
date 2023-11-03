/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UI;

import Entity.Constants;
import Entity.FileManager;
import Entity.Fonts;
import Entity.GranulometryGraph;
import Entity.Graph;
import Entity.Tamices;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import javax.swing.DefaultCellEditor;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

/**
 *
 * @author osmel
 */
public class UITamized extends javax.swing.JFrame {

    private Fonts font;
    private FileManager fileManager;

    public UITamized() {
        initComponents();
        init();
    }

    private void init() {
        fileManager = new FileManager();
        setTitle(Constants.TITLE + " - Granulométria");
        setLocationRelativeTo(null);
//        setIconImage(new ImageIcon(getClass().getResource("/dispersas/Icon.png")).getImage());
        font = new Fonts();
        jTableTamices.getTableHeader().setOpaque(false);
        jTableTamices.getTableHeader().setBackground(Color.decode("#388E3C"));
        jTableTamices.getTableHeader().setForeground(Color.WHITE);
        font();
        setSieved();
        getTableCellEditor(jTableTamices);

        jButtonSaveAs.setVisible(false);
    }

    private void font() {
        jTableTamices.getTableHeader().setFont(font.Font(font.ROBOTO_BOLD, 0, 12));
        jTableTamices.setFont(font.Font(font.ROBOTO_REGULAR, 0, 12));
        jTextFieldWeightSample.setFont(font.Font(font.ROBOTO_REGULAR, 1, 14));
        jLabel1.setFont(font.Font(font.ROBOTO_MEDIUM, 1, 18));
        jLabelFile.setFont(font.Font(font.ROBOTO_LIGHT, 0, 11));
        jLabelFileInfo.setFont(font.Font(font.ROBOTO_LIGHT, 0, 11));
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
        Double percentagePasa = calculatePercentagePass(weightReturned);
        model.setValueAt(percentagePasa, row, 3);
        Double currentPercentage = 100.0;
        int previousRow = getLastPercentage(row, model);

        if (previousRow != -1) {
            currentPercentage = (Double) model.getValueAt(previousRow, 4);
        }

        model.setValueAt((currentPercentage - percentagePasa), row, 4);
    }

    private int getLastPercentage(int row, DefaultTableModel model) {
        for (int i = row - 1; i >= 0; i--) {
            if (model.getValueAt(i, 4) != null) {
                return i;
            }
        }
        return -1;
    }

    private Double calculatePercentagePass(Double weightReturned) {
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

    private void getTableCellEditor(JTable jTable) {
        DefaultTableModel model = (DefaultTableModel) jTable.getModel();
        TableColumn column2 = jTable.getColumnModel().getColumn(2);

        column2.setCellEditor(new DefaultCellEditor(new JTextField()) {
            @Override
            public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
                JTextField editor = (JTextField) super.getTableCellEditorComponent(table, value, isSelected, row, column);

                editor.addFocusListener(new FocusAdapter() {
                    @Override
                    public void focusLost(FocusEvent e) {
                        updateValues(row);
                    }

                    private void updateValues(int row) {
                        // Realiza los cálculos en tiempo real y actualiza las columnas 3 y 4
                        Double weightReturned = 0.0;
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

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jButtonMinimize = new javax.swing.JButton();
        jButtonExit = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        jLabelFileInfo = new javax.swing.JLabel();
        jLabelFile = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableTamices = new javax.swing.JTable();
        jPanel4 = new javax.swing.JPanel();
        jTextFieldWeightSample = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jButtonGraph = new javax.swing.JButton();
        jButtonRefresh = new javax.swing.JButton();
        jButtonClear = new javax.swing.JButton();
        jButtonOpen = new javax.swing.JButton();
        jButtonSave = new javax.swing.JButton();
        jButtonSaveAs = new javax.swing.JButton();
        jButtonBack = new javax.swing.JButton();
        jLabelActionBg = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);

        jPanel1.setBackground(new java.awt.Color(51, 51, 51));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel5.setBackground(new java.awt.Color(56, 142, 60));
        jPanel5.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/logos/SueloSmart_v2_70x35.png"))); // NOI18N
        jPanel5.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 8, 70, 35));

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
        jPanel5.add(jButtonMinimize, new org.netbeans.lib.awtextra.AbsoluteConstraints(605, 18, 16, 16));

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
        jPanel5.add(jButtonExit, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 18, 16, 16));

        jPanel1.add(jPanel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 660, 50));

        jPanel6.setBackground(new java.awt.Color(56, 142, 60));
        jPanel6.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabelFileInfo.setForeground(new java.awt.Color(255, 255, 51));
        jLabelFileInfo.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jPanel6.add(jLabelFileInfo, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 0, 300, 20));

        jLabelFile.setForeground(new java.awt.Color(255, 255, 255));
        jLabelFile.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jPanel6.add(jLabelFile, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 0, 300, 20));

        jPanel1.add(jPanel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 410, 660, 20));

        jPanel2.setBackground(new java.awt.Color(51, 51, 51));
        jPanel2.setLayout(new java.awt.BorderLayout());

        jTableTamices.setAutoCreateRowSorter(true);
        jTableTamices.setBackground(new java.awt.Color(51, 51, 51));
        jTableTamices.setForeground(new java.awt.Color(224, 224, 224));
        jTableTamices.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "N° Tamiz", "Abertura (mm)", "Peso retenido (g)", "% pasa", "% retenido"
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
        jTableTamices.setGridColor(new java.awt.Color(76, 175, 80));
        jTableTamices.setSelectionBackground(new java.awt.Color(76, 175, 80));
        jScrollPane1.setViewportView(jTableTamices);

        jPanel2.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jPanel1.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 140, 610, 250));

        jPanel4.setBackground(new java.awt.Color(51, 51, 51));
        jPanel4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jTextFieldWeightSample.setBackground(new java.awt.Color(51, 51, 51));
        jTextFieldWeightSample.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTextFieldWeightSample.setForeground(new java.awt.Color(224, 224, 224));
        jTextFieldWeightSample.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextFieldWeightSample.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createEtchedBorder(), javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        jTextFieldWeightSample.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTextFieldWeightSampleFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextFieldWeightSampleFocusLost(evt);
            }
        });
        jPanel4.add(jTextFieldWeightSample, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 190, 40));

        jPanel1.add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 90, 190, 40));

        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("DISTRIBUCIÓN GRANULOMÉTRICA");
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 54, 653, 30));

        jPanel3.setBackground(new java.awt.Color(51, 51, 51));
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

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
        jPanel3.add(jButtonGraph, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 12, 16, 16));

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
        jPanel3.add(jButtonRefresh, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 12, 16, 16));

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
        jPanel3.add(jButtonClear, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 12, 16, 16));

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
        jPanel3.add(jButtonOpen, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 12, 16, 16));

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
        jPanel3.add(jButtonSave, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 12, 16, 16));

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
        jPanel3.add(jButtonSaveAs, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 12, 16, 16));

        jButtonBack.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/buttons/back_16x16.png"))); // NOI18N
        jButtonBack.setBorderPainted(false);
        jButtonBack.setContentAreaFilled(false);
        jButtonBack.setFocusPainted(false);
        jButtonBack.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/buttons/back_16x16.png"))); // NOI18N
        jButtonBack.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/buttons/back_hover_16x16.png"))); // NOI18N
        jButtonBack.setRolloverSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/buttons/back_16x16.png"))); // NOI18N
        jButtonBack.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/buttons/back_hover_16x16.png"))); // NOI18N
        jPanel3.add(jButtonBack, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 12, 16, 16));

        jLabelActionBg.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/buttons/action_bg_600X40.png"))); // NOI18N
        jPanel3.add(jLabelActionBg, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 600, 40));

        jPanel1.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(25, 90, 600, 40));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 654, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonExitMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonExitMousePressed
        System.exit(0);
    }//GEN-LAST:event_jButtonExitMousePressed

    private void jButtonMinimizeMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonMinimizeMousePressed
        setState(UITamized.ICONIFIED);
    }//GEN-LAST:event_jButtonMinimizeMousePressed

    private void jButtonOpenMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonOpenMousePressed
        try {
            setTableTamices(fileManager.openFile(), jTableTamices);
        } catch (Exception e) {
        }
        jTextFieldWeightSample.setText(fileManager.getValue().trim() + " g");
        this.jButtonSaveAs.setVisible(true);
        
        jLabelFileInfo.setText("Listo");
        jLabelTimer(jLabelFileInfo, 0);
        jLabelFile.setText("File: " + fileManager.getFile().getName());
        jButtonSaveAs.setVisible(true);
    }//GEN-LAST:event_jButtonOpenMousePressed

    private void jButtonSaveMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonSaveMousePressed
        double value;
        try {
            value = Double.parseDouble((String) jTextFieldWeightSample.getText().trim().replace("g", ""));
        } catch (NumberFormatException | NullPointerException e) {
            value = 0;
        }

        jLabelFileInfo.setText(fileManager.save(value, getMatriz(jTableTamices)));
        jLabelTimer(jLabelFileInfo, 0);
        jLabelFile.setText("File: " + fileManager.getFile().getName());
        jButtonSaveAs.setVisible(true);
    }//GEN-LAST:event_jButtonSaveMousePressed

    private void jButtonSaveAsMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonSaveAsMousePressed
        double value;
        try {
            value = Double.parseDouble((String) jTextFieldWeightSample.getText().trim().replace("g", ""));
        } catch (NumberFormatException | NullPointerException e) {
            value = 0;
        }

        fileManager.saveAs(value, getMatriz(jTableTamices));
    }//GEN-LAST:event_jButtonSaveAsMousePressed

    private void jButtonRefreshMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonRefreshMousePressed
        refreshPreviousValues(jTableTamices);
    }//GEN-LAST:event_jButtonRefreshMousePressed

    private void jButtonClearMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonClearMousePressed
        clearTableValues(jTableTamices);
        jTextFieldWeightSample.setText("");
    }//GEN-LAST:event_jButtonClearMousePressed

    private void jButtonGraphMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonGraphMousePressed
        GranulometryGraph granulometryGraph = new GranulometryGraph(getMatriz(jTableTamices));
        Graph graph = new Graph(getMatriz(jTableTamices));
    }//GEN-LAST:event_jButtonGraphMousePressed

    private void jTextFieldWeightSampleFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextFieldWeightSampleFocusLost
        jTextFieldWeightSample.setText(jTextFieldWeightSample.getText().trim() + " g");
    }//GEN-LAST:event_jTextFieldWeightSampleFocusLost

    private void jTextFieldWeightSampleFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextFieldWeightSampleFocusGained
        jTextFieldWeightSample.setText(jTextFieldWeightSample.getText().replace("g", "").trim());
    }//GEN-LAST:event_jTextFieldWeightSampleFocusGained

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;

                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(UITamized.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            new UITamized().setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonBack;
    private javax.swing.JButton jButtonClear;
    private javax.swing.JButton jButtonExit;
    private javax.swing.JButton jButtonGraph;
    private javax.swing.JButton jButtonMinimize;
    private javax.swing.JButton jButtonOpen;
    private javax.swing.JButton jButtonRefresh;
    private javax.swing.JButton jButtonSave;
    private javax.swing.JButton jButtonSaveAs;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabelActionBg;
    private javax.swing.JLabel jLabelFile;
    private javax.swing.JLabel jLabelFileInfo;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTableTamices;
    private javax.swing.JTextField jTextFieldWeightSample;
    // End of variables declaration//GEN-END:variables

}
