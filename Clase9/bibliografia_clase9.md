# Bibliografía de repaso — Clase 9: Asociaciones JPA y JPQL

Este documento es para releer después de la clase, con calma, usando como referencia el proyecto `cursosapp` y las slides que ya tenés. No repite el paso a paso del lab (eso está en `lab_clase9.md`) — acá el objetivo es que entiendas **por qué** funciona cada cosa, no solo que la hiciste funcionar.

---

## 1. Por qué un `String` no alcanza

Antes de este lab, `Curso` tenía:

```java
private String profesor;
```

Un texto suelto no puede:

- Garantizar que "Esteban Ortega" y "esteban ortega" sean el mismo profesor.
- Guardar datos propios del profesor (email, especialidad) sin repetirlos en cada curso.
- Responder de forma confiable preguntas como "¿qué cursos da este profesor?".

La solución es modelar `Profesor` como una entidad propia y **conectarla** con `Curso` mediante una asociación JPA — el equivalente, en código, a una foreign key de base de datos.

---

## 2. Qué es una asociación JPA

Es la forma en que JPA (y Hibernate, que es la implementación que usa Spring Boot) representa una foreign key entre dos tablas usando anotaciones sobre atributos Java, en vez de que vos escribas el `JOIN` a mano en cada consulta.

```java
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "profesor_id", nullable = false)
private Profesor profesor;
```

Esto hace dos cosas:

1. Le dice a Hibernate que agregue una columna `profesor_id` a la tabla `cursos`, que apunta al `id` de la tabla `profesores`.
2. Convierte el campo `profesor` de `Curso` en un objeto `Profesor` completo, no en un texto.

---

## 3. Los 4 tipos de relación

| Anotación | Se lee así | Ejemplo | ¿La usamos hoy? |
|---|---|---|---|
| `@ManyToOne` | Muchos de "este" lado apuntan a uno del otro lado | Muchos `Curso` → 1 `Profesor` | Sí |
| `@OneToMany` | Uno de "este" lado tiene muchos del otro (es el inverso de `@ManyToOne`) | 1 `Profesor` → muchos `Curso` | Se menciona, no se implementa |
| `@OneToOne` | Uno a uno | 1 Persona → 1 Pasaporte | No |
| `@ManyToMany` | Muchos a muchos | Estudiantes ↔ Cursos (inscripciones) | No |

En la mayoría de los proyectos reales (y probablemente en el tuyo del curso), `@ManyToOne` es la relación que más vas a usar. `@ManyToMany` casi siempre termina modelada como dos `@ManyToOne` hacia una tabla intermedia (por ejemplo, una entidad `Inscripcion` con `estudiante_id` y `curso_id`), porque esa tabla intermedia suele necesitar columnas propias (fecha, nota, etc.).

---

## 4. Lado dueño vs lado inverso

En una relación entre dos entidades, **solo un lado tiene la foreign key** en su tabla. Ese es el lado "dueño". El otro lado, si existe, solo sirve para navegar la relación al revés, y se declara con `mappedBy`.

```java
// Lado dueño: Curso. Tiene profesor_id en su propia tabla.
@ManyToOne
@JoinColumn(name = "profesor_id")
private Profesor profesor;

// Lado inverso (NO implementado en este lab): Profesor.
// Si existiera, se vería así:
@OneToMany(mappedBy = "profesor")
private List<Curso> cursos;
```

En este lab, `Curso` es el dueño. No agregamos el lado inverso en `Profesor` a propósito — no hacía falta para el objetivo de la clase, y evita complicaciones (una colección lazy mal usada puede generar su propio problema de N+1, y si más adelante exponés esto como API REST, un ciclo `Curso → Profesor → cursos → Profesor → ...` puede romper la serialización a JSON).

---

## 5. `FetchType`: LAZY vs EAGER

Esto es lo más importante de la clase para entender bien el problema de N+1.

| Relación | Fetch por defecto según la especificación JPA |
|---|---|
| `@ManyToOne` | EAGER |
| `@OneToOne` | EAGER |
| `@OneToMany` | LAZY |
| `@ManyToMany` | LAZY |

Fijate: si no hubiéramos escrito `fetch = FetchType.LAZY` explícitamente en `Curso.profesor`, por default hubiera sido **EAGER** (por ser `@ManyToOne`). En el lab lo forzamos a LAZY a propósito, porque es la forma más directa de que el problema de N+1 se vea claramente en los logs.

- **LAZY:** el profesor no se trae de la base de datos hasta que llamás `curso.getProfesor()` (o algo que lo use, como `curso.getProfesor().getNombre()` en una vista). Más eficiente en teoría, pero si no se maneja bien, genera una consulta extra por cada acceso.
- **EAGER:** el profesor se trae siempre, automáticamente, junto con el curso. Nunca vas a ver el error de N+1 en los logs de forma tan evidente, pero seguís trayendo datos de más aunque no los necesites — el problema de fondo (traer datos que no hacen falta) no desaparece, solo se esconde.

---

## 6. El problema N+1

Es un problema de performance clásico, no exclusivo de Spring o Java — aparece en cualquier ORM.

**Qué pasa:** una consulta trae una lista de N registros (por ejemplo, 6 cursos). Si cada uno tiene una relación LAZY que se termina usando (como `profesor`), Hibernate dispara **una consulta adicional por cada registro** para resolverla. Total: 1 + N consultas donde alcanzaba con 1.

```
1 consulta:   SELECT * FROM cursos;                          -- trae 6 cursos
6 consultas:  SELECT * FROM profesores WHERE id = ?;          -- una por cada curso
```

