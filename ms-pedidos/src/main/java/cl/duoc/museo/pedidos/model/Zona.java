package cl.duoc.museo.pedidos.model;

// zona de despacho con su tarifa: costo = (base + costo por kg × peso) × recargo
public record Zona(String nombre, TipoDestino tipo, int tarifaBase, int costoPorKg, int diasMin, int diasMax) {
}
