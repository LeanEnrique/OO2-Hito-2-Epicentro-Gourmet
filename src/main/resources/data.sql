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
(5, '2026-10-15', 2, 2);

INSERT IGNORE INTO item_plato (id, pedido_id, plato_id, cantidad, precio_unitario, costo_unitario) VALUES
(1, 1, 1, 2, 8500, 3500),
(2, 1, 2, 2, 4000, 1200),
(3, 2, 1, 1, 8500, 3500),
(4, 3, 1, 3, 8500, 3500),
(5, 3, 2, 1, 4000, 1200),
(6, 4, 3, 12, 1500, 600),
(7, 4, 4, 2, 7000, 2800),
(8, 5, 3, 6, 1500, 600);
