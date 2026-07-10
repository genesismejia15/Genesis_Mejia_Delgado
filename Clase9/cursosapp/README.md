# cursosapp — Proyecto base canónico SC-403

Aplicación Spring Boot + Thymeleaf + Bootstrap con CRUD completo sobre el dominio **Curso**, persistido en MySQL con JPA/Hibernate.

Este proyecto reemplaza a los anteriores `Clase_4_Base` / `Clase_4_Base_S7` como punto de partida canónico del curso. Nace de la versión CRUD de Clase 6, limpio de Firebase (no se usó en clase) y renombrado con un nombre genérico para no atarlo a ninguna semana en particular. A partir de S9 se usa como base para todos los labs (asociaciones, security, APIs REST, deployment).

---

## Qué tiene

- Spring Boot 4.0.6 + Java 25
- Thymeleaf con `spring.thymeleaf.cache=false` (reload sin reiniciar)
- Bootstrap 5.3 + Bootstrap Icons por CDN
- CSS propio en `static/css/styles.css`
- Fragmento **navbar** y **footer** reutilizables (`fragments/header.html`)
- CRUD completo de `Curso` (listar, ver detalle, crear, editar, eliminar) con modal de confirmación y toasts
- Persistencia con MySQL + Spring Data JPA (`CursoRepository`, `CursoService`)
- Validaciones con Bean Validation (`@NotBlank`, `@Size`, `@Min`, `@Max`)
- DevTools activo

## Qué NO tiene (a propósito)

- Sin Firebase / subida de imágenes — se sacó porque no se llegó a revisar en clase.
- Sin asociaciones JPA todavía — el campo `profesor` en `Curso` es un `String` simple. Esa es justamente la conversión que se hace en el lab de **S9** (pasa a ser una relación `@ManyToOne` hacia una entidad `Profesor`).
- Sin Spring Security — se agrega en S10/S11.

---

## Cómo correr

1. Tener MySQL Server corriendo en `localhost:3306`.
2. Crear la base de datos:
   ```sql
   CREATE DATABASE cursoswebdb CHARACTER SET utf8mb4;
   ```
3. Definir la variable de entorno `MYSQL_PASSWORD` (o completarla directo en `application.properties`, sin subirla al repo).
4. Arrancar la app — Hibernate crea la tabla `cursos` automáticamente.
5. Cargar los datos de ejemplo con `seed-data.sql` (Workbench: File → Open SQL Script → Execute).
6. Abrir <http://localhost:8080>

```bash
# Desde la carpeta cursosapp/
mvnw.cmd spring-boot:run      # Windows
./mvnw spring-boot:run        # Linux/Mac
```

O abrir el proyecto y darle Run a `CursosappApplication.java`.

---

## Estructura (organizada por capas)

```
cursosapp/
├── pom.xml
├── seed-data.sql                   (INSERTs de ejemplo)
├── postman-collection.json         (peticiones de ejemplo)
├── README.md
├── .gitignore
└── src/main/
    ├── java/com/ufide/cursosapp/
    │   ├── CursosappApplication.java     (main - queda en el root)
    │   │
    │   ├── controller/                    Capa Controller (web)
    │   │   ├── HomeController.java
    │   │   └── CursoController.java       (CRUD completo)
    │   │
    │   ├── entity/                        Capa Entity (modelo de datos)
    │   │   └── Curso.java                 (@Entity con validaciones)
    │   │
    │   ├── repository/                    Capa Repository (acceso a BD)
    │   │   └── CursoRepository.java       (extiende JpaRepository)
    │   │
    │   └── service/                       Capa Service (logica de negocio)
    │       └── CursoService.java
    │
    └── resources/
        ├── application.properties
        ├── static/css/styles.css
        └── templates/
            ├── home.html
            ├── cursos.html
            ├── curso.html
            ├── cursos/form.html
            └── fragments/header.html      (navbar + footer)
```

**Por qué esta organización?** Refleja visualmente las 4 capas del MVC ampliado (patrón _package by layer_, estándar en proyectos Spring Boot). Cada capa solo conoce a la de abajo: Controller → Service → Repository → Entity.

---

## URLs disponibles

| URL | Qué muestra |
|---|---|
| `/` | Página de inicio. Acepta `?nombre=` opcional. |
| `/cursos` | Lista de cursos (grid responsive). |
| `/cursos/{id}` | Detalle de un curso. |
| `/cursos/nuevo` | Formulario de creación. |
| `/cursos/{id}/editar` | Formulario de edición. |

---

## Si algo falla

- **App no arranca:** verificar que `CursosappApplication.java` esté en `src/main/java/com/ufide/cursosapp/` y el `pom.xml` apunte al artifact `cursosapp`.
- **Bootstrap no carga:** chequear conexión a internet (los CDNs).
- **`Failed to determine driver`:** falta `mysql-connector-j` en el `pom.xml` o no está corriendo MySQL.
- **`Access denied for user 'root'`:** password mal en `application.properties` / variable `MYSQL_PASSWORD`.
- **Tabla `cursos` no se crea:** falta `spring.jpa.hibernate.ddl-auto=update` o alguna anotación `@Entity` faltante en `Curso`.
- **Página vacía:** la tabla está vacía. Ejecutar `seed-data.sql`.
