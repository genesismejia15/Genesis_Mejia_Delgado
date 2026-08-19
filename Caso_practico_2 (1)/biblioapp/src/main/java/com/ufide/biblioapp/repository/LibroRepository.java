package com.ufide.biblioapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.ufide.biblioapp.entity.Libro;


public interface LibroRepository extends JpaRepository<Libro, Long> {

    // ==========================================================
    // CASO PRACTICO 2 - REQUISITO 5 (Ejemplo 2, dado):
    // "Libros que nunca se prestaron" va aca, porque la consulta
    // arranca en FROM Libro. El codigo completo esta en el
    // enunciado (caso_practico_2.md, seccion R5, Ejemplo 2) -
    // copialo tal cual, no hace falta modificarlo.
    //
    // El Ejemplo 1 (ranking de libros mas prestados) y la consulta
    // que tenes que escribir vos (prestamosAtrasados) van en
    // PrestamoRepository, que vos vas a crear junto con la
    // entidad Prestamo.
    // ==========================================================
    @Query("SELECT l FROM Libro l LEFT JOIN Prestamo p ON p.libro = l WHERE p.id IS NULL")
List<Libro> librosNuncaPrestados();


    List<Libro> findByCategoria(String categoria);
}
