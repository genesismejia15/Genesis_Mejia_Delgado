package com.ufide.biblioapp.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ufide.biblioapp.entity.Libro;
import com.ufide.biblioapp.entity.Prestamo;
import com.ufide.biblioapp.entity.Usuario;
import com.ufide.biblioapp.repository.PrestamoRepository;

@Service
public class PrestamoService {

    @Autowired
    private PrestamoRepository prestamoRepository;

    @Autowired
    private LibroService libroService;

    public List<Prestamo> listar() {
        return prestamoRepository.findAll();
    }

    public List<Prestamo> listarPorUsuario(Usuario usuario) {
        return prestamoRepository.findByUsuario(usuario);
    }

    public Optional<Prestamo> buscarPorId(Long id) {
        return prestamoRepository.findById(id);
    }

    public Prestamo registrar(Prestamo prestamo) {
        prestamo.setFechaPrestamo(LocalDate.now());
        prestamo.setFechaLimite(LocalDate.now().plusDays(14));

        Libro libro = prestamo.getLibro();

        if (libro.getCopiasDisponibles() > 0) {
            libro.setCopiasDisponibles(libro.getCopiasDisponibles() - 1);
            libroService.guardar(libro);

            return prestamoRepository.save(prestamo);
        }

        return null;
    }

    public void devolver(Long id) {
        Prestamo prestamo = prestamoRepository.findById(id).orElse(null);

        if (prestamo != null && prestamo.getFechaDevolucion() == null) {
            prestamo.setFechaDevolucion(LocalDate.now());

            Libro libro = prestamo.getLibro();
            libro.setCopiasDisponibles(libro.getCopiasDisponibles() + 1);

            libroService.guardar(libro);
            prestamoRepository.save(prestamo);
        }
    }
}
