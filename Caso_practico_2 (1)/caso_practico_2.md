# Caso Práctico #2 — BiblioApp

## Información general

| Dato | Valor |
|------|-------|
| Curso | SC-403 Desarrollo de Aplicaciones Web y Patrones |
| Universidad | Fidélitas |
| Modalidad | Individual, **100% asincrónico** (sin sesión de clase dedicada) |
| Valor | **15%** de la nota final |
| Se presenta | Al final de la clase de Semana 13 |
| Fecha de entrega | Mediados de Semana 15 (mismo día que el artículo IEEE — fecha exacta confirmada en Moodle) |
| Material entregado | `biblioapp/` (proyecto base) + este documento |

---

## Contexto del caso

Sos parte de un equipo que desarrolla **BiblioApp**, un sistema interno para gestionar el préstamo de libros de una biblioteca. La versión actual del proyecto solo permite **ver el catálogo de libros** y **loguearse** — no distingue roles ni presta libros todavía. Te toca completarlo.

El proyecto base ya tiene:

- Entidad `Libro` con todos los campos.
- Repositorio, `Service` y `Controller` con el catálogo (`GET /libros` y `GET /libros/{id}`) funcionando.
- Entidad `Usuario` con login personalizado (BCrypt) ya funcionando — cualquier usuario logueado puede ver todo, todavía no hay roles que restrinjan nada.
- Vistas Thymeleaf con Bootstrap (catálogo + detalle + login).
- `seed-data.sql` con 12 libros y 3 usuarios de ejemplo (1 bibliotecario, 2 lectores).

Tu trabajo es **agregar el préstamo de libros, los roles, una API REST y una consulta avanzada** aplicando todo lo aprendido desde la Semana 9 hasta la Semana 13.

Este caso cierra el temario del curso: cubre asociaciones JPA (S9), seguridad y roles (S10-S11), constantes de rol, API REST y JWT (S12), y deja el deployment como algo opcional de puntos extra (S13).

---

## Lo que tenés que entregar

### 1. Implementación funcional

Debe cumplir todos los **requisitos obligatorios** (ver siguiente sección).

### 2. Código fuente subido al repositorio personal

En tu repo personal del curso, en una carpeta `Caso_Practico_2/biblioapp/`.

### 3. Capturas de pantalla (carpeta `Caso_Practico_2/evidencia/`)

Mínimo:

- `01-catalogo.png` — catálogo de libros con disponibilidad visible.
- `02-prestamo.png` — formulario o pantalla de registrar un préstamo (rol bibliotecario).
- `03-403.png` — un lector intentando acceder a una función de bibliotecario y viendo la página de acceso denegado.
- `04-postman-login.png` — request de login en Postman devolviendo el token o la sesión.
- `05-postman-api.png` — request a la API de libros en Postman con la respuesta JSON visible.
- `06-atrasados.png` — resultado de la consulta JPQL de préstamos atrasados (Requisito 5.3).

### 4. Colección de Postman (`Caso_Practico_2/biblioapp.postman_collection.json`)

Con al menos las requests usadas para las capturas 04 y 05.

### 5. README de lógica (`Caso_Practico_2/SOLUCION.md`)

Documento corto (1 a 2 páginas) explicando con tus palabras:

- Cómo modelaste la relación `Prestamo → Libro` y por qué.
- Qué hace cada `@PreAuthorize` que agregaste y por qué esa regla y no otra.
- Cómo escribiste tu propia consulta JPQL del Requisito 5.3 (explicá el razonamiento, no solo pegues el código).
- Qué endpoints de tu API implementaste y qué código de estado devuelve cada uno.
- Cualquier decisión técnica adicional (ej. cómo calculaste la fecha límite del préstamo).

**No copies código aquí. Explicalo en prosa.** Si en la calificación parece autogenerado, vale 0.

---

## Requisitos obligatorios

### R1. Configuración inicial

- [ ] Copiar `biblioapp/` dentro de tu repo personal en `Caso_Practico_2/`.
- [ ] Crear la base `biblioappdb` y configurar `DB_PASSWORD`.
- [ ] Verificar que la app arranca y que `/libros` muestra el catálogo con los 12 libros de `seed-data.sql`.
- [ ] Verificar que podés loguearte con los 3 usuarios de ejemplo (ver `README.md` del proyecto base para las credenciales).

### R2. La entidad `Prestamo` y su asociación con `Libro` (Semana 9)

