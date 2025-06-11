package main;

import javax.swing.SwingUtilities;
import modelo.HojaCalculo;
import view.VentanaPrincipal;

/**
 *
 * @author jzepeda
 */
public class main {
    public static void main(String[] args) {
        final HojaCalculo hojaDeCalculo = new HojaCalculo(30, 16);

        hojaDeCalculo.setContenidoCelda(0, 0, "10");
        hojaDeCalculo.setContenidoCelda(0, 1, "20");
       
        // Ejemplo de como podria ingresar una formula desde el codigo         
        // hojaDeCalculo.setContenidoCelda(1, 3, "=SUMA((0,0),(0,1))");

        SwingUtilities.invokeLater(() -> {
            
            VentanaPrincipal ventana = new VentanaPrincipal(hojaDeCalculo);           
            ventana.actualizarTablaConModeloCompleto();
            ventana.setVisible(true);
        });
    }
}
