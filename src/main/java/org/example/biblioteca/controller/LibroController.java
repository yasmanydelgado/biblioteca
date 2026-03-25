package org.example.biblioteca.controller;

import java.nio.charset.StandardCharsets;

import org.example.biblioteca.dto.LibroForm;
import org.example.biblioteca.model.EstadoLibro;
import org.example.biblioteca.service.ArchivoLibroService;
import org.example.biblioteca.service.LibroService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/")
public class LibroController {

    private final LibroService libroService;
    private final ArchivoLibroService archivoLibroService;

    public LibroController(LibroService libroService, ArchivoLibroService archivoLibroService) {
        this.libroService = libroService;
        this.archivoLibroService = archivoLibroService;
    }

    @GetMapping
    public String verInicio(Model model) {
        cargarModelo(model);
        return "index";
    }

    @PostMapping("/libros")
    public String crearLibro(@Valid @ModelAttribute("libroForm") LibroForm libroForm,
                             BindingResult bindingResult,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            cargarModelo(model);
            return "index";
        }

        try {
            libroService.crearLibro(libroForm);
            redirectAttributes.addFlashAttribute("mensaje", "Libro registrado correctamente");
        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
            cargarModelo(model);
            return "index";
        }

        return "redirect:/";
    }

    @PostMapping("/libros/{id}/calificacion")
    public String calificarLibro(@PathVariable Long id,
                                 @RequestParam Integer calificacion,
                                 RedirectAttributes redirectAttributes) {
        try {
            libroService.calificarLibro(id, calificacion);
            redirectAttributes.addFlashAttribute("mensaje", "Calificacion actualizada");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }

        return "redirect:/";
    }

    @GetMapping("/libros/exportar")
    public ResponseEntity<ByteArrayResource> exportarCsv() {
        String contenido = archivoLibroService.exportarCsv(libroService.listarOrdenadosPorTitulo());
        byte[] bytes = contenido.getBytes(StandardCharsets.UTF_8);

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=libros.csv")
            .contentType(MediaType.parseMediaType("text/csv"))
            .contentLength(bytes.length)
            .body(new ByteArrayResource(bytes));
    }

    @PostMapping("/libros/importar")
    public String importarCsv(@RequestParam("archivo") MultipartFile archivo,
                              RedirectAttributes redirectAttributes) {
        try {
            libroService.guardarTodos(archivoLibroService.importarCsv(archivo));
            redirectAttributes.addFlashAttribute("mensaje", "Fichero cargado correctamente");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }

        return "redirect:/";
    }

    @ModelAttribute("estados")
    public EstadoLibro[] estados() {
        return EstadoLibro.values();
    }

    private void cargarModelo(Model model) {
        if (!model.containsAttribute("libroForm")) {
            model.addAttribute("libroForm", new LibroForm());
        }
        model.addAttribute("libros", libroService.listarOrdenadosPorTitulo());
        model.addAttribute("librosNoLeidos", libroService.listarNoLeidos());
        model.addAttribute("autoresMasLeidos", libroService.obtenerAutoresMasLeidos());
    }
}
