package unla.epicentro.entities;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@DiscriminatorValue("FOOD_TRUCK")
@Getter
@Setter
@NoArgsConstructor
public class FoodTruck extends UnidadDeVenta {

	private String patente;
	private boolean conexionElectrica;

	@Override
	public String getTipo() {
		return "Food Truck";
	}

	@Override
	protected String getPrefijoCodigo() {
		return "FT";
	}
}
