-- Datos de prueba de Epicentro Gourmet.
-- Se ejecuta automáticamente al levantar la app (INSERT IGNORE: si ya existen, no los duplica).
-- Todos los usuarios tienen la contraseña: 1234

-- Festivales (con sus costos)
INSERT IGNORE INTO festival (id, nombre, temporada, fecha_inicio, fecha_fin, por_superficie, por_montaje, por_electricidad, sueldo_base, por_antiguedad, plus_cocinero, plus_ayudante, plus_lavaplatos) VALUES
(1, 'Festival de Invierno', 'Invierno', '2026-07-01', '2026-07-20', 500, 10, 2000, 100000, 5000, 150000, 100000, 50000),
(2, 'Festival de Primavera', 'Primavera', '2026-10-01', '2026-11-30', 500, 10, 2000, 100000, 5000, 150000, 100000, 50000),
(3, 'Festival de Verano', 'Verano', '2027-01-10', '2027-01-20', 500, 10, 2000, 100000, 5000, 150000, 100000, 50000);

-- Personas: responsables (PERSONA) y empleados (CAJERO / COCINERO)
INSERT IGNORE INTO persona (id, tipo, nombre, apellido, dni, email, fecha_nacimiento, fecha_ingreso, activo, turno, especialidad, categoria) VALUES
(1, 'PERSONA', 'Carla', 'Gómez', '30111222', 'carla@epicentro.com', '1985-04-12', '2024-01-10', 1, NULL, NULL, NULL),
(2, 'PERSONA', 'Diego', 'Ruiz', '28333444', 'diego@epicentro.com', '1980-09-30', '2024-02-15', 1, NULL, NULL, NULL),
(3, 'CAJERO', 'Lucía', 'Fernández', '38555666', 'lucia@epicentro.com', '1995-06-20', '2023-03-01', 1, 'NOCHE', NULL, NULL),
(4, 'COCINERO', 'Martín', 'López', '35777888', 'martin@epicentro.com', '1990-11-05', '2020-05-10', 1, NULL, 'Parrilla', 'COCINERO'),
(5, 'COCINERO', 'Sofía', 'Díaz', '42999000', 'sofia@epicentro.com', '2000-01-25', '2025-02-01', 1, NULL, 'Bacha', 'LAVAPLATOS');

-- Usuarios (los empleados usan su email como usuario)
INSERT IGNORE INTO usuario (id, username, password, rol, persona_id, activo) VALUES
(1, 'admin', '$2a$10$XULjli2/6kcrQppnErMpV..sFRmXngoND6VaVGxHWhVQ3oRBKW9DC', 'ADMINISTRADOR', NULL, 1),
(2, 'carla', '$2a$10$XULjli2/6kcrQppnErMpV..sFRmXngoND6VaVGxHWhVQ3oRBKW9DC', 'RESPONSABLE', 1, 1),
(3, 'diego', '$2a$10$XULjli2/6kcrQppnErMpV..sFRmXngoND6VaVGxHWhVQ3oRBKW9DC', 'RESPONSABLE', 2, 1),
(4, 'lucia@epicentro.com', '$2a$10$XULjli2/6kcrQppnErMpV..sFRmXngoND6VaVGxHWhVQ3oRBKW9DC', 'EMPLEADO', 3, 1),
(5, 'martin@epicentro.com', '$2a$10$XULjli2/6kcrQppnErMpV..sFRmXngoND6VaVGxHWhVQ3oRBKW9DC', 'EMPLEADO', 4, 1),
(6, 'sofia@epicentro.com', '$2a$10$XULjli2/6kcrQppnErMpV..sFRmXngoND6VaVGxHWhVQ3oRBKW9DC', 'EMPLEADO', 5, 1);

-- Unidades de venta
INSERT IGNORE INTO unidad_de_venta (id, tipo, nombre_comercial, codigo, superficie, responsable_id, patente, conexion_electrica, cant_carpas, tiempo_de_armado) VALUES
(1, 'FOOD_TRUCK', 'La Burguesía', 'FT00000001', 12, 1, 'AB123CD', 1, NULL, NULL),
(2, 'PUESTO', 'Empanadas del Norte', 'PD00000002', 20, 2, NULL, NULL, 2, 45);

-- Unidades habilitadas en cada festival
INSERT IGNORE INTO festival_unidad (festival_id, unidad_id) VALUES
(1, 1), (2, 1), (2, 2), (3, 2);

