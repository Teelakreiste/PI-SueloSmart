/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UI;

import Entity.Constants;
import Entity.Fonts;
import Entity.Tamices;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import javax.swing.DefaultCellEditor;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

/**
 *
 * @author osmel
 */
public class UITamizado extends javax.swing.JFrame {

    private Fonts font;

    public UITamizado() {
        initComponents();
        init();
    }

    private void init() {
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
    }

    private void font() {
        jTableTamices.getTableHeader().setFont(font.Font(font.ROBOTO_BOLD, 0, 12));
        jTableTamices.setFont(font.Font(font.ROBOTO_REGULAR, 0, 12));
        jTextFieldWeightSample.setFont(font.Font(font.ROBOTO_REGULAR, 1, 14));
        jLabel1.setFont(font.Font(font.ROBOTO_MEDIUM, 1, 18));
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
            weightSample = Double.parseDouble(jTextFieldWeightSample.getText());
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
                value = 0.0;
            }
        }

    }

    private void clearTableValues(JTable jTable) {
        DefaultTableModel model = (DefaultTableModel) jTable.getModel();
        model.setRowCount(0); // Establece el número de filas en 0
        setSieved();
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
                        } catch (NumberFormatException | ClassCastException e) {
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
        jPanel6 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableTamices = new javax.swing.JTable();
        jPanel4 = new javax.swing.JPanel();
        jTextFieldWeightSample = new javax.swing.JTextField();
        jButtonBack = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jButtonGraph = new javax.swing.JButton();
        jButtonRefresh = new javax.swing.JButton();
        jButtonClear = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(51, 51, 51));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel5.setBackground(new java.awt.Color(56, 142, 60));
        jPanel5.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/logos/SueloSmart_v2_70x35.png"))); // NOI18N
        jPanel5.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, 70, 35));

        jPanel1.add(jPanel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 660, 50));

        jPanel6.setBackground(new java.awt.Color(56, 142, 60));
        jPanel6.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
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
                java.lang.String.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class
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
        jTextFieldWeightSample.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "PESO MUESTRA (g)", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11), new java.awt.Color(56, 142, 60))); // NOI18N
        jPanel4.add(jTextFieldWeightSample, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 190, 40));

        jPanel1.add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 90, 190, 40));

        jButtonBack.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/buttons/back_32x32.png"))); // NOI18N
        jButtonBack.setBorderPainted(false);
        jButtonBack.setContentAreaFilled(false);
        jButtonBack.setFocusPainted(false);
        jButtonBack.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/buttons/back_32x32.png"))); // NOI18N
        jButtonBack.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/buttons/back_hover_32x32.png"))); // NOI18N
        jButtonBack.setRolloverSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/buttons/back_32x32.png"))); // NOI18N
        jButtonBack.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/buttons/back_hover_32x32.png"))); // NOI18N
        jButtonBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonBackActionPerformed(evt);
            }
        });
        jPanel1.add(jButtonBack, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 95, 32, 32));

        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("DISTRIBUCIÓN GRANULOMÉTRICA");
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 54, 653, 30));

        jPanel3.setBackground(new java.awt.Color(51, 51, 51));
        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Actions", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11), new java.awt.Color(56, 142, 60))); // NOI18N
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jButtonGraph.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/buttons/graph.png"))); // NOI18N
        jButtonGraph.setBorderPainted(false);
        jButtonGraph.setContentAreaFilled(false);
        jButtonGraph.setFocusPainted(false);
        jButtonGraph.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/buttons/graph.png"))); // NOI18N
        jButtonGraph.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/buttons/graph_hover.png"))); // NOI18N
        jButtonGraph.setRolloverSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/buttons/graph.png"))); // NOI18N
        jButtonGraph.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/buttons/graph_hover.png"))); // NOI18N
        jButtonGraph.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonGraphActionPerformed(evt);
            }
        });
        jPanel3.add(jButtonGraph, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 15, 16, 16));

        jButtonRefresh.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/buttons/update.png"))); // NOI18N
        jButtonRefresh.setBorderPainted(false);
        jButtonRefresh.setContentAreaFilled(false);
        jButtonRefresh.setFocusPainted(false);
        jButtonRefresh.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/buttons/update.png"))); // NOI18N
        jButtonRefresh.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/buttons/update_hover.png"))); // NOI18N
        jButtonRefresh.setRolloverSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/buttons/update.png"))); // NOI18N
        jButtonRefresh.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/buttons/update_hover.png"))); // NOI18N
        jButtonRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRefreshActionPerformed(evt);
            }
        });
        jPanel3.add(jButtonRefresh, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 15, 16, 16));

        jButtonClear.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/buttons/clear.png"))); // NOI18N
        jButtonClear.setBorderPainted(false);
        jButtonClear.setContentAreaFilled(false);
        jButtonClear.setFocusPainted(false);
        jButtonClear.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/buttons/clear.png"))); // NOI18N
        jButtonClear.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/buttons/clear_hover.png"))); // NOI18N
        jButtonClear.setRolloverSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/buttons/clear.png"))); // NOI18N
        jButtonClear.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/buttons/clear_hover.png"))); // NOI18N
        jButtonClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonClearActionPerformed(evt);
            }
        });
        jPanel3.add(jButtonClear, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 15, 16, 16));

        jPanel1.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 90, 130, 40));

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

    private void jButtonBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonBackActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButtonBackActionPerformed

    private void jButtonGraphActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonGraphActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButtonGraphActionPerformed

    private void jButtonRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRefreshActionPerformed
        refreshPreviousValues(jTableTamices);
    }//GEN-LAST:event_jButtonRefreshActionPerformed

    private void jButtonClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonClearActionPerformed
        clearTableValues(jTableTamices);
    }//GEN-LAST:event_jButtonClearActionPerformed

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
            java.util.logging.Logger.getLogger(UITamizado.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        }
        //</editor-fold>
        //</editor-fold>

        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            new UITamizado().setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonBack;
    private javax.swing.JButton jButtonClear;
    private javax.swing.JButton jButtonGraph;
    private javax.swing.JButton jButtonRefresh;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
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
