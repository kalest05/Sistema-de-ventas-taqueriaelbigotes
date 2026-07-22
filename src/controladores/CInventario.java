package controladores;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import conexion.ConexionBDSQLServer;

/**
 * CONTROLADOR INVENTARIO
 * Conectado a:
 * - Insumo
 * - Inventario
 */
public class CInventario {

    public static Connection Conexion = null;
    public static String sql;
    public static PreparedStatement sentencia;
    public static ResultSet rs;

    /* =====================================================
     * CARGAR INVENTARIO (JOIN CON INSUMO)
     * ===================================================== */
    public static List<Object[]> cargarInventario() {
        List<Object[]> lista = new ArrayList<>();

        try {
            Conexion = ConexionBDSQLServer.GetConexion();

            sql = """
                  SELECT i.idInventario,
                         ins.nombre AS nombreInsumo,
                         ins.unidadMedida,
                         i.cantidadDisponible,
                         i.fechaActualizacion
                  FROM Inventario i
                  INNER JOIN Insumo ins ON i.idInsumo = ins.idInsumo
                  ORDER BY ins.nombre
                  """;

            sentencia = Conexion.prepareStatement(sql);
            rs = sentencia.executeQuery();

            while (rs.next()) {
                Object[] item = new Object[5];
                item[0] = rs.getInt("idInventario");
                item[1] = rs.getString("nombreInsumo");
                item[2] = rs.getString("unidadMedida");
                item[3] = rs.getDouble("cantidadDisponible");
                item[4] = rs.getTimestamp("fechaActualizacion");
                lista.add(item);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                    "Error al cargar inventario\n" + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }

        return lista;
    }


    /* =====================================================
     * AGREGAR PRODUCTO COMPLETO (INSUMO + INVENTARIO)
     * ===================================================== */
    public static void agregarProductoCompleto(String nombre, String unidadMedida, double cantidad) {
        try {
            Conexion = ConexionBDSQLServer.GetConexion();
            Conexion.setAutoCommit(false); // Iniciar transacción

            // Insertar en Insumo
            sql = "INSERT INTO Insumo (nombre, unidadMedida) VALUES (?, ?)";
            sentencia = Conexion.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS);
            sentencia.setString(1, nombre);
            sentencia.setString(2, unidadMedida);
            sentencia.executeUpdate();
            
            rs = sentencia.getGeneratedKeys();
            int idInsumo = 0;
            if (rs.next()) {
                idInsumo = rs.getInt(1);
            }

            // Insertar en Inventario
            sql = "INSERT INTO Inventario (idInsumo, cantidadDisponible) VALUES (?, ?)";
            sentencia = Conexion.prepareStatement(sql);
            sentencia.setInt(1, idInsumo);
            sentencia.setDouble(2, cantidad);
            sentencia.executeUpdate();

            Conexion.commit(); // Confirmar transacción
            JOptionPane.showMessageDialog(null, "Producto agregado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLException e) {
            try {
                Conexion.rollback(); // Revertir si hay error
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            JOptionPane.showMessageDialog(null, "Error al agregar producto\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                Conexion.setAutoCommit(true); // Restaurar auto-commit
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    /* =====================================================
     * ACTUALIZAR CANTIDAD
     * ===================================================== */
    public static void actualizarCantidad(int idInventario, double nuevaCantidad) {

        try {

            Conexion = ConexionBDSQLServer.GetConexion();

            sql = """
                  UPDATE Inventario
                  SET cantidadDisponible = ?,
                      fechaActualizacion = GETDATE()
                  WHERE idInventario = ?
                  """;

            sentencia = Conexion.prepareStatement(sql);
            sentencia.setDouble(1, nuevaCantidad);
            sentencia.setInt(2, idInventario);

            sentencia.executeUpdate();

            JOptionPane.showMessageDialog(null,
                    "Cantidad actualizada correctamente.",
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(null,
                    "Error al actualizar\n" + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /* =====================================================
     * ELIMINAR REGISTRO INVENTARIO
     * ===================================================== */
    public static void eliminarInventario(int idInventario) {
        try {
            Conexion = ConexionBDSQLServer.GetConexion();
            Conexion.setAutoCommit(false); // Iniciar transacción

            // Obtener idInsumo antes de eliminar
            sql = "SELECT idInsumo FROM Inventario WHERE idInventario = ?";
            sentencia = Conexion.prepareStatement(sql);
            sentencia.setInt(1, idInventario);
            rs = sentencia.executeQuery();
            int idInsumo = 0;
            if (rs.next()) {
                idInsumo = rs.getInt("idInsumo");
            }

            // Eliminar de Inventario
            sql = "DELETE FROM Inventario WHERE idInventario = ?";
            sentencia = Conexion.prepareStatement(sql);
            sentencia.setInt(1, idInventario);
            sentencia.executeUpdate();

            // Eliminar de Insumo
            sql = "DELETE FROM Insumo WHERE idInsumo = ?";
            sentencia = Conexion.prepareStatement(sql);
            sentencia.setInt(1, idInsumo);
            sentencia.executeUpdate();

            Conexion.commit(); // Confirmar transacción
            JOptionPane.showMessageDialog(null, "Producto eliminado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLException e) {
            try {
                Conexion.rollback(); // Revertir si hay error
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            JOptionPane.showMessageDialog(null, "Error al eliminar producto\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                Conexion.setAutoCommit(true); // Restaurar auto-commit
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

	/* =====================================================
 * CARGAR INSUMOS PARA SELECCIÓN
 * ===================================================== */
public static List<Object[]> cargarInsumos() {
    List<Object[]> lista = new ArrayList<>();
    try {
        Conexion = ConexionBDSQLServer.GetConexion();
        sql = "SELECT idInsumo, nombre, unidadMedida FROM Insumo ORDER BY nombre";
        sentencia = Conexion.prepareStatement(sql);
        rs = sentencia.executeQuery();
        while (rs.next()) {
            lista.add(new Object[]{rs.getInt("idInsumo"), rs.getString("nombre"), rs.getString("unidadMedida")});
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Error al cargar insumos\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
    return lista;
}


}
