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
@DiscriminatorValue("COCINERO")
@Getter
@Setter
@NoArgsConstructor
public class Cocinero extends Persona {

	private String especialidad;

	@Enumerated(EnumType.STRING)
	private Categoria categoria;

	@Override
	public String getRolLaboral() {
		return "Cocina - " + categoria;
	}

	// (sueldo base + plus por categoría) * factor según duración del festival
	@Override
	public BigDecimal calcularSueldo(Festival festival) {
		Costo costos = festival.getCostos();
		return costos.getSueldoBase().add(costos.plusPorCategoria(categoria)).multiply(festival.factorLiquidacion());
	}
}
