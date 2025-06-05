/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main;

import javax.swing.SwingUtilities;
import modelo.LibroTrabajo;
import view.VentanaPrincipal;

/**
 *
 * @author jzepeda
 */
public class main {
    public static void main(String[] args) {
        // 1. Crear el modelo (LibroTrabajo)
        LibroTrabajo libro = new LibroTrabajo();
        // Hoja hojaInicial = libro.crearNuevaHoja("Hoja1"); // El controlador lo haría

        // 2. Crear la vista, pasándole el modelo
        SwingUtilities.invokeLater(() -> {
            VentanaPrincipal ventana = new VentanaPrincipal(libro);
            ventana.setVisible(true);
        });
        // 3. (Faltante aquí) Crear el Controlador, pasándole el modelo y la vista.
        // ControladorHojaCalculo controlador = new ControladorHojaCalculo(libro, ventana);
        // El controlador se encargaría de añadir la primera hoja a la vista si es necesario
        // por ejemplo, llamando a ventana.añadirNuevaHoja("Hoja1");
    }
}
