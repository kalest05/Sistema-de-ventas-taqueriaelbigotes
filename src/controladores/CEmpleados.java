package controladores;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import conexion.ConexionBDSQLServer;

/**
 * Controlador de Empleados.
 * BD: tablas Empleado y Usuario.
 * No elimina registros, solo marca activo = 0.
 */
public class CEmpleados {

    /*
     * Obtiene TODOS los empleados: activos primero, inactivos al final.
     * Retorna: [idEmpleado, nombre, puesto, usuario, rol, activo(boolean)]
     * Usado por PanelEmpleados para mostrar la lista completa.
     */
    public static List<Object[]> obtenerTodosEmpleados() {
        List<Object[]> lista = new ArrayList<>();

        String sql =
            "SELECT e.idEmpleado, e.nombre, e.puesto, u.usuario, u.rol, e.activo " +
            "FROM Empleado e " +
            "INNER JOIN Usuario u ON e.idEmpleado = u.id_empleado " +
            "ORDER BY e.activo DESC, e.nombre";

        try (Connection con = ConexionBDSQLServer.GetConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(new Object[]{
                    rs.getInt("idEmpleado"),
                    rs.getString("nombre"),
                    rs.getString("puesto"),
                    rs.getString("usuario"),
                    rs.getString("rol"),
                    rs.getBoolean("activo")
                });
            }

        } catch (Exception e) {
            System.err.println("Error al obtener empleados: " + e.getMessage());
        }

        return lista;
    }

    /*
     * Actualiza nombre, puesto, usuario y rol.
     * Si password es null o vacío, no se modifica la contraseña.
     */
    public static void actualizarEmpleado(int idEmpleado, String nombre, String puesto,
                                           String usuario, String password, String rol) {
        String sqlEmp = "UPDATE Empleado SET nombre = ?, puesto = ? WHERE idEmpleado = ?";

        try (Connection con = ConexionBDSQLServer.GetConexion();
             PreparedStatement ps = con.prepareStatement(sqlEmp)) {
            ps.setString(1, nombre);
            ps.setString(2, puesto);
            ps.setInt(3, idEmpleado);
            ps.executeUpdate();
        } catch (Exception e) {
            System.err.println("Error al actualizar empleado: " + e.getMessage());
        }

        boolean cambiarPass = password != null && !password.isEmpty();
        String sqlUsr = cambiarPass
            ? "UPDATE Usuario SET usuario = ?, password = ?, rol = ? WHERE id_empleado = ?"
            : "UPDATE Usuario SET usuario = ?, rol = ? WHERE id_empleado = ?";

        try (Connection con = ConexionBDSQLServer.GetConexion();
             PreparedStatement ps = con.prepareStatement(sqlUsr)) {
            if (cambiarPass) {
                ps.setString(1, usuario);
                ps.setString(2, password);
                ps.setString(3, rol);
                ps.setInt(4, idEmpleado);
            } else {
                ps.setString(1, usuario);
                ps.setString(2, rol);
                ps.setInt(3, idEmpleado);
            }
            ps.executeUpdate();
        } catch (Exception e) {
            System.err.println("Error al actualizar usuario: " + e.getMessage());
        }
    }

    /*
     * Marca empleado como inactivo (activo = 0).
     * CLogin.validarLogin ya verifica e.activo = 1 en el JOIN,
     * así que el empleado inactivo no podrá iniciar sesión.
     */
    public static void inactivarEmpleado(int idEmpleado) {
        String sql = "UPDATE Empleado SET activo = 0 WHERE idEmpleado = ?";

        try (Connection con = ConexionBDSQLServer.GetConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idEmpleado);
            ps.executeUpdate();
        } catch (Exception e) {
            System.err.println("Error al inactivar empleado: " + e.getMessage());
        }
    }

    /*
     * Reactiva un empleado marcado como inactivo.
     */
    public static void activarEmpleado(int idEmpleado) {
        String sql = "UPDATE Empleado SET activo = 1 WHERE idEmpleado = ?";

        try (Connection con = ConexionBDSQLServer.GetConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idEmpleado);
            ps.executeUpdate();
        } catch (Exception e) {
            System.err.println("Error al activar empleado: " + e.getMessage());
        }
    }

    /*
     * Agrega un nuevo empleado y crea su usuario en una misma operación.
     */
    public static void agregarEmpleado(String nombre, String puesto,
                                        String usuario, String password, String rol) {
        int idGenerado = -1;

        String sqlEmp = "INSERT INTO Empleado (nombre, puesto, activo) VALUES (?, ?, 1)";

        try (Connection con = ConexionBDSQLServer.GetConexion();
             PreparedStatement ps = con.prepareStatement(sqlEmp, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, nombre);
            ps.setString(2, puesto);
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) idGenerado = keys.getInt(1);
            }
        } catch (Exception e) {
            System.err.println("Error al insertar empleado: " + e.getMessage());
            return;
        }

        if (idGenerado < 0) return;

        String sqlUsr =
            "INSERT INTO Usuario (usuario, password, rol, id_empleado) VALUES (?, ?, ?, ?)";

        try (Connection con = ConexionBDSQLServer.GetConexion();
             PreparedStatement ps = con.prepareStatement(sqlUsr)) {
            ps.setString(1, usuario);
            ps.setString(2, password);
            ps.setString(3, rol);
            ps.setInt(4, idGenerado);
            ps.executeUpdate();
        } catch (Exception e) {
            System.err.println("Error al insertar usuario: " + e.getMessage());
        }
    }
}
