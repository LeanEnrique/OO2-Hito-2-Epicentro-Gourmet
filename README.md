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

### Paso 1: Instalar los requisitos

- **Java 17 o superior.** Para verificarlo: `java -version`.
- **MySQL 8 o superior.**
- **Maven no hace falta instalarlo:** el proyecto trae `mvnw`, que lo descarga solo la primera vez.

### Paso 2: Clonar el repositorio

```bash
git clone https://github.com/LeanEnrique/OO2-Hito-2-Epicentro-Gourmet.git
cd OO2-Hito-2-Epicentro-Gourmet
```

### Paso 3: Instalar y levantar MySQL

**Mac (con Homebrew)**

```bash
brew install mysql
brew services start mysql
mysql -u root -e "ALTER USER 'root'@'localhost' IDENTIFIED BY 'root';"
```

El último comando le pone la contraseña `root` al usuario `root`, que es la que usa la aplicación por defecto.

**Windows**

1. Descargar e instalar MySQL Community Server desde https://dev.mysql.com/downloads/installer/
2. Durante la instalación, cuando lo pida, definir la contraseña del usuario `root`. Si se usa `root` como contraseña, no hace falta configurar nada más para la base.
3. Verificar que el servicio "MySQL" esté iniciado (Servicios de Windows).

**No hace falta crear la base de datos ni las tablas:** al arrancar, la aplicación crea la base `epicentro_gourmet`, sus tablas y carga los datos de prueba.

### Paso 4: Configurar las variables de entorno

La aplicación lee su configuración de variables de entorno. Todas tienen un valor por defecto, así que solo hay que definir las que sean distintas.

| Variable | Valor por defecto | Para qué se usa |
|---|---|---|
| `DB_HOST` | `localhost` | Servidor de MySQL |
| `DB_PORT` | `3306` | Puerto de MySQL |
| `DB_NAME` | `epicentro_gourmet` | Nombre de la base de datos |
| `DB_USER` | `root` | Usuario de MySQL |
| `DB_PASSWORD` | `root` | Contraseña de MySQL |
| `MAIL_HOST` | `smtp.gmail.com` | Servidor de correo saliente |
| `MAIL_PORT` | `587` | Puerto del servidor de correo |
| `MAIL_USERNAME` | (vacío) | Cuenta de Gmail desde la que se envían los mails |
| `MAIL_PASSWORD` | (vacío) | Contraseña de aplicación de esa cuenta de Gmail |

**Envío de mails (opcional).** Al dar de alta un empleado se le envía un mail con su usuario y su contraseña. Para que funcione:

1. Entrar a https://myaccount.google.com con la cuenta de Gmail del proyecto.
2. En **Seguridad**, activar la **Verificación en 2 pasos**.
3. Buscar **Contraseñas de aplicaciones**, crear una y copiar el código de 16 letras sin espacios.
4. Usar ese código como `MAIL_PASSWORD` (no la contraseña normal de Gmail).

Sin estas variables la aplicación funciona igual: el empleado se crea, la pantalla avisa que no se pudo enviar el mail y **muestra la contraseña temporal generada** para no perder el alta.

Las credenciales nunca se suben al repositorio: se definen solo en la máquina donde se ejecuta.

### Paso 5: Ejecutar la aplicación

**Desde la terminal (Mac / Linux)**, en la carpeta del proyecto:

```bash
export MAIL_USERNAME="cuenta.del.proyecto@gmail.com"
export MAIL_PASSWORD="codigodeaplicacion"
./mvnw spring-boot:run
```

**Desde la terminal (Windows PowerShell)**, en la carpeta del proyecto:

```powershell
$env:MAIL_USERNAME="cuenta.del.proyecto@gmail.com"
$env:MAIL_PASSWORD="codigodeaplicacion"
.\mvnw.cmd spring-boot:run
```

Si la contraseña de MySQL no es `root`, agregar también `DB_PASSWORD` de la misma forma.

**Desde el IDE**

- **IntelliJ IDEA:** abrir la carpeta del proyecto, ir a *Run > Edit Configurations > Environment variables* y cargar `MAIL_USERNAME=...;MAIL_PASSWORD=...`. Ejecutar la clase `EpicentroGourmetApplication`.
- **Spring Tool Suite / Eclipse:** *File > Import > Existing Maven Projects*. En *Run Configurations > Environment* cargar las variables y ejecutar como *Spring Boot App*.
- **Visual Studio Code:** con el Extension Pack for Java instalado, ejecutar `EpicentroGourmetApplication` y cargar las variables en la sección `env` del `launch.json` (sin subir ese archivo al repositorio).

La aplicación está lista cuando en la consola aparece `Started EpicentroGourmetApplication`.

### Paso 6: Abrir la aplicación

Entrar a **http://localhost:8080**

### Paso 7: Iniciar sesión con los usuarios de prueba

Los datos de prueba están en `src/main/resources/data.sql` y se cargan solos en cada arranque, sin duplicarse. Todos los usuarios tienen la contraseña **`1234`**.

| Usuario | Rol | Datos |
|---|---|---|
| `admin` | Administrador | Ve todos los festivales y sus reportes |
| `carla` | Responsable | Food Truck "La Burguesía" |
| `diego` | Responsable | Puesto Desarmable "Empanadas del Norte" |
| `valentina` | Responsable | Food Truck "Pizza Rodante" (sin conexión eléctrica) |
| `jorge` | Responsable | Puesto Desarmable "Dulces de Campo" |
| `lucia@epicentro.com` | Empleado (Cajero) | Trabaja hoy en La Burguesía (Primavera) |
| `martin@epicentro.com` | Empleado (Cocinero) | Trabajó en La Burguesía y trabaja hoy en Empanadas del Norte |
| `sofia@epicentro.com` | Empleado (Lavaplatos) | Trabaja hoy en Empanadas del Norte |
| `tomas@epicentro.com` | Empleado (Cajero) | Trabaja hoy en Pizza Rodante |
| `camila@epicentro.com` | Empleado (Cocinero ayudante) | Trabaja hoy en Dulces de Campo |
| `nicolas@epicentro.com` | Empleado (Lavaplatos) | Sin festival hoy; su próximo festival es Verano |
| `pablo@epicentro.com` | Empleado (Cajero) | Dado de baja: no puede iniciar sesión hasta que lo reactiven |

Festivales cargados: Invierno (finalizado), Primavera (01/10/2026 al 30/11/2026, con 4 unidades) y Verano (próximo). Los pedidos solo se pueden cargar en un festival en curso.

## Casos de uso implementados

En el repositorio también está el listado en [`docs/casos-de-uso.md`](docs/casos-de-uso.md), con las reglas de negocio de liquidación de haberes y de canon.

Cada caso de uso se desarrolló en su rama `CU<Funcionalidad><Integrante>` y se integró a `main` por Pull Request:

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
