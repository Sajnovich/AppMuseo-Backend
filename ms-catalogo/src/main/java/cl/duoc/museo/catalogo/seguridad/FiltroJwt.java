package cl.duoc.museo.catalogo.seguridad;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

// Se ejecuta en cada petición: si trae "Authorization: Bearer <token>" válido,
// deja al usuario autenticado con su rol (ROLE_ADMINISTRADOR, ROLE_INVESTIGADOR o ROLE_CLIENTE)
public class FiltroJwt extends OncePerRequestFilter {

    private final JwtService jwt;

    public FiltroJwt(JwtService jwt) {
        this.jwt = jwt;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest peticion, HttpServletResponse respuesta, FilterChain cadena)
            throws ServletException, IOException {
        String cabecera = peticion.getHeader("Authorization");
        if (cabecera != null && cabecera.startsWith("Bearer ")) {
            jwt.validar(cabecera.substring(7)).ifPresent(datos -> {
                String rol = datos.get("rol", String.class);
                var autenticacion = new UsernamePasswordAuthenticationToken(
                        datos.getSubject(), null, List.of(new SimpleGrantedAuthority("ROLE_" + rol)));
                SecurityContextHolder.getContext().setAuthentication(autenticacion);
            });
        }
        cadena.doFilter(peticion, respuesta);
    }
}
