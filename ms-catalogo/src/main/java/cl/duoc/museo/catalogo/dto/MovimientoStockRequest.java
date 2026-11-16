package cl.duoc.museo.catalogo.dto;

import jakarta.validation.constraints.Min;

public record MovimientoStockRequest(@Min(value = 1, message = "La cantidad debe ser al menos 1") int cantidad) {
}
