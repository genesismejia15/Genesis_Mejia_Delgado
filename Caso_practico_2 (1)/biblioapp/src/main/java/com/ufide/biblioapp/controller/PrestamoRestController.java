package com.ufide.biblioapp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ufide.biblioapp.entity.Prestamo;
import com.ufide.biblioapp.service.PrestamoService;

@RestController
@RequestMapping("/api/prestamos")
public class PrestamoRestController {

    @Autowired
    private PrestamoService prestamoService;

    @GetMapping("/atrasados")
    public List<Prestamo> atrasados() {
        return prestamoService.prestamosAtrasados();
    }
}