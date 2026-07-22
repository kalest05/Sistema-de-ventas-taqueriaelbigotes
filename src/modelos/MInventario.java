package modelos;

import java.util.Date;

public class MInventario {
	    /**
	 * @return the idInventario
	 */
	public int getIdInventario() {
		return idInventario;
	}
	/**
	 * @param idInventario the idInventario to set
	 */
	public void setIdInventario(int idInventario) {
		this.idInventario = idInventario;
	}
	/**
	 * @return the idInsumo
	 */
	public int getIdInsumo() {
		return idInsumo;
	}
	/**
	 * @param idInsumo the idInsumo to set
	 */
	public void setIdInsumo(int idInsumo) {
		this.idInsumo = idInsumo;
	}
	/**
	 * @return the cantidadDisponible
	 */
	public double getCantidadDisponible() {
		return cantidadDisponible;
	}
	/**
	 * @param cantidadDisponible the cantidadDisponible to set
	 */
	public void setCantidadDisponible(double cantidadDisponible) {
		this.cantidadDisponible = cantidadDisponible;
	}
	/**
	 * @return the fechaActualizacion
	 */
	public Date getFechaActualizacion() {
		return fechaActualizacion;
	}
	/**
	 * @param fechaActualizacion the fechaActualizacion to set
	 */
	public void setFechaActualizacion(Date fechaActualizacion) {
		this.fechaActualizacion = fechaActualizacion;
	}
		int idInventario;
	     int idInsumo;
	     double cantidadDisponible;
	     Date fechaActualizacion;
}
