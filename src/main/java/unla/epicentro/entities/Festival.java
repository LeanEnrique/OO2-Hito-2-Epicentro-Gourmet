package unla.epicentro.entities;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Festival {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String nombre;
	private String temporada;
	private LocalDate fechaInicio;
	private LocalDate fechaFin;

	@Embedded
	private Costo costos;

	@ManyToMany
	@JoinTable(name = "festival_unidad", joinColumns = @JoinColumn(name = "festival_id"), inverseJoinColumns = @JoinColumn(name = "unidad_id"))
	private Set<UnidadDeVenta> unidades = new HashSet<>();

	public long getCantidadDias() {
		return ChronoUnit.DAYS.between(fechaInicio, fechaFin) + 1;
	}

	public boolean estaActivo(LocalDate fecha) {
		return !fecha.isBefore(fechaInicio) && !fecha.isAfter(fechaFin);
	}

	// Hasta 15 días 50%, de 16 a 30 100%, más de 30: (int(dias/15) + 1) * 50%
	public BigDecimal factorLiquidacion() {
		long dias = getCantidadDias();
		long mitades;
		if (dias <= 15) {
			mitades = 1;
		} else if (dias <= 30) {
			mitades = 2;
		} else {
			mitades = dias / 15 + 1;
		}
		return BigDecimal.valueOf(mitades).multiply(new BigDecimal("0.5"));
	}
}
