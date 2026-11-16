package cl.duoc.museo.pedidos.dto;

import cl.duoc.museo.pedidos.model.TipoEnvio;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.util.List;

// La app manda solo QUÉ compra y CUÁNTO. Los precios y el envío los calcula el servidor,
// así nadie puede alterar el total desde el teléfono
public record PedidoRequest(
        @NotBlank(message = "Falta el usuario") @Email String correoUsuario,
        @NotBlank(message = "El nombre es obligatorio") @Size(min = 3, max = 80, message = "Debe tener al menos 3 letras") String nombreCliente,
        @NotBlank(message = "El correo es obligatorio") @Email(message = "Formato de correo inválido") String correoCliente,
        @Size(max = 200) String direccion,
        String zona,
        TipoEnvio tipoEnvio,
        @NotEmpty(message = "El carrito está vacío") List<@Valid ItemRequest> items
) {
}
