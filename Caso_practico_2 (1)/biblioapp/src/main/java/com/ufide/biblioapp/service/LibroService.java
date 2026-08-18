package com.ufide.biblioapp.service;

import com.ufide.biblioapp.entity.Libro;
import com.ufide.biblioapp.repository.LibroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LibroService {

    @Autowired
    private LibroRepository libroRepository;

    public List<Libro> listar() {
        return libroRepository.findAll();
    }

    public Optional<Libro> buscarPorId(Long id) {
        return libroRepository.findById(id);
    }

    public Libro guardar(Libro libro) {
        return libroRepository.save(libro);
    }

    // ==========================================================
    // CASO PRACTICO 2 - REQUISITO 2:
    // Cuando implementes el registro de prestamos en PrestamoService,
    // vas a necesitar descontar/sumar copiasDisponibles aca. Un
    // metodo util podria ser:
    //
    //   public void descontarCopia(Libro libro) {
    //       libro.setCopiasDisponibles(libro.getCopiasDisponibles() - 1);
    //       libroRepository.save(libro);
    //   }
    //
    // (y el equivalente para devolver una copia)
    // ==========================================================
}
