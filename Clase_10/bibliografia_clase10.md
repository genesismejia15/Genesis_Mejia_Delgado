# Bibliografía de repaso — Clase 10: Spring Security 1 (autenticación)

Este documento es para releer después de la clase, con calma, usando como referencia el proyecto `cursosapp` y las slides que ya tenés. No repite el paso a paso del lab (eso está en `lab_clase10.md`) — acá el objetivo es que entiendas **por qué** funciona cada cosa.

---

## 1. Qué protege Spring Security hoy y qué no

Hoy tu app pasa de estar completamente abierta a exigir login para casi todo. Pero **ojo con una confusión común**: aunque ya existe el campo `rol` en `Usuario` (`ADMIN` o `USER`), **todavía no hay ninguna regla que lo use**. Cualquier usuario logueado, sea ADMIN o USER, puede crear, editar y eliminar cursos por igual. Eso se resuelve la próxima clase (S11) con `@PreAuthorize`.

En resumen:
- **S10 (hoy):** ¿quién sos? — login, logout, sesión.
- **S11 (la próxima):** ¿qué podés hacer según tu rol? — autorización.

---

## 2. ¿Qué es un Bean?

Vas a ver la palabra "bean" todo el lab de hoy (`@Bean`, `@Service`), así que vale la pena pararse acá un momento. Ya lo habíamos tocado por arriba en Clase 4 cuando apareció `@Autowired` — ahora lo vemos con ejemplos reales.

**¿Qué es un bean?** Un objeto Java normal, pero en vez de crearlo vos con `new`, lo crea y lo administra Spring. Vive dentro del **`ApplicationContext`** (el contenedor de Spring), y Spring te lo entrega (lo **inyecta**) donde lo necesites.

Hoy ya creaste 3 beans, de 3 formas distintas:

```java
@Configuration
public class SecurityConfig {

    @Bean                                      // <- un metodo @Bean...
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean                                      // <- ...puede haber varios
    public SecurityFilterChain filterChain(HttpSecurity http) { ... }
}

@Service                                       // <- una clase @Service tambien es un bean
public class CustomUserDetailsService implements UserDetailsService { ... }
```

`@Configuration` marca la clase como "fuente de beans". Cada método `@Bean` adentro se ejecuta una sola vez, y lo que devuelve queda guardado en el `ApplicationContext`. `@Service` es otra forma de decirle a Spring "esta clase completa es un bean" — sin necesidad de un método `@Bean` aparte.

**La idea clave:** en ningún lugar del proyecto vas a encontrar `new SecurityConfig()` ni `new CustomUserDetailsService()`. Spring los descubre solo (por las anotaciones) y los crea él.

---

## 3. IoC y Dependency Injection — por qué Spring te los entrega solo

Esto es lo que hace posible lo de arriba. Dos términos que van siempre juntos:

- **Inversión de Control (IoC):** el concepto. En vez de que tu clase decida qué implementación usar y la cree ella misma, **alguien externo decide y crea por vos** — en este caso, el contenedor de Spring. "El framework crea las cosas, no vos" (la misma frase de Clase 4).
- **Dependency Injection (DI):** la técnica concreta para lograr IoC. Spring te *inyecta* la dependencia (por constructor, por `@Autowired`, etc.) en vez de que vos la instancies a mano.

Comparación directa:

```java
// SIN DI - la clase crea su propia dependencia (acoplamiento fuerte)
public class AuthManager {
    private UserDetailsService uds = new CustomUserDetailsService();
}

// CON DI - Spring se la entrega desde afuera
public class AuthManager {
    private UserDetailsService uds;   // Spring me lo pasa solo
}
```

Ejemplo real de hoy: el `AuthenticationManager` que Spring Security arma internamente necesita un `UserDetailsService` y un `PasswordEncoder` para funcionar. Vos nunca escribiste ese cableado — Spring encontró tus beans (`CustomUserDetailsService` por `@Service`, `passwordEncoder()` por `@Bean`) y los conectó solo.