- [ ] Crear la entidad `Prestamo` desde cero, con al menos estos campos: `id`, `libro` (relación `@ManyToOne` hacia `Libro`), `usuario` (relación `@ManyToOne` hacia `Usuario`, quien pidió el libro), `fechaPrestamo` (`LocalDate`), `fechaLimite` (`LocalDate`, 14 días después de `fechaPrestamo`), `fechaDevolucion` (`LocalDate`, nulo mientras el libro no se devuelve).
- [ ] Crear `PrestamoRepository`, `PrestamoService` y `PrestamoController` (vistas HTML) siguiendo la arquitectura por capas del curso.
- [ ] Al registrar un préstamo, descontar una unidad de `copiasDisponibles` en el `Libro` correspondiente; al registrar una devolución, sumarla de vuelta.
- [ ] Las vistas de préstamo deben mostrar los datos del libro (`prestamo.getLibro().getTitulo()`, no solo un ID) — igual que hiciste con `Curso`/`Profesor` en la Semana 9.

> **Documentación oficial:**
> - `@ManyToOne`: https://docs.jboss.org/hibernate/orm/6.4/userguide/html_single/Hibernate_User_Guide.html#associations-many-to-one
> - Evitar el problema N+1 con `JOIN FETCH` si listás préstamos con su libro: ya lo viste en la Semana 9.

### R3. Roles y seguridad (Semanas 10-11)

- [ ] Definir dos roles: `BIBLIOTECARIO` y `LECTOR` (los usuarios de `seed-data.sql` ya vienen con estos roles asignados).
- [ ] Crear un `enum Rol { BIBLIOTECARIO, LECTOR }` como tabla de constantes — no uses los strings `"BIBLIOTECARIO"`/`"LECTOR"` sueltos en tu código Java, validá contra el enum (mismo patrón que viste en la Semana 12).
- [ ] Restringir con `@PreAuthorize` al menos estas 3 operaciones:
  - Crear/editar/eliminar un `Libro` → solo `BIBLIOTECARIO`.
  - Registrar un préstamo o marcar una devolución → solo `BIBLIOTECARIO`.
  - Ver el listado completo de todos los préstamos (de todos los usuarios) → solo `BIBLIOTECARIO`. Un `LECTOR` solo puede ver sus propios préstamos.
- [ ] Configurar una página de acceso denegado (403) personalizada, igual que en la Semana 11.

> **Pista para "ver solo mis préstamos" (LECTOR):** ya usaste una pieza de este patrón en la Semana 12, en `UsuarioController.eliminar()`: `Authentication auth` se puede inyectar directo como parámetro de un método de `@Controller`, y `auth.getName()` te da el username de quien está logueado en ese momento. Acá el paso extra es buscar el `Usuario` completo a partir de ese username (`usuarioService.buscarPorUsername(...)`) y escribir en tu propio `PrestamoRepository` un método `List<Prestamo> findByUsuario(Usuario usuario)` — con ese nombre exacto, Spring Data JPA lo implementa solo a partir de la firma, sin que tengas que escribir `@Query`.

> **Documentación oficial:**
> - `@PreAuthorize`: https://docs.spring.io/spring-security/reference/servlet/authorization/method-security.html

### R4. API REST (Semana 12)

- [ ] Crear `LibroRestController` con:
  - `GET /api/libros` — catálogo completo en JSON, **público** (no requiere login).
  - `GET /api/libros/{id}` — un libro por id, `ResponseEntity` con `404` si no existe.
  - `POST /api/libros` — crear un libro, `@Valid @RequestBody`, solo `BIBLIOTECARIO`, devuelve `201 Created`.
- [ ] Crear `PrestamoRestController` con:
  - `GET /api/prestamos/atrasados` — usa la consulta del Requisito 5.3 (ver abajo), devuelve `200` con la lista en JSON.

  (Registrar un préstamo vía API, `POST /api/prestamos`, ya NO es obligatorio — quedó como bonus chico, ver sección de puntos extra. El registro de préstamos vía HTML del Requisito 2 ya cubre esa lógica de negocio; volver a escribirla en la API es trabajo repetido sin aprendizaje nuevo, así que se movió a opcional.)
- [ ] Los endpoints de escritura deben devolver `400 Bad Request` si el JSON no cumple las validaciones de `@Valid`.
- [ ] Documentar y probar todos los endpoints anteriores con Postman (ver Requisito 6).

> **Documentación oficial:**
> - `@RestController`, `@RequestBody`, `ResponseEntity`: ya vistos en la Semana 12.

### R5. Consultas JPQL avanzadas (cierre de Unidad 3)

