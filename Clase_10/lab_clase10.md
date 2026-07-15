# Lab Clase 10 — Spring Security 1 (login + BCrypt + roles)

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

Hasta ahora, `cursosapp` es una aplicación completamente abierta: cualquiera que sepa la URL puede ver, crear, editar o eliminar cursos. En este lab agregás **Spring Security** para exigir login antes de entrar, con contraseñas guardadas de forma segura (nunca en texto plano) usando **BCrypt**.

Esta clase es la primera de dos sobre seguridad. Hoy resolvés la parte de **autenticación** (¿quién sos?): login, logout, tabla de usuarios. La próxima clase (S11) resuelve la **autorización** (¿qué podés hacer según tu rol?) con `@PreAuthorize`.

---

## Objetivos de aprendizaje

Al terminar este lab vas a haber demostrado que sos capaz de:

1. Agregar Spring Security a un proyecto Spring Boot existente.
2. Modelar una entidad `Usuario` con contraseña hasheada con BCrypt.
3. Implementar un `UserDetailsService` que conecta Spring Security con tu propia tabla de usuarios.
4. Configurar un `SecurityFilterChain` con `formLogin()` y `logout()`.
5. Construir una página de login propia en Thymeleaf.
6. Usar el dialecto `sec:` de Thymeleaf para mostrar contenido según el estado de la sesión.

---

## Material entregado

| Archivo/Carpeta | Descripción |
|---|---|
| `cursosapp/` | Mismo proyecto de S9 (CRUD de `Curso` con `Profesor` asociado), con los cambios de este lab pre-comentados. |
| `seed-data.sql` | Datos de ejemplo, se actualiza durante el lab. |

El proyecto ya tiene el CRUD completo de `Curso` funcionando, sin ninguna protección todavía. Eso es lo que cambia hoy.

---

## Antes de empezar

Verificá que el proyecto base arranca sin problemas **antes** de tocar nada:

```bash
cd cursosapp
mvnw.cmd spring-boot:run     # Windows
./mvnw spring-boot:run       # Linux/Mac
```

Abrí <http://localhost:8080/cursos> y confirmá que entrás directo, sin que te pida ningún login.

---

## Cómo está organizado el código pre-comentado

Vas a encontrar los mismos dos tipos de marcas que en S9:

- **Bloques `<!-- CLASE 10 - PASO X.Y -->` / `// CLASE 10 - PASO X.Y`** dentro de archivos que YA existen (`pom.xml`, `fragments/header.html`, `login.html`): son líneas o bloques comentados, listos para descomentar.
- **Archivos `package-info.md`** en los paquetes `entity/`, `repository/`, y en dos paquetes **nuevos** (`security/`, `config/`): contienen el código completo de un archivo que todavía NO existe. Copiá el bloque de código de adentro a un archivo nuevo con el nombre indicado.

---

## Parte A — Dependencia y entidad `Usuario`

1. Abrí `pom.xml`. Vas a ver un bloque comentado marcado **PASO A.1** con dos dependencias nuevas: `spring-boot-starter-security` y `thymeleaf-extras-springsecurity6`. Descomentalo.
2. Abrí `entity/package-info.md`. Copiá el código de `Usuario.java` a un archivo nuevo `entity/Usuario.java` (**PASO A.2**). Fijate los tres campos: `username`, `password` y `rol` (String simple, valores `"ADMIN"` o `"USER"`).
3. Abrí `repository/package-info.md`. Copiá el código a un archivo nuevo `repository/UsuarioRepository.java` (**PASO A.3**). Tiene un solo método propio: `findByUsername`.

En este punto el proyecto todavía no tiene ninguna protección activa — solo agregaste las piezas de datos. Eso viene en la Parte B y C.

---

## Parte B — `UserDetailsService`

Spring Security no sabe nada de tu tabla `usuarios` a menos que se lo digas. Ese puente se llama `UserDetailsService`.

1. Creá el paquete nuevo `com.ufide.cursosapp.security` (al mismo nivel que `controller`, `entity`, etc.).
2. Abrí `security/package-info.md`. Copiá el código a un archivo nuevo `security/CustomUserDetailsService.java` (**PASO B.1**).
3. Fijate qué hace: busca el `Usuario` por `username` con el repository, y arma un `UserDetails` de Spring Security con `.roles(usuario.getRol())`. Spring Security agrega el prefijo `ROLE_` solo — no lo escribás vos en la base de datos.

---

## Parte C — Configuración de seguridad

1. Creá el paquete nuevo `com.ufide.cursosapp.config`.
2. Abrí `config/package-info.md`. Copiá el código a un archivo nuevo `config/SecurityConfig.java` (**PASO C.1**).
3. Abrí `HomeController.java` (paquete `controller/`). Vas a ver un método comentado marcado **PASO C.2**: `@GetMapping("/login")`, que devuelve la vista `login`. Descomentalo. `SecurityConfig` ya le dice a Spring Security que la página de login está en `/login` (`loginPage("/login")`), pero eso solo indica la URL — hace falta este método para que algo responda ahí. Sin él, vas a ver un error 404 (`Whitelabel Error Page`) en vez de tu login.
4. Leé el método `filterChain`: define qué URLs son públicas (`permitAll()`) y cuáles requieren sesión (`anyRequest().authenticated()`), y configura `formLogin()` apuntando a una página de login propia (`/login`) y `logout()`.
5. (**PASO C.3**, opcional para experimentar) Probá comentar temporalmente `.anyRequest().authenticated()` y ver que `/cursos` vuelve a ser público — después descomentalo de nuevo.

