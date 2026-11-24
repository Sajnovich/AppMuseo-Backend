package cl.duoc.museo.usuarios.seguridad;

import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDateTime;

// Respuestas JSON para 401 (sin token o token inválido) y 403 (rol sin permiso), con el mismo formato de ErrorRespuesta
final class RespuestasSeguridad {

    private RespuestasSeguridad() { }

    static void escribir(HttpServletResponse respuesta, int estado, String mensaje) throws IOException {
        respuesta.setStatus(estado);
        respuesta.setContentType("application/json;charset=UTF-8");
        respuesta.getWriter().write("{\"estado\":" + estado + ",\"mensaje\":\"" + mensaje
                + "\",\"campos\":{},\"fecha\":\"" + LocalDateTime.now() + "\"}");
    }
}
