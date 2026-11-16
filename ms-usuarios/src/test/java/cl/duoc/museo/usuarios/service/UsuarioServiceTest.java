package cl.duoc.museo.usuarios.service;

import cl.duoc.museo.usuarios.dto.ActualizarPerfilRequest;
import cl.duoc.museo.usuarios.dto.LoginRequest;
import cl.duoc.museo.usuarios.dto.RegistroRequest;
import cl.duoc.museo.usuarios.exception.ReglaNegocioException;
import cl.duoc.museo.usuarios.model.Rol;
import cl.duoc.museo.usuarios.model.Usuario;
import cl.duoc.museo.usuarios.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository repositorio;

    private final PasswordEncoder encoder = new BCryptPasswordEncoder();
    private UsuarioService servicio;
    private Usuario estudiante;

    @BeforeEach
    void preparar() {
        servicio = new UsuarioService(repositorio, encoder);
        estudiante = new Usuario("Estudiante Demo", "estudiante@museo.cl", encoder.encode("duoc2026"),
                "Estudiante", Rol.CLIENTE, 2);
        estudiante.setId(3L);
    }

    @Test
    void registroGuardaLaClaveCifradaYConRolCliente() {
        when(repositorio.existsByCorreoIgnoreCase("nuevo@museo.cl")).thenReturn(false);
        when(repositorio.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        Usuario creado = servicio.registrar(new RegistroRequest("Nueva Persona", " Nuevo@Museo.cl ", "clave2026", "Estudiante", 1));

        assertEquals("nuevo@museo.cl", creado.getCorreo());
        assertEquals(Rol.CLIENTE, creado.getRol());
        assertNotEquals("clave2026", creado.getClaveHash());
        assertTrue(encoder.matches("clave2026", creado.getClaveHash()));
    }

    @Test
    void noSePuedeRegistrarUnCorreoRepetido() {
        when(repositorio.existsByCorreoIgnoreCase("estudiante@museo.cl")).thenReturn(true);

        assertThrows(ReglaNegocioException.class,
                () -> servicio.registrar(new RegistroRequest("Otra", "estudiante@museo.cl", "clave2026", "Estudiante", 0)));
        verify(repositorio, never()).save(any());
    }

    @Test
    void loginCorrectoDevuelveElUsuario() {
        when(repositorio.findByCorreoIgnoreCase("estudiante@museo.cl")).thenReturn(Optional.of(estudiante));

        Usuario usuario = servicio.login(new LoginRequest("estudiante@museo.cl", "duoc2026", Rol.CLIENTE));

        assertEquals("Estudiante Demo", usuario.getNombre());
    }

    @Test
    void loginConClaveIncorrectaFalla() {
        when(repositorio.findByCorreoIgnoreCase("estudiante@museo.cl")).thenReturn(Optional.of(estudiante));

        CredencialesInvalidasException e = assertThrows(CredencialesInvalidasException.class,
                () -> servicio.login(new LoginRequest("estudiante@museo.cl", "otra", null)));
        assertEquals(UsuarioService.CREDENCIALES_INVALIDAS, e.getMessage());
    }

    @Test
    void loginConRolDistintoNoDejaEntrar() {
        when(repositorio.findByCorreoIgnoreCase("estudiante@museo.cl")).thenReturn(Optional.of(estudiante));

        assertThrows(RolIncorrectoException.class,
                () -> servicio.login(new LoginRequest("estudiante@museo.cl", "duoc2026", Rol.ADMINISTRADOR)));
    }

    @Test
    void editarPerfilPideLaClaveActual() {
        when(repositorio.findById(3L)).thenReturn(Optional.of(estudiante));

        assertThrows(CredencialesInvalidasException.class, () -> servicio.actualizarPerfil(3L,
                new ActualizarPerfilRequest("Nombre Nuevo", "estudiante@museo.cl", "mala", null, null)));
    }

    @Test
    void editarPerfilCambiaNombreYClave() {
        when(repositorio.findById(3L)).thenReturn(Optional.of(estudiante));
        when(repositorio.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        Usuario actualizado = servicio.actualizarPerfil(3L,
                new ActualizarPerfilRequest("Matías Demo", "estudiante@museo.cl", "duoc2026", "nueva2027", 4));

        assertEquals("Matías Demo", actualizado.getNombre());
        assertEquals(4, actualizado.getAvatar());
        assertTrue(encoder.matches("nueva2027", actualizado.getClaveHash()));
    }
}
