package cl.duoc.museo.pedidos.dto;

import cl.duoc.museo.pedidos.service.Cotizacion;

public record CotizacionResponse(String zona, String tipoEnvio, int pesoFacturable, int costoEnvio,
                                 boolean envioGratis, int diasMin, int diasMax, String plazo) {

    public static CotizacionResponse desde(Cotizacion c) {
        return new CotizacionResponse(c.zona().nombre(), c.tipoEnvio().name(), c.pesoFacturable(), c.costoEnvio(),
                c.envioGratis(), c.diasMin(), c.diasMax(), c.plazo());
    }
}
