package cl.duoc.museo.catalogo.seguridad;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;

// Ayudas para revisar quién hace la petición (dueño del recurso o administrador)
public final class Sesion {

    private Sesion() { }

    public static boolean esAdmin(Authentication auth) {
        return auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMINISTRADOR"));
    }

    // Solo el dueño (mismo correo del token) o un administrador pueden continuar
    public static void exigirDuenoOAdmin(Authentication auth, String correoDueno) {
        if (esAdmin(auth)) return;
        if (auth == null || correoDueno == null || !correoDueno.equalsIgnoreCase(auth.getName())) {
            throw new AccessDeniedException("No tienes permiso para ver o modificar datos de otro usuario");
        }
    }
}