-- Platos
INSERT IGNORE INTO plato (id, nombre, precio, costo, unidad_id, activo) VALUES
(1, 'Hamburguesa clásica', 8500, 3500, 1, 1),
(2, 'Papas fritas', 4000, 1200, 1, 1),
(3, 'Empanada de carne', 1500, 600, 2, 1),
(4, 'Locro', 7000, 2800, 2, 1);

-- Personal asignado a cada unidad en cada festival
INSERT IGNORE INTO asignacion_personal (id, persona_id, festival_id, unidad_id) VALUES
(1, 3, 1, 1),
(2, 3, 2, 1),
(3, 4, 1, 1),
(4, 4, 2, 2),
(5, 5, 2, 2);

-- Pedidos con sus items
INSERT IGNORE INTO pedido (id, fecha, festival_id, unidad_id) VALUES
(1, '2026-07-05', 1, 1),
(2, '2026-07-12', 1, 1),
(3, '2026-10-05', 2, 1),
(4, '2026-10-10', 2, 2),
(5, '2026-10-15', 2, 2),
(6, '2026-10-18', 2, 1),
(7, '2026-10-25', 2, 1),
(8, '2026-11-01', 2, 1),
(9, '2026-11-05', 2, 1),
(10, '2026-11-08', 2, 1);

INSERT IGNORE INTO item_plato (id, pedido_id, plato_id, cantidad, precio_unitario, costo_unitario) VALUES
(1, 1, 1, 2, 8500, 3500),
(2, 1, 2, 2, 4000, 1200),
(3, 2, 1, 1, 8500, 3500),
(4, 3, 1, 3, 8500, 3500),
(5, 3, 2, 1, 4000, 1200),
(6, 4, 3, 12, 1500, 600),
(7, 4, 4, 2, 7000, 2800),
(8, 5, 3, 6, 1500, 600),
(9, 6, 1, 10, 8500, 3500),
(10, 6, 2, 5, 4000, 1200),
(11, 7, 1, 12, 8500, 3500),
(12, 7, 2, 6, 4000, 1200),
(13, 8, 1, 10, 8500, 3500),
(14, 8, 2, 5, 4000, 1200),
(15, 9, 1, 8, 8500, 3500),
(16, 9, 2, 8, 4000, 1200),
(17, 10, 1, 10, 8500, 3500),
(18, 10, 2, 5, 4000, 1200);

-- ------------------------------------------------------------------
-- Datos adicionales: más unidades, personal, platos y pedidos para
-- que los reportes (ranking, mayor canon, rentabilidad, plato estrella)
-- tengan varias unidades para comparar.
-- ------------------------------------------------------------------

-- Responsables y empleados nuevos
-- Pablo (id 10) está dado de baja, para probar la reactivación
INSERT IGNORE INTO persona (id, tipo, nombre, apellido, dni, email, fecha_nacimiento, fecha_ingreso, activo, turno, especialidad, categoria) VALUES
(6, 'PERSONA', 'Valentina', 'Sosa', '33222111', 'valentina@epicentro.com', '1988-03-14', '2024-05-20', 1, NULL, NULL, NULL),
(7, 'PERSONA', 'Jorge', 'Medina', '27444555', 'jorge@epicentro.com', '1978-12-02', '2024-06-01', 1, NULL, NULL, NULL),
(8, 'CAJERO', 'Tomás', 'Romero', '40111333', 'tomas@epicentro.com', '1998-08-09', '2024-06-01', 1, 'MANANA', NULL, NULL),
(9, 'COCINERO', 'Camila', 'Torres', '41222444', 'camila@epicentro.com', '1999-02-17', '2023-09-15', 1, NULL, 'Pastelería', 'AYUDANTE'),
(10, 'CAJERO', 'Pablo', 'Gutiérrez', '36555777', 'pablo@epicentro.com', '1992-05-30', '2022-01-10', 0, 'NOCHE', NULL, NULL),
(11, 'COCINERO', 'Nicolás', 'Acosta', '43666888', 'nicolas@epicentro.com', '2001-10-21', '2025-08-01', 1, NULL, 'Bacha', 'LAVAPLATOS');

