package unla.epicentro.entities;

import java.math.BigDecimal;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@DiscriminatorValue("CAJERO")
@Getter
@Setter
@NoArgsConstructor
public class Cajero extends Persona {

	@Enumerated(EnumType.STRING)
	private Turno turno;

	@Override
	public String getRolLaboral() {
		return "Cajero (" + turno + ")";
	}

	// (sueldo base + años de antigüedad * monto por año) * factor según duración del festival
	@Override
	public BigDecimal calcularSueldo(Festival festival) {
		return festival.getCostos().getSueldoBase().add(calcularAdicional(festival)).multiply(festival.factorLiquidacion());
	}

	@Override
	public BigDecimal calcularAdicional(Festival festival) {
		return festival.getCostos().getPorAntiguedad()
				.multiply(BigDecimal.valueOf(getAniosAntiguedad(festival.getFechaInicio())));
	}

	@Override
	public String getConceptoAdicional(Festival festival) {
		return "Antigüedad (" + getAniosAntiguedad(festival.getFechaInicio()) + " años)";
	}
}
