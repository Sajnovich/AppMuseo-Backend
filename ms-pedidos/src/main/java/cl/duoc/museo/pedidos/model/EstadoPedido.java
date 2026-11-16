package cl.duoc.museo.pedidos.model;

// el pedido avanza: pendiente -> confirmada -> enviada -> entregada
public enum EstadoPedido {
    PENDIENTE("Pendiente"),
    CONFIRMADA("Confirmada"),
    ENVIADA("Enviada"),
    ENTREGADA("Entregada");

    private final String etiqueta;

    EstadoPedido(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    // null si ya está entregado
    public EstadoPedido siguiente() {
        EstadoPedido[] orden = values();
        return ordinal() + 1 < orden.length ? orden[ordinal() + 1] : null;
    }
}
