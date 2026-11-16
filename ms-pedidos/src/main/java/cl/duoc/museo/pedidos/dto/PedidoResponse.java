package cl.duoc.museo.pedidos.dto;

import cl.duoc.museo.pedidos.model.EstadoPedido;
import cl.duoc.museo.pedidos.model.Pedido;

import java.time.LocalDateTime;
import java.util.List;

public record PedidoResponse(
        Long id, String codigo, String correoUsuario, String nombreCliente, String correoCliente,
        String zonaEnvio, String tipoEnvio, String direccion,
        int subtotal, int costoEnvio, int total, int neto, int iva, int cantidadProductos,
        String estado, String estadoEtiqueta, String siguienteEstado, LocalDateTime fecha,
        List<LineaResponse> lineas
) {
    public static PedidoResponse desde(Pedido p) {
        EstadoPedido siguiente = p.getEstado().siguiente();
        return new PedidoResponse(p.getId(), p.getCodigo(), p.getCorreoUsuario(), p.getNombreCliente(),
                p.getCorreoCliente(), p.getZonaEnvio(), p.getTipoEnvio() == null ? null : p.getTipoEnvio().name(),
                p.getDireccion(), p.getSubtotal(), p.getCostoEnvio(), p.getTotal(), p.getNeto(), p.getIva(),
                p.getCantidadProductos(), p.getEstado().name(), p.getEstado().getEtiqueta(),
                siguiente == null ? null : siguiente.name(), p.getFecha(),
                p.getLineas().stream().map(LineaResponse::desde).toList());
    }
}
