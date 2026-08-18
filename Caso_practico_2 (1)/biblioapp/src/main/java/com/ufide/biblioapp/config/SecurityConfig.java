package com.ufide.biblioapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

// ==========================================================
// CASO PRACTICO 2 - REQUISITO 3:
// Esta configuracion hoy deja pasar a CUALQUIER usuario logueado
// a CUALQUIER ruta (con .authenticated()). Tu trabajo es:
//
//   1. Agregar @EnableMethodSecurity arriba de esta clase (para
//      que funcionen los @PreAuthorize que vas a poner en los
//      controllers).
//   2. Dejar /libros y /api/libros (GET) como publico (catalogo).
//   3. Dejar el resto de rutas de escritura protegidas por rol
//      usando @PreAuthorize en el metodo del controller
//      correspondiente (no hace falta listarlas todas aca).
// ==========================================================
@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/libros", "/libros/**", "/login", "/css/**", "/js/**").permitAll()
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/libros", true)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            );

        return http.build();
    }
}
