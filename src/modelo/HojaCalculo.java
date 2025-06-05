package modelo;

/**
 *
 * @author jzepeda
 */
public class HojaCalculo {
    private String nombre;
    private Celda[][] celdas; // Ej: Celda[100][26] para 100 filas y 26 columnas (A-Z)
    private static final int DEFAULT_FILAS = 100;
    private static final int DEFAULT_COLS = 26;
    
    public HojaCalculo(String nombre) {
        this.nombre = nombre;
        this.celdas = new Celda[DEFAULT_FILAS][DEFAULT_COLS];

        for (int i = 0; i < DEFAULT_FILAS; i++) {
            for (int j = 0; j < DEFAULT_COLS; j++) {
                celdas[i][j] = new Celda(""); // Inicializar con celdas vacías
            }
        }        
    }
    
    public int getRowCount() { return DEFAULT_FILAS; }
    public int getColumnCount() { return DEFAULT_COLS; }

    public String getNombre() {
        return nombre;
    }

    public Celda getCelda(int fila, int columna) {
        if (fila >= 0 && fila < celdas.length && columna >= 0 && columna < celdas[0].length) {
            return celdas[fila][columna];
        }
        return null; // o lanzar excepción
    }
    
    public void setCelda(int fila, int columna, String contenido) {
        if (fila >= 0 && fila < celdas.length && columna >= 0 && columna < celdas[0].length) {
            if (celdas[fila][columna] == null) {
                celdas[fila][columna] = new Celda(contenido);
            } else {
                celdas[fila][columna].setContenidoIngresado(contenido);
            }
            // Aquí dispararías la reevaluación de esta celda y dependientes
            evaluarCelda(fila, columna);
        }
    }

    // Método para evaluar una celda, especialmente si es una fórmula
    public void evaluarCelda(int fila, int columna) {
        Celda celda = getCelda(fila, columna);
        if (celda != null && celda.esFormula()) {
            // Lógica de parseo y evaluación de la fórmula
            // Ejemplo simple: =SUM(A1:A3) o =B2*C2
            String formula = celda.getFormula();
            try {
                Object resultado = resolverFormula(formula);
                celda.setValorCalculado(resultado);
            } catch (Exception e) {
                celda.setValorCalculado("¡ERROR!"); // O un tipo de error específico
            }
        }
        // Notificar a los observadores (celdas dependientes) que deben recalcularse
        // Este es un punto crucial para la reactividad de la hoja.
    }

    // Método simplificado para resolver fórmulas (necesitará mucho más trabajo)
    private Object resolverFormula(String formula) {
        // Este es el núcleo del motor de cálculo.
        // Deberás parsear la fórmula, identificar operandos (referencias a celdas, números)
        // y operadores (+, *).
        // Ejemplo muy básico:
        if (formula.matches("[A-Z]+\\d+\\s*\\+\\s*[A-Z]+\\d+")) { // "A1+B1"
            String[] parts = formula.split("\\+");
            Celda c1 = obtenerCeldaPorReferencia(parts[0].trim());
            Celda c2 = obtenerCeldaPorReferencia(parts[1].trim());
            if (c1 != null && c2 != null && c1.getValorCalculado() instanceof Number && c2.getValorCalculado() instanceof Number) {
                return ((Number)c1.getValorCalculado()).doubleValue() + ((Number)c2.getValorCalculado()).doubleValue();
            }
        } else if (formula.matches("[A-Z]+\\d+\\s*\\*\\s*[A-Z]+\\d+")) { // "A1*B1"
             String[] parts = formula.split("\\*");
            Celda c1 = obtenerCeldaPorReferencia(parts[0].trim());
            Celda c2 = obtenerCeldaPorReferencia(parts[1].trim());
             if (c1 != null && c2 != null && c1.getValorCalculado() instanceof Number && c2.getValorCalculado() instanceof Number) {
                return ((Number)c1.getValorCalculado()).doubleValue() * ((Number)c2.getValorCalculado()).doubleValue();
            }
        }
        // Implementar SUMA de rangos, multiplicación, etc.
        // Considerar usar una librería de parsing de expresiones si se vuelve complejo,
        // pero la restricción es no usar librerías que "creen la hoja de cálculo".
        // Un parser simple customizado es viable.
        System.out.println("Fórmula no soportada (aún): " + formula);
        return "FÓRMULA?";
    }

    // Utilidad para convertir "A1" a coordenadas [fila, col]
    public Celda obtenerCeldaPorReferencia(String ref) { // "A1" -> celdas[0][0]
        if (ref == null || ref.isEmpty() || !Character.isLetter(ref.charAt(0))) return null;
        int col = ref.charAt(0) - 'A'; // 'A' -> 0, 'B' -> 1
        try {
            int row = Integer.parseInt(ref.substring(1)) - 1; // "1" -> 0
            return getCelda(row, col);
        } catch (NumberFormatException | IndexOutOfBoundsException e) {
            return null;
        }
    }
    
    // Simulación de obtención de valor para la tabla
    public Object getValorMostradoCelda(int fila, int columna) {
        Celda celda = getCelda(fila, columna);
        return (celda != null) ? celda.getValorCalculado() : "";
    }
    
    public void setValorCelda(int fila, int columna, String contenido) {
        if (fila >= 0 && fila < DEFAULT_FILAS && columna >= 0 && columna < DEFAULT_COLS) {
            celdas[fila][columna].setContenidoIngresado(contenido);
            // Aquí el Modelo real notificaría a los observadores/controlador para recalcular.
        }
    }
    
    public String getContenidoIngresadoCelda(int fila, int columna) {
        Celda celda = getCelda(fila, columna);
        return (celda != null) ? celda.getContenidoIngresado() : "";
    }
}
