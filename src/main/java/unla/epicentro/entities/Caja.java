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

// Cierre de caja de un cajero al finalizar el día: guarda el arqueo del turno
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

	private BigDecimal montoInicial = BigDecimal.ZERO;

	// Lo que el sistema calcula a partir de los pedidos cargados ese día
	private BigDecimal ventasDelDia = BigDecimal.ZERO;

	// Lo que el cajero declara haber contado al cerrar
	private BigDecimal montoDeclarado = BigDecimal.ZERO;

	// montoDeclarado - (montoInicial + ventasDelDia)
	private BigDecimal diferencia = BigDecimal.ZERO;

	private LocalDateTime fechaHoraCierre;

	private String observaciones;

	public BigDecimal getMontoEsperado() {
		return montoInicial.add(ventasDelDia);
	}

	public boolean estaAbierta() {
		return estado == EstadoCaja.ABIERTA;
	}
}
