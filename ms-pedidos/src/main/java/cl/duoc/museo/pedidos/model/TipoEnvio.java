package cl.duoc.museo.pedidos.model;

// express cuesta más pero llega en la mitad del tiempo
public enum TipoEnvio {
    ESTANDAR("Estándar", 1.0),
    EXPRESS("Express", 1.6);

    private final String etiqueta;
    private final double recargo;

    TipoEnvio(String etiqueta, double recargo) {
        this.etiqueta = etiqueta;
        this.recargo = recargo;
    }

    public String getEtiqueta() { return etiqueta; }
    public double getRecargo() { return recargo; }
}
