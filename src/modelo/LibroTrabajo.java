/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author jzepeda
 */
public class LibroTrabajo {
    private LinkedList<HojaCalculo> hojas;
    private int hojaActivaIndex;
    
    public LibroTrabajo() {
        this.hojas = new LinkedList<>();
        // Por ahora, el controlador se encargaría de añadir la primera hoja
    }

    public HojaCalculo crearNuevaHoja(String nombreSugerido) {
        String nombreFinal = nombreSugerido;
        int i = 1;
        while (getHojaPorNombre(nombreFinal) != null) {
            nombreFinal = nombreSugerido + i++;
        }
        HojaCalculo nuevaHoja = new HojaCalculo(nombreFinal);
        hojas.add(nuevaHoja);
        if (hojas.size() == 1) { // Primera hoja es la activa
            hojaActivaIndex = 0;
        }
        return nuevaHoja;
    }
    
    public HojaCalculo getHojaPorNombre(String nombre) {
        for (HojaCalculo hoja : hojas) {
            if (hoja.getNombre().equals(nombre)) return hoja;
        }
        return null;
    }
    public HojaCalculo getHojaActiva() {
        if (hojaActivaIndex >= 0 && hojaActivaIndex < hojas.size()) {
            return hojas.get(hojaActivaIndex);
        }
        return null;
    }
    
    public void setHojaActiva(int index) {
        if (index >= 0 && index < hojas.size()) {
            this.hojaActivaIndex = index;
        }
    }
    
    public List<HojaCalculo> getTodasLasHojas() {
        return hojas; 
    }
    public int getHojaActivaIndex() { 
        return hojaActivaIndex; 
    }
    
    public int getNumeroDeHojas() { 
        return hojas.size(); 
    }
    
}
