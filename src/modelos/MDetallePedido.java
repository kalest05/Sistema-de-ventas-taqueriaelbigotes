package modelos;

public class MDetallePedido {
	   /**
	 * @return the idDetalle
	 */
	public int getIdDetalle() {
		return idDetalle;
	}
	/**
	 * @param idDetalle the idDetalle to set
	 */
	public void setIdDetalle(int idDetalle) {
		this.idDetalle = idDetalle;
	}
	/**
	 * @return the idPedido
	 */
	public int getIdPedido() {
		return idPedido;
	}
	/**
	 * @param idPedido the idPedido to set
	 */
	public void setIdPedido(int idPedido) {
		this.idPedido = idPedido;
	}
	/**
	 * @return the idProducto
	 */
	public int getIdProducto() {
		return idProducto;
	}
	/**
	 * @param idProducto the idProducto to set
	 */
	public void setIdProducto(int idProducto) {
		this.idProducto = idProducto;
	}
	/**
	 * @return the cantidad
	 */
	public int getCantidad() {
		return cantidad;
	}
	/**
	 * @param cantidad the cantidad to set
	 */
	public void setCantidad(int cantidad) {
		this.cantidad = cantidad;
	}
	/**
	 * @return the subtotal
	 */
	public double getSubtotal() {
		return subtotal;
	}
	/**
	 * @param subtotal the subtotal to set
	 */
	public void setSubtotal(double subtotal) {
		this.subtotal = subtotal;
	}
	int idDetalle;
	     int idPedido;
	     int idProducto;
	     int cantidad;
	     double subtotal;
}
