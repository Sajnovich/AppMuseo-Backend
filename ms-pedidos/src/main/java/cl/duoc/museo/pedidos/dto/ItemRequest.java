package cl.duoc.museo.pedidos.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ItemRequest(
        @NotNull(message = "Falta el producto") Long productoId,
        @Min(value = 1, message = "La cantidad mínima es 1") @Max(value = 50, message = "Máximo 50 unidades por producto") int cantidad
) {
}
