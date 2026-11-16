package cl.duoc.museo.usuarios.dto;

import cl.duoc.museo.usuarios.model.Rol;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

// rol es opcional: si la app lo manda, se verifica que la cuenta sea de ese tipo
public record LoginRequest(
        @NotBlank(message = "El correo es obligatorio") @Email(message = "Formato de correo inválido") String correo,
        @NotBlank(message = "La contraseña es obligatoria") String clave,
        Rol rol
) {
}
