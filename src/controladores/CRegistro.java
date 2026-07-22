package controladores;

import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import conexion.ConexionBDSQLServer;

public class CRegistro {

    public static boolean registrarUsuario(
            String nombre,
            String puesto,
            boolean activo,
            String usuario,
            String passwordPlano,
            String rol) {

        boolean exito = false;

        String sqlEmpleado = "INSERT INTO Empleado (nombre, puesto, activo) VALUES (?, ?, ?)";
        String sqlUsuario = "INSERT INTO Usuario (usuario, password, rol, id_empleado) VALUES (?, ?, ?, ?)";

        try (Connection con = ConexionBDSQLServer.GetConexion()) {

            con.setAutoCommit(false);

            int idEmpleadoGenerado = 0;

            /* ===== INSERT EMPLEADO ===== */
            try (PreparedStatement psEmp = con.prepareStatement(sqlEmpleado, Statement.RETURN_GENERATED_KEYS)) {

                psEmp.setString(1, nombre);
                psEmp.setString(2, puesto);
                psEmp.setBoolean(3, activo);

                psEmp.executeUpdate();

                try (ResultSet rs = psEmp.getGeneratedKeys()) {
                    if (rs.next()) {
                        idEmpleadoGenerado = rs.getInt(1);
                    }
                }
            }

            /* ===== INSERT USUARIO ===== */
            try (PreparedStatement psUser = con.prepareStatement(sqlUsuario)) {

                psUser.setString(1, usuario);
                psUser.setString(2, encriptarSHA256(passwordPlano));
                psUser.setString(3, rol);
                psUser.setInt(4, idEmpleadoGenerado);

                psUser.executeUpdate();
            }

            con.commit();
            exito = true;

        } catch (Exception e) {
            System.err.println("Error al registrar usuario: " + e.getMessage());
        }

        return exito;
    }

    /* ===== SHA-256 ===== */
    private static String encriptarSHA256(String texto) {

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(texto.getBytes("UTF-8"));

            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }

            return sb.toString();

        } catch (Exception e) {
            throw new RuntimeException("Error al encriptar contraseña");
        }
    }
}
