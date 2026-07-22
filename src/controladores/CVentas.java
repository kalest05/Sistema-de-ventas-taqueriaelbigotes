package controladores;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.swing.table.DefaultTableModel;

import conexion.ConexionBDSQLServer;
import modelos.MDetallePedido;
import modelos.MProductos;

/**
 * Controlador del módulo de Ventas
 * Base de datos: TaqueriaBD
 * 
 * @author Milagros Guadalupe Camacho Camacho
 * @version 3.0
 */
public class CVentas {

    /* =========================================================
     * CARGAR PRODUCTOS
     * ========================================================= */
    public static List<MProductos> cargarProductos() {

        List<MProductos> lista = new ArrayList<>();

        String sql = "SELECT idProducto, nombre, precio, descripcion, urlFoto,activo FROM Producto";

        try (Connection con = ConexionBDSQLServer.GetConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                MProductos p = new MProductos();
                p.setIdProducto(rs.getInt("idProducto"));
                p.setNombre(rs.getString("nombre"));
                p.setDescripcion(rs.getString("descripcion"));
                p.setPrecio(rs.getDouble("precio"));
                p.setUrlFoto(rs.getString("urlFoto"));
                p.setActivo((rs.getInt("activo")==1));

                lista.add(p);
            }

        } catch (Exception e) {
            System.err.println("Error al cargar productos: " + e.getMessage());
        }