En la Semana 9 vimos `JOIN FETCH` para resolver el problema N+1. Existen técnicas de JPQL más allá de eso — agregaciones, exclusión de registros, proyecciones a DTO. Acá tenés **dos ejemplos ya resueltos** para que estudies el patrón, y **una tercera consulta que tenés que escribir vos**, porque la va a usar tu propia API (Requisito 4).

**Los Ejemplos 1 y 2 son material de estudio, no entregables separados.** No hay checkbox ni criterio de rúbrica que los pida a ellos solos — están para que veas el patrón antes de escribir el Ejemplo 3 vos mismo. Copiarlos e integrarlos es opcional (el bonus chico de la sección de puntos extra premia hacerlo con Ejemplo 1). Lo único obligatorio de este requisito es el Ejemplo 3.

#### Ejemplo 1 (dado) — Ranking de libros más prestados

Usa `GROUP BY` + `COUNT` + una proyección a DTO (en vez de traer la entidad completa, arma un objeto liviano solo con lo que hace falta mostrar):

```java
// dto/LibroRankingDTO.java
public class LibroRankingDTO {
    private String titulo;
    private Long totalPrestamos;
    public LibroRankingDTO(String titulo, Long totalPrestamos) {
        this.titulo = titulo;
        this.totalPrestamos = totalPrestamos;
    }
    // getters
}

// PrestamoRepository.java
@Query("SELECT new com.ufide.biblioapp.dto.LibroRankingDTO(p.libro.titulo, COUNT(p)) " +
       "FROM Prestamo p GROUP BY p.libro.titulo ORDER BY COUNT(p) DESC")
List<LibroRankingDTO> librosMasPrestados();
```

Esta consulta va en `PrestamoRepository` (repositorio que vos vas a crear junto con la entidad `Prestamo`, Requisito 2) — el código de arriba está completo, solo copialo tal cual junto con el DTO, **ajustando el paquete de la constructor-expression al tuyo** (`com.ufide.biblioapp.dto.LibroRankingDTO` si copiaste el proyecto base sin cambiarle el nombre — revisá el `package` de tu propio `DTO` si no coincide). Opcionalmente podés exponerla en una vista `/libros/ranking` (ver bonus chico).

#### Ejemplo 2 (dado) — Libros que nunca se prestaron

Usa un `LEFT JOIN` con condición `ON` para encontrar registros de una tabla que **no tienen relación** en la otra — una técnica distinta a `JOIN FETCH` (que resuelve el problema opuesto: traer datos relacionados que SÍ existen):

```java
@Query("SELECT l FROM Libro l LEFT JOIN Prestamo p ON p.libro = l WHERE p.id IS NULL")
List<Libro> librosNuncaPrestados();
```

Esta consulta va en `LibroRepository` (ya existe en el proyecto base) — el código de arriba está completo, solo copialo tal cual.

#### Ejemplo 3 (a completar por vos) — Préstamos atrasados

Un préstamo está atrasado si **no se ha devuelto** (`fechaDevolucion IS NULL`) y **ya pasó la fecha límite** (`fechaLimite` es anterior a hoy). Escribí vos la consulta JPQL siguiendo el patrón de los dos ejemplos anteriores:

```java
// PrestamoRepository.java — completar
@Query("...")
List<Prestamo> prestamosAtrasados();
```

- [ ] Escribir la consulta `prestamosAtrasados()` en `PrestamoRepository`.
- [ ] Conectarla al endpoint `GET /api/prestamos/atrasados` del Requisito 4.
- [ ] Mostrarla también en una vista HTML para el bibliotecario (ej. una sección "Préstamos atrasados" en el dashboard, resaltada en rojo).

> **Pista:** `CURRENT_DATE` es una función JPQL que devuelve la fecha de hoy, útil para comparar contra `fechaLimite`.
>
> **Documentación oficial:** https://docs.spring.io/spring-data/jpa/reference/jpa/query-methods.html#jpa.query-methods.at-query

### R6. Postman

- [ ] Armar una colección de Postman con al menos: login (si implementaste JWT, guardar el token; si no, cookie de sesión), `GET /api/libros`, `GET /api/libros/{id}` (caso 200 y caso 404), `POST /api/libros` (caso bibliotecario 201, caso lector 403), `GET /api/prestamos/atrasados`.
- [ ] Exportar la colección como `.json` y subirla junto con el código.

### R7. Workflow Git

- [ ] Mínimo 3 commits con mensajes convencionales (`feat:`, `fix:`, `docs:`, etc.).
- [ ] Push final al repo personal con la estructura `Caso_Practico_2/`.

