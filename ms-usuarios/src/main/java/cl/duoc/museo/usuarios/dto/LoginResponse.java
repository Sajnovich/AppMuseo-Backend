package cl.duoc.museo.usuarios.dto;

import cl.duoc.museo.usuarios.model.Usuario;
import cl.duoc.museo.usuarios.seguridad.JwtService.TokenEmitido;

import java.time.LocalDateTime;

// Respuesta del login: el perfil (igual que UsuarioResponse) más el token JWT que la app envía en cada petición
public record LoginResponse(Long id, String nombre, String correo, String tipoUsuario, String rol, int avatar,
                            LocalDateTime fechaRegistro, String token, String tipoToken, LocalDateTime expiraEn) {

    public static LoginResponse desde(Usuario u, TokenEmitido t) {
        return new LoginResponse(u.getId(), u.getNombre(), u.getCorreo(), u.getTipoUsuario(), u.getRol().name(),
                u.getAvatar(), u.getFechaRegistro(), t.token(), "Bearer", t.expiraEn());
    }
}
