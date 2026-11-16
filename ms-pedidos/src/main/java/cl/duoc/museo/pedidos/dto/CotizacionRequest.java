package cl.duoc.museo.pedidos.dto;

import cl.duoc.museo.pedidos.model.TipoEnvio;
import jakarta.validation.constraints.*;

public record CotizacionRequest(
        @NotBlank(message = "Elige una zona") String zona,
        @Positive(message = "El peso debe ser mayor a 0") @DecimalMax(value = "30", message = "Máximo 30 kg por envío") double pesoKg,
        @PositiveOrZero int subtotal,
        TipoEnvio tipoEnvio
) {
}