        return lista;
    }
    
    /* =========================================================
     * REGISTRAR PEDIDO (CON SESIÓN)
     * ========================================================= */
    public static int registrarPedido(double total, int idEmpleado) {

        int idPedidoGenerado = 0;

        String sql = "INSERT INTO Pedido (total, idEmpleado, fecha) VALUES (?, ?, GETDATE())";

        try (Connection con = ConexionBDSQLServer.GetConexion();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setDouble(1, total);
            ps.setInt(2, idEmpleado);

            int filas = ps.executeUpdate();

            if (filas > 0) {

                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        idPedidoGenerado = rs.getInt(1);
                    }
                }
            }

        } catch (Exception e) {
            System.err.println("Error al registrar pedido: " + e.getMessage());
        }

        return idPedidoGenerado;
    }

    /* =========================================================
     * REGISTRAR DETALLE PEDIDO
     * ========================================================= */
    public static void registrarDetalle(MDetallePedido detalle) {

        String sql = "INSERT INTO DetallePedido "
                + "(idPedido, idProducto, cantidad, subtotal) "
                + "VALUES (?, ?, ?, ?)";

        try (Connection con = ConexionBDSQLServer.GetConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, detalle.getIdPedido());
            ps.setInt(2, detalle.getIdProducto());
            ps.setInt(3, detalle.getCantidad());
            ps.setDouble(4, detalle.getSubtotal());

            ps.executeUpdate();

        } catch (Exception e) {
            System.err.println("Error al registrar detalle: " + e.getMessage());
        }
    }

	    /* =========================================================
     * OBTENER ÓRDENES DEL DÍA
     * ========================================================= */
    public static List<Object[]> obtenerOrdenesDelDia() {

        List<Object[]> lista = new ArrayList<>();

        String sql = "SELECT idPedido, fecha, total, idEmpleado FROM Pedido WHERE CAST(fecha AS DATE) = CAST(GETDATE() AS DATE)";

        try (Connection con = ConexionBDSQLServer.GetConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                Object[] orden = new Object[4];
                orden[0] = rs.getInt("idPedido");
                orden[1] = rs.getTimestamp("fecha");
                orden[2] = rs.getDouble("total");
                orden[3] = rs.getInt("idEmpleado");

                lista.add(orden);
            }

        } catch (Exception e) {
            System.err.println("Error al obtener órdenes del día: " + e.getMessage());
        }

        return lista;
    }

	    /* =========================================================
     * OBTENER DETALLES DE UN PEDIDO
     * ========================================================= */
    public static List<Object[]> obtenerDetallesPedido(int idPedido) {
        List<Object[]> lista = new ArrayList<>();
        
        String sql = "SELECT dp.idProducto, p.nombre, dp.cantidad, dp.subtotal " +
                     "FROM DetallePedido dp " +
                     "INNER JOIN Producto p ON dp.idProducto = p.idProducto " +
                     "WHERE dp.idPedido = ?";
        
        try (Connection con = ConexionBDSQLServer.GetConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, idPedido);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Object[] detalle = new Object[4];
                    detalle[0] = rs.getInt("idProducto");
                    detalle[1] = rs.getString("nombre");
                    detalle[2] = rs.getInt("cantidad");
                    detalle[3] = rs.getDouble("subtotal");
                    lista.add(detalle);
                }
            }
        } catch (Exception e) {
            System.err.println("Error al obtener detalles del pedido: " + e.getMessage());
        }
        
        return lista;
    }

    /* =========================================================
     * CARGAR PRODUCTOS PARA SELECCIÓN
     * ========================================================= */
    public static List<Object[]> cargarProductosParaSeleccion() {
        List<Object[]> lista = new ArrayList<>();
        
        String sql = "SELECT idProducto, nombre, precio FROM Producto WHERE activo = 1";
        
        try (Connection con = ConexionBDSQLServer.GetConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                Object[] producto = new Object[3];
                producto[0] = rs.getInt("idProducto");
                producto[1] = rs.getString("nombre");
                producto[2] = rs.getDouble("precio");
                lista.add(producto);
            }
        } catch (Exception e) {
            System.err.println("Error al cargar productos para selección: " + e.getMessage());
        }
        
        return lista;
    }

    /* =========================================================
     * ACTUALIZAR DETALLES DEL PEDIDO
     * ========================================================= */
    public static void actualizarDetallesPedido(int idPedido, DefaultTableModel modelo, double nuevoTotal) {
        // Eliminar detalles existentes
        String sqlDelete = "DELETE FROM DetallePedido WHERE idPedido = ?";
        try (Connection con = ConexionBDSQLServer.GetConexion();
             PreparedStatement ps = con.prepareStatement(sqlDelete)) {
            ps.setInt(1, idPedido);
            ps.executeUpdate();
        } catch (Exception e) {
            System.err.println("Error al eliminar detalles antiguos: " + e.getMessage());
        }
        
        // Insertar nuevos detalles
        String sqlInsert = "INSERT INTO DetallePedido (idPedido, idProducto, cantidad, subtotal) VALUES (?, ?, ?, ?)";
        try (Connection con = ConexionBDSQLServer.GetConexion();
             PreparedStatement ps = con.prepareStatement(sqlInsert)) {
            
            for (int i = 0; i < modelo.getRowCount(); i++) {
                ps.setInt(1, idPedido);
                ps.setInt(2, (int) modelo.getValueAt(i, 0));
                ps.setInt(3, (int) modelo.getValueAt(i, 2));
                ps.setDouble(4, (double) modelo.getValueAt(i, 3));
                ps.executeUpdate();
            }
        } catch (Exception e) {
            System.err.println("Error al insertar nuevos detalles: " + e.getMessage());
        }
        
        // Actualizar total del pedido
        actualizarTotalPedido(idPedido, nuevoTotal);
    }

    /* =========================================================
     * ACTUALIZAR TOTAL DEL PEDIDO
     * ========================================================= */
    public static void actualizarTotalPedido(int idPedido, double nuevoTotal) {
        String sql = "UPDATE Pedido SET total = ? WHERE idPedido = ?";
        
        try (Connection con = ConexionBDSQLServer.GetConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setDouble(1, nuevoTotal);
            ps.setInt(2, idPedido);
            ps.executeUpdate();
        } catch (Exception e) {
            System.err.println("Error al actualizar total del pedido: " + e.getMessage());
        }
    }

    public static List<MProductos> cargarProductosPorCategoria(String categoria) {

        List<MProductos> lista = new ArrayList<>();

        String sql = "SELECT idProducto, nombre, precio, descripcion, urlFoto, categoria, activo " +
                     "FROM Producto WHERE activo = 1 AND categoria = ?";

        try (Connection con = ConexionBDSQLServer.GetConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, categoria);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    MProductos producto = new MProductos();
                    producto.setIdProducto(rs.getInt("idProducto"));
                    producto.setNombre(rs.getString("nombre"));
                    producto.setPrecio(rs.getDouble("precio"));
                    producto.setDescripcion(rs.getString("descripcion"));
                    producto.setUrlFoto(rs.getString("urlFoto"));
                    producto.setCategoria(rs.getString("categoria"));
                    producto.setActivo(rs.getBoolean("activo"));

                    lista.add(producto);
                }
            }

        } catch (Exception e) {
            System.err.println("Error al cargar productos por categoría: " + e.getMessage());
        }

        return lista;
    }
}
