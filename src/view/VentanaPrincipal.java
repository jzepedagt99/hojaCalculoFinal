package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.util.Vector;
import javax.swing.*;
import static javax.swing.SwingConstants.CENTER;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import modelo.Celda;
import modelo.HojaCalculo;

/**
 *
 * @author jzepeda
 */
public class VentanaPrincipal extends JFrame {
    
    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private JTextField campoFormula;
    private JButton btnAplicar;
    private JLabel celdaSeleccionada;

    private final int NUM_FILAS_DEFAULT; 
    private final int NUM_COLUMNAS_DEFAULT; 

    private HojaCalculo hojaCalculoModelo;
    private int filaSeleccionada = -1;
    private int columnaSeleccionada = -1;

    // Para el encabezado de filas
    private JTable tablaEncabezadoFilas;
    private RowHeaderTableModel modeloEncabezadoFilas;

    public VentanaPrincipal(HojaCalculo modeloExterno) {
        this.hojaCalculoModelo = modeloExterno;
        this.NUM_FILAS_DEFAULT = modeloExterno.getFilas();
        this.NUM_COLUMNAS_DEFAULT = modeloExterno.getColumnas();

        setTitle("Hoja de cálculo");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(850, 600);
        setLayout(new BorderLayout(5, 5));

        JPanel panelSuperior = new JPanel(new BorderLayout(5, 5));
        celdaSeleccionada = new JLabel("F(x) : ");
        celdaSeleccionada.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        campoFormula = new JTextField();
        btnAplicar = new JButton("Aplicar");

        panelSuperior.add(celdaSeleccionada, BorderLayout.WEST);
        panelSuperior.add(campoFormula, BorderLayout.CENTER);
        panelSuperior.add(btnAplicar, BorderLayout.EAST);
        panelSuperior.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 5));
        add(panelSuperior, BorderLayout.NORTH);

        String[] nombresColumnas = generarNombresColumnas(NUM_COLUMNAS_DEFAULT);

        modeloTabla = new DefaultTableModel(null, nombresColumnas) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return true; 
            }

            @Override
            public void setValueAt(Object aValue, int row, int column) {                

                System.out.println("JTable.DefaultTableModel.setValueAt: Fila=" + row + ", Col=" + column + ", ValorEntrante=" + aValue);

                if (hojaCalculoModelo != null && aValue instanceof String) {
                    hojaCalculoModelo.setContenidoCelda(row, column, (String) aValue);
                   
                    // Se necesita refrescar la tabla.
                    hojaCalculoModelo.recalcularHojaCompleta();
                    actualizarTablaConModeloCompleto(); // Refresca toda la vista

                    //Actualizar el campo de fórmula si la celda editada es la seleccionada
                    if (row == filaSeleccionada && column == columnaSeleccionada) {
                        Celda celdaActualizada = hojaCalculoModelo.getCelda(row, column);
                        String contenidoCelda = (celdaActualizada.getFormula() != null)
                                ? celdaActualizada.getFormula() : celdaActualizada.getValorMostrado();
                        campoFormula.setText(contenidoCelda);
                    }
                } else {

                    super.setValueAt(aValue, row, column);
                }
            }
        };
        modeloTabla.setRowCount(NUM_FILAS_DEFAULT);

        tabla = new JTable(modeloTabla);
        tabla.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tabla.setCellSelectionEnabled(true);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setRowHeight(25);

        // --- Configuración del Encabezado de Filas ---
        modeloEncabezadoFilas = new RowHeaderTableModel(NUM_FILAS_DEFAULT);
        tablaEncabezadoFilas = new JTable(modeloEncabezadoFilas);
        tablaEncabezadoFilas.setPreferredScrollableViewportSize(new Dimension(50, 0)); // Ancho del encabezado de fila
        tablaEncabezadoFilas.getColumnModel().getColumn(0).setPreferredWidth(50);
        tablaEncabezadoFilas.setDefaultRenderer(Object.class, new RowHeaderRenderer(tabla)); // Renderer personalizado
        tablaEncabezadoFilas.setRowHeight(tabla.getRowHeight()); // Sincronizar altura
        tablaEncabezadoFilas.setFocusable(false);
        tablaEncabezadoFilas.setCellSelectionEnabled(false);
        tablaEncabezadoFilas.setIntercellSpacing(new Dimension(0, 0));
        
        JTableHeader corner = tablaEncabezadoFilas.getTableHeader();
        corner.setResizingAllowed(false);
        corner.setReorderingAllowed(false);
        corner.setPreferredSize(new Dimension(50, tabla.getTableHeader().getPreferredSize().height)); // Altura de cabecera
        
        JScrollPane scrollPane = new JScrollPane(tabla);
        scrollPane.setRowHeaderView(tablaEncabezadoFilas); // ¡Añade el encabezado de filas aquí!
        
        TableColumnModel columnModel = tabla.getColumnModel();
        for (int i = 0; i < NUM_COLUMNAS_DEFAULT; i++) {
            columnModel.getColumn(i).setPreferredWidth(75);
        }

        add(scrollPane, BorderLayout.CENTER);

        ListSelectionListener cellSelectionListener = e -> {
            if (!e.getValueIsAdjusting()) {
                filaSeleccionada = tabla.getSelectedRow();
                columnaSeleccionada = tabla.getSelectedColumn();

                if (filaSeleccionada != -1 && columnaSeleccionada != -1) {
                    setEtiquetaCeldaSeleccionada(filaSeleccionada, columnaSeleccionada);
                    Celda celda = hojaCalculoModelo.getCelda(filaSeleccionada, columnaSeleccionada);
                    if (celda != null) {
                        String contenidoParaCampo = (celda.getFormula() != null) ? celda.getFormula() : celda.getValorMostrado();
                        campoFormula.setText(contenidoParaCampo);
                    }
                }
            }
        };
        tabla.getSelectionModel().addListSelectionListener(cellSelectionListener);
        tabla.getColumnModel().getSelectionModel().addListSelectionListener(cellSelectionListener);

        btnAplicar.addActionListener(e -> {
            if (filaSeleccionada != -1 && columnaSeleccionada != -1) {
                String contenidoDesdeCampo = campoFormula.getText();
                // Actualizar el modelo lógico
                hojaCalculoModelo.setContenidoCelda(filaSeleccionada, columnaSeleccionada, contenidoDesdeCampo);

                hojaCalculoModelo.recalcularHojaCompleta();
                actualizarTablaConModeloCompleto();
        
            }
        });

        // Cargar datos iniciales del modelo a la vista
        actualizarTablaConModeloCompleto();
        setLocationRelativeTo(null);
    }

    private String[] generarNombresColumnas(int numColumnas) {
        String[] nombres = new String[numColumnas];
        for (int i = 0; i < numColumnas; i++) {
            nombres[i] = convertirAColumnaExcel(i);
        }
        return nombres;
    }

    private String convertirAColumnaExcel(int indiceColumna) {
        StringBuilder sb = new StringBuilder();
        if (indiceColumna < 0) {
            return "";
        }
        int idx = indiceColumna;
        do {
            sb.insert(0, (char) ('A' + idx % 26));
            idx = (idx / 26) - 1;
        } while (idx >= 0);
        return sb.toString();
    }

    public void actualizarTablaConModeloCompleto() {
        if (hojaCalculoModelo == null || modeloTabla == null) {
            return;
        }

        Celda[][] datosCeldas = hojaCalculoModelo.getMatrizCeldas();
        int filasModelo = hojaCalculoModelo.getFilas();
        int columnasModelo = hojaCalculoModelo.getColumnas();

        if (modeloTabla.getRowCount() != filasModelo) {
            modeloTabla.setRowCount(filasModelo);
            if (modeloEncabezadoFilas != null) {
                modeloEncabezadoFilas.setRowCount(filasModelo);
            }
        }
 
        for (int i = 0; i < filasModelo; i++) {
            for (int j = 0; j < columnasModelo; j++) {
                String valorAMostrar = "";
                if (datosCeldas[i][j] != null) {
                    valorAMostrar = datosCeldas[i][j].getValorMostrado();
                }
                
                if (modeloTabla.getRowCount() > i && modeloTabla.getColumnCount() > j) {
                
                    Vector rowVector = (Vector) modeloTabla.getDataVector().get(i);
                    if (rowVector.size() > j) {
                        rowVector.setElementAt(valorAMostrar, j);
                    } else {
                
                    }
                }
            }
        }
        modeloTabla.fireTableDataChanged(); // Notifica a la JTable que todo pudo haber cambiado
        if (tablaEncabezadoFilas != null) {
            tablaEncabezadoFilas.repaint();
        }
    }

    public void setEtiquetaCeldaSeleccionada(int fila, int columna) {
        String nombreCol = convertirAColumnaExcel(columna);
        celdaSeleccionada.setText("Celda: " + nombreCol + (fila + 1)); 
    }

    static class RowHeaderTableModel extends AbstractTableModel {

        private int rowCount;

        public RowHeaderTableModel(int rowCount) {
            this.rowCount = rowCount;
        }

        public void setRowCount(int rowCount) {
            int oldRowCount = this.rowCount;
            this.rowCount = rowCount;
            if (rowCount > oldRowCount) {
                fireTableRowsInserted(oldRowCount, rowCount - 1);
            } else if (rowCount < oldRowCount) {
                fireTableRowsDeleted(rowCount, oldRowCount - 1);
            }
        }

        @Override
        public int getRowCount() {
            return rowCount;
        }

        @Override
        public int getColumnCount() {
            return 1;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            return Integer.toString(rowIndex + 1);
        }

        @Override
        public String getColumnName(int column) {
            return ""; 
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return false;
        }
    }

    static class RowHeaderRenderer extends DefaultTableCellRenderer {

        private JTable mainTable;

        public RowHeaderRenderer(JTable mainTable) {
            this.mainTable = mainTable;
            setHorizontalAlignment(CENTER);
            setOpaque(true);
            setBorder(UIManager.getBorder("TableHeader.cellBorder")); 
            
            if (mainTable != null) {
                mainTable.getSelectionModel().addListSelectionListener(e -> repaint());
            }
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus,
                int row, int column) {
            if (mainTable != null) {
                JTableHeader header = mainTable.getTableHeader();
                if (header != null) {
                    setForeground(header.getForeground());
                    setBackground(header.getBackground());
                    setFont(header.getFont());
                }
                if (mainTable.getSelectedRow() == row) {
                    setFont(getFont().deriveFont(Font.BOLD));                
                }

            } else {
                setBackground(Color.LIGHT_GRAY);
                setFont(table.getFont().deriveFont(Font.BOLD));
            }

            setText((value == null) ? "" : value.toString());
            return this;
        }
    }
}
