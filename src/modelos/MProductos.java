package modelos;

public class MProductos {
int idProducto;
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
 * @return the nombre
 */
public String getNombre() {
	return nombre;
}
/**
 * @param nombre the nombre to set
 */
public void setNombre(String nombre) {
	this.nombre = nombre;
}
/**
 * @return the precio
 */
public double getPrecio() {
	return precio;
}
/**
 * @param precio the precio to set
 */
public void setPrecio(double precio) {
	this.precio = precio;
}
/**
 * @return the descripcion
 */
public String getDescripcion() {
	return descripcion;
}
/**
 * @param descripcion the descripcion to set
 */
public void setDescripcion(String descripcion) {
	this.descripcion = descripcion;
}
/**
 * @return the urlFoto
 */
public String getUrlFoto() {
	return urlFoto;
}
/**
 * @param urlFoto the urlFoto to set
 */
public void setUrlFoto(String urlFoto) {
	this.urlFoto = urlFoto;
}
/**
 * @return the activo
 */
public boolean isActivo() {
	return activo;
}
/**
 * @param activo the activo to set
 */
public void setActivo(boolean activo) {
	this.activo = activo;
}
String nombre;
double precio;
 String descripcion;
 String urlFoto;
 boolean activo;
 /**
 * @return the categoria
 */
public String getCategoria() {
	return categoria;
}
/**
 * @param categoria the categoria to set
 */
public void setCategoria(String categoria) {
	this.categoria = categoria;
}
String categoria;
}
