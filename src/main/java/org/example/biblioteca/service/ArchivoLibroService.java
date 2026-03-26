package org.example.biblioteca.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.example.biblioteca.model.EstadoLibro;
import org.example.biblioteca.model.Libro;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ArchivoLibroService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public String exportarJson(List<Libro> libros) {
        try {
            List<LibroJson> librosJson = libros.stream()
                .map(this::toJson)
                .toList();
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(librosJson);
        } catch (Exception e) {
            throw new RuntimeException("Error al exportar JSON", e);
        }
    }

    public List<Libro> importarJson(MultipartFile archivo) {
        if (archivo == null || archivo.isEmpty()) {
            throw new IllegalArgumentException("Debes seleccionar un fichero JSON");
        }

        try {
            String contenido = new String(archivo.getBytes(), StandardCharsets.UTF_8);
            List<LibroJson> librosJson = objectMapper.readValue(contenido, new TypeReference<List<LibroJson>>() {});
            return librosJson.stream()
                .map(this::fromJson)
                .toList();
        } catch (IOException e) {
            throw new IllegalArgumentException("No se pudo leer el fichero JSON: " + e.getMessage());
        }
    }

    private LibroJson toJson(Libro libro) {
        LibroJson json = new LibroJson();
        json.titulo = libro.getTitulo();
        json.autor = libro.getAutor();
        json.genero = libro.getGenero();
        json.numeroPaginas = libro.getNumeroPaginas();
        json.isbn = libro.getIsbn();
        json.estado = libro.getEstado().name();
        json.calificacion = libro.getCalificacion();
        return json;
    }

    private Libro fromJson(LibroJson json) {
        Libro libro = new Libro();
        libro.setTitulo(json.titulo);
        libro.setAutor(json.autor);
        libro.setGenero(json.genero);
        libro.setNumeroPaginas(json.numeroPaginas);
        libro.setIsbn(json.isbn);
        libro.setEstado(EstadoLibro.valueOf(json.estado));
        libro.setCalificacion(json.calificacion);
        return libro;
    }

    private static class LibroJson {
        public String titulo;
        public String autor;
        public String genero;
        public Integer numeroPaginas;
        public String isbn;
        public String estado;
        public Integer calificacion;
    }
}