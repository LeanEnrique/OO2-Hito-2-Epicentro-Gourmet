package unla.epicentro.entities;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Pedido {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private LocalDate fecha;

	@ManyToOne
	private Festival festival;

	@ManyToOne
	private UnidadDeVenta unidad;

	@OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL)
	@OrderBy("id ASC")
	private List<ItemPlato> items = new ArrayList<>();

	public void agregarItem(Plato plato, int cantidad) {
		items.add(new ItemPlato(this, plato, cantidad));
	}

	public BigDecimal getTotal() {
		return items.stream().map(ItemPlato::getSubtotal).reduce(BigDecimal.ZERO, BigDecimal::add);
	}
}
