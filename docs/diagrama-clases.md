# Diagrama de clases — Gestión de un Festival

Modelo de dominio de Epicentro Gourmet. Se puede visualizar directamente en GitHub (Mermaid) o pegar el
bloque en <https://mermaid.live> para exportarlo como PNG/PDF.

```mermaid
classDiagram
    direction LR

    class Festival {
        -Long id
        -String nombre
        -String temporada
        -LocalDate fechaInicio
        -LocalDate fechaFin
        -Costo costos
        -Set~UnidadDeVenta~ unidades
        +getCantidadDias() long
        +factorLiquidacion() BigDecimal
        +getPorcentajeFactor() int
        +getEstado() String
        +isPeriodoValido() boolean
        +estaActivo(LocalDate) boolean
    }

    class Costo {
        <<embeddable>>
        -BigDecimal porSuperficie
        -BigDecimal porMontaje
        -BigDecimal porElectricidad
        -BigDecimal sueldoBase
        -BigDecimal porAntiguedad
        -BigDecimal plusCocinero
        -BigDecimal plusAyudante
        -BigDecimal plusLavaplatos
        +plusPorCategoria(Categoria) BigDecimal
    }

    class UnidadDeVenta {
        <<abstract>>
        -Long id
        -String nombreComercial
        -String codigo
        -BigDecimal superficie
        -Persona responsable
        +getTipo()* String
        +getPrefijoCodigo()* String
        +tieneCodigoValido() boolean
    }

    class FoodTruck {
        -String patente
        -boolean conexionElectrica
        +getTipo() String
    }

    class PuestoDesarmable {
        -int cantCarpas
        -int tiempoDeArmado
        +getTipo() String
    }

    class Persona {
        -Long id
        -String nombre
        -String apellido
        -String dni
        -String email
        -LocalDate fechaNacimiento
        -LocalDate fechaIngreso
        -boolean activo
        +esMayorDeEdad() boolean
        +getAniosAntiguedad(LocalDate) int
        +calcularSueldo(Festival) BigDecimal
        +calcularAdicional(Festival) BigDecimal
        +getConceptoAdicional(Festival) String
    }

    class Cajero {
        -Turno turno
        +calcularSueldo(Festival) BigDecimal
    }

    class Cocinero {
        -String especialidad
        -Categoria categoria
        +calcularSueldo(Festival) BigDecimal
    }

    class Usuario {
        -Long id
        -String username
        -String password
        -Rol rol
        -boolean activo
    }

    class Plato {
        -Long id
        -String nombre
        -BigDecimal precio
        -BigDecimal costo
        -boolean activo
    }

    class Pedido {
        -Long id
        -LocalDate fecha
        +getTotal() BigDecimal
        +agregarItem(Plato, int) void
    }

    class ItemPlato {
        -Long id
        -int cantidad
        -BigDecimal precioUnitario
        -BigDecimal costoUnitario
        +getSubtotal() BigDecimal
    }

    class AsignacionPersonal {
        -Long id
    }

    class Caja {
        -Long id
        -LocalDate fecha
        -Turno turno
        -EstadoCaja estado
        -BigDecimal montoInicial
        -BigDecimal ventasDelDia
        -BigDecimal montoDeclarado
        -BigDecimal diferencia
        -LocalDateTime fechaHoraCierre
        -String observaciones
        +getMontoEsperado() BigDecimal
        +estaAbierta() boolean
    }

    class Rol {
        <<enumeration>>
        ADMINISTRADOR
        RESPONSABLE
        EMPLEADO
    }

    class Turno {
        <<enumeration>>
        MANANA
        NOCHE
    }

    class Categoria {
        <<enumeration>>
        COCINERO
        AYUDANTE
        LAVAPLATOS
    }

    class EstadoCaja {
        <<enumeration>>
        ABIERTA
        CERRADA
    }

    Festival *-- Costo : costos
    Festival "0..*" -- "0..*" UnidadDeVenta : unidades habilitadas
    UnidadDeVenta <|-- FoodTruck
    UnidadDeVenta <|-- PuestoDesarmable
    UnidadDeVenta "*" -- "1" Persona : responsable
    UnidadDeVenta "1" -- "0..*" Plato : ofrece
    UnidadDeVenta "1" -- "0..*" AsignacionPersonal : personal asignado
    UnidadDeVenta "1" -- "0..*" Caja : cajas
    Persona <|-- Cajero
    Persona <|-- Cocinero
    Persona "1" -- "0..1" Usuario : credencial
    AsignacionPersonal "*" -- "1" Persona
    AsignacionPersonal "*" -- "1" Festival
    Pedido "*" -- "1" Festival : ocurre en
    Pedido "*" -- "1" UnidadDeVenta : entrega
    Pedido "1" *-- "1..*" ItemPlato : items
    ItemPlato "*" -- "1" Plato
    Caja "*" -- "1" Festival
    Caja "*" -- "1" Cajero
    Caja "*" -- "1" Turno
    Caja "*" -- "1" EstadoCaja
    Usuario "*" -- "1" Rol
    Persona ..> Categoria : cocineros
```

## Notas de modelado

| Decisión | Justificación |
|---|---|
| `Festival` **tiene** un `Costo` embebido (no una tabla aparte) | Evita montos hardcodeados en los métodos: cada festival define sus propios valores y `Costo` es un `@Embeddable` que se guarda en la misma tabla. |
| Herencia `SINGLE_TABLE` en `UnidadDeVenta` | Food Truck y Puesto Desarmable comparten identidad, responsable y superficie; solo difieren en atributos y en la fórmula de canon (`calcularCanon` resuelve con `instanceof`). |
| Herencia `SINGLE_TABLE` en `Persona` | Una `Persona` "a secas" es el **responsable** de una unidad; `Cajero` y `Cocinero` son el **personal** que cobra sueldo. Así el responsable no cotiza como empleado. |
| `AsignacionPersonal` como entidad intermedia | Resuelve el muchos-a-muchos entre personal, festival y unidad: un empleado puede trabajar en distintos festivales y en distintas unidades. |
| `ItemPlato` guarda precio y costo unitarios | Snapshot histórico: si el plato cambia de precio, los pedidos ya registrados y los reportes no se alteran. |
| `Caja` nueva (Hito 2) | Cierre de caja del cajero: guarda el arqueo del turno, las ventas calculadas desde los pedidos y la diferencia declarada. |
| `Plato.activo` y `Persona.activo` | Baja lógica: no se borra el registro, se desactiva; se reactiva con un método específico. |
