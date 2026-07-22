package controladores;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import conexion.ConexionBDSQLServer;

public class CLogin {
    /* =========================================================
     * VALIDAR LOGIN
     * ========================================================= */
    public static Object[] validarLogin(String usuario, String password) {

        String sql = "SELECT u.id_empleado, u.rol, e.nombre FROM Usuario u " +
                     "INNER JOIN Empleado e ON u.id_empleado = e.idEmpleado " +
                     "WHERE u.usuario = ? AND u.password = ? AND e.activo = 1";

        try (Connection con = ConexionBDSQLServer.GetConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, usuario);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Object[] resultado = new Object[3];
                    resultado[0] = rs.getInt("id_empleado");
                    resultado[1] = rs.getString("rol");
                    resultado[2] = rs.getString("nombre");
                    return resultado;
                }
            }

        } catch (Exception e) {
            System.err.println("Error al validar login: " + e.getMessage());
        }

        return null;
    }

	    /* =========================================================
     * INSERTAR EMPLEADO
     * ========================================================= */
    public static int insertarEmpleado(String nombre, String puesto) {
        String sql = "INSERT INTO Empleado (nombre, puesto) VALUES (?, ?)";
        
        try (Connection con = ConexionBDSQLServer.GetConexion();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setString(1, nombre);
            ps.setString(2, puesto);
            ps.executeUpdate();
            
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (Exception e) {
            System.err.println("Error al insertar empleado: " + e.getMessage());
        }
        
        return 0;
    }

    /* =========================================================
     * INSERTAR USUARIO
     * ========================================================= */
    public static boolean insertarUsuario(String usuario, String password, String rol, int idEmpleado) {
        String sql = "INSERT INTO Usuario (usuario, password, rol, id_empleado) VALUES (?, ?, ?, ?)";
        
        try (Connection con = ConexionBDSQLServer.GetConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, usuario);
            ps.setString(2, password);
            ps.setString(3, rol);
            ps.setInt(4, idEmpleado);
            
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("Error al insertar usuario: " + e.getMessage());
        }
        
        return false;
    }
}
