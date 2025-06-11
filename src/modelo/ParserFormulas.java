package modelo;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author jzepeda
 */
public class ParserFormulas {
    
    // Pattern unicamente para funciones como SUMA((0,0),(1,1)) o MULTIPLICACION((0,0),(0,1))
    
    private static final Pattern PATRON_1 = Pattern.compile("\\((\\d+)\\s*,\\s*(\\d+)\\)");
    
    // Pattern solo para para permitir =5+5 o =9*9 en ese formato
    
    private static final Pattern PATRON_2 = Pattern.compile("^(\\d+(?:\\.\\d+)?)\\s*([+*])\\s*(\\d+(?:\\.\\d+)?)$");

    /**
     * Verifica si lo ingresado es una formula
     * @param contenido valor a validar.
     * @return true si comienza con "=", false si no.
     */
    public static boolean esFormula(String contenido) {
        return contenido != null && contenido.startsWith("=");
    }

    /**
     * Convirte la formula.
     * @param formula la formula debe tener este formato (ej: "=SUMA((0,0),(0,1))" o "=5+5").
     * @param hoja La hoja de cálculo para obtener valores de celdas referenciadas.
     * @return El resultado del cálculo como Double.
     */
    public static Double parsearYCalcular(String formula, HojaCalculo hoja) throws IllegalArgumentException {
        if (!esFormula(formula)) {
            throw new IllegalArgumentException("Entrada no es una fórmula válida: " + formula);
        }

        String contenidoFormula = formula.substring(1).trim();

        Matcher matcherAritmetica = PATRON_2.matcher(contenidoFormula);
        if (matcherAritmetica.matches()) {
            try {
                double num1 = Double.parseDouble(matcherAritmetica.group(1));
                String operador = matcherAritmetica.group(2);
                double num2 = Double.parseDouble(matcherAritmetica.group(3));

                switch (operador) {
                    case "+":
                        return num1 + num2;
                    case "*":
                        return num1 * num2;
                    
                    default:
                        throw new IllegalArgumentException("Operador aritmético desconocido: " + operador);
                }
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Error al parsear números en la fórmula aritmética: " + contenidoFormula, e);
            }
        }

        
        String contenidoFuncion = contenidoFormula.toUpperCase();

        String nombreFuncion = null;
        List<Celda> operandosCelda = new ArrayList<>();

        if (contenidoFuncion.startsWith("SUMA(")) {
            nombreFuncion = "SUMA";
            if (!contenidoFuncion.endsWith(")")) {
                 throw new IllegalArgumentException("Fórmula de función malformada, falta ')' al final: " + formula);
            }
            operandosCelda = extraerOperandos(contenidoFuncion.substring("SUMA(".length(), contenidoFuncion.length() - 1), hoja);
        } else if (contenidoFuncion.startsWith("MULTIPLICACION(")) {
            nombreFuncion = "MULTIPLICACION";
             if (!contenidoFuncion.endsWith(")")) {
                 throw new IllegalArgumentException("Fórmula de función malformada, falta ')' al final: " + formula);
            }
            operandosCelda = extraerOperandos(contenidoFuncion.substring("MULTIPLICACION(".length(), contenidoFuncion.length() - 1), hoja);
        } else {
            throw new IllegalArgumentException("Fórmula desconocida o formato incorrecto: " + formula);
        }

        if (operandosCelda.isEmpty() && (nombreFuncion.equals("SUMA") || nombreFuncion.equals("MULTIPLICACION"))) {
            
             System.err.println("Advertencia: Función " + nombreFuncion + " llamada sin operandos válidos en: " + formula);
            
        }

        // Realizar la operación
        Double resultado = null;
        if ("SUMA".equals(nombreFuncion)) {
            resultado = 0.0;
            for (Celda celda : operandosCelda) {
                if (celda.getValorCalculado() == null) {
                    System.err.println("Advertencia: Celda referenciada ("+ celda.getFormula() +") sin valor numérico para SUMA. Tratada como 0.");                    
                } else {
                    resultado = resultado + celda.getValorCalculado();
                }
            }
        } else if ("MULTIPLICACION".equals(nombreFuncion)) {
            if (operandosCelda.isEmpty()) { 
                throw new IllegalArgumentException("MULTIPLICACION requiere al menos un operando.");
            }
            resultado = 1.0;
            for (Celda celda : operandosCelda) {
                if (celda.getValorCalculado() == null) {
                    System.err.println("Advertencia: Celda referenciada sin valor numérico para MULTIPLICACION. Resultado podría ser afectado.");
                    
                    return null; 
                }
                resultado = resultado * celda.getValorCalculado();
            }
        }
        return resultado;
    }

    private static List<Celda> extraerOperandos(String argumentosString, HojaCalculo hoja) throws IllegalArgumentException {
        List<Celda> celdas = new ArrayList<>();
        Matcher matcher = PATRON_1.matcher(argumentosString.trim());

        while (matcher.find()) {
            try {
                int fila = Integer.parseInt(matcher.group(1));
                int columna = Integer.parseInt(matcher.group(2));

                Celda celdaReferenciada = hoja.getCelda(fila, columna);
                if (celdaReferenciada == null) {
                    throw new IllegalArgumentException("Referencia a celda inválida o fuera de rango: (" + fila + "," + columna + ")");
                }
                celdas.add(celdaReferenciada);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Formato de coordenadas inválido en: " + matcher.group(0));
            }
        }
        if (celdas.isEmpty() && !argumentosString.trim().isEmpty()) {
            throw new IllegalArgumentException("Argumentos de fórmula no contienen coordenadas válidas: " + argumentosString);
        }
        return celdas;
        
    }

    public static void main(String[] args) {
        HojaCalculo hojaPrueba = new HojaCalculo(5, 5);
        hojaPrueba.setContenidoCelda(0, 0, "10"); // A1 = 10
        hojaPrueba.setContenidoCelda(0, 1, "20"); // B1 = 20
        hojaPrueba.setContenidoCelda(1, 0, "5");  // A2 = 5

        String formulaSumaRefs = "=SUMA((0,0),(0,1),(1,0))"; // 10 + 20 + 5 = 35
        String formulaMultiRefs = "=MULTIPLICACION((0,0),(1,0))"; // 10 * 5 = 50
        String formulaSumaSimple = "=5+5"; // 10
        String formulaMultiSimple = "=5*2"; // 10        
        String formulaCeldaTexto = "=SUMA((0,0),(0,1))"; 
        
    }
}