---

## Rúbrica de evaluación (15%)

La nota se asigna sobre 100 y se multiplica por 0.15 para el porcentaje final.

| # | Criterio | Peso |
|---|----------|:--:|
| 1 | App arranca sin errores, BD configurada, catálogo base funcionando | 5 |
| 2 | Entidad `Prestamo` con asociación `@ManyToOne` a `Libro` y `Usuario`, funcional en vistas (R2) | 15 |
| 3 | Enum `Rol` como constante, sin strings sueltos (R3) | 5 |
| 4 | `@PreAuthorize` en las 3 operaciones indicadas + página 403 (R3) | 15 |
| 5 | API REST: endpoints de `Libro` con `@Valid`/`ResponseEntity` correctos (R4) | 15 |
| 6 | API REST + JPQL: `GET /api/prestamos/atrasados` con la consulta `prestamosAtrasados()` correcta, conectada también a una vista (R4+R5) | 15 |
| 7 | Colección de Postman con las pruebas indicadas (R6) | 10 |
| 8 | Código organizado en capas MVC | 10 |
| 9 | Workflow Git (R7) | 5 |
| 10 | `SOLUCION.md` completo y coherente con el código entregado | 5 |

**Nota (2026-08-04):** esta rúbrica se simplificó respecto a una versión anterior con 11 criterios — se fusionaron los dos que evaluaban por separado el endpoint de `Prestamo` y su consulta JPQL (eran, en la práctica, la misma pieza de código) en uno solo, y se subió el peso de "código organizado en capas" para compensar. El total sigue sumando 100.

---

## Puntos extra (bonus)

> **Regla de habilitación:** los bonus **solo se evalúan si completaste al menos el 65% de la nota obligatoria** (al menos 65 puntos sumando los criterios 1 a 10). Si cumplís el umbral, los bonus **sí pueden superar los 100 puntos**.

### Bonus +10 — Autenticación JWT sobre la API

Agregar un `POST /api/auth/login` que devuelva un JWT, y proteger los endpoints de escritura de la API con ese token en vez (o además) de la sesión — mismo patrón que viste en la Semana 12 (`JwtService`, `JwtAuthFilter`, `@PreAuthorize` compartido entre sesión y JWT).

### Bonus +5 — CRUD completo de Usuarios

Pantalla de administración (solo `BIBLIOTECARIO`) para crear, editar y eliminar usuarios lectores, con el mismo patrón de validación de rol que viste en la Semana 11-12.

### Bonus +5 — Recuperación de contraseña por correo

Flujo de "olvidé mi contraseña" con Spring Mail y token de un solo uso, igual que en la Semana 11.

### Bonus +10 — Deployment real (opcional)

Desplegar BiblioApp en Render.com con base de datos MySQL en Aiven, con URL pública funcionando (catálogo y login accesibles desde internet). Adjuntar la URL en `SOLUCION.md`. Este bonus es completamente opcional — no afecta la nota si no lo hacés, y no se espera que todos lo completen dado el tiempo disponible.

### Bonus chicos

- **+3** `POST /api/prestamos` — registrar un préstamo vía API (solo `BIBLIOTECARIO`, `@Valid @RequestBody`, `201 Created`). Era obligatorio en versiones anteriores del enunciado; ahora es opcional porque duplica la lógica de negocio que ya escribiste en el Requisito 2 — hacelo si querés practicar una vez más el patrón de la Semana 12, no cambia mucho tu nota si lo salteás.
- **+3** Ranking de libros más prestados (Ejemplo 1) expuesto también como vista HTML con Bootstrap (barras o tabla ordenada).
- **+3** Endpoint `GET /api/libros/categoria/{categoria}` filtrando el catálogo por categoría.
- **+2** Badge visual "Sin copias disponibles" en el catálogo cuando `copiasDisponibles == 0`.

---

## Penalizaciones automáticas

| Falta | Penalidad |
|-------|-----------|
| Subir contraseña de MySQL o clave JWT al repo | Nota máxima 60 |
| App no arranca | Los criterios 2-8 se evalúan en 0 |
| Plagio detectable entre estudiantes | **Nota 0 a ambas partes** y reporte a la dirección |
| Código generado con IA sin entender la lógica (detectable por inconsistencias en `SOLUCION.md`) | Nota máxima 50 |
| Entrega tardía sin justificación | Según reglamento del curso |

---

## Política anti-plagio y uso de IA

