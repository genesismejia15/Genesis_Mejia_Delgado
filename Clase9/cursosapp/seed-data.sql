-- ============================================================
-- Seed data - cursosapp (S9: asociaciones Curso <-> Profesor)
-- ============================================================
-- Ejecutar en MySQL Workbench DESPUES de:
--   1) Haber creado la BD cursoswebdb
--   2) Haber arrancado la app con las entidades Curso y Profesor
--      (Hibernate crea las tablas "profesores" y "cursos" automaticamente,
--       con la FK profesor_id en cursos)
-- ============================================================

USE cursoswebdb;

-- Si vienen de la version anterior (profesor como String), limpiar todo:
-- SET FOREIGN_KEY_CHECKS = 0;
-- TRUNCATE TABLE cursos;
-- TRUNCATE TABLE profesores;
-- SET FOREIGN_KEY_CHECKS = 1;

-- 1) Insertar profesores primero (Curso depende de Profesor)
INSERT INTO profesores (nombre, email, especialidad) VALUES
('Esteban Ortega', 'esteban.ortega@ufide.ac.cr', 'Desarrollo Web y Patrones'),
('Maria Elena Vargas', 'maria.vargas@ufide.ac.cr', 'Bases de Datos'),
('Carlos Solano', 'carlos.solano@ufide.ac.cr', 'Seguridad Informatica');

-- Verificar
SELECT * FROM profesores;

-- 2) Insertar cursos referenciando el id del profesor (profesor_id)
INSERT INTO cursos (nombre, descripcion, creditos, profesor_id) VALUES
('Fundamentos Web',
 'Introduccion a HTML5, CSS3 y arquitectura cliente/servidor.',
 3, 1),

('Spring Boot',
 'Backend con Java 25, MVC, Thymeleaf y Bootstrap.',
 4, 1),

('Bases de Datos',
 'MySQL, Workbench, JPA, Hibernate y modelo relacional.',
 4, 2),

('Patrones de Diseno',
 'MVC, Repository, Service, Inyeccion de Dependencias.',
 3, 1),

('Seguridad Web',
 'Autenticacion, autorizacion y buenas practicas con Spring Security.',
 4, 3),

('APIs REST',
 'Servicios JSON, consumo desde clientes, despliegue en la nube.',
 3, 1);

-- Verificar
SELECT c.id, c.nombre, c.creditos, p.nombre AS profesor
FROM cursos c
JOIN profesores p ON p.id = c.profesor_id;

-- ============================================================
-- Queries utiles para demostrar en clase (JPQL / SQL)
-- ============================================================

-- Ver el SQL real que genera el JOIN FETCH del CursoRepository:
-- (activar spring.jpa.show-sql=true y comparar contra esto)
-- SELECT c.*, p.* FROM cursos c JOIN profesores p ON p.id = c.profesor_id;

-- Cursos de un profesor especifico
-- SELECT * FROM cursos WHERE profesor_id = 1;

-- Contar cursos por profesor
-- SELECT p.nombre, COUNT(c.id) AS total_cursos
-- FROM profesores p LEFT JOIN cursos c ON c.profesor_id = p.id
-- GROUP BY p.nombre;

-- Borrar un curso (para demostrar deleteById)
-- DELETE FROM cursos WHERE id = 6;
