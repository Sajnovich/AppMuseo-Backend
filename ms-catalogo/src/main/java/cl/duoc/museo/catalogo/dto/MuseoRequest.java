package cl.duoc.museo.catalogo.dto;

import jakarta.validation.constraints.*;

// datos para crear o editar un museo (lo usa el Administrador)
public record MuseoRequest(
        @NotBlank(message = "El nombre es obligatorio") @Size(max = 120) String nombre,
        @NotBlank(message = "El país es obligatorio") String pais,
        @NotBlank(message = "La ciudad es obligatoria") String ciudad,
        @NotBlank(message = "El tipo es obligatorio") String tipo,
        @NotBlank(message = "La descripción es obligatoria") @Size(max = 255) String descripcion,
        @Size(max = 1000) String detalle,
        @DecimalMin(value = "-90", message = "Latitud fuera de rango") @DecimalMax(value = "90", message = "Latitud fuera de rango") double latitud,
        @DecimalMin(value = "-180", message = "Longitud fuera de rango") @DecimalMax(value = "180", message = "Longitud fuera de rango") double longitud,
        @Min(value = 2000, message = "Año inválido") @Max(value = 2100, message = "Año inválido") int anioLevantamiento,
        @DecimalMin("0") @DecimalMax("5") double calificacion,
        @PositiveOrZero int resenas
) {
}
