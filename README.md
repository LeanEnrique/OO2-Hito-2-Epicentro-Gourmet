# Epicentro Gourmet - Sistema de Gestión de Festivales

Trabajo Práctico Grupal - Orientación a Objetos II - Hito 2 (Spring Boot)

Aplicación web para gestionar los festivales del predio "Epicentro Gourmet": unidades de venta (Food Trucks y Puestos Desarmables), su personal, sus platos, los pedidos y los reportes económicos de cada festival.

## Grupo

**Grupo N°:** 8

| Integrante | Usuario de GitHub |
|---|---|
| Leandro Enrique | [LeanEnrique](https://github.com/LeanEnrique) |
| Santiago Saavedra | [SANTIAGO-SAAVEDRA](https://github.com/SANTIAGO-SAAVEDRA) |

## Diagrama de clases

[Ver diagrama de clases en Excalidraw](https://excalidraw.com/#json=0f9GxMyCLy8cORLukNdGN,AGoVLvNzy6u-gR297RGKSA)

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
| `MAIL_USERNAME` | (vacío) | Cuenta de Gmail desde la que se envían los mails |
| `MAIL_PASSWORD` | (vacío) | Contraseña de aplicación de esa cuenta de Gmail |

**Envío de mails (opcional).** Al dar de alta un empleado se le envía un mail con su usuario y su contraseña. Para que funcione:

1. Entrar a https://myaccount.google.com con la cuenta de Gmail del proyecto.
2. En **Seguridad**, activar la **Verificación en 2 pasos**.
3. Buscar **Contraseñas de aplicaciones**, crear una y copiar el código de 16 letras sin espacios.
4. Usar ese código como `MAIL_PASSWORD` (no la contraseña normal de Gmail).

Sin estas variables la aplicación funciona igual: el empleado se crea y la pantalla avisa que no se pudo enviar el mail.

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

El detalle de cada caso de uso (actor, precondiciones, flujo principal y validaciones) está en la [planilla de casos de uso](https://docs.google.com/spreadsheets/d/1S3U1pHMbsgvy2uycLjsaXvLY4sytavOzBSljxlQpTQw/edit?gid=1888066767#gid=1888066767).
