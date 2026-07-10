# Lab Clase 9 — Asociaciones JPA (Curso ↔ Profesor) + JPQL básico

## Información general

| Dato | Valor |
|------|-------|
| Curso | SC-403 Desarrollo de Aplicaciones Web y Patrones |
| Universidad | Fidélitas |
| Modalidad | En clase, guiado por el profesor |
| Evaluación | Ninguna — es práctica de laboratorio, no se entrega |
| Tiempo estimado | 75 minutos |

---

## Propósito

Hasta ahora `Curso` guarda el nombre del profesor como un simple `String`. Eso no escala: no podés saber el email del profesor, no podés listar todos los cursos de un mismo profesor, y si dos cursos tienen el "mismo" profesor, en realidad son dos textos sueltos sin ninguna relación real entre sí.

En este lab convertís ese `String` en una relación real de JPA (`@ManyToOne`) hacia una nueva entidad `Profesor`, y de paso vas a ver en vivo el problema de **N+1 consultas** y cómo resolverlo con JPQL y `JOIN FETCH`.

---

## Objetivos de aprendizaje

Al terminar este lab vas a haber demostrado que sos capaz de:

1. Modelar una relación `@ManyToOne` entre dos entidades JPA.
2. Distinguir el lado "dueño" de la relación y usar `@JoinColumn` correctamente.
3. Reconocer el problema N+1 en los logs de Hibernate.
4. Escribir una consulta JPQL con `JOIN FETCH` para resolverlo.
5. Adaptar un Controller y una vista Thymeleaf para trabajar con una relación en vez de un dato suelto.

---

## Material entregado

| Archivo/Carpeta | Descripción |
|---|---|
| `cursosapp/` | Proyecto Spring Boot base (mismo CRUD de Curso que ya conocés de clases anteriores), con los cambios de este lab pre-comentados. |
| `seed-data.sql` | Datos de ejemplo, se actualiza durante el lab. |

El proyecto ya tiene el CRUD completo de `Curso` funcionando (listar, ver, crear, editar, eliminar). El campo `profesor` hoy es un `String` simple — ese es el que vas a convertir en relación.

---

## Antes de empezar

Verificá que el proyecto base arranca sin problemas **antes** de tocar nada:

```bash
cd cursosapp
mvnw.cmd spring-boot:run     # Windows
./mvnw spring-boot:run       # Linux/Mac
```

Abrí <http://localhost:8080/cursos> y confirmá que ves la lista de cursos con el profesor como texto plano.

Activá el log de SQL si todavía no está (ya viene activado en `application.properties`):

```properties
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

---

## Cómo está organizado el código pre-comentado

Vas a encontrar dos tipos de marcas en el proyecto:

- **Bloques `// CLASE 9 - PASO X.Y`** dentro de archivos que YA existen (`Curso.java`, `CursoRepository.java`, `CursoService.java`, `CursoController.java`, los templates): son líneas comentadas, listas para descomentar.
- **Archivos `package-info.md`** en los paquetes `entity/`, `repository/` y `service/`: contienen el código completo de un archivo que todavía NO existe (`Profesor.java`, `ProfesorRepository.java`, `ProfesorService.java`). Copiá el bloque de código de adentro a un archivo nuevo con el nombre indicado.

---

## Parte A — Convertir `profesor` en una relación

Abrí `entity/Curso.java`. Vas a ver el campo `profesor` como `String`, y justo abajo un bloque comentado marcado **PASO A.1** con la versión como relación.

1. Borrá las 3 líneas del `String profesor` (con `@NotBlank` y `@Size`).
2. Descomentá las 4 líneas del bloque **PASO A.1** (`@NotNull`, `@ManyToOne`, `@JoinColumn`, `private Profesor profesor;`).
3. Agregá los imports que pide el comentario: `FetchType`, `JoinColumn`, `ManyToOne` (de `jakarta.persistence`) y `NotNull` (de `jakarta.validation.constraints`).
4. Repetí el mismo descomentar/borrar en el bloque **PASO A.2** (constructor) y **PASO A.3** (getter/setter).

En este punto el proyecto **NO compila todavía** — falta que exista la clase `Profesor`. Eso es la Parte B.

---

## Parte B — Crear `Profesor`

1. Abrí `entity/package-info.md`. Copiá el código de `Profesor.java` a un archivo nuevo `entity/Profesor.java`.
2. Abrí `repository/package-info.md`. Copiá el código a un archivo nuevo `repository/ProfesorRepository.java`.
3. Abrí `service/package-info.md`. Copiá el código a un archivo nuevo `service/ProfesorService.java`.
4. Compilá / corré la app de nuevo. Ahora sí debería levantar sin errores, y Hibernate va a crear la tabla `profesores` sola.

