package cl.duoc.museo.pedidos.service;

import cl.duoc.museo.pedidos.model.TipoEnvio;
import cl.duoc.museo.pedidos.model.Zona;

public record Cotizacion(Zona zona, TipoEnvio tipoEnvio, int pesoFacturable, int costoEnvio, boolean envioGratis) {

    public int diasMin() {
        return tipoEnvio == TipoEnvio.EXPRESS ? Math.max(1, zona.diasMin() / 2) : zona.diasMin();
    }

    public int diasMax() {
        return tipoEnvio == TipoEnvio.EXPRESS ? Math.max(1, zona.diasMax() / 2) : zona.diasMax();
    }

    public String plazo() {
        return diasMin() == diasMax() ? diasMin() + " día hábil" : diasMin() + " a " + diasMax() + " días hábiles";
    }
}
