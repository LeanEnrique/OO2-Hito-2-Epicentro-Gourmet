package unla.epicentro.entities;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo")
@Getter
@Setter
@NoArgsConstructor
public abstract class UnidadDeVenta {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String nombreComercial;

	@Column(unique = true, length = 10)
	private String codigo;

	private BigDecimal superficie;

	@ManyToOne
	private Persona responsable;

	@OneToMany(mappedBy = "unidad")
	private List<Plato> platos = new ArrayList<>();

	@OneToMany(mappedBy = "unidad")
	private List<AsignacionPersonal> asignaciones = new ArrayList<>();

	@OneToMany(mappedBy = "unidad")
	private List<Pedido> pedidos = new ArrayList<>();

	public abstract String getTipo();

	// Cada tipo de unidad tiene su prefijo: FT para Food Truck, PD para Puesto Desarmable
	protected abstract String getPrefijoCodigo();

	// Código: prefijo de 2 letras según el tipo + 8 dígitos (ej: FT00000001)
	public boolean tieneCodigoValido() {
		return codigo != null && codigo.matches(getPrefijoCodigo() + "\\d{8}");
	}
}
