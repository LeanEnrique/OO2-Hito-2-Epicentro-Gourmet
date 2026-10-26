package unla.epicentro.entities;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@DiscriminatorValue("PUESTO")
@Getter
@Setter
@NoArgsConstructor
public class PuestoDesarmable extends UnidadDeVenta {

	private int cantCarpas;
	private int tiempoDeArmado;

	@Override
	public String getTipo() {
		return "Puesto Desarmable";
	}

	@Override
	protected String getPrefijoCodigo() {
		return "PD";
	}
}
