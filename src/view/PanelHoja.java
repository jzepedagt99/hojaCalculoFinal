/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view;

import java.awt.BorderLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import modelo.HojaCalculo;

/**
 *
 * @author jzepeda
 */
public class PanelHoja extends JPanel {
    private JTable tabla;
    private MiTableModel tableModel;

    public PanelHoja(HojaCalculo hoja) {
        setLayout(new BorderLayout());
        tableModel = new MiTableModel(hoja);
        tabla = new JTable(tableModel);

        tabla.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); // Para que el scroll horizontal funcione bien
        tabla.setCellSelectionEnabled(true);

        // Ajustar ancho de la columna de números de fila
        tabla.getColumnModel().getColumn(0).setPreferredWidth(40);
        tabla.getColumnModel().getColumn(0).setMinWidth(40);
        tabla.getColumnModel().getColumn(0).setMaxWidth(60);
        // Centrar números de fila
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        tabla.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);


        JScrollPane scrollPane = new JScrollPane(tabla);
        add(scrollPane, BorderLayout.CENTER);
    }

    public JTable getTabla() {
        return tabla;
    }

    public MiTableModel getTableModel() {
        return tableModel;
    }

    public void actualizarHoja(HojaCalculo nuevaHoja) {
        tableModel.setHoja(nuevaHoja);
    }
}
