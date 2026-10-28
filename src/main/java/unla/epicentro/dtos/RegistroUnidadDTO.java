package unla.epicentro.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

// Datos del formulario de registro de una unidad de venta con su responsable
@Getter
@Setter
public class RegistroUnidadDTO {

	// Responsable
	@NotBlank(message = "El nombre es obligatorio")
	private String nombre;

	@NotBlank(message = "El apellido es obligatorio")
	private String apellido;

	@NotBlank(message = "El DNI es obligatorio")
	private String dni;

	@NotBlank(message = "El email es obligatorio")
	@Email(message = "El email no es válido")
	private String email;

	@NotNull(message = "La fecha de nacimiento es obligatoria")
	@Past(message = "La fecha de nacimiento debe ser pasada")
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
	private LocalDate fechaNacimiento;

	@NotBlank(message = "El usuario es obligatorio")
	private String username;

	@NotBlank(message = "La contraseña es obligatoria")
	@Size(min = 4, message = "La contraseña debe tener al menos 4 caracteres")
	private String password;

	// Unidad de venta
	@NotBlank(message = "El nombre comercial es obligatorio")
	private String nombreComercial;

	@NotBlank(message = "El código es obligatorio")
	@Size(min = 10, max = 10, message = "El código debe tener 10 caracteres")
	private String codigo;

	@NotNull(message = "La superficie es obligatoria")
	@Positive(message = "La superficie debe ser mayor a 0")
	private BigDecimal superficie;

	@NotNull(message = "Elegí un festival")
	private Long festivalId;

	@NotBlank(message = "Elegí el tipo de unidad")
	private String tipo; // FOOD_TRUCK o PUESTO

	// Food Truck
	private String patente;
	private boolean conexionElectrica;

	// Puesto Desarmable
	private Integer cantCarpas;
	private Integer tiempoDeArmado;
}