Compilá y corré la app. Ya debería pedirte login para entrar a `/cursos`. Como `login.html` todavía no tiene el formulario descomentado (eso es la Parte D), vas a ver tu propia página en construcción — el título y el mensaje de usuarios de prueba, pero sin form todavía — no la pantalla default de Spring Security (esa solo aparece si nunca configurás `loginPage()`, y acá ya la configuramos desde el PASO C.1).

---

## Parte D — Página de login propia

1. Abrí `templates/login.html`. Ya tiene la estructura Bootstrap armada. Descomentá el bloque de mensajes (**PASO D.1**): muestra un error si el login falló (`?error`) o un aviso si cerraste sesión (`?logout`) — Spring Security agrega esos parámetros solo a la URL, no hace falta ningún código en el Controller.
2. Descomentá el `<form>` (**PASO D.2**). Fijate que usa `name="username"` y `name="password"` — son los nombres que `formLogin()` espera por defecto — y que `action="/login"` con `method="post"` es el endpoint que Spring Security intercepta automáticamente (no existe, ni hace falta, un `LoginController`).
3. Recargá la app y probá loguearte con `admin` / `admin123` (una vez que hayas cargado el seed en la Parte F).

---

## Parte E — Navbar con sesión

1. Abrí `templates/fragments/header.html`. El namespace `xmlns:sec` ya está agregado en la etiqueta `<html>` (**PASO E.1**).
2. Descomentá el bloque de la navbar marcado **PASO E.2**. Usa `sec:authorize="isAuthenticated()"` para mostrar el nombre de usuario y un botón de "Salir", y `sec:authorize="isAnonymous()"` para mostrar el link de "Iniciar sesión" cuando no hay sesión activa.
3. Recargá cualquier página — el navbar debe cambiar según si estás logueado o no.

---

## Parte F — Usuarios de prueba

1. Abrí `seed-data.sql`. Al final vas a ver un bloque comentado marcado **PASO F.1** con tres `INSERT` de usuarios. Las contraseñas ya vienen hasheadas con BCrypt — nunca insertes una contraseña en texto plano en esta tabla.
2. Arrancá la app (para que Hibernate cree la tabla `usuarios`), descomentá el bloque y ejecutalo en Workbench.
3. Probá loguearte con los tres usuarios (`admin/admin123`, `profesor/profesor123`, `estudiante/estudiante123`). Los tres deberían poder hacer el CRUD completo de cursos — la restricción por rol es la clase que viene.

---

## Problemas comunes

| Síntoma | Solución |
|---|---|
| El login por defecto de Spring Security (pantalla gris, sin estilos) sigue apareciendo | Falta `loginPage("/login")` en `SecurityConfig`, o `login.html` todavía no tiene el form descomentado. |
| `Whitelabel Error Page` al ir a `/login` | Falta el `@GetMapping("/login")` en `HomeController.java` (**PASO C.2**) — `loginPage("/login")` en `SecurityConfig` solo indica la URL, no sirve la vista. También puede ser que el template `login.html` no esté en `src/main/resources/templates/`, o que haya un error de sintaxis Thymeleaf en el archivo. |
| Redirige a `/login` en un loop infinito | `/login` no está en `.permitAll()` dentro de `authorizeHttpRequests`. |
| `Bad credentials` con usuario/contraseña que "deberían" ser correctos | El `INSERT` de `seed-data.sql` no se ejecutó, o la contraseña quedó en texto plano en vez del hash BCrypt. |
| El navbar no muestra el nombre de usuario ni cambia con la sesión | Falta el namespace `xmlns:sec="http://www.thymeleaf.org/extras/spring-security"` en `header.html`, o la dependencia `thymeleaf-extras-springsecurity6` en el `pom.xml`. |
| `403` al hacer logout (botón "Salir") | El formulario de logout tiene que usar `th:action` (no `action` a secas) para que Thymeleaf agregue el token CSRF automáticamente. |
| `Table 'usuarios' doesn't exist` al ejecutar el seed | Arrancá la app primero (con `Usuario.java` ya creado) para que Hibernate cree la tabla, y recién después corré el `INSERT`. |

---

## Recursos de consulta

| Tema | Enlace |
|---|---|
| Spring Security — Reference Documentation | https://docs.spring.io/spring-security/reference/index.html |
| Spring Security — Servlet Authentication (formLogin) | https://docs.spring.io/spring-security/reference/servlet/authentication/index.html |
| Spring Security — Password Storage (BCrypt) | https://docs.spring.io/spring-security/reference/features/authentication/password-storage.html |
| Thymeleaf + Spring Security integration | https://github.com/thymeleaf/thymeleaf-extras-springsecurity |
| Baeldung — Spring Security Login Form | https://www.baeldung.com/spring-security-login |

---

## Preguntas

Cualquier duda durante el lab podés consultarla en:

- El canal `Consultas` del equipo de Teams.
- Directamente en clase, levantando la mano.
