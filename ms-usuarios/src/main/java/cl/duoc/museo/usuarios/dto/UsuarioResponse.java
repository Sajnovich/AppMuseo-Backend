package cl.duoc.museo.usuarios.dto;

import cl.duoc.museo.usuarios.model.Usuario;

import java.time.LocalDateTime;

// lo que se devuelve a la app: nunca incluye la contraseña
public record UsuarioResponse(Long id, String nombre, String correo, String tipoUsuario,
                              String rol, int avatar, LocalDateTime fechaRegistro) {

    public static UsuarioResponse desde(Usuario u) {
        return new UsuarioResponse(u.getId(), u.getNombre(), u.getCorreo(), u.getTipoUsuario(),
                u.getRol().name(), u.getAvatar(), u.getFechaRegistro());
    }
}
