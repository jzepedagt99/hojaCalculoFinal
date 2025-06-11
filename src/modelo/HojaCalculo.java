package modelo;

/**
 *
 * @author jzepeda
 */
public class HojaCalculo {
    private Celda[][] matrizCeldas;
    private final int filas;
    private final int columnas;

    public HojaCalculo(int filas, int columnas) {
        if (filas <= 0 || columnas <= 0) {
            throw new IllegalArgumentException("Las filas y columnas deben ser mayores que cero.");
        }
        this.filas = filas;
        this.columnas = columnas;
        this.matrizCeldas = new Celda[filas][columnas];
        inicializarCeldas();
    }

    private void inicializarCeldas() {
        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                matrizCeldas[i][j] = new Celda(this);
            }
        }
    }

    /**
     * Obtiene la celda
     * @param fila Índice de la fila.
     * @param columna Índice de la columna.
     * @return La Celda en esa posición, o null si los índices están fuera de rango.
     */
    public Celda getCelda(int fila, int columna) {
        if (fila >= 0 && fila < filas && columna >= 0 && columna < columnas) {
            return matrizCeldas[fila][columna];
        }
        System.err.println("Error: Intento de acceso a celda fuera de rango: (" + fila + "," + columna + ")");
        return null; // O lanzar una excepción
    }

    /**
     * Establece el contenido (valor o fórmula) de una celda específica.
     * @param fila Índice de la fila.
     * @param columna Índice de la columna.
     * @param contenido El string a ingresar en la celda.
     */
    public void setContenidoCelda(int fila, int columna, String contenido) {
        Celda celda = getCelda(fila, columna);
        if (celda != null) {
            celda.setContenido(contenido);
        }
    }

    /**
     * Recalcular todas las celdas que contienen fórmulas.
     */
    public void recalcularHojaCompleta() {
        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                if (matrizCeldas[i][j].getFormula() != null) {
                    matrizCeldas[i][j].evaluarFormula();
                }
            }
        }
    }

    public Celda[][] getMatrizCeldas() {
        return matrizCeldas;
    }
    
    public int getFilas() {
        return filas;
    }

    public int getColumnas() {
        return columnas;
    }

    // Método de prueba para imprimir la hoja en consola 
    public void imprimirHoja() {
        System.out.println("--- Hoja de Cálculo ---");
        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                System.out.print("[" + (matrizCeldas[i][j].getValorMostrado().isEmpty() ? " " : matrizCeldas[i][j].getValorMostrado()) + "]\t");
            }
            System.out.println();
        }
        System.out.println("-----------------------");
    }
}
