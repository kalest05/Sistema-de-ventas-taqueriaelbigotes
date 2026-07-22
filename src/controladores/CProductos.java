package controladores;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import conexion.ConexionBDSQLServer;

/**
 * Controlador de Productos. BD: tabla Producto. No elimina registros, solo
 * marca activo = 0.
 */
public class CProductos {

	/*
	 * Obtiene TODOS los productos: activos primero, inactivos al final. Retorna:
	 * [idProducto, nombre, precio, descripcion, activo(boolean)] Usado por
	 * PanelProductos para mostrar lista completa de gestión. No incluye urlFoto —
	 * no es necesario en la tabla de gestión.
	 */
	public static List<Object[]> obtenerTodosProductos() {

		List<Object[]> lista = new ArrayList<>();

		String sql = "SELECT idProducto, nombre, precio, descripcion, categoria, activo " + "FROM Producto "
				+ "ORDER BY activo DESC, nombre";

		try (Connection con = ConexionBDSQLServer.GetConexion();
				PreparedStatement ps = con.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {

			while (rs.next()) {
				lista.add(new Object[] { rs.getInt("idProducto"), // 0
						rs.getString("nombre"), // 1
						rs.getDouble("precio"), // 2
						rs.getString("descripcion"), // 3
						rs.getString("categoria"), // 4
						rs.getBoolean("activo") // 5
				});
			}

		} catch (Exception e) {
			System.err.println("Error al obtener productos: " + e.getMessage());
			e.printStackTrace(); // 👈 agrega esto para ver el error real
		}

		return lista;
	}

	/*
	 * Obtiene urlFoto de un producto específico. Usado por el modal de edición para
	 * mostrar la preview.
	 */
	public static String obtenerUrlFoto(int idProducto) {
		String sql = "SELECT urlFoto FROM Producto WHERE idProducto = ?";

		try (Connection con = ConexionBDSQLServer.GetConexion(); PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setInt(1, idProducto);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next())
					return rs.getString("urlFoto");
			}
		} catch (Exception e) {
			System.err.println("Error al obtener urlFoto: " + e.getMessage());
		}
		return null;
	}

	/*
	 * Actualiza nombre, precio, descripcion y urlFoto de un producto.
	 */
	public static void actualizarProducto(int idProducto, String nombre, double precio, String descripcion,
			String urlFoto, String nuevaCategoria) {

		String sql = "UPDATE Producto SET " + "nombre = ?, " + "precio = ?, " + "descripcion = ?, " + "urlFoto = ?, "
				+ "categoria = ? " + "WHERE idProducto = ?";

		try (Connection con = ConexionBDSQLServer.GetConexion(); PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setString(1, nombre);
			ps.setDouble(2, precio);
			ps.setString(3, descripcion);
			ps.setString(4, urlFoto);
			ps.setString(5, nuevaCategoria);
			ps.setInt(6, idProducto);

			ps.executeUpdate();

		} catch (Exception e) {
			System.err.println("Error al actualizar producto: " + e.getMessage());
			e.printStackTrace(); // 👈 importante mientras desarrollas
		}
	}

	/*
	 * Marca un producto como inactivo (activo = 0). No aparece en PanelOrden ni en
	 * CVentas.cargarProductos().
	 */
	public static void inactivarProducto(int idProducto) {
		String sql = "UPDATE Producto SET activo = 0 WHERE idProducto = ?";

		try (Connection con = ConexionBDSQLServer.GetConexion(); PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setInt(1, idProducto);
			ps.executeUpdate();
		} catch (Exception e) {
			System.err.println("Error al inactivar producto: " + e.getMessage());
		}
	}

	/*
	 * Reactiva un producto inactivo.
	 */
	public static void activarProducto(int idProducto) {
		String sql = "UPDATE Producto SET activo = 1 WHERE idProducto = ?";

		try (Connection con = ConexionBDSQLServer.GetConexion(); PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setInt(1, idProducto);
			ps.executeUpdate();
		} catch (Exception e) {
			System.err.println("Error al activar producto: " + e.getMessage());
		}
	}

	/*
	 * Agrega un nuevo producto activo.
	 */
	public static void agregarProducto(String nombre, double precio, String descripcion, String urlFoto,
			String categoria) {

		String sql = "INSERT INTO Producto (nombre, precio, descripcion, urlFoto, categoria, activo) "
				+ "VALUES (?, ?, ?, ?, ?, 1)";

		try (Connection con = ConexionBDSQLServer.GetConexion(); PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setString(1, nombre);
			ps.setDouble(2, precio);
			ps.setString(3, descripcion);
			ps.setString(4, urlFoto);
			ps.setString(5, categoria);

			ps.executeUpdate();

		} catch (Exception e) {
			System.err.println("Error al agregar producto: " + e.getMessage());
			e.printStackTrace();
		}
	}
}
