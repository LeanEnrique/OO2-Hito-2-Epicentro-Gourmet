package unla.epicentro.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import unla.epicentro.entities.Persona;
import unla.epicentro.entities.UnidadDeVenta;

public interface PersonaRepository extends JpaRepository<Persona, Long> {

	boolean existsByDni(String dni);

	boolean existsByEmail(String email);

	boolean existsByDniAndIdNot(String dni, Long id);

	boolean existsByEmailAndIdNot(String email, Long id);

	@Query("""
		select distinct p
		from AsignacionPersonal a
		join a.persona p
		where a.unidad = :unidad
		and p.activo = true
		and type(p) in (Cajero, Cocinero)
		and (
			:busqueda = ''
			or lower(p.nombre) like lower(concat('%', :busqueda, '%'))
			or lower(p.apellido) like lower(concat('%', :busqueda, '%'))
			or lower(p.dni) like lower(concat('%', :busqueda, '%'))
			or lower(p.email) like lower(concat('%', :busqueda, '%'))
		)
		order by p.apellido asc, p.nombre asc
	""")
	List<Persona> buscarEmpleadosDeUnidad(
			@Param("unidad") UnidadDeVenta unidad,
			@Param("busqueda") String busqueda);
}