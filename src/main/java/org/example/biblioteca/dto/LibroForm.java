package org.example.biblioteca.dto;

import org.example.biblioteca.model.EstadoLibro;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class LibroForm {

    @NotBlank(message = "El titulo es obligatorio")
    private String titulo;

    @NotBlank(message = "El autor es obligatorio")
    private String autor;

    @NotBlank(message = "El genero es obligatorio")
    private String genero;

    @NotNull(message = "El numero de paginas es obligatorio")
    @Min(value = 1, message = "El numero de paginas debe ser mayor que cero")
    private Integer numeroPaginas;

    @NotBlank(message = "El ISBN es obligatorio")
    @Size(max = 30, message = "El ISBN no puede superar 30 caracteres")
    private String isbn;

    @NotNull(message = "El estado es obligatorio")
    private EstadoLibro estado;

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public Integer getNumeroPaginas() {
        return numeroPaginas;
    }

    public void setNumeroPaginas(Integer numeroPaginas) {
        this.numeroPaginas = numeroPaginas;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public EstadoLibro getEstado() {
        return estado;
    }

    public void setEstado(EstadoLibro estado) {
        this.estado = estado;
    }
}
