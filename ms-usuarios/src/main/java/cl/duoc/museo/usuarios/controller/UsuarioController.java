package cl.duoc.museo.usuarios.controller;

import cl.duoc.museo.usuarios.dto.*;
import cl.duoc.museo.usuarios.model.Usuario;
import cl.duoc.museo.usuarios.seguridad.JwtService;
import cl.duoc.museo.usuarios.seguridad.Sesion;
import cl.duoc.museo.usuarios.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@Tag(name = "Usuarios", description = "Registro, inicio de sesión, perfil y roles")
public class UsuarioController {

    private final UsuarioService servicio;
    private final JwtService jwt;

    public UsuarioController(UsuarioService servicio, JwtService jwt) {
        this.servicio = servicio;
        this.jwt = jwt;
    }

    @PostMapping("/registro")
    @Operation(summary = "Crea una cuenta nueva (queda con rol CLIENTE)")
    public ResponseEntity<UsuarioResponse> registrar(@Valid @RequestBody RegistroRequest datos) {
        return ResponseEntity.status(HttpStatus.CREATED).body(UsuarioResponse.desde(servicio.registrar(datos)));
    }

    @PostMapping("/login")
    @Operation(summary = "Inicia sesión; devuelve el perfil y el token JWT (Bearer) para las demás peticiones")
    public LoginResponse login(@Valid @RequestBody LoginRequest datos) {
        Usuario usuario = servicio.login(datos);
        return LoginResponse.desde(usuario, jwt.emitir(usuario.getCorreo(), usuario.getRol().name()));
    }

    @GetMapping("/sesion")
    @Operation(summary = "Muestra qué dice el token enviado: correo y rol (sirve para comprobar el JWT)")
    public java.util.Map<String, String> sesion(Authentication auth) {
        String rol = auth.getAuthorities().iterator().next().getAuthority().replace("ROLE_", "");
        return java.util.Map.of("correo", auth.getName(), "rol", rol);
    }

    @GetMapping
    @Operation(summary = "Lista todos los usuarios (Administrador)")
    public List<UsuarioResponse> listar() {
        return servicio.listar().stream().map(UsuarioResponse::desde).toList();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Perfil de un usuario")
    public UsuarioResponse buscar(@PathVariable Long id, Authentication auth) {
        Usuario usuario = servicio.buscar(id);
        Sesion.exigirDuenoOAdmin(auth, usuario.getCorreo());
        return UsuarioResponse.desde(usuario);
    }

    @GetMapping("/correo/{correo}")
    @Operation(summary = "Busca un usuario por su correo")
    public UsuarioResponse porCorreo(@PathVariable String correo, Authentication auth) {
        Sesion.exigirDuenoOAdmin(auth, correo);
        return UsuarioResponse.desde(servicio.buscarPorCorreo(correo));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Edita nombre, correo, avatar y opcionalmente la contraseña")
    public UsuarioResponse actualizar(@PathVariable Long id, @Valid @RequestBody ActualizarPerfilRequest datos,
                                      Authentication auth) {
        Sesion.exigirDuenoOAdmin(auth, servicio.buscar(id).getCorreo());
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
