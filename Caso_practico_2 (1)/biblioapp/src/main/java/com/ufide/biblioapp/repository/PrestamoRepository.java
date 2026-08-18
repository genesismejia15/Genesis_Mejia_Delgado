package com.ufide.biblioapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ufide.biblioapp.entity.Prestamo;
import com.ufide.biblioapp.entity.Usuario;

public interface PrestamoRepository extends JpaRepository<Prestamo, Long> {

    List<Prestamo> findByUsuario(Usuario usuario);
}