-- =============================================================
--  AppMuseo · Consultas para revisar la base en MySQL Workbench
--  Ejecuta cada bloque por separado con el rayo con cursor (Ctrl/Cmd + Enter)
--  Las tablas las crea Hibernate al arrancar cada microservicio.
-- =============================================================

-- 1) Las tres bases (una por microservicio)
SHOW DATABASES LIKE 'museo_%';


-- ---------------- ms-usuarios ----------------
USE museo_usuarios;
SHOW TABLES;

-- usuarios y su rol (la clave se guarda cifrada con BCrypt, nunca en texto plano)
SELECT id, nombre, correo, tipo_usuario, rol, LEFT(clave_hash, 20) AS clave_cifrada, fecha_registro
FROM usuarios
ORDER BY id;

-- cuántos usuarios hay por rol
SELECT rol, COUNT(*) AS cantidad FROM usuarios GROUP BY rol;


-- ---------------- ms-catalogo ----------------
USE museo_catalogo;
SHOW TABLES;

-- los 14 museos del mapa
SELECT id, nombre, pais, ciudad, tipo, latitud, longitud, calificacion
FROM museos
ORDER BY pais, nombre;

-- museos por país
SELECT pais, COUNT(*) AS museos FROM museos GROUP BY pais ORDER BY museos DESC;

-- productos con su stock (los cursos guardan los cupos en "stock")
SELECT id, nombre, categoria, precio, stock, peso_kg, codigo_barra
FROM productos
ORDER BY id;

-- productos con poco stock (alerta para el administrador)
SELECT nombre, stock FROM productos WHERE stock < 10 ORDER BY stock;


-- ---------------- ms-pedidos ----------------
USE museo_pedidos;
SHOW TABLES;

-- pedidos con totales, neto e IVA (los calcula el servidor)
SELECT id, correo_usuario, zona_envio, tipo_envio, subtotal, costo_envio, total, neto, iva, estado, fecha
FROM pedidos
ORDER BY fecha DESC;

-- detalle (líneas) de cada pedido: es la boleta
SELECT p.id AS pedido, l.nombre AS producto, l.cantidad, l.precio_unitario,
       l.cantidad * l.precio_unitario AS total_linea
FROM pedidos p
JOIN lineas_pedido l ON l.pedido_id = p.id
ORDER BY p.id, l.id;

-- ventas por estado
SELECT estado, COUNT(*) AS pedidos, SUM(total) AS monto
FROM pedidos
GROUP BY estado;

-- productos más vendidos
SELECT nombre, SUM(cantidad) AS unidades, SUM(cantidad * precio_unitario) AS monto
FROM lineas_pedido
GROUP BY nombre
ORDER BY unidades DESC;
