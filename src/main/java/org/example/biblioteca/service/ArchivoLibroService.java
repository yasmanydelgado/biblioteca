package org.example.biblioteca.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.example.biblioteca.model.EstadoLibro;
import org.example.biblioteca.model.Libro;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ArchivoLibroService {

    public String exportarCsv(List<Libro> libros) {
        StringBuilder builder = new StringBuilder();
        builder.append("titulo;autor;genero;numeroPaginas;isbn;estado;calificacion\n");

        for (Libro libro : libros) {
            builder.append(limpiar(libro.getTitulo())).append(';')
                .append(limpiar(libro.getAutor())).append(';')
                .append(limpiar(libro.getGenero())).append(';')
                .append(libro.getNumeroPaginas()).append(';')
                .append(limpiar(libro.getIsbn())).append(';')
                .append(libro.getEstado().name()).append(';')
                .append(libro.getCalificacion() == null ? "" : libro.getCalificacion())
                .append('\n');
        }

        return builder.toString();
    }

    public List<Libro> importarCsv(MultipartFile archivo) {
        if (archivo == null || archivo.isEmpty()) {
            throw new IllegalArgumentException("Debes seleccionar un fichero CSV");
        }

        List<Libro> libros = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(
            new InputStreamReader(archivo.getInputStream(), StandardCharsets.UTF_8))) {

            String linea = reader.readLine();
            if (linea == null) {
                return libros;
            }

            while ((linea = reader.readLine()) != null) {
                if (linea.isBlank()) {
                    continue;
                }

                String[] columnas = linea.split(";", -1);
                if (columnas.length < 7) {
                    throw new IllegalArgumentException("Formato CSV invalido");
                }

                Libro libro = new Libro();
                libro.setTitulo(columnas[0].trim());
                libro.setAutor(columnas[1].trim());
                libro.setGenero(columnas[2].trim());
                libro.setNumeroPaginas(Integer.parseInt(columnas[3].trim()));
                libro.setIsbn(columnas[4].trim());
                libro.setEstado(EstadoLibro.valueOf(columnas[5].trim()));
                libro.setCalificacion(columnas[6].isBlank() ? null : Integer.parseInt(columnas[6].trim()));
                libros.add(libro);
            }

            return libros;
        } catch (IOException | RuntimeException ex) {
            throw new IllegalArgumentException("No se pudo leer el fichero CSV", ex);
        }
    }

    private String limpiar(String valor) {
        return valor == null ? "" : valor.replace(';', ',').trim();
    }
}
