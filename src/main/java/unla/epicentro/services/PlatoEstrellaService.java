
package unla.epicentro.services;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import unla.epicentro.dtos.PlatoDTO;
import unla.epicentro.entities.Festival;
import unla.epicentro.entities.ItemPlato;
import unla.epicentro.entities.Pedido;
import unla.epicentro.entities.Plato;
import unla.epicentro.entities.UnidadDeVenta;
import unla.epicentro.repositories.PedidoRepository;

@Service
public class PlatoEstrellaService {

    private final PedidoRepository pedidoRepository;

    public PlatoEstrellaService(PedidoRepository pedidoRepository) {
        this.pedidoRepository = pedidoRepository;
    }

    @Transactional(readOnly = true)
    public PlatoDTO obtenerPlatoEstrella(
            UnidadDeVenta unidad,
            Festival festival) {

        if (unidad == null || festival == null) {
            throw new IllegalArgumentException(
                    "El festival y la unidad son obligatorios");
        }

        boolean pertenece = festival.getUnidades().stream()
                .anyMatch(u -> u.getId().equals(unidad.getId()));

        if (!pertenece) {
            throw new IllegalArgumentException(
                    "La unidad no pertenece al festival seleccionado");
        }

        List<Pedido> pedidos =
                pedidoRepository.findByFestivalAndUnidad(festival, unidad);

        Map<Long, Integer> cantidades = new LinkedHashMap<>();
        Map<Long, Plato> platos = new LinkedHashMap<>();

        for (Pedido pedido : pedidos) {

            for (ItemPlato item : pedido.getItems()) {

                Plato plato = item.getPlato();
                Long id = plato.getId();

                cantidades.merge(id, item.getCantidad(), Integer::sum);
                platos.putIfAbsent(id, plato);
            }
        }

        if (cantidades.isEmpty()) {
            return null;
        }

        Long idPlatoEstrella = cantidades.entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .get()
                .getKey();

        Plato platoEstrella = platos.get(idPlatoEstrella);

        PlatoDTO dto = new PlatoDTO();
        dto.setId(platoEstrella.getId());
        dto.setNombre(platoEstrella.getNombre());
        dto.setPrecio(platoEstrella.getPrecio());
        dto.setCosto(platoEstrella.getCosto());

        return dto;
    }
}
