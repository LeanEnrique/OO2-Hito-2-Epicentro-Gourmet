# Epicentro Gourmet - Sistema de Gestión de Festivales

Trabajo Práctico Grupal - Orientación a Objetos II - Hito 2 (Spring Boot)

Aplicación web para gestionar los festivales del predio "Epicentro Gourmet": unidades de venta (Food Trucks y Puestos Desarmables), su personal, sus platos, los pedidos y los reportes económicos de cada festival.

## Grupo

**Grupo N°:** 8

| Integrante | Usuario de GitHub |
|---|---|
| Leandro Enrique | [LeanEnrique](https://github.com/LeanEnrique) |
| Santiago Saavedra | [SANTIAGO-SAAVEDRA](https://github.com/SANTIAGO-SAAVEDRA) |
| Yamil Miño | [akeboshi](https://github.com/akeboshi) |

## Diagrama de clases

[Ver diagrama de clases en Excalidraw](https://excalidraw.com/#json=0f9GxMyCLy8cORLukNdGN,AGoVLvNzy6u-gR297RGKSA)

Además, en el repositorio está el modelo documentado en [`docs/diagrama-clases.md`](docs/diagrama-clases.md), con el diagrama en Mermaid y las notas de modelado (por qué `Costo` es embebido, por qué `Persona` y `UnidadDeVenta` usan `SINGLE_TABLE`, etc.).

## Tecnologías

- Java 17
- Spring Boot (Web, Data JPA, Security, Validation, Mail)
- Thymeleaf
- MySQL 8
- Maven (incluido en el proyecto mediante el wrapper `mvnw`)
- OpenPDF (generación del recibo de sueldo)

## Cómo ejecutar el proyecto

### 1. Requisitos

Java 17 o superior y MySQL 8 o superior. Maven no hace falta instalarlo: el proyecto trae `mvnw`.

### 2. Clonar el repositorio

```bash
git clone https://github.com/LeanEnrique/OO2-Hito-2-Epicentro-Gourmet.git
cd OO2-Hito-2-Epicentro-Gourmet
```

### 3. Levantar MySQL

La aplicación se conecta con el usuario `root` y la contraseña `root`, y crea sola la base `epicentro_gourmet`, sus tablas y los datos de prueba.

- **Mac:**
  ```bash
  brew install mysql
  brew services start mysql
  mysql -u root -e "ALTER USER 'root'@'localhost' IDENTIFIED BY 'root';"
  ```
- **Windows:** instalar [MySQL Community Server](https://dev.mysql.com/downloads/installer/) y, cuando lo pida, poner `root` como contraseña del usuario `root`.

Si tu MySQL usa otra contraseña, definila en `DB_PASSWORD` (paso 4).

### 4. Configurar las variables de entorno (opcional)

Todas tienen un valor por defecto, así que solo hay que definir las que cambien:

| Variable | Por defecto | Para qué |
|---|---|---|
| `DB_USER` / `DB_PASSWORD` | `root` / `root` | Usuario y contraseña de MySQL |
| `DB_HOST` / `DB_PORT` / `DB_NAME` | `localhost` / `3306` / `epicentro_gourmet` | Servidor y base de MySQL |
| `MAIL_USERNAME` | (vacío) | Cuenta de Gmail que envía los mails |
| `MAIL_PASSWORD` | (vacío) | [Contraseña de aplicación](https://myaccount.google.com/apppasswords) de esa cuenta (no la contraseña normal) |

Con `MAIL_USERNAME` y `MAIL_PASSWORD`, al dar de alta un empleado le llega un mail con su usuario y contraseña. Sin ellas la aplicación funciona igual y muestra la contraseña del empleado en pantalla. Las credenciales nunca se suben al repositorio.

```bash
# Mac / Linux
export MAIL_USERNAME="cuenta.del.proyecto@gmail.com"
export MAIL_PASSWORD="codigodeaplicacion"
```

```powershell
# Windows (PowerShell)
$env:MAIL_USERNAME="cuenta.del.proyecto@gmail.com"
$env:MAIL_PASSWORD="codigodeaplicacion"
```

Desde el IDE, se cargan en la configuración de ejecución (*Environment variables*).

### 5. Ejecutar

En la misma terminal donde se definieron las variables:

```bash
./mvnw spring-boot:run        # Mac / Linux
.\mvnw.cmd spring-boot:run    # Windows
```

También se puede ejecutar la clase `EpicentroGourmetApplication` desde el IDE. Cuando en la consola aparece `Started EpicentroGourmetApplication`, entrar a **http://localhost:8080**.

### 6. Usuarios de prueba

Todos tienen la contraseña **`1234`**.

| Usuario | Rol | Datos |
|---|---|---|
| `admin` | Administrador | Ve todos los festivales y sus reportes |
| `carla` | Responsable | Food Truck "La Burguesía" |
| `diego` | Responsable | Puesto Desarmable "Empanadas del Norte" |
| `valentina` | Responsable | Food Truck "Pizza Rodante" (sin conexión eléctrica) |
| `jorge` | Responsable | Puesto Desarmable "Dulces de Campo" |
| `lucia@epicentro.com` | Cajera | Toma pedidos en La Burguesía; tiene historial de cajas |
| `tomas@epicentro.com` | Cajero | Toma pedidos en Pizza Rodante; tiene historial de cajas |
| `martin@epicentro.com` | Cocinero | Trabajó en La Burguesía y hoy trabaja en Empanadas del Norte |
| `sofia@epicentro.com` | Lavaplatos | Trabaja hoy en Empanadas del Norte |
| `camila@epicentro.com` | Cocinera ayudante | Trabaja hoy en Dulces de Campo |
| `nicolas@epicentro.com` | Lavaplatos | Sin festival hoy; su próximo festival es Verano |
| `pablo@epicentro.com` | Cajero | Dado de baja: no puede iniciar sesión |

Festivales cargados: Invierno (finalizado), Primavera (en curso, del 01/10/2026 al 30/11/2026) y Verano (próximo).

## Casos de uso implementados

El listado completo, con las reglas de negocio de liquidación de haberes y de canon, está en [`docs/casos-de-uso.md`](docs/casos-de-uso.md).

Cada caso de uso se desarrolló en su rama `CU<Funcionalidad><Integrante>` y se integró a `master` por Pull Request:

| # | Caso de uso | Actor | Responsable | Pull Request |
|---|---|---|---|---|
| 1 | Estructura base del proyecto, seguridad y login por roles | Todos | Leandro Enrique | [#1](https://github.com/LeanEnrique/OO2-Hito-2-Epicentro-Gourmet/pull/1), [#2](https://github.com/LeanEnrique/OO2-Hito-2-Epicentro-Gourmet/pull/2) |
| 2 | Página de inicio con el listado de festivales | Público | Leandro Enrique | [#3](https://github.com/LeanEnrique/OO2-Hito-2-Epicentro-Gourmet/pull/3) |
| 3 | Registro de unidad de venta con su responsable (+ mail de bienvenida) | Público | Leandro Enrique | [#4](https://github.com/LeanEnrique/OO2-Hito-2-Epicentro-Gourmet/pull/4) |
| 4 | Ver festivales y sus unidades de venta (panel de administración) | Administrador | Leandro Enrique | [#7](https://github.com/LeanEnrique/OO2-Hito-2-Epicentro-Gourmet/pull/7) |
| 5 | Alta de pedido del responsable para su unidad | Responsable | Leandro Enrique | [#5](https://github.com/LeanEnrique/OO2-Hito-2-Epicentro-Gourmet/pull/5) |
| 6 | Liquidación de haberes con detalle del cálculo | Empleado | Leandro Enrique | [#6](https://github.com/LeanEnrique/OO2-Hito-2-Epicentro-Gourmet/pull/6) |
| 7 | Descarga del recibo de sueldo en PDF | Empleado | Leandro Enrique | [#11](https://github.com/LeanEnrique/OO2-Hito-2-Epicentro-Gourmet/pull/11) |
| 8 | Ver festival y unidad actual en la que trabaja + historial | Empleado | Leandro Enrique | [#16](https://github.com/LeanEnrique/OO2-Hito-2-Epicentro-Gourmet/pull/16) |
| 9 | Ranking de unidades por recaudación | Administrador | Leandro Enrique | [#17](https://github.com/LeanEnrique/OO2-Hito-2-Epicentro-Gourmet/pull/17) |
| 10 | ABM de empleados y platos (baja lógica y reactivación) | Responsable | Santiago Saavedra | [#8](https://github.com/LeanEnrique/OO2-Hito-2-Epicentro-Gourmet/pull/8) |
| 11 | Cálculo de canon por unidad | Responsable | Santiago Saavedra | [#9](https://github.com/LeanEnrique/OO2-Hito-2-Epicentro-Gourmet/pull/9) |
| 12 | Unidades con mayor canon (`ReporteMayoresCanonDTO`) | Administrador | Santiago Saavedra | [#9](https://github.com/LeanEnrique/OO2-Hito-2-Epicentro-Gourmet/pull/9) |
| 13 | Reporte de recaudación de un festival (`ReporteVentaDTO`) | Administrador | Santiago Saavedra | [#13](https://github.com/LeanEnrique/OO2-Hito-2-Epicentro-Gourmet/pull/13) |
| 14 | Cálculo de rentabilidad neta | Administrador | Santiago Saavedra | [#14](https://github.com/LeanEnrique/OO2-Hito-2-Epicentro-Gourmet/pull/14) |
| 15 | Rentabilidad neta entre dos fechas | Administrador | Santiago Saavedra | [#14](https://github.com/LeanEnrique/OO2-Hito-2-Epicentro-Gourmet/pull/14) |
| 16 | Plato estrella (`PlatoDTO`) | Administrador | Santiago Saavedra | [#15](https://github.com/LeanEnrique/OO2-Hito-2-Epicentro-Gourmet/pull/15) |
| 17 | **Cerrar caja del día (Cajero)** | Empleado | Yamil Miño | [#21](https://github.com/LeanEnrique/OO2-Hito-2-Epicentro-Gourmet/pull/21) |
| 18 | **Liquidación de haberes del personal de la unidad** | Responsable | Yamil Miño | [#21](https://github.com/LeanEnrique/OO2-Hito-2-Epicentro-Gourmet/pull/21) |
| 19 | **Filtros combinados de empleados** (tipo, turno, categoría) | Responsable | Yamil Miño | [#21](https://github.com/LeanEnrique/OO2-Hito-2-Epicentro-Gourmet/pull/21) |

## Ajustes post defensa

| Integrante | Ajustes | Pull Request |
|---|---|---|
| Leandro Enrique | Pedidos tomados por el cajero con la caja abierta, caja con totales automáticos (abrir, cerrar, reanudar), liquidación de sueldo en una sección propia, platos sin nombres repetidos y reactivación de platos dados de baja, y correcciones en los reportes del responsable | [#22](https://github.com/LeanEnrique/OO2-Hito-2-Epicentro-Gourmet/pull/22) |
