package cl.duoc.museo.pedidos.client;

// ms-catalogo no respondió: HTTP 503
public class CatalogoNoDisponibleException extends RuntimeException {
    public CatalogoNoDisponibleException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
