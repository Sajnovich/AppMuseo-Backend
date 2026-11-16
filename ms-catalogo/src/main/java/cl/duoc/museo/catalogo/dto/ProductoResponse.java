package cl.duoc.museo.catalogo.dto;

import cl.duoc.museo.catalogo.model.Producto;

public record ProductoResponse(
        Long id, String nombre, String descripcion, int precio, String categoria,
        int stock, double pesoKg, String codigoBarra, String modalidad, Integer semanas,
        boolean destacado, boolean requiereEnvio, int limiteCompra, String disponibilidad
) {
    public static ProductoResponse desde(Producto p) {
        String disponibilidad;
        if (p.esCurso()) {
            disponibilidad = p.getStock() + " cupos · " + p.getModalidad() + " · " + p.getSemanas() + " semanas";
        } else {
            disponibilidad = p.getStock() > 0 ? "Stock: " + p.getStock() : "Agotado";
        }
        return new ProductoResponse(p.getId(), p.getNombre(), p.getDescripcion(), p.getPrecio(),
                p.getCategoria().name(), p.getStock(), p.getPesoKg(), p.getCodigoBarra(),
                p.getModalidad(), p.getSemanas(), p.isDestacado(), p.requiereEnvio(),
                p.limiteCompra(), disponibilidad);
    }
}
