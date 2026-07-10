CLASE 9 - PARTE B: crear el archivo `ProfesorRepository.java` en este
mismo paquete (`repository/`), copiando el bloque de abajo.

```java
package com.ufide.cursosapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ufide.cursosapp.entity.Profesor;

public interface ProfesorRepository
        extends JpaRepository<Profesor, Long> {
}
```

Reglas:

- Igual que `CursoRepository`: interface, extiende `JpaRepository<Entidad, TipoDelId>`.
- No necesita metodos propios — el CRUD basico (`findAll`, `findById`, `save`,
  `deleteById`) ya viene incluido.