Cargá los datos de `seed-data.sql` (primero los `profesores`, después los `cursos` con su `profesor_id`).

---

## Parte C — Ver el problema N+1 y resolverlo con JPQL

1. Entrá a <http://localhost:8080/cursos> y mirá la consola. Vas a ver **un SELECT por cada curso** para traer su profesor (porque la relación es `LAZY` y el Controller usa `cursoService.listar()`, que llama a `findAll()`). Con 6 cursos, son 7 consultas en vez de 1.
2. Abrí `repository/CursoRepository.java` y descomentá el método del **PASO C.2** (`findAllConProfesor()` con `@Query` y `JOIN FETCH`). Agregá los imports que indica el comentario del **PASO C.1**.
3. Abrí `service/CursoService.java` y descomentá `listarConProfesor()` (**PASO C.3**).
4. Abrí `controller/CursoController.java` y aplicá los pasos **D.1 a D.5**: importar y autowirear `ProfesorService`, cambiar `listar()` por `listarConProfesor()` en el método `listar` del controller, y agregar `profesores` al modelo en los métodos que muestran el formulario.
5. Recargá `/cursos` y compará el log: ahora es **una sola consulta** con `JOIN`.

---

## Parte D — Actualizar las vistas

1. En `templates/cursos.html` (**PASO E.1**) y `templates/curso.html` (**PASO E.2**), cambiá `${c.profesor}` / `${curso.profesor}` por `${c.profesor.nombre}` / `${curso.profesor.nombre}`.
2. En `templates/cursos/form.html` (**PASO E.3**), reemplazá el `<input>` de texto por el `<select>` que está comentado justo abajo. Fijate que el `<select>` usa `name="profesor"` (no `th:field`) — Spring convierte automáticamente el `id` del profesor seleccionado a un objeto `Profesor` real gracias al `DomainClassConverter` que registra Spring Data.

Probá crear un curso nuevo y editar uno existente. El dropdown debe mostrar los profesores cargados por `seed-data.sql`, y al editar debe quedar pre-seleccionado el profesor correcto.

---

## Problemas comunes

| Síntoma | Solución |
|---|---|
| `Cannot resolve symbol Profesor` en `Curso.java` | Todavía no creaste `entity/Profesor.java` (Parte B). El orden importa: primero Parte B, o al menos tenerla lista antes de compilar la Parte A. |
| La app no arranca: `Table 'cursos' doesn't exist` o error de columna `profesor_id` | Si ya tenías la tabla `cursos` de antes (con la columna `profesor` como texto), Hibernate no la migra sola. Hay que dropear la tabla vieja o correr `TRUNCATE`/`DROP TABLE cursos;` y dejar que Hibernate la recree. |
| Sigue viendo N+1 en los logs después del PASO C | Confirmá que el Controller (`CursoController.listar()`) está usando `listarConProfesor()` y no `listar()`. |
| El dropdown de profesores aparece vacío | Confirmá que el Controller manda el atributo `"profesores"` al modelo en `mostrarFormNuevo` y `mostrarFormEditar` (PASO D.4 / D.5), y que cargaste `seed-data.sql` de profesores. |
| Al editar un curso, el dropdown no queda pre-seleccionado | Revisá el `th:selected` del `<option>` — tiene que comparar `curso.profesor.id == p.id`, no el objeto completo. |
| `Failed to convert property value` al guardar el form | El `<select>` tiene que tener `name="profesor"` (no otro nombre), y los `value` de cada `<option>` tienen que ser el `id` del profesor (numérico). |

---

## Recursos de consulta

| Tema | Enlace |
|---|---|
| Spring Data JPA — relaciones | https://docs.spring.io/spring-data/jpa/reference/jpa/entity-persistence.html |
| Hibernate — Fetching strategies | https://docs.jboss.org/hibernate/orm/current/userguide/html_single/Hibernate_User_Guide.html#fetching |
| Spring Data JPA — JPQL con `@Query` | https://docs.spring.io/spring-data/jpa/reference/jpa/query-methods.html |
| Baeldung — N+1 problem | https://www.baeldung.com/hibernate-common-performance-problems-in-logs |

---

## Preguntas

Cualquier duda durante el lab podés consultarla en:

- El canal `Consultas` del equipo de Teams.
- Directamente en clase, levantando la mano.
