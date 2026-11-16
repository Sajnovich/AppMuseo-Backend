package cl.duoc.museo.catalogo.controller;

import cl.duoc.museo.catalogo.dto.MuseoRequest;
import cl.duoc.museo.catalogo.dto.MuseoResponse;
import cl.duoc.museo.catalogo.service.MuseoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/museos")
@Tag(name = "Museos", description = "Museos virtuales del mapa")
public class MuseoController {

    private final MuseoService servicio;

    public MuseoController(MuseoService servicio) {
        this.servicio = servicio;
    }

    @GetMapping
    @Operation(summary = "Lista los museos; se puede filtrar por país o tipo")
    public List<MuseoResponse> listar(@RequestParam(required = false) String pais,
                                      @RequestParam(required = false) String tipo) {
        return servicio.listar(pais, tipo).stream().map(MuseoResponse::desde).toList();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Ficha de un museo")
    public MuseoResponse buscar(@PathVariable Long id) {
        return MuseoResponse.desde(servicio.buscar(id));
    }

    @GetMapping("/codigo/{codigo}")
    @Operation(summary = "Busca un museo por su código QR (ej: MUSEO-5)")
    public MuseoResponse porCodigo(@PathVariable String codigo) {
        return MuseoResponse.desde(servicio.buscarPorCodigo(codigo));
    }

    @PostMapping
    @Operation(summary = "Crea un museo (Administrador)")
    public ResponseEntity<MuseoResponse> crear(@Valid @RequestBody MuseoRequest datos) {
        return ResponseEntity.status(HttpStatus.CREATED).body(MuseoResponse.desde(servicio.crear(datos)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Edita un museo (Administrador)")
    public MuseoResponse actualizar(@PathVariable Long id, @Valid @RequestBody MuseoRequest datos) {
        return MuseoResponse.desde(servicio.actualizar(id, datos));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Elimina un museo (Administrador)")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        servicio.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
