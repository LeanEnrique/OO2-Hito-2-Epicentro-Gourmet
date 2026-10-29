package unla.epicentro.services;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import unla.epicentro.entities.Costo;
import unla.epicentro.entities.Festival;
import unla.epicentro.entities.FoodTruck;
import unla.epicentro.entities.PuestoDesarmable;

class CanonServiceTest {

    private final CanonService canonService = new CanonService();

    @Test
    void calcularCanonFoodTruckSinElectricidad() {

        Festival festival = crearFestival();

        FoodTruck foodTruck = new FoodTruck();
        foodTruck.setSuperficie(new BigDecimal("20"));
        foodTruck.setConexionElectrica(false);

        BigDecimal resultado = canonService.calcularCanon(foodTruck, festival);

        assertEquals(new BigDecimal("10000"), resultado);
        
    }

    @Test
    void calcularCanonFoodTruckConElectricidad() {

        Festival festival = crearFestival();

        FoodTruck foodTruck = new FoodTruck();
        foodTruck.setSuperficie(new BigDecimal("20"));
        foodTruck.setConexionElectrica(true);

        BigDecimal resultado = canonService.calcularCanon(foodTruck, festival);

        assertEquals(new BigDecimal("12000"), resultado);
    }


    @Test
    void calcularCanonPuesto() {

        Festival festival = crearFestival();

        PuestoDesarmable puesto = new PuestoDesarmable();
        puesto.setSuperficie(new BigDecimal("20"));
        puesto.setTiempoDeArmado(30);

        BigDecimal resultado = canonService.calcularCanon(puesto, festival);

        assertEquals(new BigDecimal("9700"), resultado);
    }

    private Festival crearFestival() {

        Costo costos = new Costo();

        costos.setPorSuperficie(new BigDecimal("500"));
        costos.setPorElectricidad(new BigDecimal("2000"));
        costos.setPorMontaje(new BigDecimal("10"));

        Festival festival = new Festival();
        festival.setCostos(costos);

        return festival;
    }
}