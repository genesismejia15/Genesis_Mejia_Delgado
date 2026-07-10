package com.ufide.cursosapp.service;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ufide.cursosapp.entity.Curso;
import com.ufide.cursosapp.repository.CursoRepository;

@Service
public class CursoService {

    @Autowired
    private CursoRepository repo;

    // Version con N+1: findAll() no trae el profesor, cada acceso a
    // curso.getProfesor() dispara una consulta aparte. La vamos a usar
    // primero para VER el problema en los logs (spring.jpa.show-sql=true).
    public List<Curso> listar() {
        return repo.findAll();
    }

    // CLASE 9 - PASO C.3: version optimizada con JOIN FETCH (requiere el
    // metodo findAllConProfesor() del PASO C.2 en CursoRepository).
    // Descomentar cuando ese metodo ya exista:
    //
    // public List<Curso> listarConProfesor() {
    //     return repo.findAllConProfesor();
    // }

    public Optional<Curso> buscarPorId(Long id) {
        return repo.findById(id);
    }

    public Curso guardar(Curso c) {
        return repo.save(c);
    }

    public void eliminar(Long id) {
        repo.deleteById(id);
    }
}