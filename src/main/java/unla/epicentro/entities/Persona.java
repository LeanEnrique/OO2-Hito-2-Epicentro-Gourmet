package unla.epicentro.entities;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Una Persona "a secas" es un responsable; Cajero y Cocinero son el personal que cobra sueldo
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo")
@DiscriminatorValue("PERSONA")
@Getter
@Setter
@NoArgsConstructor
public class Persona {

	public static final int MAYORIA_DE_EDAD = 18;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String nombre;
	private String apellido;

	@Column(unique = true)
	private String dni;

	@Column(unique = true)
	private String email;

	private LocalDate fechaNacimiento;
	private LocalDate fechaIngreso;
	private boolean activo = true;

	public boolean esMayorDeEdad() {
		return fechaNacimiento != null && Period.between(fechaNacimiento, LocalDate.now()).getYears() >= MAYORIA_DE_EDAD;
	}

	public int getAniosAntiguedad(LocalDate alDia) {
		return Period.between(fechaIngreso, alDia).getYears();
	}

	public String getNombreCompleto() {
		return nombre + " " + apellido;
	}

	public String getRolLaboral() {
		return "Responsable";
	}

	public BigDecimal calcularSueldo(Festival festival) {
		return BigDecimal.ZERO;
	}
}
