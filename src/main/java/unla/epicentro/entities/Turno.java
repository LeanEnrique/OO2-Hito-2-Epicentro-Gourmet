package unla.epicentro.entities;

public enum Turno {
	MANANA("Mañana"), NOCHE("Noche");

	// Texto para mostrar en pantalla (el nombre del enum es el que se guarda en la base)
	private final String descripcion;

	Turno(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getDescripcion() {
		return descripcion;
	}
}
