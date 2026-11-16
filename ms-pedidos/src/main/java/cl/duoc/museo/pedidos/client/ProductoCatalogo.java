package cl.duoc.museo.pedidos.client;

// lo que ms-pedidos necesita saber de un producto (viene de ms-catalogo)
public record ProductoCatalogo(Long id, String nombre, int precio, String categoria, int stock,
                               double pesoKg, boolean requiereEnvio, int limiteCompra) {
}
