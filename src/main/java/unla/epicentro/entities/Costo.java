package unla.epicentro.entities;

import java.math.BigDecimal;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Valores que define cada festival, para no tener montos hardcodeados en los cálculos
@Embeddable
@Getter
@Setter
@NoArgsConstructor
public class Costo {

	private BigDecimal porSuperficie;
	private BigDecimal porMontaje;
	private BigDecimal porElectricidad;
	private BigDecimal sueldoBase;
	private BigDecimal porAntiguedad;
	private BigDecimal plusCocinero;
	private BigDecimal plusAyudante;
	private BigDecimal plusLavaplatos;

	public BigDecimal plusPorCategoria(Categoria categoria) {
		return switch (categoria) {
			case COCINERO -> plusCocinero;
			case AYUDANTE -> plusAyudante;
			case LAVAPLATOS -> plusLavaplatos;
		};
	}
}
