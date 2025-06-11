
package modelo;

/**
 *
 * @author jzepeda
 */
public class Celda {
    private String valorMostrado;
    private String formula; // Ejemplo =SUMA((0,0),(0,1))
    private Double valorCalculado;

    private HojaCalculo hojaCalculo;

    public Celda(HojaCalculo hojaCalculo) {
        this.hojaCalculo = hojaCalculo;
        this.valorMostrado = "";
        this.formula = null;
        this.valorCalculado = null;
    }

    /**
     * Establece el contenido de la celda.
     * Puede ser un valor numérico directo o una fórmula.
     * @param valor El valor o fórmula a ingresar.
     */
    public void setContenido(String valor) {
        if (valor == null || valor.trim().isEmpty()) {
            this.formula = null;
            this.valorCalculado = null;
            this.valorMostrado = "";
            return;
        }

        valor = valor.trim();

        if (ParserFormulas.esFormula(valor)) {
            this.formula = valor;
            evaluarFormula();
        } else {
            this.formula = null;
            try {
                this.valorCalculado = Double.parseDouble(valor);
                this.valorMostrado = valor;
            } catch (NumberFormatException e) {
                this.valorCalculado = null; // No hay valor numérico calculable
                this.valorMostrado = valor; // Mostrar el texto tal cual                
            }
        }
    }

    /**
     * Evalúa la fórmula almacenada en esta celda.
     * Actualiza valorCalculado y valorMostrado.
     */
    public void evaluarFormula() {
        if (this.formula != null && this.hojaCalculo != null) {
            try {

                this.valorCalculado = ParserFormulas.parsearYCalcular(this.formula, this.hojaCalculo);
                this.valorMostrado = (this.valorCalculado != null) ? String.valueOf(this.valorCalculado) : "#ERROR_FORMULA";
            } catch (Exception e) {
                System.err.println("Error al evaluar la fórmula '" + this.formula + "': " + e.getMessage());
                this.valorCalculado = null;
                this.valorMostrado = "#ERROR_FORMULA";
            }
        } else if (this.formula == null && this.valorCalculado != null) {
            
        } else if (this.formula == null && this.valorCalculado == null && (this.valorMostrado != null && !this.valorMostrado.isEmpty())) {            
        }
    }

    public void setHojaPadre(HojaCalculo hoja) {
        this.hojaCalculo = hoja;
    }
    
    public String getValorMostrado() {
        return valorMostrado;
    }

    public String getFormula() {
        return formula;
    }

    public Double getValorCalculado() {
        return valorCalculado;
    }

    @Override
    public String toString() {
        return "Celda{" +
               "valorMostrado='" + valorMostrado + '\'' +
               ", formula='" + formula + '\'' +
               ", valorCalculado=" + valorCalculado +
               '}';
    }
    
}
