package unla.epicentro.dtos;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CanonDTO {
    
    private Long unidadId;
    private String nombreComercial;
    private String codigo;
    private String tipo;
    private BigDecimal canon;
}