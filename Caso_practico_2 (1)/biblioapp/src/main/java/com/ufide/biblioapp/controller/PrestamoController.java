package com.ufide.biblioapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ufide.biblioapp.entity.Libro;
import com.ufide.biblioapp.entity.Prestamo;
import com.ufide.biblioapp.entity.Usuario;
import com.ufide.biblioapp.service.LibroService;
import com.ufide.biblioapp.service.PrestamoService;
import com.ufide.biblioapp.service.UsuarioService;

@Controller
public class PrestamoController {

    @Autowired
    private PrestamoService prestamoService;

    @Autowired
    private LibroService libroService;

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/prestamos/nuevo")
    public String nuevo(@RequestParam Long libroId, Model model) {
        Libro libro = libroService.buscarPorId(libroId).orElse(null);

        model.addAttribute("libro", libro);

        return "prestamo-form";
    }

    @PostMapping("/prestamos/guardar")
    public String guardar(@RequestParam Long libroId,
                          @RequestParam String username) {

        Libro libro = libroService.buscarPorId(libroId).orElse(null);
        Usuario usuario = usuarioService.buscarPorUsername(username);

        if (libro != null && usuario != null) {
            Prestamo prestamo = new Prestamo();
            prestamo.setLibro(libro);
            prestamo.setUsuario(usuario);

            prestamoService.registrar(prestamo);
        }

        return "redirect:/libros";
    }

    @PostMapping("/prestamos/devolver")
    public String devolver(@RequestParam Long id) {
        prestamoService.devolver(id);
        return "redirect:/prestamos";
    }

    @GetMapping("/prestamos")
    @PreAuthorize("hasRole('BIBLIOTECARIO')")
    public String listar(Model model) {
       model.addAttribute("prestamos", prestamoService.listar());
       return "prestamos";
}
}
