package org.example.biblioteca.service;

import java.util.Comparator;
import java.util.List;

import org.example.biblioteca.dto.LibroForm;
import org.example.biblioteca.model.EstadoLibro;
import org.example.biblioteca.model.Libro;
import org.example.biblioteca.repository.LibroRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LibroService {

    private final LibroRepository libroRepository;

    public LibroService(LibroRepository libroRepository) {
        this.libroRepository = libroRepository;
    }

    @Transactional
    public void crearLibro(LibroForm form) {
        libroRepository.findByIsbn(form.getIsbn())
            .ifPresent(libro -> {
                throw new IllegalArgumentException("Ya existe un libro con ese ISBN");
            });

        Libro libro = new Libro();
        libro.setTitulo(form.getTitulo().trim());
        libro.setAutor(form.getAutor().trim());
        libro.setGenero(form.getGenero().trim());
        libro.setNumeroPaginas(form.getNumeroPaginas());
        libro.setIsbn(form.getIsbn().trim());
        libro.setEstado(form.getEstado());
        libro.setCalificacion(null);
        libroRepository.save(libro);
    }

    @Transactional
    public void calificarLibro(Long id, Integer calificacion) {
        if (calificacion == null || calificacion < 1 || calificacion > 5) {
            throw new IllegalArgumentException("La calificacion debe estar entre 1 y 5");
        }

        Libro libro = libroRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Libro no encontrado"));

        if (libro.getEstado() != EstadoLibro.LEIDO) {
            throw new IllegalArgumentException("Solo se pueden calificar libros leidos");
        }

        libro.setCalificacion(calificacion);
        libroRepository.save(libro);
    }

    @Transactional(readOnly = true)
    public List<Libro> listarOrdenadosPorTitulo() {
        return libroRepository.findAllByOrderByTituloAsc();
    }

    @Transactional(readOnly = true)
    public List<Libro> listarNoLeidos() {
        return libroRepository.findByEstadoOrderByTituloAsc(EstadoLibro.NO_LEIDO);
    }

    @Transactional(readOnly = true)
    public List<String> obtenerAutoresMasLeidos() {
        List<Object[]> conteos = libroRepository.countAutoresLeidos();
        long maximo = conteos.stream()
            .map(fila -> (Long) fila[1])
            .max(Comparator.naturalOrder())
            .orElse(0L);

        return conteos.stream()
            .filter(fila -> ((Long) fila[1]).equals(maximo))
            .map(fila -> (String) fila[0])
            .toList();
    }

    @Transactional
    public void guardarTodos(List<Libro> libros) {
        libroRepository.deleteAllInBatch();
        libroRepository.saveAll(libros);
    }

    @Transactional(readOnly = true)
    public Libro buscarPorId(Long id) {
        return libroRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Libro no encontrado"));
    }

    @Transactional
    public void actualizarLibro(Long id, LibroForm form) {
        Libro libro = libroRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Libro no encontrado"));

        // Verificar que el ISBN no este en uso por otro libro
        libroRepository.findByIsbn(form.getIsbn())
            .ifPresent(otroLibro -> {
                if (!otroLibro.getId().equals(id)) {
                    throw new IllegalArgumentException("Ya existe otro libro con ese ISBN");
                }
            });

        libro.setTitulo(form.getTitulo().trim());
        libro.setAutor(form.getAutor().trim());
        libro.setGenero(form.getGenero().trim());
        libro.setNumeroPaginas(form.getNumeroPaginas());
        libro.setIsbn(form.getIsbn().trim());
        libro.setEstado(form.getEstado());
        libroRepository.save(libro);
    }
}
