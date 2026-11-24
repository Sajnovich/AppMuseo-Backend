package cl.duoc.museo.catalogo.seguridad;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// Reglas de acceso de ms-catalogo: qué rutas son públicas y qué rol necesita cada una.
// Sin token válido -> 401; con token pero sin el rol -> 403.
@Configuration
public class ConfiguracionSeguridad {

    @Bean
    public SecurityFilterChain cadenaSeguridad(HttpSecurity http, JwtService jwt) throws Exception {
        http
                .csrf(csrf -> csrf.disable())   // API REST sin cookies: no aplica CSRF
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(reglas -> reglas
                        .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**", "/error").permitAll()
                        // consultar el catálogo, los museos y los QR es público
                        .requestMatchers(HttpMethod.GET, "/api/**").permitAll()
                        // mover stock lo usa ms-pedidos reenviando el token del usuario que compra
                        .requestMatchers(HttpMethod.POST, "/api/productos/*/descontar-stock", "/api/productos/*/reponer-stock").authenticated()
                        // fichas de museos: Investigador (las registra y corrige) y Administrador (las revisa).
                        // Eliminar una ficha publicada queda solo para el Administrador
                        .requestMatchers(HttpMethod.DELETE, "/api/museos/**").hasRole("ADMINISTRADOR")
                        .requestMatchers("/api/museos/**").hasAnyRole("INVESTIGADOR", "ADMINISTRADOR")
                        // productos y cursos: alta, modificación y desactivación solo el Administrador
                        .requestMatchers("/api/productos/**").hasRole("ADMINISTRADOR")
                        .anyRequest().authenticated())
                .exceptionHandling(e -> e
                        .authenticationEntryPoint((req, res, ex) ->
                                RespuestasSeguridad.escribir(res, 401, "Falta el token JWT o no es válido. Inicia sesión de nuevo"))
                        .accessDeniedHandler((req, res, ex) ->
                                RespuestasSeguridad.escribir(res, 403, "Tu rol no tiene permiso para esta acción")))
                .addFilterBefore(new FiltroJwt(jwt), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
