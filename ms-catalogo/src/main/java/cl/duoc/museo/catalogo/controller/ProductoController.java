package cl.duoc.museo.catalogo.controller;

import cl.duoc.museo.catalogo.dto.MovimientoStockRequest;
import cl.duoc.museo.catalogo.dto.ProductoRequest;
import cl.duoc.museo.catalogo.dto.ProductoResponse;
import cl.duoc.museo.catalogo.model.Categoria;
import cl.duoc.museo.catalogo.service.ProductoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
@Tag(name = "Productos", description = "Tienda: merch, conservación, publicaciones y cursos")
public class ProductoController {

    private final ProductoService servicio;

    public ProductoController(ProductoService servicio) {
        this.servicio = servicio;
    }

    @GetMapping
    @Operation(summary = "Lista el catálogo; filtros opcionales por categoría y texto")
    public List<ProductoResponse> listar(@RequestParam(required = false) Categoria categoria,
                                         @RequestParam(required = false, name = "q") String texto) {
        return servicio.listar(categoria, texto).stream().map(ProductoResponse::desde).toList();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Detalle de un producto")
    public ProductoResponse buscar(@PathVariable Long id) {
        return ProductoResponse.desde(servicio.buscar(id));
    }

    @GetMapping("/codigo/{codigo}")
    @Operation(summary = "Busca un producto por su código (ej: MDM-001)")
    public ProductoResponse porCodigo(@PathVariable String codigo) {
        return ProductoResponse.desde(servicio.buscarPorCodigo(codigo));
    }

    @PostMapping
    @Operation(summary = "Crea un producto (Administrador)")
    public ResponseEntity<ProductoResponse> crear(@Valid @RequestBody ProductoRequest datos) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ProductoResponse.desde(servicio.crear(datos)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Edita un producto (Administrador)")
    public ProductoResponse actualizar(@PathVariable Long id, @Valid @RequestBody ProductoRequest datos) {
        return ProductoResponse.desde(servicio.actualizar(id, datos));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Elimina un producto (Administrador)")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        servicio.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/descontar-stock")
    @Operation(summary = "Descuenta stock al confirmar una compra (lo llama ms-pedidos)")
    public ProductoResponse descontar(@PathVariable Long id, @Valid @RequestBody MovimientoStockRequest datos) {
        return ProductoResponse.desde(servicio.descontarStock(id, datos.cantidad()));
    }

    @PostMapping("/{id}/reponer-stock")
    @Operation(summary = "Devuelve stock cuando se anula una compra (lo llama ms-pedidos)")
    public ProductoResponse reponer(@PathVariable Long id, @Valid @RequestBody MovimientoStockRequest datos) {
        return ProductoResponse.desde(servicio.reponerStock(id, datos.cantidad()));
    }
}
