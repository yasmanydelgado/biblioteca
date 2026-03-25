package org.example.biblioteca.model;

public enum EstadoLibro {
    LEIDO("Leido"),
    NO_LEIDO("No leido"),
    LEYENDO_ACTUALMENTE("Leyendo actualmente");

    private final String etiqueta;

    EstadoLibro(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}
