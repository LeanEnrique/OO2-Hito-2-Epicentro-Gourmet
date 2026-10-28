package unla.epicentro.entities;

import java.math.BigDecimal;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class ItemPlato {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	private Pedido pedido;

	@ManyToOne
	private Plato plato;

	private int cantidad;

	// Se guardan precio y costo del momento, por si después cambian en el plato
	private BigDecimal precioUnitario;
	private BigDecimal costoUnitario;

	public ItemPlato(Pedido pedido, Plato plato, int cantidad) {
		this.pedido = pedido;
		this.plato = plato;
		this.cantidad = cantidad;
		this.precioUnitario = plato.getPrecio();
		this.costoUnitario = plato.getCosto();
	}

	public BigDecimal getSubtotal() {
		return precioUnitario.multiply(BigDecimal.valueOf(cantidad));
	}
}
