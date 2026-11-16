package cl.duoc.museo.usuarios.dto;

import cl.duoc.museo.usuarios.model.Rol;
import jakarta.validation.constraints.NotNull;

public record CambioRolRequest(@NotNull(message = "El rol es obligatorio") Rol rol) {
}
