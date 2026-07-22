package modelos;

public class MInsumo {

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
	 * @return the unidadMedida
	 */
	public String getUnidadMedida() {
		return unidadMedida;
	}
	/**
	 * @param unidadMedida the unidadMedida to set
	 */
	public void setUnidadMedida(String unidadMedida) {
		this.unidadMedida = unidadMedida;
	}
	int idInsumo;
     String nombre;
     String unidadMedida;
}
