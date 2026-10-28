package unla.epicentro.dtos;

import java.math.BigDecimal;

// Una línea de la liquidación de sueldo de un empleado (no persiste)
public record LiquidacionDTO(Long festivalId, String festival, String unidad, String rol, long diasFestival,
		BigDecimal sueldo) {
}
