package com.ufide.primerproyecto;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class homeController {

    @GetMapping("/") 
    public String home(Model model, @RequestParam String nombre){
        model.addAttribute( "nombre", nombre.toUpperCase());
        return "home"; 
    }
}
