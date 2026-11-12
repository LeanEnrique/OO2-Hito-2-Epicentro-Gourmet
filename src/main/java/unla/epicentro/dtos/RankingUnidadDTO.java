package unla.epicentro.dtos;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;

// Una fila del ranking de unidades de un festival (no persiste)
@Getter
@AllArgsConstructor
public class RankingUnidadDTO {

	private int posicion;
	private String nombreComercial;
	private String codigo;
	private String tipo;
	private long cantidadPedidos;
	private BigDecimal recaudacion;
}
