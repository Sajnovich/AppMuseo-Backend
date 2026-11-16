package cl.duoc.museo.catalogo.dto;

import cl.duoc.museo.catalogo.model.Categoria;
import jakarta.validation.constraints.*;

// datos para crear o editar un producto (lo usa el Administrador)
public record ProductoRequest(
        @NotBlank(message = "El nombre es obligatorio") @Size(max = 120) String nombre,
        @NotBlank(message = "La descripción es obligatoria") @Size(max = 255) String descripcion,
        @Positive(message = "El precio debe ser mayor a 0") int precio,
        @NotNull(message = "La categoría es obligatoria") Categoria categoria,
        @PositiveOrZero(message = "El stock no puede ser negativo") int stock,
        @PositiveOrZero(message = "El peso no puede ser negativo") @DecimalMax(value = "30", message = "Máximo 30 kg") double pesoKg,
        @Pattern(regexp = "^MDM-\\d{3}$", message = "El código debe tener el formato MDM-000") String codigoBarra,
        String modalidad,
        @Positive Integer semanas,
        boolean destacado
) {
}
