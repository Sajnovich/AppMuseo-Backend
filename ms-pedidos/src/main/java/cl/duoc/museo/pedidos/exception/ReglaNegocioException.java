package cl.duoc.museo.pedidos.exception;

// una regla del negocio no se cumple (ej: no hay stock). Se traduce en un HTTP 409
public class ReglaNegocioException extends RuntimeException {
    public ReglaNegocioException(String mensaje) {
        super(mensaje);
    }
}
