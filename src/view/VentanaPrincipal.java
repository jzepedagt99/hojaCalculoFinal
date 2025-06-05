/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import javax.swing.*;
import modelo.HojaCalculo;
import modelo.LibroTrabajo;

/**
 *
 * @author jzepeda
 */
public class VentanaPrincipal extends JFrame {
    private LibroTrabajo libroTrabajoModelo; // Referencia al Modelo
    private JTextField barraFormulas;
    private JTabbedPane pestañasHojas;
    private JButton btnNuevaHoja;

    // Referencias al controlador (aún no implementado aquí)
    // private ControladorHojaCalculo controlador;

    public VentanaPrincipal(LibroTrabajo libro) {
        this.libroTrabajoModelo = libro;
        // this.controlador = new ControladorHojaCalculo(libro, this); // Idealmente

        setTitle("Mini Hoja de Cálculo");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null); // Centrar en pantalla

        initComponents();
        // Inicialmente, el controlador debería crear la primera hoja y añadirla
        // Simulación aquí:
        if (libroTrabajoModelo.getNumeroDeHojas() == 0) {
            añadirNuevaHoja("Hoja1"); // El controlador lo haría
        } else {
            // Refrescar pestañas si ya hay hojas (ej. al cargar un archivo)
            for(HojaCalculo hoja : libroTrabajoModelo.getTodasLasHojas()){
                 PanelHoja panelHoja = new PanelHoja(hoja);
                 pestañasHojas.addTab(hoja.getNombre(), panelHoja);
            }
        }
    }

    private void initComponents() {
        // --- Barra de Fórmulas ---
        JPanel panelSuperior = new JPanel(new BorderLayout());
        JLabel lblFx = new JLabel("  fx: ");
        barraFormulas = new JTextField();
        panelSuperior.add(lblFx, BorderLayout.WEST);
        panelSuperior.add(barraFormulas, BorderLayout.CENTER);
        add(panelSuperior, BorderLayout.NORTH);

        // --- Pestañas para las Hojas ---
        pestañasHojas = new JTabbedPane();
        add(pestañasHojas, BorderLayout.CENTER);

        // --- Botón para Nueva Hoja ---
        btnNuevaHoja = new JButton("Añadir Hoja");
        JPanel panelInferior = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelInferior.add(btnNuevaHoja);
        add(panelInferior, BorderLayout.SOUTH);

        // --- Listeners (manejados por el Controlador idealmente) ---
        btnNuevaHoja.addActionListener(e -> {
            // Esto sería manejado por el Controlador
            String nombreNuevaHoja = "Hoja" + (libroTrabajoModelo.getNumeroDeHojas() + 1);
            añadirNuevaHoja(nombreNuevaHoja);
        });

        barraFormulas.addActionListener(e -> {
            // Esto sería manejado por el Controlador
            int filaSeleccionada = -1;
            int colSeleccionada = -1;

            PanelHoja panelActual = (PanelHoja) pestañasHojas.getSelectedComponent();
            if (panelActual != null) {
                filaSeleccionada = panelActual.getTabla().getSelectedRow();
                // +1 porque la columna 0 de la tabla es el número de fila.
                colSeleccionada = panelActual.getTabla().getSelectedColumn() -1; 
            }

            if (filaSeleccionada != -1 && colSeleccionada != -1) {
                System.out.println("Vista: Barra de fórmulas Enter. Celda: [" + filaSeleccionada + "," + colSeleccionada + "] = " + barraFormulas.getText());
                // El controlador tomaría este valor y actualizaría el modelo
                // libroTrabajoModelo.getHojaActiva().setValorCelda(filaSeleccionada, colSeleccionada, barraFormulas.getText());
                // ((MiTableModel)panelActual.getTabla().getModel()).fireTableCellUpdated(filaSeleccionada, colSeleccionada+1);

                // Simulación directa para ver el cambio (rompe MVC para esta demo)
                HojaCalculo hojaActiva = libroTrabajoModelo.getHojaActiva();
                if(hojaActiva != null) {
                    hojaActiva.setValorCelda(filaSeleccionada, colSeleccionada, barraFormulas.getText());
                    // Actualizar la tabla
                     ((MiTableModel)panelActual.getTabla().getModel()).fireTableCellUpdated(filaSeleccionada, colSeleccionada + 1);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Seleccione una celda primero.", "Información", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        
        pestañasHojas.addChangeListener(e -> {
            int indiceSeleccionado = pestañasHojas.getSelectedIndex();
            if (indiceSeleccionado != -1) {
                libroTrabajoModelo.setHojaActiva(indiceSeleccionado);
                System.out.println("Vista: Pestaña cambiada a: " + libroTrabajoModelo.getHojaActiva().getNombre());
                // Actualizar barra de fórmulas si hay celda seleccionada
                actualizarBarraFormulasConCeldaSeleccionada();
            }
        });
        
        // Listener para JTable (para actualizar barra de fórmulas al seleccionar celda)
        // Este listener se añade a cada JTable cuando se crea la pestaña.
    }
    
    // Este método sería llamado por el controlador cuando se crea una hoja en el modelo.
    public void añadirNuevaHoja(String nombreSugerido) {
        HojaCalculo nuevaHojaModelo = libroTrabajoModelo.crearNuevaHoja(nombreSugerido); // El modelo la crea
        PanelHoja nuevoPanelHoja = new PanelHoja(nuevaHojaModelo);

        // Añadir listener a la JTable del nuevo panel para actualizar la barra de fórmulas
        nuevoPanelHoja.getTabla().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) { // Para evitar eventos duplicados
                actualizarBarraFormulasConCeldaSeleccionada();
            }
        });
        nuevoPanelHoja.getTabla().getColumnModel().getSelectionModel().addListSelectionListener(e -> {
             if (!e.getValueIsAdjusting()) {
                actualizarBarraFormulasConCeldaSeleccionada();
            }
        });

        pestañasHojas.addTab(nuevaHojaModelo.getNombre(), nuevoPanelHoja);
        pestañasHojas.setSelectedComponent(nuevoPanelHoja); // Seleccionar la nueva pestaña
        libroTrabajoModelo.setHojaActiva(pestañasHojas.getSelectedIndex());
    }

    private void actualizarBarraFormulasConCeldaSeleccionada() {
        PanelHoja panelHojaActivo = (PanelHoja) pestañasHojas.getSelectedComponent();
        if (panelHojaActivo != null) {
            JTable tablaActiva = panelHojaActivo.getTabla();
            int fila = tablaActiva.getSelectedRow();
            int colVisual = tablaActiva.getSelectedColumn(); // Columna visual en JTable

            if (fila != -1 && colVisual != -1 && colVisual > 0) { // colVisual > 0 para ignorar la columna de números de fila
                int colModelo = colVisual - 1; // Convertir a índice de columna del modelo
                HojaCalculo hojaModeloActiva = libroTrabajoModelo.getHojaActiva();
                if (hojaModeloActiva != null) {
                    barraFormulas.setText(hojaModeloActiva.getContenidoIngresadoCelda(fila, colModelo));
                }
            } else {
                 // barraFormulas.setText(""); // Opcional: limpiar si no hay celda válida
            }
        }
    }


    // --- Métodos públicos para que el Controlador interactúe con la Vista ---
    public JTabbedPane getPestanasHojas() {
        return pestañasHojas;
    }

    public JTextField getBarraFormulas() {
        return barraFormulas;
    }

    public void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void refrescarHojaActual() {
        PanelHoja panelActual = (PanelHoja) pestañasHojas.getSelectedComponent();
        if (panelActual != null) {
            // Esto es un poco general, idealmente el TableModel tendría métodos más específicos
            // o el controlador llamaría a fireTableCellUpdated, fireTableRowsUpdated, etc.
            ((MiTableModel) panelActual.getTabla().getModel()).fireTableDataChanged();
        }
    }
    
    public void seleccionarPestana(int index){
        if(index >= 0 && index < pestañasHojas.getTabCount()){
            pestañasHojas.setSelectedIndex(index);
        }
    }
}
