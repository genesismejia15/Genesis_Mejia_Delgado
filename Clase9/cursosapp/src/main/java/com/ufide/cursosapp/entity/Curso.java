package com.ufide.cursosapp.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// CLASE 9 - PASO A.0 Añadir los import necesarios
//import jakarta.persistence.FetchType;
//import jakarta.persistence.JoinColumn;
//import jakarta.persistence.ManyToOne;
//import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "cursos")
public class Curso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede tener mas de 100 caracteres")
    @Column(nullable = false)
    private String nombre;

    @Size(max = 500, message = "La descripcion no puede tener mas de 500 caracteres")
    private String descripcion;

    @Min(value = 1, message = "Los creditos deben ser al menos 1")
    @Max(value = 8, message = "Los creditos no pueden superar los 8")
    private int creditos;

    @NotBlank(message = "El profesor es obligatorio")
    @Size(max = 80)
    private String profesor;

    // CLASE 9 - PASO A.1: profesor pasa de ser un String suelto a una
    // relacion real con la entidad Profesor (que se crea en este mismo lab,
    // ver entity/package-info.md). Para aplicarlo:
    //   1) Borrar las 3 lineas de arriba (@NotBlank, @Size, private String profesor)
    //   2) Descomentar las 4 lineas de abajo
    //   3) Agregar los imports: jakarta.persistence.FetchType, JoinColumn, ManyToOne
    //      y jakarta.validation.constraints.NotNull
    //
    // @NotNull(message = "El profesor es obligatorio")
    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "profesor_id", nullable = false)
    // private Profesor profesor;

    public Curso() {
    }

    public Curso(Long id, String nombre, String descripcion, int creditos, String profesor) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.creditos = creditos;
        this.profesor = profesor;
    }

    // CLASE 9 - PASO A.2: si cambiaste el tipo de profesor a Profesor (paso A.1),
    // este constructor tambien debe recibir Profesor en vez de String:
    //
    // public Curso(Long id, String nombre, String descripcion, int creditos, Profesor profesor) {
    //     this.id = id;
    //     this.nombre = nombre;
    //     this.descripcion = descripcion;
    //     this.creditos = creditos;
    //     this.profesor = profesor;
    // }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public int getCreditos() { return creditos; }
    public void setCreditos(int creditos) { this.creditos = creditos; }

    public String getProfesor() { return profesor; }
    public void setProfesor(String profesor) { this.profesor = profesor; }

    // CLASE 9 - PASO A.3: si cambiaste el tipo de profesor (paso A.1),
    // el getter/setter tambien cambia de tipo. Borrar los 2 de arriba y
    // descomentar estos 2:
    //
    // public Profesor getProfesor() { return profesor; }
    // public void setProfesor(Profesor profesor) { this.profesor = profesor; }
}
