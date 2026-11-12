# Listado de casos de uso — Hito 2

Actores: **Administrador**, **Responsable**, **Empleado (Cajero / Cocinero)** y **Público** (sin sesión).

## A. Público / comunes

| CU | Nombre | Descripción | Vista / Endpoint |
|---|---|---|---|
| A1 | Página de inicio | Lista los festivales precargados (nombre, temporada, fechas, días y cantidad de unidades) y ofrece registrarse o iniciar sesión | `GET /` → `index.html` |
| A2 | Login | Autenticación con Spring Security y BCrypt; redirige al panel según el rol | `GET/POST /login`, `GET /home` |
| A3 | Registro de unidad de venta con responsable | Da de alta el responsable, su unidad y el usuario (usuario y contraseña obligatorios); envía mail de bienvenida | `GET/POST /registro` |
| A4 | Logout | Cierra la sesión | `POST /logout` |

## B. Administrador

| CU | Nombre | Descripción | Vista / Endpoint |
|---|---|---|---|
| B1 | Ver festivales | Panel con el festival seleccionado, sus datos y sus unidades de venta | `GET /admin` |
| B2 | Ver unidades de venta de un festival | Listado con código, nombre comercial, tipo, superficie y responsable | `GET /admin` → `admin/inicio.html` |
| B3 | Ranking de unidades | Unidades del festival ordenadas descendentemente por recaudación (`RankingUnidadDTO`, no persiste) | `GET /admin/ranking` |
| B4 | Unidades con mayor canon | Dado un festival y un parámetro `x`, devuelve las `x` unidades que más pagan de canon con nombre comercial, código, tipo y canon (`ReporteMayoresCanonDTO`, no persiste) | `GET /admin/mayor-canon?x=3` |
| B5 | Cálculo de rentabilidad neta | Ingresos − costos de platos − sueldos − canon de una unidad en un festival | `GET /admin/rentabilidad` |
| B6 | Rentabilidad entre dos fechas | Igual que B5 pero acotado a un rango dentro del festival | `GET /admin/rentabilidad?desde=&hasta=` |
| B7 | Plato estrella | Dado un festival y una unidad, devuelve el `PlatoDTO` más pedido | `GET /admin/plato-estrella` |

## C. Responsable

| CU | Nombre | Descripción | Vista / Endpoint |
|---|---|---|---|
| C1 | Cálculo de canon | Monto a pagar por la unidad en un festival (Food Truck: superficie × costo + electricidad; Puesto: superficie × costo − montaje × tiempo) | `GET /canon` |
| C2 | Alta de empleado | Crea el empleado, su usuario (email) y una contraseña aleatoria, lo asigna a un festival y envía el mail | `GET/POST /responsable/empleados` |
| C3 | Modificación de empleado | Edita datos personales, tipo, turno o categoría | `GET/POST /responsable/empleados/{id}` |
| C4 | Baja lógica de empleado | Desactiva la persona y su usuario (no borra) | `POST /responsable/empleados/{id}/baja` |
| C5 | Reactivación de empleado | Método de actualización específico para volver a dar de alta una entidad eliminada | `POST /responsable/empleados/{id}/reactivar` |
| C6 | Filtros de empleados por uno o varios atributos | Búsqueda libre + filtros de tipo, turno y categoría, trayendo solo entidades no dadas de baja | `GET /responsable/empleados?tipo=&turno=&categoria=` |
| C7 | Alta de plato | Nombre, precio de venta y costo de producción | `GET/POST /responsable/platos` |
| C8 | Modificación, baja lógica y reactivación de plato | Mismo esquema que empleados | `/responsable/platos/{id}` |
| C9 | Alta de pedido | Registra un pedido con la fecha del día, el festival en curso, la unidad y los platos con su cantidad | `GET/POST /responsable/pedidos` |
| C10 | Inscripción de la unidad a un festival | Habilita la unidad en un festival que no terminó | `POST /responsable/festivales` |
| C11 | Detalle de un festival de la unidad | Pedidos del festival y recaudación de la unidad | `GET /responsable/festivales/{id}` |
| C12 | Reporte de recaudación de su unidad | Recaudación total del festival (`ReporteVentaDTO`) y la parte de su unidad | `GET /responsable/reportes` |
| C13 | Rentabilidad neta de su unidad | Ingresos, costos, sueldos, canon y resultado | `GET /responsable/reportes/rentabilidad` |
| C14 | Rentabilidad de su unidad entre dos fechas | Rango dentro del festival | `GET /responsable/reportes/rentabilidad?desde=&hasta=` |
| C15 | Plato estrella de su unidad | Plato con mayor cantidad pedida (`PlatoDTO`) | `GET /responsable/reportes/plato-estrella` |
| C16 | Liquidación de haberes de su personal | Sueldo de cada empleado de la unidad en un festival, con el detalle del cálculo y total de nómina | `GET /responsable/reportes/liquidacion` |

## D. Empleado

| CU | Nombre | Descripción | Vista / Endpoint |
|---|---|---|---|
| D1 | Ver su información | Sus datos, los festivales y las unidades en las que trabajó | `GET /empleado` |
| D2 | Festival activo y unidad actual | Indica si hoy tiene que trabajar, en qué festival y en qué unidad (o cuál es el próximo) | `GET /empleado` |
| D3 | Sueldo por festival | Liquidación por cada festival trabajado, con detalle del cálculo (base, adicional, factor por duración) | `GET /empleado?festival=` |
| D4 | Descarga del recibo de sueldo en PDF | Genera el recibo con OpenPDF | `GET /empleado/recibo?festival=` |
| D5 | **Cerrar caja** (solo Cajero) | Abre la caja del turno, calcula las ventas del día desde los pedidos, registra el monto contado y guarda la diferencia de arqueo | `GET /empleado/caja`, `POST /empleado/caja/abrir`, `POST /empleado/caja/cerrar` |

---

## Regla de negocio: liquidación de haberes

```
Hasta 15 días .......... 50% de (sueldo base + adicional)
De 16 a 30 días ........ 100% de (sueldo base + adicional)
Más de 30 días ......... (int(días / 15) + 1) × 50% × (sueldo base + adicional)
```

- **Cocinero**: adicional = plus por categoría (Cocinero / Ayudante / Lavaplatos).
- **Cajero**: adicional = años de antigüedad × monto por año.

Ejemplos verificados contra el enunciado:

| Caso | Cálculo | Total |
|---|---|---|
| 38 días, lavaplatos | `(int(38/15)+1) × 50% × (100.000 + 50.000)` | **$225.000** |
| 52 días, cajero con 3 años | `(int(52/15)+1) × 50% × (100.000 + 3 × 5.000)` | **$230.000** |

## Regla de negocio: canon

- **Food Truck**: `superficie × costoPorSuperficie` + `costoPorElectricidad` si requiere conexión.
- **Puesto Desarmable**: `superficie × costoPorSuperficie` − `costoPorMontaje × tiempoDeArmado`.

Los valores se toman de los costos del festival (`Costo`), nunca hardcodeados en el método.
