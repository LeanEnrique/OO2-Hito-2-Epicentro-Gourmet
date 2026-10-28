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
		Costo costos = festival.getCostos();
		BigDecimal antiguedad = costos.getPorAntiguedad()
				.multiply(BigDecimal.valueOf(getAniosAntiguedad(festival.getFechaInicio())));
		return costos.getSueldoBase().add(antiguedad).multiply(festival.factorLiquidacion());
	}
}
