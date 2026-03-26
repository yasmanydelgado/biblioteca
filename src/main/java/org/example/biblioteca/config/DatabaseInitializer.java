package org.example.biblioteca.config;

import org.example.biblioteca.repository.LibroRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DatabaseInitializer {

    @Bean
    public CommandLineRunner initDatabase(LibroRepository libroRepository) {
        return args -> {
            // Forzar inicializacion de la base de datos al arrancar
            libroRepository.count();
        };
    }
}