Este caso práctico es un **examen individual**. La nota refleja **lo que vos sabés hacer**, no lo que otra persona o herramienta hace por vos.

**No está permitido:**

- Copiar código de otro estudiante.
- Compartir tu código con otro estudiante durante el examen.
- **Usar modelos generativos de IA (ChatGPT, Claude, Copilot, etc.) para resolver el examen completo.** Si los usás como apoyo puntual de consulta, tenés que poder explicar la lógica final en `SOLUCION.md` — especialmente la consulta JPQL del Requisito 5.3, que es la parte que más se presta a copiar sin entender.
- Pegar el enunciado completo en un modelo y entregar la salida.

**Sí está permitido:**

- Consultar la documentación oficial (Spring, Spring Security, Thymeleaf, Bootstrap).
- Consultar tus notas de clase, tus prácticas y casos anteriores, y los proyectos vistos (`cursosapp`, `eventapp`, `tiendaapp`).
- Consultar el material del curso (presentaciones, guiones, lecturas).
- Pedir aclaración al profesor sobre el enunciado.

Durante la corrección se revisa el código con detectores de plagio y se compara con código generado por IA. Si la diferencia entre tu código y el `SOLUCION.md` sugiere autogeneración, la nota baja al máximo 50.

---

## Cómo entregar

Subí al campus virtual (Moodle), en el espacio designado para esta práctica:

1. **URL del repositorio** completa.
2. **Hash del último commit** (`git log --oneline -1`).

No subir el código ni las capturas al campus. La revisión se hace en GitHub.

Estructura esperada en tu repo:

```
tu-repositorio-del-curso/
├── Clase_1/ ... Clase_13/
├── Practica_1/
├── Practica_2/
├── Caso_Practico_1/
└── Caso_Practico_2/
    ├── biblioapp/                          <- proyecto Spring Boot
    │   ├── pom.xml
    │   ├── src/
    │   └── seed-data.sql
    ├── biblioapp.postman_collection.json
    ├── evidencia/
    │   ├── 01-catalogo.png
    │   ├── 02-prestamo.png
    │   ├── 03-403.png
    │   ├── 04-postman-login.png
    │   ├── 05-postman-api.png
    │   └── 06-atrasados.png
    └── SOLUCION.md                         <- explicación de la lógica
```

---

## Problemas comunes

| Síntoma | Solución |
|---------|----------|
| `Access denied for user 'root'` | Verificar `DB_PASSWORD` con `echo $env:DB_PASSWORD` en terminal nueva. |
| `Unknown database 'biblioappdb'` | Crear la BD con el SQL del paso 1 del README del proyecto base. |
| `@PreAuthorize` no bloquea nada | Falta `@EnableMethodSecurity` en `SecurityConfig` (viste esto en la Semana 11). |
| La consulta JPQL de atrasados no devuelve nada | Revisar que `fechaLimite` esté en el pasado en tus datos de prueba — probá cambiando la fecha de un préstamo de `seed-data.sql` a una fecha vieja. |
| `LazyInitializationException` al mostrar `prestamo.getLibro().getTitulo()` en una vista | Falta `JOIN FETCH` en la consulta que trae la lista de préstamos (mismo problema de la Semana 9). |
| API devuelve `403` aunque el usuario sea `BIBLIOTECARIO` | Revisar que el rol en la base de datos sea exactamente `"BIBLIOTECARIO"` (sin `ROLE_` de prefijo si tu `hasRole()` ya lo agrega automáticamente). |
| Postman devuelve `401` en vez de `403` | `401` es "no autenticado" (falta login/token), `403` es "autenticado pero sin permiso" — revisar cuál de los dos casos estás probando. |
| `./mvnw: Permission denied` en Linux/Mac | `chmod +x mvnw` y volver a intentar. |

---

## Recursos permitidos

- Tu repo personal con Práctica 1, Práctica 2, Caso Práctico 1, y los ejemplos de clase (`cursosapp` de las Semanas 9-13).
- Material del aula virtual (presentaciones, guiones, lecturas de S9 a S12).
- Documentación oficial:
  - Spring Data JPA (Query Methods y JPQL): https://docs.spring.io/spring-data/jpa/reference/jpa/query-methods.html
  - Spring Security (Method Security): https://docs.spring.io/spring-security/reference/servlet/authorization/method-security.html
  - Spring Web (REST): https://docs.spring.io/spring-framework/reference/web/webmvc.html
  - Bean Validation: https://jakarta.ee/specifications/bean-validation/3.0/
  - Postman Learning Center: https://learning.postman.com/
