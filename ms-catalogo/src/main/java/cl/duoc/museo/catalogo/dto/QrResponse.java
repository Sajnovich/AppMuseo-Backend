package cl.duoc.museo.catalogo.dto;

// resultado de escanear un QR: puede ser un museo o un producto
public record QrResponse(String tipo, Long id, String codigo, String nombre, String descripcion, String etiqueta) {
}