INSERT IGNORE INTO usuario (id, username, password, rol, persona_id, activo) VALUES
(7, 'valentina', '$2a$10$XULjli2/6kcrQppnErMpV..sFRmXngoND6VaVGxHWhVQ3oRBKW9DC', 'RESPONSABLE', 6, 1),
(8, 'jorge', '$2a$10$XULjli2/6kcrQppnErMpV..sFRmXngoND6VaVGxHWhVQ3oRBKW9DC', 'RESPONSABLE', 7, 1),
(9, 'tomas@epicentro.com', '$2a$10$XULjli2/6kcrQppnErMpV..sFRmXngoND6VaVGxHWhVQ3oRBKW9DC', 'EMPLEADO', 8, 1),
(10, 'camila@epicentro.com', '$2a$10$XULjli2/6kcrQppnErMpV..sFRmXngoND6VaVGxHWhVQ3oRBKW9DC', 'EMPLEADO', 9, 1),
(11, 'pablo@epicentro.com', '$2a$10$XULjli2/6kcrQppnErMpV..sFRmXngoND6VaVGxHWhVQ3oRBKW9DC', 'EMPLEADO', 10, 0),
(12, 'nicolas@epicentro.com', '$2a$10$XULjli2/6kcrQppnErMpV..sFRmXngoND6VaVGxHWhVQ3oRBKW9DC', 'EMPLEADO', 11, 1);

-- Unidades nuevas: un Food Truck sin conexión eléctrica y un Puesto Desarmable
INSERT IGNORE INTO unidad_de_venta (id, tipo, nombre_comercial, codigo, superficie, responsable_id, patente, conexion_electrica, cant_carpas, tiempo_de_armado) VALUES
(3, 'FOOD_TRUCK', 'Pizza Rodante', 'FT00000003', 10, 6, 'AC456EF', 0, NULL, NULL),
(4, 'PUESTO', 'Dulces de Campo', 'PD00000004', 15, 7, NULL, NULL, 3, 30);

-- Inscripciones nuevas: Primavera queda con 4 unidades, Invierno con 3 y Verano con 2
INSERT IGNORE INTO festival_unidad (festival_id, unidad_id) VALUES
(1, 2), (1, 4), (2, 3), (2, 4), (3, 1);

-- Platos nuevos (la Hamburguesa vegana está dada de baja)
INSERT IGNORE INTO plato (id, nombre, precio, costo, unidad_id, activo) VALUES
(5, 'Pizza muzzarella', 9000, 3200, 3, 1),
(6, 'Fainá', 2500, 700, 3, 1),
(7, 'Alfajor de maicena', 1200, 400, 4, 1),
(8, 'Churros (docena)', 3500, 1100, 4, 1),
(9, 'Hamburguesa vegana', 9000, 3800, 1, 0),
(10, 'Humita', 6500, 2500, 2, 1);

-- Asignaciones nuevas
-- Pablo trabajó en Invierno (aparece en "Dados de baja" de La Burguesía)
-- Nicolás está asignado a Verano (le aparece como próximo festival)
INSERT IGNORE INTO asignacion_personal (id, persona_id, festival_id, unidad_id) VALUES
(6, 8, 2, 3),
(7, 9, 2, 4),
(8, 10, 1, 1),
(9, 11, 3, 1),
(10, 5, 1, 2),
(11, 9, 1, 4);

-- Pedidos nuevos
INSERT IGNORE INTO pedido (id, fecha, festival_id, unidad_id) VALUES
(11, '2026-10-20', 2, 2),
(12, '2026-11-02', 2, 2),
(13, '2026-10-12', 2, 3),
(14, '2026-10-26', 2, 3),
(15, '2026-11-07', 2, 3),
(16, '2026-10-14', 2, 4),
(17, '2026-11-04', 2, 4),
(18, '2026-07-08', 1, 2),
(19, '2026-07-15', 1, 4),
(20, '2026-07-18', 1, 1);

INSERT IGNORE INTO item_plato (id, pedido_id, plato_id, cantidad, precio_unitario, costo_unitario) VALUES
(19, 11, 3, 24, 1500, 600),
(20, 11, 10, 6, 6500, 2500),
(21, 12, 3, 30, 1500, 600),
(22, 12, 4, 8, 7000, 2800),
(23, 13, 5, 6, 9000, 3200),
(24, 13, 6, 4, 2500, 700),
(25, 14, 5, 9, 9000, 3200),
(26, 14, 6, 6, 2500, 700),
(27, 15, 5, 7, 9000, 3200),
(28, 15, 6, 10, 2500, 700),
(29, 16, 7, 40, 1200, 400),
(30, 16, 8, 10, 3500, 1100),
(31, 17, 7, 25, 1200, 400),
(32, 17, 8, 14, 3500, 1100),
(33, 18, 3, 20, 1500, 600),
(34, 18, 4, 5, 7000, 2800),
(35, 19, 7, 30, 1200, 400),
(36, 19, 8, 8, 3500, 1100),
(37, 20, 1, 6, 8500, 3500),
(38, 20, 2, 4, 4000, 1200);
