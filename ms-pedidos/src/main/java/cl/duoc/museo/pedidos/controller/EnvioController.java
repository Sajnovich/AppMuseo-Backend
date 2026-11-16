package cl.duoc.museo.pedidos.controller;

import cl.duoc.museo.pedidos.dto.CotizacionRequest;
import cl.duoc.museo.pedidos.dto.CotizacionResponse;
import cl.duoc.museo.pedidos.model.TipoDestino;
import cl.duoc.museo.pedidos.model.Zona;
import cl.duoc.museo.pedidos.service.CotizadorEnvio;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/envios")
@Tag(name = "Envíos", description = "Zonas de despacho y cotizador")
public class EnvioController {

    private final CotizadorEnvio cotizador;

    public EnvioController(CotizadorEnvio cotizador) {
        this.cotizador = cotizador;
    }

    @GetMapping("/zonas")
    @Operation(summary = "Zonas de despacho; ?tipo=NACIONAL o INTERNACIONAL")
    public List<Zona> zonas(@RequestParam(required = false) TipoDestino tipo) {
        return cotizador.zonas(tipo);
    }

    @PostMapping("/cotizar")
    @Operation(summary = "Calcula el costo y plazo de un envío")
    public CotizacionResponse cotizar(@Valid @RequestBody CotizacionRequest datos) {
        return CotizacionResponse.desde(
                cotizador.cotizar(cotizador.buscarZona(datos.zona()), datos.pesoKg(), datos.subtotal(), datos.tipoEnvio()));
    }
}
