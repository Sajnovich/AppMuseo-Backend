package cl.duoc.museo.pedidos.dto;

import cl.duoc.museo.pedidos.model.LineaPedido;

public record LineaResponse(Long productoId, String nombre, int cantidad, int precioUnitario, int total) {

    public static LineaResponse desde(LineaPedido l) {
        return new LineaResponse(l.getProductoId(), l.getNombre(), l.getCantidad(), l.getPrecioUnitario(), l.getTotal());
    }
}
