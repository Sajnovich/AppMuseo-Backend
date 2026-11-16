package cl.duoc.museo.usuarios.dto;

import jakarta.validation.constraints.*;

// siempre se pide la contraseña actual para confirmar que es la persona dueña de la cuenta
public record ActualizarPerfilRequest(
        @NotBlank(message = "El nombre es obligatorio") @Size(min = 3, max = 80, message = "Debe tener al menos 3 letras") String nombre,
        @NotBlank(message = "El correo es obligatorio") @Email(message = "Formato de correo inválido") String correo,
        @NotBlank(message = "Ingresa tu contraseña actual") String claveActual,
        @Pattern(regexp = "^$|^(?=.*[A-Za-zÁÉÍÓÚáéíóúÑñ])(?=.*\\d).{6,}$", message = "Mínimo 6 caracteres, con letras y números") String nuevaClave,
        @Min(0) @Max(20) Integer avatar
) {
}
