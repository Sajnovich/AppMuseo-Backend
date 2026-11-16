package cl.duoc.museo.pedidos.dto;

import cl.duoc.museo.pedidos.model.Pedido;

import java.time.LocalDateTime;
import java.util.List;

// boleta electrónica simulada: los precios incluyen IVA, se desglosa neto e IVA 19 %
public record BoletaResponse(String emisor, String numero, LocalDateTime fecha, String cliente, String correo,
                             String despacho, List<LineaResponse> lineas,
                             int subtotal, int envio, int neto, int iva, int total) {

    public static BoletaResponse desde(Pedido p) {
        String despacho = p.getZonaEnvio() == null || p.getZonaEnvio().isBlank()
                ? "Sin despacho (solo cursos)"
                : p.getZonaEnvio() + " · " + (p.getTipoEnvio() == null ? "" : p.getTipoEnvio().getEtiqueta());
        return new BoletaResponse("MUSEO DE LOS MUSEOS", p.getCodigo(), p.getFecha(), p.getNombreCliente(),
                p.getCorreoCliente(), despacho, p.getLineas().stream().map(LineaResponse::desde).toList(),
                p.getSubtotal(), p.getCostoEnvio(), p.getNeto(), p.getIva(), p.getTotal());
    }
}
