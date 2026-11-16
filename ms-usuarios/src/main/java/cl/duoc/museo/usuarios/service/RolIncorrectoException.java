package cl.duoc.museo.usuarios.service;

// la cuenta existe y la clave es correcta, pero no es del tipo elegido: HTTP 403
public class RolIncorrectoException extends RuntimeException {
    public RolIncorrectoException(String mensaje) {
        super(mensaje);
    }
}
