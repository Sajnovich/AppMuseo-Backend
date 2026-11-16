package cl.duoc.museo.usuarios.service;

import cl.duoc.museo.usuarios.dto.ActualizarPerfilRequest;
import cl.duoc.museo.usuarios.dto.LoginRequest;
import cl.duoc.museo.usuarios.dto.RegistroRequest;
import cl.duoc.museo.usuarios.exception.RecursoNoEncontradoException;
import cl.duoc.museo.usuarios.exception.ReglaNegocioException;
import cl.duoc.museo.usuarios.model.Rol;
import cl.duoc.museo.usuarios.model.Usuario;
import cl.duoc.museo.usuarios.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UsuarioService {

    // mismo mensaje para correo o clave incorrectos: no se le dice al atacante cuál de los dos falló
    static final String CREDENCIALES_INVALIDAS = "Correo o contraseña incorrectos";

    private final UsuarioRepository repositorio;
    private final PasswordEncoder encoder;

    public UsuarioService(UsuarioRepository repositorio, PasswordEncoder encoder) {
        this.repositorio = repositorio;
        this.encoder = encoder;
    }

    // Las cuentas nuevas siempre parten como CLIENTE. Solo un administrador cambia roles
    @Transactional
    public Usuario registrar(RegistroRequest datos) {
        String correo = normalizar(datos.correo());
        if (repositorio.existsByCorreoIgnoreCase(correo)) {
            throw new ReglaNegocioException("Ya existe una cuenta con ese correo");
        }
        Usuario nuevo = new Usuario(datos.nombre().trim(), correo, encoder.encode(datos.clave()),
                datos.tipoUsuario().trim(), Rol.CLIENTE, datos.avatar());
        return repositorio.save(nuevo);
    }

    @Transactional(readOnly = true)
    public Usuario login(LoginRequest datos) {
        Usuario usuario = repositorio.findByCorreoIgnoreCase(normalizar(datos.correo()))
                .orElseThrow(() -> new CredencialesInvalidasException(CREDENCIALES_INVALIDAS));
        if (!encoder.matches(datos.clave(), usuario.getClaveHash())) {
            throw new CredencialesInvalidasException(CREDENCIALES_INVALIDAS);
        }
        // en la app se elige primero el tipo de cuenta: si no coincide no se deja entrar
        if (datos.rol() != null && datos.rol() != usuario.getRol()) {
            throw new RolIncorrectoException("Esta cuenta no es de tipo " + datos.rol().name().toLowerCase()
                    + ". Elige el tipo de cuenta correcto");
        }
        return usuario;
    }

    @Transactional(readOnly = true)
    public List<Usuario> listar() {
        return repositorio.findAll();
    }

    @Transactional(readOnly = true)
    public Usuario buscar(Long id) {
        return repositorio.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el usuario con id " + id));
    }

    @Transactional(readOnly = true)
    public Usuario buscarPorCorreo(String correo) {
        return repositorio.findByCorreoIgnoreCase(normalizar(correo))
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe una cuenta con el correo " + correo));
    }

    @Transactional
    public Usuario actualizarPerfil(Long id, ActualizarPerfilRequest datos) {
        Usuario usuario = buscar(id);
        if (!encoder.matches(datos.claveActual(), usuario.getClaveHash())) {
            throw new CredencialesInvalidasException("La contraseña actual no es correcta");
        }
        String nuevoCorreo = normalizar(datos.correo());
        if (!nuevoCorreo.equalsIgnoreCase(usuario.getCorreo()) && repositorio.existsByCorreoIgnoreCase(nuevoCorreo)) {
            throw new ReglaNegocioException("Ese correo ya está en uso");
        }
        usuario.setNombre(datos.nombre().trim());
        usuario.setCorreo(nuevoCorreo);
        if (datos.avatar() != null) {
            usuario.setAvatar(datos.avatar());
        }
        if (datos.nuevaClave() != null && !datos.nuevaClave().isBlank()) {
            usuario.setClaveHash(encoder.encode(datos.nuevaClave()));
        }
        return repositorio.save(usuario);
    }

    @Transactional
    public Usuario cambiarRol(Long id, Rol rol) {
        Usuario usuario = buscar(id);
        usuario.setRol(rol);
        return repositorio.save(usuario);
    }

    @Transactional
    public void eliminar(Long id) {
        repositorio.delete(buscar(id));
    }

    private static String normalizar(String correo) {
        return correo == null ? "" : correo.trim().toLowerCase();
    }
}