---

## 4. Cómo se arma la protección: `SecurityFilterChain`

Antes de entrar al bean en sí, vale la pena ver el recorrido completo de un request, de punta a punta:

```
Request (GET /cursos)
   -> SecurityFilterChain revisa si la URL es publica
        - si es publica (permitAll())  -> pasa directo al Controller
        - si requiere sesion y no hay  -> redirige a /login
   -> (en el POST /login) AuthenticationManager entra en accion
        -> llama a UserDetailsService.loadUserByUsername()  (tu CustomUserDetailsService)
        -> usa PasswordEncoder.matches() para comparar la contrasena
   -> si coincide, arma la sesion (SecurityContext)
   -> RECIEN AHI el Controller ve el request
```

Dos atajos para tener claros: si la URL es pública, el request nunca pasa por `AuthenticationManager` ni por tu `UserDetailsService`. Y si las credenciales no coinciden, Spring Security corta ahí mismo y redirige a `/login` — tu `Controller` nunca se entera de ese intento fallido.

Spring Security se engancha como un **filtro** que revisa cada request ANTES de que llegue a tu Controller. Vos le decís qué reglas aplicar en un bean:

```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.authorizeHttpRequests(auth -> auth
            .requestMatchers("/", "/login", "/css/**").permitAll()
            .anyRequest().authenticated())
        .formLogin(form -> form.loginPage("/login").permitAll())
        .logout(logout -> logout.permitAll());
    return http.build();
}
```

Las reglas de `authorizeHttpRequests` se leen **en orden**: la primera que matchea la URL es la que se aplica. Por eso `/`, `/login` y `/css/**` van marcadas como públicas (`permitAll()`) ANTES de la regla general `anyRequest().authenticated()`, que exige sesión para todo lo demás (incluido `/cursos/**`, aunque no lo veas escrito explícitamente).

---

## 5. `UserDetailsService` — el puente entre tu tabla y Spring Security

Spring Security no sabe nada de tu entidad `Usuario` a menos que se lo digas. `CustomUserDetailsService` es exactamente ese puente:

```java
@Override
public UserDetails loadUserByUsername(String username) {
    Usuario usuario = usuarioRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException(...));
    return User.builder()
            .username(usuario.getUsername())
            .password(usuario.getPassword())
            .roles(usuario.getRol())
            .build();
}
```

Cuando alguien intenta loguearse, Spring Security llama a este método solo, compara la contraseña tipeada contra el hash guardado, y si coincide, te deja entrar. Vos no escribís esa comparación a mano — solo le decís CÓMO buscar al usuario.

**Detalle para tener en cuenta:** `.roles("ADMIN")` le agrega el prefijo `ROLE_` por debajo (queda como `ROLE_ADMIN` internamente). No hace falta que vos escribas ese prefijo en la base de datos ni en el código — Spring lo agrega solo. Esto te va a importar la próxima clase cuando veamos `hasRole()` vs `hasAuthority()`.

---

## 6. BCrypt — por qué nunca vas a ver una contraseña en texto plano

BCrypt es un algoritmo de hashing pensado específicamente para contraseñas:

- Es **irreversible**: no existe un "decrypt", solo podés comparar (`matches()`), nunca "leer" la contraseña original a partir del hash.
- Cada hash incluye un **salt aleatorio** — la misma contraseña genera un hash distinto cada vez que la hasheás. Por eso no podés saber si dos usuarios comparten contraseña con solo mirar la tabla.
- Tiene un **costo configurable** (rounds) — se puede hacer más lento a propósito a medida que las computadoras se vuelven más rápidas, para que siga siendo difícil de forzar por fuerza bruta.

En el proyecto, `SecurityConfig` define un bean `PasswordEncoder`:

```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}
```

Y en `seed-data.sql`, las contraseñas ya vienen como hashes (`$2b$10$...`), nunca como texto plano.

