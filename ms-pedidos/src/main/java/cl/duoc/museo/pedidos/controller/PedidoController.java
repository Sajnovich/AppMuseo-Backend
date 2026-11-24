package cl.duoc.museo.pedidos.controller;

import cl.duoc.museo.pedidos.dto.BoletaResponse;
import cl.duoc.museo.pedidos.dto.PedidoRequest;
import cl.duoc.museo.pedidos.dto.PedidoResponse;
import cl.duoc.museo.pedidos.service.PedidoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import cl.duoc.museo.pedidos.seguridad.Sesion;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
@Tag(name = "Pedidos", description = "Compras, seguimiento y boleta")
public class PedidoController {

    private final PedidoService servicio;

    public PedidoController(PedidoService servicio) {
        this.servicio = servicio;
    }

    @PostMapping
    @Operation(summary = "Confirma una compra: calcula precios y envío y descuenta stock")
    public ResponseEntity<PedidoResponse> crear(@Valid @RequestBody PedidoRequest datos, Authentication auth) {
        // un cliente solo puede comprar a su nombre (el correo del pedido debe ser el del token)
        Sesion.exigirDuenoOAdmin(auth, datos.correoUsuario());
        return ResponseEntity.status(HttpStatus.CREATED).body(PedidoResponse.desde(servicio.crear(datos)));
    }

    @GetMapping
    @Operation(summary = "Lista los pedidos; con ?correo= solo los de ese usuario. Sin correo: todos (Administrador)")
    public List<PedidoResponse> listar(@RequestParam(required = false) String correo, Authentication auth) {
        if (correo == null || correo.isBlank()) {
            if (!Sesion.esAdmin(auth)) throw new AccessDeniedException("Solo el Administrador puede ver todas las solicitudes");
        } else {
            Sesion.exigirDuenoOAdmin(auth, correo);
        }
        return servicio.listar(correo).stream().map(PedidoResponse::desde).toList();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Detalle y seguimiento de un pedido")
    public PedidoResponse buscar(@PathVariable Long id, Authentication auth) {
        return PedidoResponse.desde(propio(id, auth));
    }

    @GetMapping("/{id}/boleta")
    @Operation(summary = "Boleta del pedido con neto e IVA")
    public BoletaResponse boleta(@PathVariable Long id, Authentication auth) {
        return BoletaResponse.desde(propio(id, auth));
    }

    @PatchMapping("/{id}/avanzar")
    @Operation(summary = "Pasa el pedido al siguiente estado: Pendiente, Confirmada, Enviada, Entregada (Administrador)")
    public PedidoResponse avanzar(@PathVariable Long id) {
        return PedidoResponse.desde(servicio.avanzarEstado(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Anula un pedido que aún no se despacha y devuelve el stock")
    public ResponseEntity<Void> anular(@PathVariable Long id, Authentication auth) {
        propio(id, auth);
        servicio.anular(id);
        return ResponseEntity.noContent().build();
    }

    // busca el pedido y revisa que sea del usuario del token (o que sea el Administrador)
    private cl.duoc.museo.pedidos.model.Pedido propio(Long id, Authentication auth) {
        var pedido = servicio.buscar(id);
        Sesion.exigirDuenoOAdmin(auth, pedido.getCorreoUsuario());
        return pedido;
    }
}
