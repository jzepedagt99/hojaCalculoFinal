
package modelo;

/**
 *
 * @author jzepeda
 */
public class Celda {
    private String contenidoIngresado; // Ej: "10", "Hola", "=A1+B1"
    private Object valorCalculado;    // Ej: 10, "Hola", resultado de A1+B1
    private String formula;           // Ej: "A1+B1" (si es una fórmula)
    // Podría tener referencias a celdas que dependen de esta (observadores)
    // y celdas de las que esta depende.
    
     public Celda(String contenido) {
        setContenidoIngresado(contenido);
    }

    public void setContenidoIngresado(String contenido) {
        this.contenidoIngresado = contenido;
        // Lógica para determinar si es una fórmula y parsearla
        if (contenido != null && contenido.startsWith("=")) {
            this.formula = contenido.substring(1);
            // Dejar el cálculo para después o marcar como "necesita recalcular"
            this.valorCalculado = null; // O un placeholder
        } else {
            this.formula = null;
            // Intentar convertir a número si es posible, sino tratar como texto
            try {
                this.valorCalculado = Double.parseDouble(contenido);
            } catch (NumberFormatException e) {
                this.valorCalculado = contenido;
            }
        }
    }

    public String getContenidoIngresado() {
        return contenidoIngresado;
    }

    public Object getValorCalculado() {
        // Aquí podría ir la lógica de evaluación si no se hizo antes
        // o si necesita ser recalculado.
        return valorCalculado;
    }

    public void setValorCalculado(Object valor) {
        this.valorCalculado = valor;
    }

    public String getFormula() {
        return formula;
    }

    public boolean esFormula() {
        return formula != null;
    }
    
}
