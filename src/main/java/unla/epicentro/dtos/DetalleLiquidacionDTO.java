package unla.epicentro.dtos;

import java.math.BigDecimal;

// Desglose del cálculo del sueldo de un empleado en un festival (no persiste)
public record DetalleLiquidacionDTO(String festival, String unidad, String rol, long diasFestival,
		BigDecimal sueldoBase, String conceptoAdicional, BigDecimal adicional, BigDecimal subtotal,
		int porcentajeFactor, String descripcionFactor, BigDecimal total) {
}
