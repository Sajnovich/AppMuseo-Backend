package cl.duoc.museo.usuarios.controller;

import cl.duoc.museo.usuarios.dto.*;
import cl.duoc.museo.usuarios.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@Tag(name = "Usuarios", description = "Registro, inicio de sesión, perfil y roles")
public class UsuarioController {

    private final UsuarioService servicio;

    public UsuarioController(UsuarioService servicio) {
        this.servicio = servicio;
    }

    @PostMapping("/registro")
    @Operation(summary = "Crea una cuenta nueva (queda con rol CLIENTE)")
    public ResponseEntity<UsuarioResponse> registrar(@Valid @RequestBody RegistroRequest datos) {
        return ResponseEntity.status(HttpStatus.CREATED).body(UsuarioResponse.desde(servicio.registrar(datos)));
    }

    @PostMapping("/login")
    @Operation(summary = "Inicia sesión; devuelve el perfil del usuario")
    public UsuarioResponse login(@Valid @RequestBody LoginRequest datos) {
        return UsuarioResponse.desde(servicio.login(datos));
    }

    @GetMapping
    @Operation(summary = "Lista todos los usuarios (Administrador)")
    public List<UsuarioResponse> listar() {
        return servicio.listar().stream().map(UsuarioResponse::desde).toList();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Perfil de un usuario")
    public UsuarioResponse buscar(@PathVariable Long id) {
        return UsuarioResponse.desde(servicio.buscar(id));
    }

    @GetMapping("/correo/{correo}")
    @Operation(summary = "Busca un usuario por su correo")
    public UsuarioResponse porCorreo(@PathVariable String correo) {
        return UsuarioResponse.desde(servicio.buscarPorCorreo(correo));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Edita nombre, correo, avatar y opcionalmente la contraseña")
    public UsuarioResponse actualizar(@PathVariable Long id, @Valid @RequestBody ActualizarPerfilRequest datos) {
        return UsuarioResponse.desde(servicio.actualizarPerfil(id, datos));
    }

    @PatchMapping("/{id}/rol")
    @Operation(summary = "Cambia el rol de un usuario (Administrador)")
    public UsuarioResponse cambiarRol(@PathVariable Long id, @Valid @RequestBody CambioRolRequest datos) {
        return UsuarioResponse.desde(servicio.cambiarRol(id, datos.rol()));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Elimina una cuenta (Administrador)")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        servicio.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
