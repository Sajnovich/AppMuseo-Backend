package cl.duoc.museo.usuarios.exception;

import java.time.LocalDateTime;
import java.util.Map;

// formato único de error que recibe la app
public record ErrorRespuesta(int estado, String mensaje, Map<String, String> campos, LocalDateTime fecha) {

    public static ErrorRespuesta de(int estado, String mensaje) {
        return new ErrorRespuesta(estado, mensaje, Map.of(), LocalDateTime.now());
    }
}
