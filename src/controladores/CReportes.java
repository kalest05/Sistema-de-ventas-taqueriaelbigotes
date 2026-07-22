package controladores;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import conexion.ConexionBDSQLServer;

/**
 * Controlador para obtener datos de reportes y ventas
 */
public class CReportes {

    /**
     * Obtiene las ventas, cantidad de pedidos y ticket promedio para un corte específico
     */
    public static double[] obtenerDatosCorte(LocalTime horaInicio, LocalTime horaFin) {
        double[] datos = {0.0, 0, 0.0};
        
        LocalDate fechaHoy = LocalDate.now();
        LocalDateTime fechaTimeInicio = LocalDateTime.of(fechaHoy, horaInicio);
        LocalDateTime fechaTimeFin = LocalDateTime.of(fechaHoy, horaFin);
        
        // Si el corte termina al día siguiente
        if (horaFin.isBefore(horaInicio) || horaFin.equals(LocalTime.MIDNIGHT)) {
            fechaTimeFin = fechaTimeFin.plusDays(1);
        }

        String sql = "SELECT COUNT(*) AS total_pedidos, COALESCE(SUM(total), 0) AS ventas " +
                     "FROM Pedido " +
                     "WHERE fecha BETWEEN ? AND ?";

        try (Connection con = ConexionBDSQLServer.GetConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            ps.setString(1, fechaTimeInicio.format(formatter));
            ps.setString(2, fechaTimeFin.format(formatter));

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                double ventas = rs.getDouble("ventas");
                int pedidos = rs.getInt("total_pedidos");
                double promedio = (pedidos > 0) ? ventas / pedidos : 0;

                datos[0] = ventas;
                datos[1] = pedidos;
                datos[2] = promedio;
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener datos del corte: " + e.getMessage());
            e.printStackTrace();
        }

        return datos;
    }

    /**
     * Obtiene el total de ventas del día actual
     */
    public static double obtenerVentasDia() {
        double total = 0;
        String fechaHoy = LocalDate.now().toString();
        
        String sql = "SELECT COALESCE(SUM(total), 0) AS total " +
                     "FROM Pedido " +
                     "WHERE CAST(fecha AS DATE) = ?";

        try (Connection con = ConexionBDSQLServer.GetConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, fechaHoy);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                total = rs.getDouble("total");
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener ventas del día: " + e.getMessage());
            e.printStackTrace();
        }

        return total;
    }
}