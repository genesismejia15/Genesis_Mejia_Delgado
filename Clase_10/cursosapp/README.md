# cursosapp — ProyectoBase S10 (punto de partida para el lab)

> Esta es la versión **sin resolver** — la que reciben los estudiantes (junto con `Para_Estudiantes/lab_clase10.md`). La versión completa (referencia para corregir) está en `Recursos_Profesor/ProyectoFinal/cursosapp`.

Parte de la base con las asociaciones de S9 ya aplicadas (`Curso` → `Profesor`). En esta clase se agrega **Spring Security**: login form, contraseñas con BCrypt, tabla `usuarios` y roles como String simple.

---

## Qué ya viene resuelto (de S9)

- CRUD completo de `Curso` con `Profesor` asociado (`@ManyToOne`)
- `CursoRepository.findAllConProfesor()` con `JOIN FETCH`

## Qué falta resolver en esta clase (S10)

Todo marcado con `CLASE 10 - PASO X.Y`, distribuido en:

| Archivo | Qué hay que hacer |
|---|---|
| `pom.xml` | PASO A.1 — descomentar las dependencias de Security |
| `entity/package-info.md` | PASO A.2 — crear `Usuario.java` |
| `repository/package-info.md` | PASO A.3 — crear `UsuarioRepository.java` |
| `security/package-info.md` | PASO B.1 — crear `CustomUserDetailsService.java` (paquete nuevo) |
| `config/package-info.md` | PASO C.1/C.2 — crear `SecurityConfig.java` (paquete nuevo) |
| `templates/login.html` | PASO D.1/D.2 — descomentar mensajes y formulario |
| `templates/fragments/header.html` | PASO E.1/E.2 — namespace `sec:` y navbar con usuario/logout |
| `seed-data.sql` | PASO F.1 — insertar usuarios de prueba con password BCrypt |

Ver el paso a paso completo en `Para_Estudiantes/lab_clase10.md`.

## Qué NO se resuelve en esta clase (a propósito, viene en S11)

- Restricción por rol (`@PreAuthorize`) — hoy cualquier usuario autenticado puede hacer todo el CRUD.
- Página 403 personalizada.

---

## Cómo correr (una vez resuelto el lab)

1. Tener MySQL Server corriendo en `localhost:3306`.
2. Crear la base de datos (si no existe):
   ```sql
   CREATE DATABASE cursoswebdb CHARACTER SET utf8mb4;
   ```
3. Definir la variable de entorno `MYSQL_PASSWORD` (o completarla directo en `application.properties`).
4. Arrancar la app.
5. Cargar `seed-data.sql` (con el bloque de usuarios ya descomentado).
6. Abrir <http://localhost:8080>

```bash
mvnw.cmd spring-boot:run      # Windows
./mvnw spring-boot:run        # Linux/Mac
```

---

## Si algo falla

- **App no arranca despues del PASO A.1:** revisar que las dos dependencias nuevas quedaron bien cerradas en el XML (sin comentarios `<!--` sueltos).
- **`Whitelabel Error Page` en `/login`:** falta descomentar `@GetMapping("/login")` en `HomeController.java` (PASO C.2) — `loginPage("/login")` en `SecurityConfig` solo indica la URL, no sirve la vista. También puede faltar `login.html`.
- **Redirige a login infinitamente:** revisar que `/login` esté en `.permitAll()`.
- **`Bad credentials`:** el hash del seed no es un BCrypt válido, o falta descomentar el INSERT de usuarios.
- **No aparece el usuario en el navbar:** falta el namespace `xmlns:sec` en `header.html`, o la dependencia `thymeleaf-extras-springsecurity6`.
