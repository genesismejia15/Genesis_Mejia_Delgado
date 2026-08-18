package com.ufide.biblioapp.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(unique = true)
    private String username;

    @NotBlank
    private String password;

    @NotBlank
    private String nombreCompleto;

    private String email;

    // ==========================================================
    // CASO PRACTICO 2 - REQUISITO 3:
    // Este campo hoy es un String libre ("BIBLIOTECARIO" / "LECTOR").
    // Tu trabajo es crear un enum Rol con esas dos constantes y
    // validar este campo contra el enum (sin cambiar el tipo de
    // columna es suficiente - lo importante es que tu codigo Java
    // ya no escriba el string "a mano" en ningun lado, sino que
    // use Rol.BIBLIOTECARIO.name() / Rol.LECTOR.name()).
    // ==========================================================
    @NotBlank
    private String rol;

    public Usuario() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
}
