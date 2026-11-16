package cl.duoc.museo.usuarios.config;

import cl.duoc.museo.usuarios.model.Rol;
import cl.duoc.museo.usuarios.model.Usuario;
import cl.duoc.museo.usuarios.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

// Crea las tres cuentas de prueba del caso (una por rol) si todavía no existen
@Component
public class DatosIniciales implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DatosIniciales.class);

    private final UsuarioRepository repositorio;
    private final PasswordEncoder encoder;

    public DatosIniciales(UsuarioRepository repositorio, PasswordEncoder encoder) {
        this.repositorio = repositorio;
        this.encoder = encoder;
    }

    @Override
    public void run(String... args) {
        crearSiNoExiste("Administrador Demo", "admin@museo.cl", "museo123", "Profesional", Rol.ADMINISTRADOR, 3);
        crearSiNoExiste("Investigadora Demo", "investigador@museo.cl", "inves2026", "Profesional", Rol.INVESTIGADOR, 1);
        crearSiNoExiste("Estudiante Demo", "estudiante@museo.cl", "duoc2026", "Estudiante", Rol.CLIENTE, 2);
    }

    private void crearSiNoExiste(String nombre, String correo, String clave, String tipo, Rol rol, int avatar) {
        if (!repositorio.existsByCorreoIgnoreCase(correo)) {
            repositorio.save(new Usuario(nombre, correo, encoder.encode(clave), tipo, rol, avatar));
            log.info("Cuenta de prueba creada: {} ({})", correo, rol);
        }
    }
}
