package cl.duoc.museo.catalogo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

// Captura las excepciones de todos los controladores y las convierte en JSON con el código HTTP correcto
@RestControllerAdvice
public class ManejadorErrores {

    // el token es válido, pero el recurso es de otro usuario
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorRespuesta> sinPermiso(AccessDeniedException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ErrorRespuesta.de(403, e.getMessage()));
    }

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<ErrorRespuesta> noEncontrado(RecursoNoEncontradoException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ErrorRespuesta.de(404, e.getMessage()));
    }

    @ExceptionHandler(ReglaNegocioException.class)
    public ResponseEntity<ErrorRespuesta> reglaNegocio(ReglaNegocioException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ErrorRespuesta.de(409, e.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorRespuesta> argumentoInvalido(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(ErrorRespuesta.de(400, e.getMessage()));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorRespuesta> jsonInvalido(HttpMessageNotReadableException e) {
        return ResponseEntity.badRequest().body(ErrorRespuesta.de(400, "El cuerpo de la petición no es un JSON válido"));
    }

    // errores de @Valid: devuelve cada campo con su mensaje
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorRespuesta> validacion(MethodArgumentNotValidException e) {
        Map<String, String> campos = new LinkedHashMap<>();
        for (FieldError error : e.getBindingResult().getFieldErrors()) {
            campos.putIfAbsent(error.getField(), error.getDefaultMessage());
        }
        return ResponseEntity.badRequest()
                .body(new ErrorRespuesta(400, "Hay datos inválidos en la petición", campos, LocalDateTime.now()));
    }
}
