package cl.duoc.museo.catalogo.dto;

import cl.duoc.museo.catalogo.model.Museo;

import java.util.List;

public record MuseoResponse(
        Long id, String nombre, String pais, String ciudad, String tipo,
        String descripcion, String detalle, double latitud, double longitud,
        int anioLevantamiento, double calificacion, int resenas,
        String codigoQr, List<String> etiquetas
) {
    public static MuseoResponse desde(Museo m) {
        return new MuseoResponse(m.getId(), m.getNombre(), m.getPais(), m.getCiudad(), m.getTipo(),
                m.getDescripcion(), m.getDetalle(), m.getLatitud(), m.getLongitud(),
                m.getAnioLevantamiento(), m.getCalificacion(), m.getResenas(),
                m.getCodigoQr(), List.of(m.getTipo(), m.getPais(), "Virtual"));
    }
}