---

## 7. Por qué tus formularios existentes no se rompieron (CSRF)

Spring Security agrega **protección CSRF** por defecto a cualquier `POST`/`PUT`/`DELETE`. En teoría, esto debería romper todos los formularios que ya tenías (crear curso, eliminar curso) — de golpe empezarían a devolver `403`.

No pasó porque Thymeleaf ya venía usando `th:action` en todos los forms (en vez de `action` a secas). Cuando usás `th:action`, Thymeleaf agrega automáticamente un campo oculto con el token CSRF — gratis, sin que tengas que escribir nada. Por eso el botón de "Salir" (logout) también tiene que ser un `<form method="post">` con `th:action`, no un simple link `<a href="/logout">`.

---

## 8. El navbar dinámico: `sec:authorize`

En `fragments/header.html` vas a ver atributos que no habías usado antes:

```html
<li sec:authorize="isAuthenticated()">...</li>
<li sec:authorize="isAnonymous()">...</li>
<span sec:authentication="name">usuario</span>
```

`sec:authorize` muestra u oculta un elemento según una condición de seguridad (¿hay sesión? ¿no hay sesión?). `sec:authentication="name"` imprime un dato del usuario logueado (su username). Vienen de una dependencia nueva en el `pom.xml`: `thymeleaf-extras-springsecurity6`.

**Importante:** esto es solo cosmético. Ocultar un botón con `sec:authorize` no impide que alguien llame la URL directamente (por ejemplo, con Postman). La protección real siempre está en el backend — hoy en `SecurityConfig`, y desde la próxima clase también en `@PreAuthorize`.

---

## 9. Repaso rápido — dudas frecuentes

| Duda | Respuesta |
|---|---|
| ¿Por qué cualquier usuario logueado puede editar/eliminar cursos, no solo el admin? | Todavía no llegamos a esa parte — es el contenido de S11. Hoy solo resolvemos el login. |
| ¿Puedo ver la contraseña real de un usuario mirando la tabla `usuarios`? | No. El hash BCrypt no se puede "deshacer" — ni vos, ni el profesor, ni nadie que robe la base de datos puede recuperar la contraseña original. |
| ¿Por qué la misma contraseña (`admin123`) da un hash distinto en cada usuario? | Por el salt aleatorio que BCrypt agrega en cada hash. Es intencional — evita que se note si dos usuarios comparten contraseña. |
| ¿Por qué el logout es un botón y no un link? | Porque un link es un `GET`, y Spring Security exige un `POST` con token CSRF para cualquier operación que cambie estado (como cerrar sesión). |
| ¿Necesito escribir un `LoginController`? | No. `formLogin()` en `SecurityConfig` ya intercepta el `POST /login` automáticamente. Vos solo hacés la vista (`login.html`). |
| ¿Qué significa `ROLE_` en el rol? | Spring Security lo agrega solo cuando usás `.roles("ADMIN")` — vos guardás `"ADMIN"` en la base de datos, sin el prefijo. |

---

## Para seguir leyendo

| Tema | Enlace |
|---|---|
| Spring Framework — The IoC Container (beans, ApplicationContext) | https://docs.spring.io/spring-framework/reference/core/beans.html |
| Spring Security — Reference Documentation | https://docs.spring.io/spring-security/reference/index.html |
| Spring Security — Username/Password Authentication | https://docs.spring.io/spring-security/reference/servlet/authentication/passwords/index.html |
| Spring Security — Password Storage | https://docs.spring.io/spring-security/reference/features/authentication/password-storage.html |
| Baeldung — CSRF Protection with Spring MVC and Thymeleaf | https://www.baeldung.com/csrf-thymeleaf-with-spring-security |
| Baeldung — Spring Security Login Form | https://www.baeldung.com/spring-security-login |
| thymeleaf-extras-springsecurity — GitHub | https://github.com/thymeleaf/thymeleaf-extras-springsecurity |
