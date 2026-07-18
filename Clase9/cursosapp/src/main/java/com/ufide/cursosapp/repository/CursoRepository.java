package com.ufide.cursosapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.ufide.cursosapp.entity.Curso;

public interface CursoRepository
        extends JpaRepository<Curso, Long> {

    // CLASE 9 - PASO C.2: JPQL con JOIN FETCH. Trae Curso + Profesor en UNA
    // sola consulta SQL. Sin esto, listar() + acceder a curso.getProfesor()
    // dispara N+1 (una consulta extra por cada curso, porque profesor es LAZY).
    //
    @Query("SELECT c FROM Curso c JOIN FETCH c.profesor")
     List<Curso> findAllConProfesor();
}