Con 6 cursos, son 7 consultas — no se nota nada. Con 50,000 cursos, son 50,001 consultas — ahí sí se nota, y bastante.

**Cómo se detecta:** con `spring.jpa.show-sql=true` en `application.properties`, mirando la consola. Si ves un `SELECT` repetido muchas veces con el mismo patrón (cambiando solo el `id`), es N+1.

---

## 7. JPQL — no es SQL, aunque se parece

JPQL (Jakarta Persistence Query Language) es el lenguaje de consultas que usás dentro de `@Query`. La diferencia clave con SQL:

| | SQL | JPQL |
|---|---|---|
| Habla sobre | Tablas y columnas de la base de datos | Clases y campos de Java (las entidades) |
| Ejemplo | `SELECT c.*, p.* FROM cursos c JOIN profesores p ON p.id = c.profesor_id;` | `SELECT c FROM Curso c JOIN FETCH c.profesor` |
| Quién lo ejecuta | La base de datos directamente | Hibernate lo traduce a SQL real antes de mandarlo a la base de datos |

Detalles a tener en cuenta:

- Los nombres de entidad (`Curso`) y de campo (`profesor`) son **sensibles a mayúsculas/minúsculas** y tienen que coincidir exactamente con tu clase Java.
- No escribís el nombre de la tabla ni de la columna — escribís el nombre de la clase y del atributo.

---

## 8. `JOIN FETCH` — la solución al N+1 que vimos hoy

```java
@Query("SELECT c FROM Curso c JOIN FETCH c.profesor")
List<Curso> findAllConProfesor();
```

`JOIN FETCH` le dice a Hibernate: "traé la relación YA, en la misma consulta, con un `JOIN` real de SQL". El resultado es **una sola consulta**, sin importar cuántos cursos haya.

La cadena completa en el proyecto queda así:

```
CursoRepository.findAllConProfesor()   (JPQL con JOIN FETCH)
        |
CursoService.listarConProfesor()       (usa el metodo de arriba)
        |
CursoController.listar()               (usa listarConProfesor(), no listar())
```

---

## 9. El dropdown de profesores en el formulario

Si mirás `cursos/form.html`, vas a notar que el `<select>` de profesor usa `name="profesor"` en vez de `th:field="*{profesor}"` como los demás campos:

```html
<select class="form-select" name="profesor">
    <option th:each="p : ${profesores}" th:value="${p.id}"
            th:selected="${curso.profesor != null and curso.profesor.id == p.id}"
            th:text="${p.nombre}"></option>
</select>
```

No hace falta que escribas ningún conversor a mano: Spring Data registra automáticamente algo llamado `DomainClassConverter` para cada entidad que tenga un `JpaRepository`. Gracias a eso, cuando el formulario envía `profesor=3`, Spring lo convierte solo al objeto `Profesor` real (buscándolo por ese `id`).

El detalle de por qué se usa `name=` en vez de `th:field` es una optimización técnica algo avanzada (evita un problema de performance conocido de Thymeleaf al pre-seleccionar valores en un `<select>` de una entidad). Si te interesa profundizar, está documentado en el link de Spring Data al final de este documento — no es indispensable para aprobar el curso, pero está bueno saber que existe.

---

## 10. Repaso rápido — dudas frecuentes

| Duda | Respuesta |
|---|---|
| ¿`@ManyToOne` no era EAGER por defecto? ¿Por qué vemos un problema de N+1 entonces? | Es EAGER por defecto, sí. En este lab lo forzamos a LAZY a propósito para poder ver el problema y aprender a resolverlo. |
| ¿Siempre tengo que usar `JOIN FETCH` en vez de `findAll()`? | No. Solo cuando de verdad vas a usar la relación. Si nunca accedés a `profesor`, dejarlo LAZY sin resolver es más eficiente. |
| ¿`JOIN FETCH` es lo mismo que un `JOIN` de SQL? | Se traduce a un `JOIN` real en el SQL final, pero vos lo escribís en términos de entidades y campos de Java, no de tablas y columnas. |
| ¿Por qué `Profesor` no tiene una lista de sus `Curso`? | Sería el lado inverso de la relación (`mappedBy`). No se implementa en este lab a propósito — lo podés agregar solo en tu proyecto final si te hace falta. |
| ¿Qué pasa si borro un profesor que ya tiene cursos asignados? | La base de datos rechaza el borrado mientras haya cursos apuntando a ese `profesor_id` (`nullable = false` en la relación). Es integridad referencial, un concepto que ya vieron en Bases de Datos. |

---

## Para seguir leyendo

| Tema | Enlace |
|---|---|
| Especificación Jakarta Persistence (JPA) | https://jakarta.ee/specifications/persistence/ |
| Spring Data JPA — Reference Documentation | https://docs.spring.io/spring-data/jpa/reference/ |
| Spring Data JPA — Query methods (`@Query`, JPQL) | https://docs.spring.io/spring-data/jpa/reference/jpa/query-methods.html |
| Hibernate ORM User Guide — Fetching strategies | https://docs.jboss.org/hibernate/orm/current/userguide/html_single/Hibernate_User_Guide.html#fetching |
| Baeldung — N+1 Problem in Hibernate and Spring Data JPA | https://www.baeldung.com/spring-hibernate-n1-problem |
| Baeldung — Eager/Lazy Loading in Hibernate | https://www.baeldung.com/hibernate-lazy-eager-loading |
| Spring Data Commons — `DomainClassConverter` API | https://docs.spring.io/spring-data/commons/docs/current/api/org/springframework/data/repository/support/DomainClassConverter.html |
