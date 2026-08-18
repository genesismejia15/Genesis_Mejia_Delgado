package com.ufide.biblioapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.ufide.biblioapp.entity.Prestamo;
import com.ufide.biblioapp.entity.Usuario;

public interface PrestamoRepository extends JpaRepository<Prestamo, Long> {

    List<Prestamo> findByUsuario(Usuario usuario);

    @Query("SELECT p FROM Prestamo p WHERE p.fechaDevolucion IS NULL AND p.fechaLimite < CURRENT_DATE")
    List<Prestamo> prestamosAtrasados();
}