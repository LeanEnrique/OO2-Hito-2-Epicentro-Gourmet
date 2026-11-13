package unla.epicentro.entities;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Caja de un cajero: se abre en 0, se le asocian los pedidos que toma y al cerrarla guarda sus totales
@Entity
@Getter
@Setter
@NoArgsConstructor
public class Caja {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private LocalDate fecha;

	@ManyToOne
	private Festival festival;

	@ManyToOne
	private UnidadDeVenta unidad;

	@ManyToOne
	private Cajero cajero;

	@Enumerated(EnumType.STRING)
	private Turno turno;

	@Enumerated(EnumType.STRING)
	private EstadoCaja estado = EstadoCaja.ABIERTA;

	// Totales de los pedidos tomados con esta caja: se calculan solos al cerrarla
	private int cantidadPedidos;

	private BigDecimal ventasDelDia = BigDecimal.ZERO;

	private LocalDateTime fechaHoraCierre;

	public boolean estaAbierta() {
		return estado == EstadoCaja.ABIERTA;
	}
}
