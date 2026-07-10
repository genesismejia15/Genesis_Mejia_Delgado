CLASE 9 - PARTE B (continuacion): crear el archivo `ProfesorService.java`
en este mismo paquete (`service/`), copiando el bloque de abajo.

```java
package com.ufide.cursosapp.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ufide.cursosapp.entity.Profesor;
import com.ufide.cursosapp.repository.ProfesorRepository;

@Service
public class ProfesorService {

    @Autowired
    private ProfesorRepository repo;

    public List<Profesor> listar() {
        return repo.findAll();
    }
}
```

Reglas:

- Solo necesita `listar()` — el form de Curso lo usa para llenar el
  dropdown de profesores. No hay pantallas propias de Profesor en este lab.
