package com.ufide.eventapp.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Entidad Evento - representa un evento publico (concierto, taller, charla...).
 *
 * NOTA IMPORTANTE para el Caso Practico 1:
 *   Esta entidad tiene los campos pero NO tiene validaciones.
 *   Como parte del examen tenes que aplicar las anotaciones de
 *   Bean Validation que veas necesarias (@NotBlank, @Size, @Future, etc).
 *
 *   Tampoco hay metodos util tipo isLleno() o isProximo() - si te sirven
 *   para la vista, podes agregarlos.
 */
@Entity
@Table(name = "eventos")
public class Evento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")

    @Column(nullable = false, length = 120)
    @Size(max = 120, message = "El nombre no puede pasar de 120 caracteres")
    
    private String nombre;

    @Size(max = 500, message = "La descripción no puede pasar de 500 caracteres")

    @Column(length = 500)
    private String descripcion;

    /** Fecha del evento (sin hora). */
    @NotNull(message = "La fecha es obligatoria")
    @FutureOrPresent(message = "La fecha debe ser hoy o futura") 

    @Column(nullable = false)
    private LocalDate fecha;

    @NotBlank(message = "El lugar es obligatorio")

    @Column(length = 100)
    private String lugar;

    /** Categoria libre: "Musica", "Conferencia", "Deporte", "Taller", etc. */
   @NotBlank(message = "La categoría es obligatoria")

    @Column(length = 50)
    private String categoria;

    @Size(max = 80, message = "El organizador no puede pasar de 80 caracteres")

    @Column(length = 80)
    private String organizador;

    /** Cupo total disponible. */
    @Positive(message = "El cupo máximo debe ser mayor a 0")

    private int cupoMaximo;

    /** Tickets ya vendidos. */
    @PositiveOrZero(message = "Los cupos vendidos no pueden ser negativos")
    
    private int cuposVendidos;

    /** Precio de la entrada (0 si es gratis). */
    @PositiveOrZero(message = "El precio no puede ser negativo")

    private double precio;

    public Evento() {}

    public Evento(String nombre, String descripcion, LocalDate fecha, String lugar,
                  String categoria, String organizador,
                  int cupoMaximo, int cuposVendidos, double precio) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.fecha = fecha;
        this.lugar = lugar;
        this.categoria = categoria;
        this.organizador = organizador;
        this.cupoMaximo = cupoMaximo;
        this.cuposVendidos = cuposVendidos;
        this.precio = precio;
    }

    // Getters y setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public String getLugar() { return lugar; }
    public void setLugar(String lugar) { this.lugar = lugar; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public String getOrganizador() { return organizador; }
    public void setOrganizador(String organizador) { this.organizador = organizador; }

    public int getCupoMaximo() { return cupoMaximo; }
    public void setCupoMaximo(int cupoMaximo) { this.cupoMaximo = cupoMaximo; }

    public int getCuposVendidos() { return cuposVendidos; }
    public void setCuposVendidos(int cuposVendidos) { this.cuposVendidos = cuposVendidos; }

    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }

    public boolean isLleno(){
        return cuposVendidos >= cupoMaximo; }

    public String getFechaFormateada() {
    if (fecha == null) {
        return "Sin fecha";
    }

    String[] meses = {
        "enero", "febrero", "marzo", "abril", "mayo", "junio",
        "julio", "agosto", "septiembre", "octubre", "noviembre", "diciembre"
    };

    int dia = fecha.getDayOfMonth();
    String mes = meses[fecha.getMonthValue() - 1];
    int anio = fecha.getYear();

    return dia + " de " + mes + " de " + anio;
}

}

