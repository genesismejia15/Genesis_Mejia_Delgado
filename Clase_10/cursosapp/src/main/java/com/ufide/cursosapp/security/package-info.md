CLASE 10 - PASO B.1: crear el paquete `security/` (si no existe) y adentro `CustomUserDetailsService.java`, copiando el bloque de abajo.

```java
package com.ufide.cursosapp.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.ufide.cursosapp.entity.Usuario;
import com.ufide.cursosapp.repository.UsuarioRepository;

// Spring Security necesita saber COMO buscar un usuario y armar su UserDetails.
// Esta clase es el puente entre nuestra tabla "usuarios" y lo que Security entiende.
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "No existe un usuario con username: " + username));

        // Spring Security espera el rol prefijado con "ROLE_" para que
        // hasRole("ADMIN") funcione en S11.
        return User.builder()
                .username(usuario.getUsername())
                .password(usuario.getPassword())
                .roles(usuario.getRol())
                .build();
    }
}
```

Nota: este paquete no existia antes de esta clase. Requiere que ya exista `entity.Usuario` (PASO A.2) y `repository.UsuarioRepository` (PASO A.3).
