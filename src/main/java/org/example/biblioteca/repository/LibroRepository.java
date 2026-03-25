package org.example.biblioteca.repository;

import java.util.List;
import java.util.Optional;

import org.example.biblioteca.model.EstadoLibro;
import org.example.biblioteca.model.Libro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface LibroRepository extends JpaRepository<Libro, Long> {

    List<Libro> findAllByOrderByTituloAsc();

    List<Libro> findByEstadoOrderByTituloAsc(EstadoLibro estado);

    Optional<Libro> findByIsbn(String isbn);

    @Query("""
        select l.autor, count(l)
        from Libro l
        where l.estado = org.example.biblioteca.model.EstadoLibro.LEIDO
        group by l.autor
        order by l.autor asc
        """)
    List<Object[]> countAutoresLeidos();
}
