package cl.duoc.museo.catalogo.controller;

import cl.duoc.museo.catalogo.dto.QrResponse;
import cl.duoc.museo.catalogo.service.QrService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/qr")
@Tag(name = "Escáner QR", description = "Resuelve lo que lee la cámara de la app")
public class QrController {

    private final QrService servicio;

    public QrController(QrService servicio) {
        this.servicio = servicio;
    }

    @GetMapping("/{codigo}")
    @Operation(summary = "Devuelve el museo o producto que corresponde al código escaneado")
    public QrResponse resolver(@PathVariable String codigo) {
        return servicio.resolver(codigo);
    }
}
