package unla.epicentro.entities;

public enum Categoria {
	COCINERO("Cocinero"), AYUDANTE("Cocinero ayudante"), LAVAPLATOS("Lavaplatos");

	// Texto para mostrar en pantalla (el nombre del enum es el que se guarda en la base)
	private final String descripcion;

	Categoria(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getDescripcion() {
		return descripcion;
	}
}
