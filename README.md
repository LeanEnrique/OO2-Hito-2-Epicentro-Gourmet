# Epicentro Gourmet — TP OO2 · Hito 2

Sistema de gestión del centro de convenciones y predio ferial **Epicentro Gourmet**: festivales, unidades de
venta, personal, platos, pedidos, canon y reportes de rentabilidad.

Trabajo Práctico Grupal 2026 — Licenciatura en Sistemas, Orientación a Objetos II (UNLa).

---

## 1. Datos del grupo

- **Número de grupo:** _(completar)_
- **Repositorio:** https://github.com/haruita/OO2-Hito-2-Epicentro-Gourmet
- **Diagrama de clases:** [docs/diagrama-clases.md](docs/diagrama-clases.md)
- **Listado de casos de uso:** [docs/casos-de-uso.md](docs/casos-de-uso.md)

| Integrante | Usuario de GitHub |
|---|---|
| Leandro Enrique | [@LeanEnrique](https://github.com/LeanEnrique) |
| Santiago Saavedra | [@SANTIAGO-SAAVEDRA](https://github.com/SANTIAGO-SAAVEDRA) |
| Yamil Miño | [@akeboshi](https://github.com/akeboshi) |

---

## 2. Tecnologías

| Tecnología | Uso |
|---|---|
| Java 17 | Lenguaje |
| Spring Boot 4.1.1 | Framework (Spring MVC + Thymeleaf) |
| Spring Data JPA / Hibernate | Persistencia |
| Spring Security | Autenticación por roles y BCrypt |
| MySQL 8 | Base de datos |
| Maven | Gestión de dependencias |
| OpenPDF | Generación del recibo de sueldo en PDF |
| spring-boot-starter-mail | Envío de mails de alta |

---

## 3. Cómo correr la aplicación

### 3.1 Requisitos

- JDK 17 o superior (`java -version`)
- MySQL 8 en ejecución
- No hace falta instalar Maven: el proyecto trae el wrapper (`mvnw` / `mvnw.cmd`)

### 3.2 Base de datos

La aplicación crea la base si no existe (`createDatabaseIfNotExist=true`) y actualiza el esquema con
`spring.jpa.hibernate.ddl-auto=update`. Si preferís crearla a mano:

```sql
CREATE DATABASE epicentro_gourmet CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 3.3 Variables de entorno

Todas tienen un valor por defecto para desarrollo local; se pueden pisar sin tocar el código:

| Variable | Default | Descripción |
|---|---|---|
| `DB_HOST` | `localhost` | Host de MySQL |
| `DB_PORT` | `3306` | Puerto de MySQL |
| `DB_NAME` | `epicentro_gourmet` | Nombre de la base |
| `DB_USER` | `root` | Usuario |
| `DB_PASSWORD` | `root` | Contraseña |
| `MAIL_HOST` | `smtp.gmail.com` | Servidor SMTP |
| `MAIL_PORT` | `587` | Puerto SMTP |
| `MAIL_USERNAME` | `no-reply@epicentrogourmet.com` | Remitente |
| `MAIL_PASSWORD` | _(vacío)_ | Contraseña de aplicación del SMTP |

Ejemplo en PowerShell:

```powershell
$env:DB_USER="root"; $env:DB_PASSWORD="root"; $env:MAIL_USERNAME="tu@gmail.com"; $env:MAIL_PASSWORD="clave-de-aplicacion"
.\mvnw.cmd spring-boot:run
```

> **Sin SMTP configurado** la aplicación funciona igual: el alta de empleado se completa y la contraseña
> temporal se muestra en pantalla en lugar de perderse.

### 3.4 Ejecutar

```bash
./mvnw spring-boot:run        # Linux / macOS
.\mvnw.cmd spring-boot:run    # Windows
```

Luego abrir <http://localhost:8080>.

### 3.5 Datos de prueba

[`src/main/resources/data.sql`](src/main/resources/data.sql) corre automáticamente en cada arranque
(`spring.sql.init.mode=always` + `INSERT IGNORE`, así que no duplica) y carga:

- 3 festivales con todos sus costos (uno finalizado, uno **en curso hoy** y uno futuro)
- usuarios de los **tres roles**
- 2 unidades de venta con su responsable
- 6 platos y el personal asignado a cada unidad
- 5 pedidos con sus ítems (precio y costo históricos)

**Contraseña de todos los usuarios de prueba: `1234`**

| Usuario | Rol | Detalle |
|---|---|---|
| `admin` | Administrador | Usuario precargado |
| `carla` | Responsable | Food Truck "La Burguesía" (`FT00000001`) |
| `diego` | Responsable | Puesto "Empanadas del Norte" (`PD00000002`) |
| `lucia@epicentro.com` | Empleado | **Cajero**, turno noche |
| `martin@epicentro.com` | Empleado | Cocinero |
| `sofia@epicentro.com` | Empleado | Cocinero (lavaplatos) |

---

## 4. Casos de uso implementados y responsables

Cada caso de uso se desarrolló en su rama `CU<Funcionalidad><Integrante>` y se integró a `main` por Pull Request.

| # | Caso de uso | Actor | Responsable | Pull Request |
|---|---|---|---|---|
| 1 | Estructura base del proyecto, seguridad y login por roles | Todos | Leandro Enrique | [#1](https://github.com/haruita/OO2-Hito-2-Epicentro-Gourmet/pull/1) |
| 2 | Página de inicio con el listado de festivales | Público | Leandro Enrique | rama `CUPaginaInicioLeandroEnrique` |
| 3 | Registro de unidad de venta con su responsable (+ mail de bienvenida) | Público | Leandro Enrique | rama `CURegistroUnidadLeandroEnrique` |
| 4 | Ver festivales y sus unidades de venta (panel de administración) | Administrador | Leandro Enrique | [#7](https://github.com/haruita/OO2-Hito-2-Epicentro-Gourmet/pull/7) |
| 5 | Alta de pedido del responsable para su unidad | Responsable | Leandro Enrique | rama `CUAltaPedidoLeandroEnrique` |
| 6 | Liquidación de haberes con detalle del cálculo | Empleado | Leandro Enrique | rama `CULiquidacionSueldoLeandroEnrique` |
| 7 | Descarga del recibo de sueldo en PDF | Empleado | Leandro Enrique | [#11](https://github.com/haruita/OO2-Hito-2-Epicentro-Gourmet/pull/11) |
| 8 | Ver festival y unidad actual en la que trabaja + historial | Empleado | Leandro Enrique | [#16](https://github.com/haruita/OO2-Hito-2-Epicentro-Gourmet/pull/16) |
| 9 | Ranking de unidades por recaudación | Administrador | Leandro Enrique | [#17](https://github.com/haruita/OO2-Hito-2-Epicentro-Gourmet/pull/17) |
| 10 | ABM de empleados y platos (baja lógica y reactivación) | Responsable | Santiago Saavedra | [#8](https://github.com/haruita/OO2-Hito-2-Epicentro-Gourmet/pull/8) |
| 11 | Cálculo de canon por unidad | Responsable | Santiago Saavedra | [#9](https://github.com/haruita/OO2-Hito-2-Epicentro-Gourmet/pull/9) |
| 12 | Unidades con mayor canon (`ReporteMayoresCanonDTO`) | Administrador | Santiago Saavedra | [#9](https://github.com/haruita/OO2-Hito-2-Epicentro-Gourmet/pull/9) |
| 13 | Reporte de recaudación de un festival (`ReporteVentaDTO`) | Administrador | Santiago Saavedra | [#13](https://github.com/haruita/OO2-Hito-2-Epicentro-Gourmet/pull/13) |
| 14 | Cálculo de rentabilidad neta | Administrador | Santiago Saavedra | [#14](https://github.com/haruita/OO2-Hito-2-Epicentro-Gourmet/pull/14) |
| 15 | Rentabilidad neta entre dos fechas | Administrador | Santiago Saavedra | [#14](https://github.com/haruita/OO2-Hito-2-Epicentro-Gourmet/pull/14) |
| 16 | Plato estrella (`PlatoDTO`) | Administrador | Santiago Saavedra | [#15](https://github.com/haruita/OO2-Hito-2-Epicentro-Gourmet/pull/15) |
| 17 | **Cerrar caja del día (Cajero)** | Empleado | Yamil Miño | ver sección 5 |
| 18 | **Reportes del responsable sobre su unidad** (recaudación, rentabilidad, fechas, plato estrella) | Responsable | Yamil Miño | ver sección 5 |
| 19 | **Liquidación de haberes del personal de la unidad** | Responsable | Yamil Miño | ver sección 5 |
| 20 | **Filtros combinados de empleados** (tipo, turno, categoría) | Responsable | Yamil Miño | ver sección 5 |

---

## 5. Aportes de Yamil Miño (@akeboshi)

Rama: **`CUCerrarCajaAkeboshi`**

### Casos de uso nuevos

**17. Cerrar Caja (Cajero)** — el único caso de uso del enunciado que faltaba.
El cajero abre su caja al comenzar el turno y la cierra al finalizar el día. El sistema calcula las
**ventas del día a partir de los pedidos cargados** por el responsable (no se tipean a mano), compara con
el monto declarado y guarda la **diferencia de arqueo**.

- Entidades: [`Caja`](src/main/java/unla/epicentro/entities/Caja.java), [`EstadoCaja`](src/main/java/unla/epicentro/entities/EstadoCaja.java)
- Repositorio: [`CajaRepository`](src/main/java/unla/epicentro/repositories/CajaRepository.java)
- Servicio: [`CajaService`](src/main/java/unla/epicentro/services/CajaService.java)
- Endpoints: `GET /empleado/caja`, `POST /empleado/caja/abrir`, `POST /empleado/caja/cerrar`
- Vista: [`empleado/caja.html`](src/main/resources/templates/empleado/caja.html)

**18. Reportes del responsable sobre su unidad** — el enunciado asigna estos reportes al *Responsable*,
pero solo estaban expuestos al administrador. Se agregaron rutas propias que **reusan los servicios
existentes** y quedan siempre acotadas a la unidad del responsable autenticado (no puede consultar otra):

- `GET /responsable/reportes` → Reporte de recaudación ([`ReporteVentaDTO`](src/main/java/unla/epicentro/dtos/ReporteVentaDTO.java)) + recaudación de su unidad
- `GET /responsable/reportes/rentabilidad` → rentabilidad neta y entre dos fechas
- `GET /responsable/reportes/plato-estrella` → plato estrella ([`PlatoDTO`](src/main/java/unla/epicentro/dtos/PlatoDTO.java))
- Controlador: [`ReporteResponsableController`](src/main/java/unla/epicentro/controllers/ReporteResponsableController.java)

**19. Liquidación de haberes del personal** — el responsable puede calcular y ver el sueldo de **todo su
personal** en un festival (antes solo el empleado veía el propio). Ver
[`LiquidacionService.liquidarPersonal`](src/main/java/unla/epicentro/services/LiquidacionService.java).

**20. Filtros combinados de empleados** — filtro por **tipo** (cajero/cocinero), **turno** y **categoría**,
combinables con la búsqueda libre, trayendo únicamente las entidades **no dadas de baja lógicamente**.
Ver [`EmpleadoService.buscar`](src/main/java/unla/epicentro/services/EmpleadoService.java).

### Validaciones de carga agregadas

1. **Período del festival válido** (`fechaFin` no puede ser anterior a `fechaInicio`) —
   [`Festival.isPeriodoValido()`](src/main/java/unla/epicentro/entities/Festival.java), aplicada en
   [`FestivalService`](src/main/java/unla/epicentro/services/FestivalService.java) y
   [`PedidoService`](src/main/java/unla/epicentro/services/PedidoService.java).
2. **El empleado solo se asigna a un festival donde su unidad está habilitada** (antes el formulario listaba
   todos los festivales y permitía dejar datos inconsistentes).
3. **El canon no puede quedar negativo** (el descuento por montaje no puede superar el costo por superficie) —
   [`CanonService`](src/main/java/unla/epicentro/services/CanonService.java).

### Bugs corregidos

- El alta de empleado informaba *"se enviaron los datos de acceso por email"* aunque el SMTP fallara, y la
  contraseña generada se perdía. Ahora se muestra la contraseña temporal en pantalla cuando el mail no sale.
- El formulario de alta de empleado ofrecía festivales en los que la unidad no participaba.
- Se agregaron páginas de error propias para **403** y **404** (antes aparecía la pantalla Whitelabel).

---

## 6. Decisiones de diseño

- **Sin datos hardcodeados en los métodos**: los montos (sueldo base, plus por categoría, antigüedad, costo
  por superficie/montaje/electricidad) están encapsulados en la clase [`Costo`](src/main/java/unla/epicentro/entities/Costo.java),
  embebida en cada `Festival`. Cambiar los valores de un festival no toca el código.
- **Herencia `SINGLE_TABLE`**: `UnidadDeVenta` → `FoodTruck` / `PuestoDesarmable`, y `Persona` → `Cajero` /
  `Cocinero`. Cada subtipo resuelve su propio cálculo de canon o de sueldo (polimorfismo).
- **Código de unidad validado por tipo**: [`UnidadDeVenta.tieneCodigoValido()`](src/main/java/unla/epicentro/entities/UnidadDeVenta.java)
  exige `FT` + 8 dígitos para Food Truck y `PD` + 8 dígitos para Puesto Desarmable (10 caracteres).
- **`ItemPlato` guarda precio y costo del momento**: si después cambia el precio del plato, los pedidos viejos
  no se recalculan y los reportes históricos siguen siendo correctos.
- **Baja lógica**: personas y platos tienen `activo`; se dan de baja sin borrar y se reactivan con un método
  específico, que es lo que pide el enunciado.
- **DTOs de reporte no persistentes**: `ReporteVentaDTO`, `RankingUnidadDTO`, `ReporteMayoresCanonDTO`,
  `ReporteRentabilidadDTO`, `PlatoDTO`, `LiquidacionDTO`, `DetalleLiquidacionDTO`.

---

## 7. Flujos para probar en la defensa

1. **Login y roles** — entrar con `admin`, `carla` y `lucia@epicentro.com` (contraseña `1234`) y ver que cada
   uno cae en un panel distinto.
2. **Registro de responsable con unidad** — `/registro`, crear un Food Truck con código `FT00000009` y entrar
   con el usuario creado.
3. **Alta de empleado** — como `carla`: *Empleados → Nuevo empleado*, asignar al "Festival de Primavera"
   (en curso). Se genera usuario y contraseña, y se envía el mail.
4. **Alta de plato** — como `carla`: *Platos → Nuevo plato*.
5. **Alta de pedido** — como `carla`: entrar al Festival de Primavera → *Nuevo pedido*.
6. **Cerrar caja** — como `lucia@epicentro.com`: *Cerrar caja* → abrir con monto inicial → cerrar con el monto
   contado. La pantalla muestra las ventas del día (el pedido del paso 5) y la diferencia de arqueo.
7. **Reportes del responsable** — como `carla`: *Reportes* → recaudación, rentabilidad, plato estrella y
   liquidación de haberes de su personal.
8. **Reportes del administrador** — como `admin`: ranking de unidades, unidades con mayor canon, rentabilidad
   neta y plato estrella.
9. **Recibo de sueldo en PDF** — como `martin@epicentro.com`: *Mi panel* → *Descargar recibo PDF*.
