package org.example.biblioteca.controller;

import java.nio.charset.StandardCharsets;

import org.example.biblioteca.dto.LibroForm;
import org.example.biblioteca.model.EstadoLibro;
import org.example.biblioteca.model.Libro;
import org.example.biblioteca.service.ArchivoLibroService;
import org.example.biblioteca.service.LibroService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
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

        libroService.crearLibro(libroForm);
        redirectAttributes.addFlashAttribute("mensaje", "Libro registrado correctamente");
        return "redirect:/";
    }

    @GetMapping("/libros/{id}/editar")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
        Libro libro = libroService.buscarPorId(id);

        LibroForm form = new LibroForm();
        form.setTitulo(libro.getTitulo());
        form.setAutor(libro.getAutor());
        form.setGenero(libro.getGenero());
        form.setNumeroPaginas(libro.getNumeroPaginas());
        form.setIsbn(libro.getIsbn());
        form.setEstado(libro.getEstado());

        model.addAttribute("libroForm", form);
        model.addAttribute("libroId", id);
        model.addAttribute("editando", true);
        model.addAttribute("estados", EstadoLibro.values());
        model.addAttribute("libros", libroService.listarOrdenadosPorTitulo());
        model.addAttribute("librosNoLeidos", libroService.listarNoLeidos());
        model.addAttribute("autoresMasLeidos", libroService.obtenerAutoresMasLeidos());

        return "index";
    }

    @PostMapping("/libros/{id}/editar")
    public String actualizarLibro(@PathVariable Long id,
                                   @Valid @ModelAttribute("libroForm") LibroForm libroForm,
                                   BindingResult bindingResult,
                                   Model model,
                                   RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("libroId", id);
            model.addAttribute("editando", true);
            model.addAttribute("estados", EstadoLibro.values());
            model.addAttribute("libros", libroService.listarOrdenadosPorTitulo());
            model.addAttribute("librosNoLeidos", libroService.listarNoLeidos());
            model.addAttribute("autoresMasLeidos", libroService.obtenerAutoresMasLeidos());
            return "index";
        }

        libroService.actualizarLibro(id, libroForm);
        redirectAttributes.addFlashAttribute("mensaje", "Libro actualizado correctamente");
        return "redirect:/";
    }

    @PostMapping("/libros/{id}/calificacion")
    public String calificarLibro(@PathVariable Long id,
                                 @RequestParam Integer calificacion,
                                 RedirectAttributes redirectAttributes) {
        libroService.calificarLibro(id, calificacion);
        redirectAttributes.addFlashAttribute("mensaje", "Calificacion actualizada");
        return "redirect:/";
    }

    @PostMapping("/libros/{id}/eliminar")
    public String eliminarLibro(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        libroService.eliminarLibro(id);
        redirectAttributes.addFlashAttribute("mensaje", "Libro eliminado correctamente");
        return "redirect:/";
    }

    @GetMapping("/libros/exportar")
    public ResponseEntity<ByteArrayResource> exportarJson() {
        String contenido = archivoLibroService.exportarJson(libroService.listarOrdenadosPorTitulo());
        byte[] bytes = contenido.getBytes(StandardCharsets.UTF_8);

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=libros.json")
            .contentType(MediaType.APPLICATION_JSON)
            .contentLength(bytes.length)
            .body(new ByteArrayResource(bytes));
    }

    @PostMapping("/libros/importar")
    public String importarJson(@RequestParam("archivo") MultipartFile archivo,
                               RedirectAttributes redirectAttributes) {
        libroService.guardarTodos(archivoLibroService.importarJson(archivo));
        redirectAttributes.addFlashAttribute("mensaje", "Fichero JSON cargado correctamente");
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

    @ExceptionHandler(IllegalArgumentException.class)
    public String manejarIllegalArgumentException(IllegalArgumentException ex, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("error", ex.getMessage());
        return "redirect:/";
    }

    @ExceptionHandler(Exception.class)
    public String manejarException(Exception ex, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("error", "Ha ocurrido un error inesperado: " + ex.getMessage());
        return "redirect:/";
    }
}