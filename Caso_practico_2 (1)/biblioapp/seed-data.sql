-- BiblioApp - datos de ejemplo
-- Ejecutar DESPUES de arrancar la app al menos una vez (para que Hibernate cree las tablas)

INSERT INTO usuarios (username, password, nombre_completo, email, rol) VALUES
-- password real para los 3: "password123" (hash BCrypt)
('bibliotecaria1', '$2a$10$DZ3Bg2gYRl0eB2P1c8x0Fu1z1Y2E1P1O3G9k4c1Rj1XxvA9YV9O0e', 'Ana Bibliotecaria', 'ana.biblio@example.com', 'BIBLIOTECARIO'),
('lector1', '$2a$10$DZ3Bg2gYRl0eB2P1c8x0Fu1z1Y2E1P1O3G9k4c1Rj1XxvA9YV9O0e', 'Luis Lector', 'luis.lector@example.com', 'LECTOR'),
('lector2', '$2a$10$DZ3Bg2gYRl0eB2P1c8x0Fu1z1Y2E1P1O3G9k4c1Rj1XxvA9YV9O0e', 'Maria Lectora', 'maria.lectora@example.com', 'LECTOR');

INSERT INTO libros (titulo, autor, isbn, categoria, copias_totales, copias_disponibles) VALUES
('Cien anios de soledad', 'Gabriel Garcia Marquez', '978-0307350438', 'Ficcion', 3, 3),
('Clean Code', 'Robert C. Martin', '978-0132350884', 'Tecnico', 5, 5),
('El principito', 'Antoine de Saint-Exupery', '978-0156012195', 'Infantil', 4, 4),
('Effective Java', 'Joshua Bloch', '978-0134685991', 'Tecnico', 3, 3),
('1984', 'George Orwell', '978-0451524935', 'Ficcion', 4, 4),
('Sapiens', 'Yuval Noah Harari', '978-0062316097', 'Ensayo', 2, 2),
('Design Patterns', 'Erich Gamma et al.', '978-0201633610', 'Tecnico', 2, 2),
('La sombra del viento', 'Carlos Ruiz Zafon', '978-8408043645', 'Ficcion', 3, 3),
('Introduction to Algorithms', 'Cormen, Leiserson, Rivest, Stein', '978-0262033848', 'Tecnico', 2, 2),
('Rayuela', 'Julio Cortazar', '978-8437604572', 'Ficcion', 2, 2),
('Spring in Action', 'Craig Walls', '978-1617294945', 'Tecnico', 3, 3),
('Breve historia del tiempo', 'Stephen Hawking', '978-0553380163', 'Ensayo', 2, 2);

-- Nota: cuando crees la entidad Prestamo (Requisito 2), agrega aca
-- algunos INSERT de prestamos de ejemplo, incluyendo al menos uno
-- con fecha_limite en el pasado y fecha_devolucion NULL, para poder
-- probar la consulta prestamosAtrasados() del Requisito 5.
