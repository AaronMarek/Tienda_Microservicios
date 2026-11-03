-- WHERE NOT EXISTS para no duplicar en cada reinicio del servicio.
INSERT INTO products (nombre, descripcion, precio, stock, categoria)
SELECT 'Portátil Lenovo IdeaPad', 'Portátil 15 pulgadas con Intel Core i5, 8GB RAM y 512GB SSD', 699.99, 15, 'informatica'
WHERE NOT EXISTS (SELECT 1 FROM products WHERE nombre = 'Portátil Lenovo IdeaPad');

INSERT INTO products (nombre, descripcion, precio, stock, categoria)
SELECT 'Raton inalambrico Logitech', 'Raton ergonomico con bateria de larga duracion', 29.99, 50, 'perifericos'
WHERE NOT EXISTS (SELECT 1 FROM products WHERE nombre = 'Raton inalambrico Logitech');

INSERT INTO products (nombre, descripcion, precio, stock, categoria)
SELECT 'Monitor Samsung 24 pulgadas', 'Monitor Full HD con panel IPS y 75Hz', 189.99, 20, 'monitores'
WHERE NOT EXISTS (SELECT 1 FROM products WHERE nombre = 'Monitor Samsung 24 pulgadas');

INSERT INTO products (nombre, descripcion, precio, stock, categoria)
SELECT 'Teclado mecanico Keychron K2', 'Teclado compacto con switches Cherry MX Red', 119.99, 30, 'perifericos'
WHERE NOT EXISTS (SELECT 1 FROM products WHERE nombre = 'Teclado mecanico Keychron K2');

INSERT INTO products (nombre, descripcion, precio, stock, categoria)
SELECT 'SSD Samsung 1TB NVMe', 'Disco SSD M.2 con velocidad de lectura 3500 MB/s', 89.99, 40, 'almacenamiento'
WHERE NOT EXISTS (SELECT 1 FROM products WHERE nombre = 'SSD Samsung 1TB NVMe');