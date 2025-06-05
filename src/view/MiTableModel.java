package view;

import javax.swing.table.AbstractTableModel;
import modelo.HojaCalculo;

/**
 *
 * @author jzepeda
 */
public class MiTableModel extends AbstractTableModel {
    private HojaCalculo hoja;

    public MiTableModel(HojaCalculo hoja) {
        this.hoja = hoja;
    }

    public void setHoja(HojaCalculo nuevaHoja) {
        this.hoja = nuevaHoja;
        fireTableStructureChanged(); // Notifica a la JTable que toda la estructura puede haber cambiado
                                     // (columnas, filas, etc.) Idealmente más granular.
        fireTableDataChanged();      // Notifica que los datos han cambiado.
    }

    @Override
    public int getRowCount() {
        return (hoja != null) ? hoja.getRowCount() : 0;
    }

    @Override
    public int getColumnCount() {
        // +1 para los números de fila
        return (hoja != null) ? hoja.getColumnCount() + 1 : 0;
    }

    @Override
    public String getColumnName(int columnIndex) {
        if (columnIndex == 0) {
            return ""; // Para la columna de números de fila
        }
        // Convertir índice de columna (0-25) a letra (A-Z)
        // columnIndex-1 porque la primera columna es para números de fila
        return Character.toString((char) ('A' + columnIndex - 1));
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (hoja == null) return null;

        if (columnIndex == 0) {
            return rowIndex + 1; // Números de fila
        }
        // columnIndex-1 para acceder al modelo de datos de la hoja
        return hoja.getValorMostradoCelda(rowIndex, columnIndex - 1);
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if (hoja == null || columnIndex == 0) return; // No editar números de fila

        // El controlador se encargaría de llamar a esto
        // hoja.setValorCelda(rowIndex, columnIndex - 1, (String) aValue);
        // fireTableCellUpdated(rowIndex, columnIndex);
        // Aquí, el controlador debería ser notificado para actualizar el modelo
        // y luego el modelo (o controlador) llamaría a fireTableCellUpdated.
        // Por ahora, solo imprimimos para simular.
        System.out.println("Vista: setValueAt llamado para [" + rowIndex + "," + (columnIndex-1) + "] = " + aValue);
        // El controlador debería tomar aValue, pasarlo a Hoja.setValorCelda,
        // Hoja recalcularía y actualizaría su Celda.valorCalculado,
        // luego se llamaría a fireTableCellUpdated.
        // Simulación directa para ver el cambio (esto rompe un poco MVC, es solo para demo)
        hoja.setValorCelda(rowIndex, columnIndex - 1, (String) aValue);
        fireTableCellUpdated(rowIndex, columnIndex);

    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex != 0; // No permitir editar la columna de números de fila
    }
}
