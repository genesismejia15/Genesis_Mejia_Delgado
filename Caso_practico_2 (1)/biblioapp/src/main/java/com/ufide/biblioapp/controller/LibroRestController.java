package com.ufide.biblioapp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ufide.biblioapp.entity.Libro;
import com.ufide.biblioapp.service.LibroService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/libros")
public class LibroRestController {

    @Autowired
    private LibroService libroService;

    @GetMapping
    public List<Libro> listar() {
        return libroService.listar();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Libro> buscarPorId(@PathVariable Long id) {
        return libroService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('BIBLIOTECARIO')")
    public ResponseEntity<Libro> guardar(@Valid @RequestBody Libro libro) {
        Libro guardado = libroService.guardar(libro);
        return ResponseEntity.status(HttpStatus.CREATED).body(guardado);
    }
    @GetMapping("/categoria/{categoria}")
    public List<Libro> buscarPorCategoria(@PathVariable String categoria) {
        return libroService.buscarPorCategoria(categoria);
}
}