package cl.duoc.museo.usuarios.service;

// se traduce en un HTTP 401
public class CredencialesInvalidasException extends RuntimeException {
    public CredencialesInvalidasException(String mensaje) {
        super(mensaje);
    }
}
