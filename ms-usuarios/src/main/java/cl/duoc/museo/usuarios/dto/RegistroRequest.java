package cl.duoc.museo.usuarios.dto;

import jakarta.validation.constraints.*;

// mismas reglas que valida la app en el formulario de registro
public record RegistroRequest(
        @NotBlank(message = "El nombre es obligatorio") @Size(min = 3, max = 80, message = "Debe tener al menos 3 letras") String nombre,
        @NotBlank(message = "El correo es obligatorio") @Email(message = "Formato de correo inválido") String correo,
        @NotBlank(message = "La contraseña es obligatoria")
        @Size(min = 6, message = "Debe tener al menos 6 caracteres")
        @Pattern(regexp = "^(?=.*[A-Za-zÁÉÍÓÚáéíóúÑñ])(?=.*\\d).+$", message = "Debe combinar letras y números") String clave,
        @NotBlank(message = "Elige un tipo de usuario") String tipoUsuario,
        @Min(0) @Max(20) int avatar
) {
}
