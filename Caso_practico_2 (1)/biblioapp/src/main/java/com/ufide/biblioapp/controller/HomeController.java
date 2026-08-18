package com.ufide.biblioapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "redirect:/libros";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    // ==========================================================
    // CASO PRACTICO 2 - REQUISITO 3:
    // Falta la ruta GET /403 con la vista de acceso denegado.
    // Crea el metodo, la vista templates/403.html, y configurala
    // en SecurityConfig con .exceptionHandling(ex -> ex
    //     .accessDeniedPage("/403"))
    // ==========================================================
}
